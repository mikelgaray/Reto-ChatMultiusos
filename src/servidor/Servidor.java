package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private final int PUERTO = 5000;
    
    public void iniciar() {
        Socket cliente = null;
        Contador contador = new Contador();
        
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            System.out.println("Esperando conexiones de clientes...");
            
            while (true) {
                try {
                    cliente = servidor.accept();
                    System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                    
                    
                    ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
                    ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                    
                    // Verificar si hay espacio para más clientes
                    if (contador.conectarCliente()) {
                        // Cliente aceptado
                        int numeroCliente = contador.getClientesConectados();
                        salida.writeObject("Bienvenido al servidor. Eres el cliente número " + numeroCliente);
                        salida.flush();
                        
                        System.out.println("Cliente aceptado. Total: " + numeroCliente);
                        
                        // Crear hilo para atender al cliente
                        new AtenderClientes(cliente, contador, entrada, salida).start();
                        
                    } else {
                        // Servidor lleno - rechazar cliente
                        salida.writeObject("Servidor lleno. Máximo 5 clientes. Inténtalo más tarde.");
                        salida.flush();
                        salida.close();
                        cliente.close();
                        System.out.println("Cliente rechazado - Servidor lleno: " + cliente.getInetAddress().getHostAddress());
                    }
                    
                } catch (Exception e) {
                    System.out.println("Error con cliente: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        s.iniciar();
    }
}