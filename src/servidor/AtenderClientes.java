package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class AtenderClientes extends Thread {
    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private String nombreUsuario;
    private List<AtenderClientes> clientesConectados;
    private Contador contador;

    public AtenderClientes(Socket socket, List<AtenderClientes> clientesConectados, Contador contador) {
        this.socket = socket;
        this.clientesConectados = clientesConectados;
        this.contador = contador;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            nombreUsuario = (String) entrada.readObject();
            

            clientesConectados.add(this);
            
            System.out.println("Usuario conectado: " + nombreUsuario);
            System.out.println("Total de usuarios: " + contador.getClientesConectados());
            

            enviarMensajeATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha unido al chat");
            enviarMensajeATodos("PUBLICO|Servidor|Usuarios Conectados: " + contador.getClientesConectados());


            while (true) {
                Object mensajeRecibido = entrada.readObject();
                String mensaje = mensajeRecibido.toString();


                if (mensaje.equals("/salir")) {
                    break;
                }

                else if (mensaje.startsWith("PUBLICO|")) {
                    enviarMensajeATodos(mensaje);
                }

                else if (mensaje.startsWith("PRIVADO|")) {
                    enviarMensajePrivado(mensaje);
                }
            }

        } catch (Exception e) {
            System.out.println("Error con el usuario " + nombreUsuario + ": " + e.getMessage());
        } finally {

            desconectarCliente();
        }
    }


    private void enviarMensajeATodos(String mensaje) {
        for (AtenderClientes cliente : clientesConectados) {
            try {
                cliente.salida.writeObject(mensaje);
                cliente.salida.flush();
            } catch (IOException e) {
                System.out.println("No se pudo enviar mensaje a " + cliente.nombreUsuario);
            }
        }
    }


    private void enviarMensajePrivado(String mensaje) {

        String[] partes = mensaje.split("\\|");
        if (partes.length < 4) return;

        String emisor = partes[1];
        String destinatario = partes[2];
        String texto = partes[3];


        for (AtenderClientes cliente : clientesConectados) {
            if (cliente.nombreUsuario.equals(destinatario)) {
                try {

                    cliente.salida.writeObject("PRIVADO|" + emisor + "|" + texto);
                    cliente.salida.flush();
                    System.out.println("Mensaje privado de " + emisor + " a " + destinatario);
                    break;
                } catch (IOException e) {
                    System.out.println("Error enviando mensaje privado a " + destinatario);
                }
            }
        }
    }


    private void desconectarCliente() {
        try {

            clientesConectados.remove(this);
            

            contador.desconectarCliente();
            

            enviarMensajeATodos("PUBLICO|Servidor|El usuario " + nombreUsuario + " se ha desconectado");
            enviarMensajeATodos("PUBLICO|Servidor|Usuarios Conectados: " + contador.getClientesConectados());
            

            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
            
            System.out.println("Usuario desconectado: " + nombreUsuario);
            System.out.println("Usuarios restantes: " + contador.getClientesConectados());

        } catch (IOException e) {
            System.out.println("Error al desconectar a " + nombreUsuario);
        }
    }
}