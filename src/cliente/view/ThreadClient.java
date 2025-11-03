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

    // Método para detener el hilo de forma segura.
    public void detener() {
        boolean cierreCorrecto = true;

        // Marcamos que el hilo debe dejar de escuchar
        activo = false;

        // Cerramos el stream de entrada si es posible
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
            // Bucle de escucha: sigue hasta que "activo" sea false o ocurra error
            while (activo) {

                Object mensajeRecibido = entrada.readObject();

                if (mensajeRecibido != null) {
                    // Convertimos a String para mostrarlo
                    String msg = mensajeRecibido.toString();

                    // Actualizamos la UI desde el hilo de Swing
                    SwingUtilities.invokeLater(() -> ui.mostrarMensaje(msg));

                } else {
                    // Si el servidor envía null, paramos la escucha
                    activo = false;
                }
            }

        } catch (IOException | ClassNotFoundException e) {

            // Si el hilo sigue “activo”, significa que NO fue una desconexión deseada
            if (activo) {
                SwingUtilities.invokeLater(() ->
                        ui.mostrarMensaje("[Sistema] Se perdió la conexión con el servidor.")
                );
            }

        }
    }
}

