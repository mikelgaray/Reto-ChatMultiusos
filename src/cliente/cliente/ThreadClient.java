package cliente.cliente;

public class ThreadClient implements Runnable {
	private String ip;
	private int puerto;
	private String usuario;

	public ThreadClient(String ip, int puerto, String usuario) {
		this.ip = ip;
		this.puerto = puerto;
		this.usuario = usuario;
	}

	@Override
	public void run() {
		// Aquí va la lógica que se ejecutará en el hilo
		System.out.println("Conectando a " + ip + ":" + puerto);
		System.out.println("Usuario: " + usuario);
		
		// Lógica de conexión y comunicación con el servidor
		// ...
	}

}
