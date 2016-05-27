package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import model.Datos;
import model.Personage;
import view.ServerView;

public class Controller extends WindowAdapter implements Runnable {
	private ServerSocket socket;
	private Datos datos;
	private ServerView vista;
	private TransactionManager transManager;
	private MatchController matchController;

	public Controller(Datos data, ServerView vista) {
		this.datos = data;
		this.vista = vista;
		transManager = new TransactionManager(this, data);
		if (!transManager.startTransactionManager())
			writeLog("Error iniciando Server...");
		else if (!checkWaitImage()) {
			writeLog("Error con las imágenes...");
		} else {
			writeLog("Iniciando controlador de Partidas...");
			matchController = new MatchController(this);
			matchController.start();
			new Thread(this).start();
		}
	}

	private boolean checkWaitImage() {
		return new File("images/wait.jpg").exists();
	}

	public synchronized void writeLog(String message) {
		vista.appendText(message);
	}

	public synchronized TransactionManager getTransManager() {
		return transManager;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			vista.appendText("Cerrando conexiones...");
			if (datos.isListening())
				socket.close();
			vista.dispose();
		} catch (Exception ex) {

		}
		System.exit(0);
	}

	@Override
	public void run() {
		vista.appendText("Escuchando por el puerto: " + Datos.serverPort);
		try {
			socket = new ServerSocket(Datos.serverPort, 50);
			while (true)
				matchController.addClient(new Cliente(socket.accept(), this));
		} catch (IOException e) {
			System.out.println("Controller:lanzarServidor => Error " + e.getLocalizedMessage());
			System.exit(0);
		}
	}

	public ArrayList<Personage> generarTablero() {
		ArrayList<Personage> tablero = new ArrayList<Personage>();
		while (tablero.size() != 15) {
			int i = (int) Math.round(Math.random() * 34);
			if (!tablero.contains(datos.getAllPersonages().get(i)))
				tablero.add(datos.getAllPersonages().get(i));
		}
		return tablero;
	}
}