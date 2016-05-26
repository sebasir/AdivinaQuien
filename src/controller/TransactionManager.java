package controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import controller.database.getData;
import controller.database.insertData;
import controller.database.updateData;

public class TransactionManager {
	private HashMap<String, String> llaves;
	private HashMap<String, String> valores;
	private static final boolean print = false;

	public String registerUser(String user, String name, String pass) {
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
							BigDecimal idUser = new insertData("user", valores, "Insertando Nuevo Usuario", print).exe();
							if (idUser != null)
								return "Usuario registrado con éxito!";
							else
								return "Error en la transacción.";
						} else
							return "Error. El usuario ya existe";
					else
						return "Error. El nombre está vacío";
				else
					return "Error. La contraseña está vacía";
			else
				return "Error. El usuario está vacío";
		} catch (Exception e) {
			return "Error en la transacción: " + e.getLocalizedMessage();
		}
	}

	public String logInUser(String user, String pass) {
		if (isValid(user))
			if (isValid(pass)) {
				String myQ = " select id_user, fullname from users where username = '" + user + "' AND password = '" + utils.encriptarClave(pass) + "'";
				ArrayList<HashMap<String, String>> datos = new getData(myQ, "Verificando Login", print).exe();
				if (datos != null && datos.size() > 0) {
					llaves = new HashMap<>();
					llaves.put("id_user", datos.get(0).get("id_user"));
					valores.put("last_login", String.valueOf(System.currentTimeMillis()));
					if (new updateData("users", llaves, valores, "Actualizando Login", print).exe())
						return "Bienvenido a AdivinaQuien " + datos.get(0).get("fullname");
					return "Imposible iniciar sesión, intente nuevamente";
				} else
					return "Error. Combinación de usuario y clave erronea";
			} else
				return "Error. Contraseña vacía.";
		else
			return "Error. Usuario vacío.";
	}

	public boolean userExist(String userName) {
		String myQ = " select id_user from users where username = '" + userName + "'";
		ArrayList<HashMap<String, String>> datos = new getData(myQ, "Obteniendo Lista de Usuarios con " + userName, print).exe();
		return datos != null && datos.size() > 0;
	}

	private boolean isValid(String field) {
		return field != null && !field.trim().isEmpty();
	}
}
