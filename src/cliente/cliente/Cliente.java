package cliente.cliente;

import java.io.IOException;
import java.net.Socket;

public class Cliente {
	public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Socket cli;
        
        cli = new Socket("127.0.0.1", 5000);
        //ObjectOutputStream salida = new ObjectOutputStream(cli.getOutputStream());
        //ObjectInputStream entrada =null; 
            cli.close();            
               
    }
}
