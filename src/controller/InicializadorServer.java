package controller;

import model.Datos;
import view.ServerView;

public class InicializadorServer {
	private static ServerView vista;
	private static Datos datos;
	private static Controller control;

	public static void main(String[] args) {
		vista = new ServerView();
		vista.setVisible(true);
		datos = new Datos();		
		control = new Controller(datos, vista);
		vista.setWindowAdapter(control);
	}
}
