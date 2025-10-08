package cliente.cliente;

public class ThreadClient implements Runnable {
	private String ip;
	private int puerto;
	private String usuario;
	private String paraUno;
	private String paraDos;
	private boolean encriptar;

	public ThreadClient(String ip, int puerto, String usuario, String paraUno, String paraDos, boolean encriptar) {
		this.ip = ip;
		this.puerto = puerto;
		this.usuario = usuario;
		this.paraUno = paraUno;
		this.paraDos = paraDos;
		this.encriptar = encriptar;
	}

	@Override
	public void run() {
		// Aquí va la lógica que se ejecutará en el hilo
		System.out.println("Conectando a " + ip + ":" + puerto);
		System.out.println("Usuario: " + usuario);
		System.out.println("Para Uno: " + paraUno);
		System.out.println("Para Dos: " + paraDos);
		System.out.println("Encriptar: " + encriptar);
		
		// Lógica de conexión y comunicación con el servidor
		// ...
	}

}
