package view;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ServerView extends JFrame {
	private static final long serialVersionUID = 1L;
	private Container contenedor;
	private JTextArea areaConsole;
	private JScrollPane scrollConsole;

	public ServerView() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {

		}

		initComponents();
		contenedor = getContentPane();
		contenedor.setLayout(new GridLayout(1, 1));
		setSize(750, 510);
		setTitle("Sistemas Distribuidos: Adivina Quién");
		setLocationRelativeTo(null);
		setResizable(false);
		areaConsole.setEditable(false);
		areaConsole.setFont(new Font("Consolas", Font.PLAIN, 12));
		scrollConsole.setViewportView(areaConsole);
		contenedor.add(scrollConsole);
		reiniciar();
	}

	private void initComponents() {
		areaConsole = new JTextArea();
		scrollConsole = new JScrollPane();
	}

	public void reiniciar() {
		areaConsole.setText("");
	}
	
	public void appendText(String text) {
		areaConsole.append(text + "\n");
	}
	
	public void setWindowAdapter(WindowAdapter control) {
		addWindowListener(control);
	}
}
