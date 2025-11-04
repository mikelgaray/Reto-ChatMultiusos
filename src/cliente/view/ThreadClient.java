package cliente.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.SwingUtilities;

public class ThreadClient implements Runnable {

    private ObjectInputStream entrada;  // Para recibir mensajes del servidor
    private ClienteView ui;             // Referencia a la interfaz para mostrar mensajes
    private volatile boolean activo = true; 
    /* Controla si el hilo debe seguir escuchando. Si NO pones volatile, 
     * podría provocar que el hilo no se detenga o siga leyendo datos cuando ya no debería.*/


    public ThreadClient(ObjectInputStream entrada, ClienteView ui) {
        this.entrada = entrada;
        this.ui = ui;
    }

    public void detener() {
        boolean cierreCorrecto = true;

        activo = false;

        try {
            if (entrada != null) {
                entrada.close();
            }
        } catch (IOException e) {
            cierreCorrecto = false;
            System.out.println("Error al cerrar stream de entrada: " + e.getMessage());
        }

        if (cierreCorrecto) {
            System.out.println("ThreadClient detenido correctamente.");
        }
    }

    /**
     * Hilo que escucha continuamente los mensajes enviados por el servidor,
     * y los envía a la interfaz gráfica para ser mostrados.
     */
    @Override
    public void run() {

        try {
            while (activo) {

                Object mensajeRecibido = entrada.readObject();

                if (mensajeRecibido != null) {
                    String msg = mensajeRecibido.toString();
                    SwingUtilities.invokeLater(() -> ui.mostrarMensaje(msg));

                } else {
                    activo = false;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            if (activo) {
                SwingUtilities.invokeLater(() ->
                        ui.mostrarMensaje("[Sistema] Se perdió la conexión con el servidor.")
                );
            }

        }
    }
}

