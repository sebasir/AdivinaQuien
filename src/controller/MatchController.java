package controller;

import java.util.ArrayList;

import model.Personage;

public class MatchController extends Thread {
	private static ArrayList<Match> matches;
	private Cliente waitingPlayer;
	private Controller controller;
	private ArrayList<Personage> tablero;

	public MatchController(Controller controller) {
		matches = new ArrayList<>();
		this.controller = controller;
	}

	public synchronized void addClient(Cliente player) {
		if (waitingPlayer == null) {
			tablero = controller.generarTablero();
			waitingPlayer = player;
			waitingPlayer.setTablero(tablero);
			waitingPlayer.start();
		} else if (!waitingPlayer.isAvailable()) {
			waitingPlayer = player;
			waitingPlayer.setTablero(tablero);
			waitingPlayer.start();
		} else {
			player.setTablero(tablero);
			controller.writeLog("Creando partida...");
			matches.add(new Match(waitingPlayer, player));
			waitingPlayer = null;
			tablero = null;
		}
	}

	public void run() {
		while (true) {
			synchronized (matches) {
				for (Match m : matches) {
					if (!m.isStarted()) {
						if (m.isMatchReady()) {
							m.sendOpponents();
							m.startMatch();
						}
					}
				}
			}
		}
	}
}
