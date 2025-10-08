package servidor;

public class Contador {
    private int clientesConectados = 0;
    private final int LIMITE = 5;

    public synchronized boolean conectarCliente() {
        if (clientesConectados < LIMITE) {
            clientesConectados++;
            return true;
        } else {
            return false;
        }
    }


    public synchronized void desconectarCliente() {
        if (clientesConectados > 0) {
            clientesConectados--;
        }
    }


    public synchronized int getClientesConectados() {
        return clientesConectados;
    }
}
