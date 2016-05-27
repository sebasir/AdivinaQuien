package controller;

import java.util.ArrayList;

import model.Personage;

public class MatchController extends Thread {
	private static ArrayList<Match> matches;
	private Cliente waitingPlayerOne;
	private Controller controller;
	private ArrayList<Personage> tablero;

	public MatchController(Controller controller) {
		matches = new ArrayList<>();
		this.controller = controller;
	}

	public synchronized void addClient(Cliente player) {
		if (waitingPlayerOne == null) {
			tablero = controller.generarTablero();
			waitingPlayerOne = player;
			waitingPlayerOne.setTablero(tablero);
			waitingPlayerOne.start();
		} else if (!waitingPlayerOne.isAvailable()) {
			waitingPlayerOne = player;
			waitingPlayerOne.setTablero(tablero);
			waitingPlayerOne.start();
		} else if (waitingPlayerOne.isReady()) {
			player.setTablero(tablero);
			player.start();
			controller.writeLog("Creando partida...");
			matches.add(new Match(waitingPlayerOne, player));
			waitingPlayerOne = null;
			tablero = null;
		}
	}

	public void run() {
		while (true) {
			synchronized (matches) {
				for (Match m : matches) {
					if (!m.isFinished()) {
						if (m.isMatchAvailable()) {
							if (!m.isStarted()) {
								if (m.isMatchReady()) {
									if (!m.isSaludoReady()) {
										m.sendOpponents();
									} else {
										if (!m.isTurnosReady()) {
											m.sendTurnos();
										} else {
											if (m.selectedPersonages())
												m.startMatch();
										}
									}
								}
							} else {
								m.processTurn();
							}
						} else if (m.isStarted()) {
							m.abortGame();
						}
					}
				}
			}
		}
	}
}
