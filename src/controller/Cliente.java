package controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import model.Personage;

public class Cliente extends Thread {
	private Socket clientSocket;
	private Controller serverController;
	private DataInputStream inStream;
	private ObjectOutputStream outStream;
	private ArrayList<Personage> tablero;
	private String userName;
	private boolean ready;
	private boolean available;

	public Cliente(Socket clientSocket, Controller serverController) {
		this.clientSocket = clientSocket;
		this.serverController = serverController;
		try {
			inStream = new DataInputStream(this.clientSocket.getInputStream());
			outStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
		} catch (IOException e) {
			serverController.writeLog("Se desconecto:" + getUserName());
		}
	}

	public void run() {
		serverController.writeLog("Un cliente se ha conectado\n");
		String[] clientMessage;
		String encrypMess;
		String message;
		String response = "";
		available = true;
		while (true) {
			try {
				encrypMess = inStream.readUTF();
				message = utils.decryptURL(encrypMess);
				response = "";
				if (message == null) {
					serverController.writeLog("Parece que alguien intentó enviar algo no válido: " + encrypMess);
					message = "NO_VALID_MESSAGE@NO_VALID_MESSAGE@NO_VALID_MESSAGE";
					response = "others@error@No conozco ese comando";
				}
				clientMessage = message.split("@");
				System.out.println(Arrays.toString(clientMessage));
				serverController.writeLog("El cliente ha enviado: \n::::>" + message + '\n');
				if (clientMessage[0].equals("ready")) {
					enviarTablero();
					setReady(true);
				} else if (clientMessage[0].equals("reg")) {
					response = "reg@" + serverController.getTransManager().registerUser(clientMessage[1], clientMessage[2], clientMessage[3]);
				} else if (clientMessage[0].equals("log")) {
					response = "log@" + serverController.getTransManager().logInUser(clientMessage[1], clientMessage[2]);
					if (response.contains("ok")) {
						enviarWait();
						setUserName(clientMessage[1]);
					}
				} else if (clientMessage[0].equals("dis")) {
					serverController.getTransManager().disconnectUser(getUserName());
					setAvailable(false);
					break;
				}
				if (!response.isEmpty())
					enviar(response);
			} catch (Exception e) {
				e.printStackTrace();
				serverController.writeLog("Server: Se desconecto " + getUserName());
				serverController.getTransManager().disconnectUser(getUserName());
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
		outStream.writeObject(utils.encryptURL(cadena));
	}

	public void enviarTablero() throws IOException {
		for (Personage p : tablero)
			outStream.writeObject(p);
	}

	public void enviarWait() throws IOException {
		byte[] waitImage = Files.readAllBytes(new File("images/wait.jpg").toPath());
		outStream.writeObject(waitImage);
		outStream.reset();
	}

	public boolean isReady() {
		return ready;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
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

	public ArrayList<Personage> getTablero() {
		return tablero;
	}

	public void setTablero(ArrayList<Personage> tablero) {
		this.tablero = tablero;
	}
}