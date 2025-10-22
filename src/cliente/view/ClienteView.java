package cliente.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cliente.cliente.ThreadClient;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JEditorPane;
import javax.swing.JCheckBox;

public class ClienteView extends JFrame implements ActionListener{
	private final int PUERTO = 5000;
	private final String IP = "127.0.0.1";

	private Socket socket;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private Thread hiloLectura;
	private boolean conectado = false;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ip;
	private JTextField puerto;
	private JTextField usuario;
	private JTextField paraUno;
	private JTextField paraDos;
	private ThreadClient threadClient;
	private JButton conectarBoton;
	private JButton desconectarBoton;
	private JLabel ipText;
	private JLabel puertoText;
	private JLabel usuarioText;
	private JLabel textoInvisible;
	private JEditorPane editorPane;
	private JCheckBox privadoBoton;
	private JLabel paraText;
	private JButton enviarBoton;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteView frame = new ClienteView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClienteView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 554, 376);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		ipText = new JLabel("IP:");
		ipText.setFont(new Font("Tahoma", Font.BOLD, 13));
		ipText.setBounds(10, 10, 44, 12);
		contentPane.add(ipText);

		puertoText = new JLabel("Puerto:");
		puertoText.setFont(new Font("Tahoma", Font.BOLD, 13));
		puertoText.setBounds(88, 10, 60, 12);
		contentPane.add(puertoText);

		usuarioText = new JLabel("Usuario:");
		usuarioText.setFont(new Font("Tahoma", Font.BOLD, 13));
		usuarioText.setBounds(200, 10, 60, 12);
		contentPane.add(usuarioText);

		conectarBoton = new JButton("Conectar");
		conectarBoton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		conectarBoton.setBounds(210, 32, 113, 20);
		contentPane.add(conectarBoton);

		desconectarBoton = new JButton("Desconectar");
		desconectarBoton.setBounds(333, 32, 122, 20);
		contentPane.add(desconectarBoton);

		ip = new JTextField();
		ip.setBounds(34, 8, 50, 18);
		contentPane.add(ip);
		ip.setColumns(10);

		puerto = new JTextField();
		puerto.setColumns(10);
		puerto.setBounds(140, 8, 50, 18);
		contentPane.add(puerto);

		textoInvisible = new JLabel("No conectado");
		textoInvisible.setFont(new Font("Tahoma", Font.BOLD, 13));
		textoInvisible.setBounds(437, 10, 105, 12);
		contentPane.add(textoInvisible);

		usuario = new JTextField();
		usuario.setBounds(270, 8, 157, 18);
		contentPane.add(usuario);
		usuario.setColumns(10);

		editorPane = new JEditorPane();
		editorPane.setBounds(0, 84, 554, 217);
		contentPane.add(editorPane);

		privadoBoton = new JCheckBox("Privado");
		privadoBoton.setFont(new Font("Tahoma", Font.BOLD, 13));
		privadoBoton.setBounds(10, 313, 75, 20);
		contentPane.add(privadoBoton);

		paraText = new JLabel("Para:");
		paraText.setFont(new Font("Tahoma", Font.BOLD, 13));
		paraText.setBounds(88, 318, 44, 12);
		contentPane.add(paraText);

		paraUno = new JTextField();
		paraUno.setBounds(130, 315, 60, 18);
		contentPane.add(paraUno);
		paraUno.setColumns(10);

		paraDos = new JTextField();
		paraDos.setBounds(199, 315, 240, 18);
		contentPane.add(paraDos);
		paraDos.setColumns(10);

		enviarBoton = new JButton("Enviar");
		enviarBoton.setBounds(449, 314, 84, 20);
		contentPane.add(enviarBoton);

		conectarBoton.addActionListener(this);
		desconectarBoton.addActionListener(this);
		enviarBoton.addActionListener(this);
		privadoBoton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		/*Cuando los labels ip, puerto y usuario estén vacíos, los botones conectar, desconectar y enviar estarán deshabilitados
		 * Cuando labels ip, puerto y usuario estén llenos, se habilitará el botón conectar. 
		 * Hasta que no le des al botón conectar, el label texto invisible saldrá como no conectado
		 * El botón de enviar será habilitado cuando esté lleno el label paraDos.
		 * Aparecerá toda la información del usurio en el editor pane
		 * Cuando el botón privado no esté seleccionado, el mensaje se enviará a todos los usuarios conectados, inhabilitando el label paraUno
		 * Cuando el botón privado esté seleccionado, se habilitará el label paraUno, y el mensaje será enviado a solo ese user. 
		 */
		
		if (e.getSource() == conectarBoton) {
			conectar();
		} else if (e.getSource() == desconectarBoton) {
			desconectar();
		} else if (e.getSource() == enviarBoton) {
			enviarMensaje();
		} else if (e.getSource() == privadoBoton) {
			paraUno.setEnabled(privadoBoton.isSelected());
		}

	}


	/*Permitir configurar IP y puerto del servidor.
	 Mostrar siempre el nombre de usuario.
	 Conectar y desconectar el cliente del servidor.
	 Enviar mensajes públicos y privados.
	 Mostrar los mensajes recibidos indicando claramente el emisor y si son públicos o privados.
	 Mantener la recepción de mensajes en un hilo independiente al envío, de forma que la interfaz
	nunca se bloquee.
	 Actualizar la interfaz de forma segura (usando las herramientas adecuadas de Swing)*/

	private void conectar() {
	    String ipClient = ip.getText();
	    String puertoClient = puerto.getText(); 
	    String usuarioClient = usuario.getText();

	    if (ipClient.isEmpty() || puertoClient.isEmpty() || usuarioClient.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Debe ingresar IP, puerto y nombre de usuario");
	    } else {
	        try {
	            // Convertimos el String a int solo en el momento de crear el socket
	            int puertoInt = Integer.parseInt(puertoClient);
	            socket = new Socket(ipClient, puertoInt); // aquí usamos el int
	            salida = new ObjectOutputStream(socket.getOutputStream());
	            entrada = new ObjectInputStream(socket.getInputStream());

	            // Enviar nombre de usuario al servidor
	            salida.writeObject(usuarioClient);

	            conectado = true;
	            textoInvisible.setText("Conectado");
	            actualizarEstadoBotones(true);

	            // Mostrar usuario en el editorPane
	            SwingUtilities.invokeLater(() -> {
	                editorPane.setText("Usuario conectado: " + usuarioClient + "\n");
	            });

	            // Iniciar hilo para escuchar mensajes del servidor
	            hiloLectura = new Thread(this::escucharMensajes);
	            hiloLectura.start();

	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(this, ex.getMessage());
	        }
	    }
	}

    private void desconectar() {
        try {
            if (conectado && salida != null) {
                salida.writeObject("/salir");
            }
            conectado = false;
            if (socket != null) socket.close();
            textoInvisible.setText("Desconectado");
            actualizarEstadoBotones(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al desconectar: " + ex.getMessage());
        }
    }

    private void enviarMensaje() {
        if (!conectado) return;
        try {
            String mensaje = paraDos.getText();
            if (privadoBoton.isSelected()) {
                String destinatario = paraUno.getText();
                if (!destinatario.isEmpty()) {
                    salida.writeObject("/privado " + destinatario + " " + mensaje);
                    agregarMensaje("Yo (privado a " + destinatario + "): " + mensaje);
                }
            } else {
                salida.writeObject("/publico " + mensaje);
                agregarMensaje("Yo (público): " + mensaje);
            }
            paraDos.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al enviar mensaje: " + ex.getMessage());
        }
    }

    private void escucharMensajes() {
        try {
            while (conectado) {
                Object recibido = entrada.readObject();
                if (recibido instanceof String) {
                    String mensaje = (String) recibido;
                    agregarMensaje(mensaje);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (conectado) {
                JOptionPane.showMessageDialog(this, "Conexión perdida con el servidor.");
                desconectar();
            }
        }
    }

    private void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            editorPane.setText(editorPane.getText() + mensaje + "\n");
        });
    }

    private void actualizarEstadoBotones(boolean conectado) {
        conectarBoton.setEnabled(!conectado);
        desconectarBoton.setEnabled(conectado);
        enviarBoton.setEnabled(conectado && !paraDos.getText().isEmpty());
        ip.setEnabled(!conectado);
        puerto.setEnabled(!conectado);
        usuario.setEnabled(!conectado);
    }
}
