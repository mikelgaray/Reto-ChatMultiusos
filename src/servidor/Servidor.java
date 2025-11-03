package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Servidor {

    private final int PUERTO = 5000;

    // Controla cuántos clientes pueden conectarse (según tus preferencias)
    private Contador contador = new Contador();

    /* Lista sincronizada para almacenar los clientes conectados
     * ArrayList NO es seguro para hilos. Si 2 hilos modifican la lista a la vez se puede corromper, lanzar errores o perder datos.
     */
    private List<AtenderClientes> clientesConectados = Collections.synchronizedList(new ArrayList<>());

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            System.out.println("Servidor iniciado en puerto " + PUERTO);

            // Aceptamos clientes indefinidamente
            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Cliente intentando conectar desde: "
                        + socketCliente.getInetAddress().getHostAddress());

                /* Consultamos al contador si todavía hay espacio.
                   Si el servidor está lleno, enviamos mensaje y cerramos. */
                if (!contador.conectarCliente()) {
                    ObjectOutputStream salida = new ObjectOutputStream(socketCliente.getOutputStream());
                    salida.writeObject("Servidor lleno. Máximo 5 clientes.");
                    salida.flush();
                    socketCliente.close();
                    System.out.println("Cliente rechazado. Servidor lleno.");
                    continue;
                }

                /* Creamos el hilo que gestionará a ese cliente.
                   Se le pasa el socket y la lista de clientes conectados. */
                AtenderClientes hiloCliente = new AtenderClientes(socketCliente, clientesConectados);
                hiloCliente.start();

                System.out.println("Cliente aceptado. Clientes conectados: " + contador.getClientesConectados());
            }

        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}