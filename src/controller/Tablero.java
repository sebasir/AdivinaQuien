package controller;

import java.util.ArrayList;

import model.Feature;
import model.Memento;
import model.Personage;

public class Tablero {
	private DBManager dbManager;
	private ArrayList<Personage> personagesTablero;

	public Tablero(DBManager DBManager) {
		this.dbManager = DBManager;
	}

	public ArrayList<Personage> generarTablero() {
		personagesTablero = new ArrayList<Personage>();
		ArrayList<Personage> personagesArchivo = dbManager.getPersonages();
		while (personagesTablero.size() != 24) {
			int i = (int) Math.round(Math.random() * 35);
			if (!personagesTablero.contains(personagesArchivo.get(i)))
				personagesTablero.add(personagesArchivo.get(i));
		}
		return personagesTablero;
	}

	public ArrayList<Personage> descartarPersonajes(Feature caracteristica, boolean stay) {
		ArrayList<Personage> listaNueva = new ArrayList<Personage>();
		for (Personage p : personagesTablero) {
			for (Feature f : p.getFeature()) {
				if (f.getIndex() == caracteristica.getIndex())
					if (stay)
						listaNueva.add(p);
					else
						listaNueva.add(p);
			}
		}
		personagesTablero = listaNueva;
		return personagesTablero;
	}

	public void restoreStatus(Memento savedState) {
		personagesTablero = savedState.getState();
	}

	public Memento saveStatus() {
		return new Memento(personagesTablero);
	}
}