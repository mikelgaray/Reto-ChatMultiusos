package cliente.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.SwingUtilities;

import cliente.view.ClienteView;

public class ThreadClient implements Runnable {
    private ObjectInputStream entrada;
    private ClienteView ui;
    private volatile boolean activo = true;

    public ThreadClient(ObjectInputStream entrada, ClienteView ui) {
        this.entrada = entrada;
        this.ui = ui;
    }

    public void detener() {
        activo = false;
        try {
            if (entrada != null) entrada.close();
        } catch (IOException e) {
        	System.out.println("Error" +e);
        }
    }

    @Override
    public void run() {
        try {
            Object mensaje;
            while (activo && (mensaje = entrada.readObject()) != null) {
                final String msg = mensaje.toString();
                SwingUtilities.invokeLater(() -> ui.mostrarMensaje(msg));
            }
        } catch (IOException | ClassNotFoundException e) {
            if (activo) {
                SwingUtilities.invokeLater(() -> ui.mostrarMensaje("[Sistema] Conexión cerrada."));
            }
        }
    }
}
