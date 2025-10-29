package servidor;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AtenderClientes extends Thread {
    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private String nombreUsuario;
    private List<AtenderClientes> clientesConectados;

    public AtenderClientes(Socket socket, List<AtenderClientes> clientesConectados) {
        this.socket = socket;
        this.clientesConectados = clientesConectados;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // Primer mensaje: el nombre de usuario
            nombreUsuario = (String) entrada.readObject();
            synchronized (clientesConectados) {
                clientesConectados.add(this);
            }

            System.out.println("Usuario conectado: " + nombreUsuario);
            enviarATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha unido al chat");
            enviarATodos("PUBLICO|Servidor|Usuarios Conectados: " + clientesConectados.size() + "");

            Object mensaje;
            while ((mensaje = entrada.readObject()) != null) {
                String msg = mensaje.toString();

                if (msg.equals("/salir")) {
                    break;
                } else if (msg.startsWith("PUBLICO|")) {
                    enviarATodos(msg);
                } else if (msg.startsWith("PRIVADO|")) {
                    enviarPrivado(msg);
                }
            }

        } catch (Exception e) {
            System.out.println("Error con " + nombreUsuario + ": " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    private void enviarATodos(String msg) {
        synchronized (clientesConectados) {
            for (AtenderClientes cliente : clientesConectados) {
                try {
                    cliente.salida.writeObject(msg);
                    cliente.salida.flush();
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje a " + cliente.nombreUsuario);
                }
            }
        }
    }

    private void enviarPrivado(String msg) {
        // Formato: PRIVADO|emisor|destinatario|mensaje
        String[] partes = msg.split("\\|", 4);
        if (partes.length < 4) return;

        String emisor = partes[1];
        String destinatario = partes[2];
        String contenido = partes[3];

        synchronized (clientesConectados) {
            for (AtenderClientes cliente : clientesConectados) {
                if (cliente.nombreUsuario.equals(destinatario)) {
                    try {
                        cliente.salida.writeObject("PRIVADO|" + emisor + "|" + contenido);
                        cliente.salida.flush();
                        System.out.println("Mensaje privado de " + emisor + " a " + destinatario);
                    } catch (IOException e) {
                        System.out.println("Error enviando privado a " + destinatario);
                    }
                }
            }
        }
    }

  private void desconectar() {
        try {
            synchronized (clientesConectados) {
                clientesConectados.remove(this);
            }
            enviarATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha desconectado");
            socket.close();
            System.out.println("Usuario desconectado: " + nombreUsuario);
        } catch (IOException e) {
            System.out.println("Error al desconectar cliente: " + e.getMessage());
        }
    }
}