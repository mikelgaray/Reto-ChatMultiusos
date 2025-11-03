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

    // Controla si el hilo debe seguir activo
    private volatile boolean activo = true;

    public AtenderClientes(Socket socket, List<AtenderClientes> clientesConectados) {
        this.socket = socket;
        this.clientesConectados = clientesConectados;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            /* El primer dato que recibimos del cliente es el nombre de usuario.
               Lo guardamos y añadimos este cliente a la lista de conectados. */
            nombreUsuario = (String) entrada.readObject();

            synchronized (clientesConectados) {
                clientesConectados.add(this);
            }

            System.out.println("Usuario conectado: " + nombreUsuario);

            // Avisamos a todos los usuarios que un nuevo cliente se ha unido
            enviarATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha unido al chat");

            // Bucle principal de escucha de mensajes
            while (activo) {
                Object mensajeRecibido = entrada.readObject();
                String msg = mensajeRecibido.toString();

                /* Según el prefijo del mensaje, decidimos si es salir, público o privado.
                   No usamos "break"; el boolean activo controla la continuidad del bucle. */
                if (msg.equals("/salir")) {
                    activo = false;
                } else if (msg.startsWith("PUBLICO|")) {
                    enviarATodos(msg);
                } else if (msg.startsWith("PRIVADO|")) {
                    enviarPrivado(msg);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cliente desconectado inesperadamente: " + nombreUsuario);
        } finally {
            desconectar();
        }
    }

    /**
     * Envía un mensaje a todos los clientes conectados.
     * Se recorre la lista sincronizada para evitar problemas de concurrencia.
     */
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

    /**
     * Envía un mensaje privado a un usuario concreto.
     * El formato esperado es: PRIVADO|emisor|destinatario|contenido
     */
    private void enviarPrivado(String msg) {
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

    /**
     * Elimina al cliente de la lista y cierra conexión.
     * También informa al resto de usuarios que se ha desconectado.
     */
    private void desconectar() {
        try {
            activo = false;

            synchronized (clientesConectados) {
                clientesConectados.remove(this);
            }

            enviarATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha desconectado");

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Usuario desconectado: " + nombreUsuario);

        } catch (IOException e) {
            System.out.println("Error al desconectar cliente: " + e.getMessage());
        }
    }
}