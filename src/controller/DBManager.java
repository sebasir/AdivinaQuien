package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import model.Feature;
import model.Personage;

public class DBManager {
	protected BufferedReader lector;
	protected BufferedWriter escritor;
	protected File Features;
	protected File Personage;
	protected File Users;
	protected File userLogin;
	protected File Feature_Personaje;
	private ArrayList<Personage> personages;
	private ArrayList<Feature> features;
	private boolean conected;

	public boolean conectar() {
		conected = false;
		try {
			Users = new File("Users.dat");
			Features = new File("Features.dat");
			return Users.canRead();
		} catch (Exception ex) {
			System.out.println("Singleton.conectar " + ex.getMessage());
			return false;
		}
	}

	public String logUser(String username) throws IOException {
		escritor = new BufferedWriter(new FileWriter(new File("userLogs.dat")));
		escritor.write(username + ":");
		if (!conected) {
			conected = true;
			return "Waiting";
		}
		escritor.write(username + "\n");
		return "Choose";
	}

	public boolean searchUser(String username) throws IOException {
		lector = new BufferedReader(new FileReader(new File("Users.dat")));
		String line = "";
		StringTokenizer myTok;
		while ((line = lector.readLine()) != null) {
			myTok = new StringTokenizer(line, ":");
			myTok.nextToken();
			if (myTok.nextToken().equals(username))
				return true;
			myTok.nextToken();
		}
		Users = null;
		return false;
	}

	public ArrayList<Feature> getFeaturesPersonage(Personage personaje) throws FileNotFoundException, IOException {
		lector = new BufferedReader(new FileReader(new File("Feature_Personajes.dat")));
		String line = "";
		StringTokenizer myTok;
		StringTokenizer myToken = null;
		ArrayList<Feature> featuresPersonage = new ArrayList<Feature>();
		while ((line = lector.readLine()) != null) {
			myTok = new StringTokenizer(line, ":");
			if (myTok.nextToken().equals(personaje.getIndex())) {
				myToken = new StringTokenizer(myTok.nextToken(), ",");
				while (myToken.hasMoreTokens())
					for (Feature f : features)
						if (Integer.parseInt(myToken.nextToken()) == f.getIndex())
							featuresPersonage.add(f);

			}
		}
		return featuresPersonage;
	}

	public void loadFeatures() throws FileNotFoundException, IOException {
		lector = new BufferedReader(new FileReader(new File("Features.dat")));
		String line = "";
		StringTokenizer myTok;
		features = new ArrayList<Feature>();
		while ((line = lector.readLine()) != null) {
			myTok = new StringTokenizer(line, ":");
			features.add(new Feature(myTok.nextToken()));
		}
	}

	public void loadPersonages() throws FileNotFoundException, IOException {
		lector = new BufferedReader(new FileReader(new File("Personage.dat")));
		personages = new ArrayList<Personage>();
		String line = "";
		while ((line = lector.readLine()) != null) {
			Personage nuevoPersonage = new Personage(0, line);
			nuevoPersonage.setFeatures(getFeaturesPersonage(nuevoPersonage));
			personages.add(nuevoPersonage);
		}
	}

	public ArrayList<Personage> getPersonages() {
		return personages;
	}

	public ArrayList<Feature> getFeatures() {
		return features;
	}
}