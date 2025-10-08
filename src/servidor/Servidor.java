package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	private final int PUERTO = 5000;
	
	public void iniciar() {
        //ServerSocket servidor = null;
        Socket cliente = null;
        ObjectInputStream entrada = null;
        ObjectOutputStream salida = null;
        int numeroCliente = 0;
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Esperando conexiones del clientes...");
            while (true) {

                try {
                    
                    cliente = servidor.accept();
                    System.out.println("Cliente conectado: "+ cliente.getInetAddress().getHostAddress());

                   //new AterederClientes(cliente).start();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (entrada != null) {
                    entrada.close();
                }
                if (salida != null) {
                    salida.close();
                }
                if (cliente != null) {
                    cliente.close();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        s.iniciar();
    }
}
