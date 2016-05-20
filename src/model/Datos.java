package model;

public class Datos {
	public static final int serverPort = 12345;
	private boolean listening;

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}
}
