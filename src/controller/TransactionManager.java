package controller;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import controller.database.getData;
import controller.database.insertData;
import controller.database.updateData;
import model.Datos;
import model.Feature;
import model.Personage;

public class TransactionManager {
	private Controller serverController;
	private HashMap<String, String> llaves;
	private HashMap<String, String> valores;
	private ArrayList<String> connectedUsers;
	private Datos datos;
	private static final boolean print = false;

	public TransactionManager(Controller serverController, Datos datos) {
		this.serverController = serverController;
		this.datos = datos;
		connectedUsers = new ArrayList<>();
	}

	public boolean startTransactionManager() {
		serverController.writeLog("Iniciando TransactionManager...");
		try {
			String myQ = " select count(*) cant from users";
			ArrayList<HashMap<String, String>> data = new getData(myQ, "Extrayendo Cantidad de Usuarios", print).exe();
			if (data != null) {
				serverController.writeLog("Conexi�n a base de datos satisfactoria...");
				serverController.writeLog("AdivinaQuien tiene " + data.get(0).get("cant") + " usuarios registrados...");
				datos.setAllPersonages(getPersonages());
				serverController.writeLog("AdivinaQuien tiene " + datos.getAllPersonages().size() + " personajes registrados...");
				return true;
			} else {
				serverController.writeLog("No hay conexi�n con la base de datos...");
				return false;
			}
		} catch (Exception e) {
			serverController.writeLog("Error Conexi�n a base de datos: " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}

	public String registerUser(String name, String user, String pass) {
		try {
			if (isValid(user))
				if (isValid(pass))
					if (isValid(name))
						if (!userExist(user)) {
							valores = new HashMap<>();
							valores.put("id_user", "default");
							valores.put("username", user);
							valores.put("fullname", name);
							valores.put("password", utils.encriptarClave(pass));
							BigDecimal idUser = new insertData("users", valores, "Insertando Nuevo Usuario", print).exe();
							if (idUser != null)
								return "ok@Usuario registrado con �xito!";
							else
								return "error@Error en la transacci�n.";
						} else
							return "error@El usuario ya existe";
					else
						return "error@El nombre est� vac�o";
				else
					return "error@La contrase�a est� vac�a";
			else
				return "error@El usuario est� vac�o";
		} catch (Exception e) {
			return "error@Error en la transacci�n: " + e.getLocalizedMessage();
		}
	}

	public String logInUser(String user, String pass) {
		if (isValid(user))
			if (isValid(pass)) {
				String myQ = " select id_user, fullname from users where username = '" + user + "' AND password = '" + utils.encriptarClave(pass) + "'";
				ArrayList<HashMap<String, String>> data = new getData(myQ, "Verificando Login", print).exe();
				if (data != null && data.size() > 0) {
					if (!connectedUsers.contains(user)) {
						llaves = new HashMap<>();
						valores = new HashMap<>();
						llaves.put("id_user", data.get(0).get("id_user"));
						valores.put("last_login", String.valueOf(System.currentTimeMillis()));
						if (new updateData("users", llaves, valores, "Actualizando Login", print).exe()) {
							connectedUsers.add(user);
							return "ok@Bienvenido a AdivinaQuien " + data.get(0).get("fullname");
						} else
							return "error@Imposible iniciar sesi�n, intente nuevamente";
					} else
						return "error@Ya hab�as iniciado sesi�n";
				} else
					return "error@Combinaci�n de usuario y clave erronea";
			} else
				return "error@Contrase�a vac�a.";
		else
			return "error@Usuario vac�o.";
	}

	public boolean userExist(String userName) {
		String myQ = " select id_user from users where username = '" + userName + "'";
		ArrayList<HashMap<String, String>> datos = new getData(myQ, "Obteniendo Lista de Usuarios con " + userName, print).exe();
		return datos != null && datos.size() > 0;
	}

	public void disconnectUser(String user) {
		if(user != null) {
			System.out.println("Removing :::> " + user);
			connectedUsers.remove(user);
		}
	}

	private boolean isValid(String field) {
		return field != null && !field.trim().isEmpty();
	}

	private ArrayList<Personage> getPersonages() {
		ArrayList<Personage> personages = new ArrayList<>();
		String myQ = " select id_personage, name from personage";
		ArrayList<HashMap<String, String>> pers = new getData(myQ, "Extrayendo Personajes", print).exe();
		try {
			if (pers != null && pers.size() > 0) {
				Personage p = null;
				for (HashMap<String, String> reg : pers) {
					p = new Personage(Integer.parseInt(reg.get("id_personage")), reg.get("name"));
					p.setImagen(Files.readAllBytes(new File("images/" + reg.get("id_personage") + ".jpg").toPath()));
					ArrayList<Feature> features = new ArrayList<>();
					myQ =	" select	f.id_feature, c.name category, f.name item " +
							" from		feature f, category c, personage_feature pf " +
							" where		f.id_category = c.id_category " +
							" and		pf.id_feature = f.id_feature " +
							" and		id_personage = " + reg.get("id_personage");
					ArrayList<HashMap<String, String>> feats = new getData(myQ, "Extrayendo Caracteristicas de " + reg.get("id_personage"), print).exe();
					for(HashMap<String, String> f : feats)
						features.add(new Feature(Integer.parseInt(f.get("id_feature")), f.get("category"), f.get("item")));
					p.setFeatures(features);
					personages.add(p);
				}
			}
		} catch (Exception e) {
			personages = null;
		}
		return personages;
	}
}