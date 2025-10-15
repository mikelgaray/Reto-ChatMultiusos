package cliente.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cliente.cliente.ThreadClient;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JCheckBox;

public class ClienteView extends JFrame implements ActionListener{

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
		conectarBoton.setBounds(252, 32, 89, 20);
		contentPane.add(conectarBoton);
		
		desconectarBoton = new JButton("Desconectar");
		desconectarBoton.setBounds(357, 32, 89, 20);
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
		editorPane.setBounds(-24, 62, 578, 239);
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
		threadClient = new ThreadClient(ip.getText(), Integer.parseInt(puerto.getText()), usuario.getText(), paraUno.getText(), paraDos.getText(), false);
		
		/*Cuando los labels ip, puerto y usuario estén vacíos, los botones conectar, desconectar y enviar estarán deshabilitados
		 * Cuando labels ip, puerto y usuario estén llenos, se habilitará el botón conectar. 
		 * Hasta que no le des al botón conectar, el label texto invisible saldrá como no conectado
		 * El botón de enviar será habilitado cuando esté lleno el label paraDos.
		 * Aparecerá toda la información del usurio en el editor pane
		 * Cuando el botón privado no esté seleccionado, el mensaje se enviará a todos los usuarios conectados, inhabilitando el label paraUno
		 * Cuando el botón privado esté seleccionado, se habilitará el label paraUno, y el mensaje será enviado a solo ese user. 
		*/
		if (ip.getText().isEmpty() || puerto.getText().isEmpty() || usuario.getText().isEmpty()) {
			conectarBoton.setEnabled(false);
			desconectarBoton.setEnabled(false);
			enviarBoton.setEnabled(false);
		} else {
			conectarBoton.setEnabled(true);
			if (e.getSource() == conectarBoton) {
				textoInvisible.setText("Conectado");
				desconectarBoton.setEnabled(true);
				new Thread(threadClient).start();
				// mostrar en editorPane la info del usuario
				if (paraDos.getText().isEmpty()) {
					enviarBoton.setEnabled(false);
				} else {
					enviarBoton.setEnabled(true);
					/*si el privadoBoton está seleccionado, el mensaje que se introduzca en el label paraDos 
					se enviará solo al usuario que esté en el label paraUno. Else, se enviará a todos los usuarios conectados.					
					*/
				}
				
			}
			
		}
	}
}
