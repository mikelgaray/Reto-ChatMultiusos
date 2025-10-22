package cliente.cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cliente.view.ClienteView;

public class Cliente {
    
    private final int PUERTO = 5000;
    private final String IP = "127.0.0.1";
    
    private String ip;
    private int puerto;
    private String nombreUsuario;
    
    public Cliente() {
        this.ip = "";
        this.puerto = 0;
        this.nombreUsuario = "";
    }

    public void iniciar() {
        Socket cliente = null;
        ObjectInputStream entrada = null;
        ObjectOutputStream salida = null;
        try {
            cliente = new Socket(IP, PUERTO);
            System.out.println("Conexión realizada con servidor");
            salida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());
            String mensaje = (String) entrada.readObject();
            System.out.println(mensaje);
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Fin cliente");
        }
    }
}
