package controller;

import controller.Cliente;

public class Match {
	private Cliente playerOne;
	private Cliente playerTwo;
	private boolean started = false;
	private boolean finished = false;
	private boolean saludoReady = false;
	private boolean turnoReady = false;
	private long initTime;
	private long endTime;
	private int turnoActual = 0;

	public Match(Cliente playerOne, Cliente playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}

	public boolean isMatchReady() {
		return playerOne.isReady() && playerTwo.isReady();
	}

	public boolean isMatchAvailable() {
		return playerOne.isAvailable() && playerTwo.isAvailable();
	}

	public boolean selectedPersonages() {
		return playerOne.getPersonage() != null && playerTwo.getPersonage() != null;
	}

	public void startMatch() {
		try {
			playerOne.enviar("ready@ok@" + playerOne.getTurno());
			playerTwo.enviar("ready@ok@" + playerTwo.getTurno());
			setInitTime(System.currentTimeMillis());
			setStarted(true);
		} catch (Exception e) {
			setFinished(true);
		}
	}

	public void sendOpponents() {
		try {
			playerTwo.enviar("user@ok@Tu contrincante es " + playerOne.getUserName());
			playerOne.enviar("user@ok@Tu contrincante es " + playerTwo.getUserName());
			setSaludoReady(true);
		} catch (Exception e) {
			setFinished(true);
		}
	}

	public void sendTurnos() {
		try {
			turnoActual = (int) Math.random() * 10;
			playerOne.setTurno(turnoActual % 2);
			playerOne.setTurno((turnoActual + 1) % 2);
			playerOne.enviar("turn@ok@Por favor, selecciona tu personaje");
			playerTwo.enviar("turn@ok@Por favor, selecciona tu personaje");
			turnoActual = 0;
			setTurnosReady(true);
		} catch (Exception e) {
			setFinished(true);
		}
	}

	public void abortGame() {
		System.out.println("abortando juego...");
		try {
			if (!playerOne.isAvailable())
				playerOne.enviar("game@error@Se ha desconectado " + playerTwo.getUserName() + "\nEl juego va a cerrarse... Ganas la partida");
			else if (!playerTwo.isAvailable())
				playerOne.enviar("game@error@Se ha desconectado " + playerOne.getUserName() + "\nEl juego va a cerrarse... Ganas la partida");
		} catch (Exception e) {

		}
		setFinished(true);
	}

	public void processTurn() {
		try {
			String history = null;
			if (turnoActual == playerOne.getTurno()) {
				if (playerOne.getGuessedPersonage() != null) {
					if (playerOne.getGuessedPersonage().equals(playerTwo.getPersonage())) {
						history = "hist@ok@" + playerOne.getUserName() + " adivino el personaje!";
						playerOne.enviar("guess@ok@Adivinaste el Personaje de " + playerTwo.getUserName() + "!");
						playerTwo.enviar("guess@ok@" + playerOne.getUserName() + " adivinó tu personaje!");
						setFinished(true);
					} else {
						history = "hist@ok@" + playerOne.getUserName() + " falló al adivinar el personaje!";
						playerOne.enviar("guess@none@No, no es así");
						playerOne.setGuessedPersonage(null);
					}
				} else {
					if (playerOne.getQuestion() != null) {
						history = "hist@ok@" + playerOne.getUserName() + " preguntó: " + playerOne.getQuestion();
						playerTwo.enviar("q@ok@" + playerOne.getQuestion());
						playerOne.setQuestion(null);
					}
					if (playerTwo.getAnswer() != null) {
						history = "hist@ok@" + playerTwo.getUserName() + " respondió: " + (playerTwo.getAnswer().equals("0") ? "Eso es correcto!" : "No, no es así");
						playerOne.enviar("a@ok@" + (playerTwo.getAnswer().equals("0") ? "Eso es correcto!" : "No, no es así"));
						playerTwo.setAnswer(null);
						turnoActual = (turnoActual + 1) % 2;
					}
				}
			} else if (turnoActual == playerTwo.getTurno()) {
				if (playerTwo.getGuessedPersonage() != null) {
					if (playerTwo.getGuessedPersonage().equals(playerTwo.getPersonage())) {
						history = "hist@ok@" + playerTwo.getUserName() + " adivino el personaje!";
						playerTwo.enviar("guess@win@Adivinaste el Personaje de " + playerOne.getUserName() + "!");
						playerOne.enviar("guess@lose@" + playerTwo.getUserName() + " adivinó tu personaje!");
						setFinished(true);
					} else {
						history = "hist@ok@" + playerTwo.getUserName() + " falló al adivinar el personaje!";
						playerTwo.enviar("guess@none@No, no es así");
						playerTwo.setGuessedPersonage(null);
					}
				} else {
					if (playerTwo.getQuestion() != null) {
						history = "hist@ok@" + playerTwo.getUserName() + " preguntó: " + playerTwo.getQuestion();
						playerOne.enviar("q@ok@" + playerTwo.getQuestion());
						playerTwo.setQuestion(null);
					}
					if (playerOne.getAnswer() != null) {
						history = "hist@ok@" + playerOne.getUserName() + " respondió: " + (playerOne.getAnswer().equals("0") ? "Eso es correcto!" : "No, no es así");
						playerTwo.enviar("a@ok@" + (playerOne.getAnswer().equals("0") ? "Eso es correcto!" : "No, no es así"));
						playerOne.setAnswer(null);
						turnoActual = (turnoActual + 1) % 2;
					}
				}
			}
			if (history != null) {
				playerOne.enviar(history);
				playerTwo.enviar(history);
			}
		} catch (Exception e) {
			setFinished(true);
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		setEndTime(System.currentTimeMillis());
		this.finished = finished;
	}

	public boolean isSaludoReady() {
		return saludoReady;
	}

	public void setSaludoReady(boolean saludoReady) {
		this.saludoReady = saludoReady;
	}

	public boolean isTurnosReady() {
		return turnoReady;
	}

	public void setTurnosReady(boolean turnoReady) {
		this.turnoReady = turnoReady;
	}

	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}