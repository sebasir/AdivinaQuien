package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import model.Datos;
import view.ServerView;

public class Controller extends WindowAdapter implements Runnable {
	private ArrayList<Match> matches;
	private ServerSocket socket;
	private Cliente jugadores[];
	private Datos datos;
	private ServerView vista;

	public Controller(Datos data, ServerView vista) {
		this.datos = data;
		this.vista = vista;
		new Thread(this).start();
		
	}

	private synchronized void createMatch() {
		matches.add(new Match(jugadores[0], jugadores[1]));
	}
	
	public void writeLog(String message) {
		vista.appendText(message);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		vista.appendText("Cerrando conexiones...");
		if (datos.isListening())
			System.out.println();
		vista.dispose();
		System.exit(0);
	}

	@Override
	public void run() {
		matches = new ArrayList<>();
		vista.appendText("Escuchando por el puerto: " + Datos.serverPort);
		try {
			jugadores = new Cliente[2];
			socket = new ServerSocket(Datos.serverPort, 50);
			while (true) {
				jugadores[0] = new Cliente(socket.accept(), this);
				jugadores[0].start();
				jugadores[1] = new Cliente(socket.accept(), this);
				jugadores[1].start();
				createMatch();
			}
		} catch (IOException e) {
			System.out.println("Controller:lanzarServidor => Error " + e.getLocalizedMessage());
		}
	}
}