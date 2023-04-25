package Controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Master.MVC;
import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import utils.Config;

public class Controller implements Service {

	private MVC hub;

	public Controller(MVC mvc) {
		this.hub = mvc;
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller started.");

		try {
			// Create a socket to connect to the server on port 1234
			Socket socket = new Socket("localhost", Config.SERVER_PORT);
			System.out.println("[CONTROLLER] Connected to server.");

			// Get the input and output streams for the socket
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			Request request = new Request(RequestCode.TEST, this);
			outputStream.writeObject(request);

			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			Response response = (Response) objectInputStream.readObject();

			System.out.println("[CONTROLLER] Server response: " + response);

			// Close the socket
			socket.close();
			System.out.println("[CONTROLLER] Connection closed.");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendRequest'");
	}

	@Override
	public void sendResponse() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendResponse'");
	}

}
