package controller;

import controller.Cliente;

public class Match {
	private Cliente playerOne;
	private Cliente playerTwo;
	private boolean started = false;
	private boolean finished = false;
	private boolean saludoReady = false;

	public Match(Cliente playerOne, Cliente playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}

	public boolean isMatchReady() {
		return playerOne.isReady() && playerTwo.isReady();
	}

	public void startMatch() {
		try {
			playerOne.enviar("ready");
			playerTwo.enviar("ready");
			setStarted(true);
		} catch (Exception e) {
			setFinished(true);
		}
	}

	public void sendOpponents() {
		try {
			playerTwo.enviar("user:" + playerOne.getUserName());
			playerOne.enviar("user:" + playerTwo.getUserName());
			setSaludoReady(true);
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
		this.finished = finished;
	}

	public boolean isSaludoReady() {
		return saludoReady;
	}

	public void setSaludoReady(boolean saludoReady) {
		this.saludoReady = saludoReady;
	}
}