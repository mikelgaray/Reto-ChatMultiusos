package servidor;

/**
 * Clase que controla cuántos clientes pueden conectarse al servidor.
 * Máximo de 5 clientes simultáneos.
 */
public class Contador {

    private int clientesConectados = 0;
    private final int LIMITE = 5;

    /**
     * Intenta conectar un nuevo cliente.
     * @return true si hay espacio y el cliente puede conectarse,
     *         false si el servidor está lleno.
     */
    public synchronized boolean conectarCliente() {
        boolean puedeConectar = false;

        if (clientesConectados < LIMITE) {
            clientesConectados++;
            puedeConectar = true;   // Se marca como conexión exitosa
        } 
        // Si no se cumple el if, simplemente queda como false

        return puedeConectar;
    }

    /**
     * Reduce el contador cuando un cliente se desconecta.
     * No baja de 0, por seguridad.
     */
    public synchronized boolean desconectarCliente() {
        boolean desconectado = false;

        if (clientesConectados > 0) {
            clientesConectados--;
            desconectado = true;  // Se marcó desconexión válida
        }

        return desconectado;
    }

    /**
     * @return cantidad actual de clientes conectados.
     */
    public synchronized int getClientesConectados() {
        return clientesConectados;
    }
}
