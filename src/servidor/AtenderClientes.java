package servidor;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtenderClientes extends Thread {
    private Socket cliente;
    private Contador contador;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    
    public AtenderClientes(Socket cliente, Contador contador, 
                          ObjectInputStream entrada, ObjectOutputStream salida) {
        this.cliente = cliente;
        this.contador = contador;
        this.entrada = entrada;
        this.salida = salida;
    }
    
    @Override
    public void run() {
        try {
            String mensaje;
            while ((mensaje = (String) entrada.readObject()) != null) {
                System.out.println("Mensaje recibido: " + mensaje);
                
                // Responder al cliente
                if (mensaje.equalsIgnoreCase("chao")) {
                    salida.writeObject("¡Hasta pronto!");
                    salida.flush();
                    break;
                } else {
                    salida.writeObject("Servidor: Recibí - " + mensaje);
                    salida.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado: " + e.getMessage());
        } finally {
            // Desconectar cliente
            contador.desconectarCliente();
            try {
                entrada.close();
                salida.close();
                cliente.close();
            } catch (Exception e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
            System.out.println("Cliente desconectado. Quedan: " + contador.getClientesConectados());
        }
    }
}