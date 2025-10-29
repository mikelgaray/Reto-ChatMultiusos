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
    private static final int MAX_CLIENTES = 5;
    private static final List<AtenderClientes> clientesConectados = Collections.synchronizedList(new ArrayList<>());

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress().getHostAddress());

                if (clientesConectados.size() >= MAX_CLIENTES) {
                    ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
                    salida.writeObject("Servidor lleno. Máximo " + MAX_CLIENTES + " clientes.");
                    salida.flush();
                    cliente.close();
                    continue;
                }

                AtenderClientes hilo = new AtenderClientes(cliente, clientesConectados);
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}