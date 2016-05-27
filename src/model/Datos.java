package model;

import java.util.ArrayList;

public class Datos {
	public static final int serverPort = 12345;
	private boolean listening;
	private ArrayList<Personage> allPersonages;

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public ArrayList<Personage> getAllPersonages() {
		return allPersonages;
	}

	public void setAllPersonages(ArrayList<Personage> allPersonages) {
		this.allPersonages = allPersonages;
	}
}
