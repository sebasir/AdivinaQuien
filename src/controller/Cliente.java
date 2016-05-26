package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente extends Thread {
	private Socket clientSocket;
	private Controller serverController;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private String userName;
	private boolean ready;

	public Cliente(Socket c, Controller s) {
		clientSocket = c;
		serverController = s;
		try {
			inStream = new DataInputStream(clientSocket.getInputStream());
			outStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			serverController.writeLog("Se desconecto:" + getUserName());
		}
	}

	public void run() {
		serverController.writeLog("Un cliente se ha conectado\n");
		String[] credentials;
		while (true) {
			try {
				String cadena = utils.decryptURL(inStream.readUTF());
				serverController.writeLog("El cliente ha enviado: \n::::>" + cadena + '\n');
				if (cadena.startsWith("user:")) {
					setUserName(cadena.substring(5));
				} else if (cadena.startsWith("ready")) {
					setReady(true);
				} else if (cadena.startsWith("reg:")) {
					cadena = cadena.substring(4);
					credentials = cadena.split("|");
					serverController.getTransManager().registerUser(credentials[0], credentials[1], credentials[2]);
				} else if (cadena.startsWith("log:")) {
					cadena = cadena.substring(4);
					credentials = cadena.split("|");
					serverController.getTransManager().logInUser(credentials[0], credentials[1]);
				}
			} catch (IOException e) {
				serverController.writeLog("Server: Se desconecto " + getUserName());
				break;
			}
		}
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enviar(String cadena) throws IOException {
		outStream.writeUTF(cadena);
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}