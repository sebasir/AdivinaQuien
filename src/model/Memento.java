package model;

import java.util.ArrayList;

public class Memento {
	private ArrayList<Personage> state;

	public Memento(ArrayList<Personage> state) {
		for (Personage p : state)
			state.add(p.clonar());
	}

	public ArrayList<Personage> getState() {
		return state;
	}
}
