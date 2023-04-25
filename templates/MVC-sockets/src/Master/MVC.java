package Master;

import Model.Model;
import Services.Server;
import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import View.View;
import utils.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import Controller.Controller;

public class MVC implements Server {

	private Model model;
	private View view;
	private Controller controller;

	public MVC() {
		this.model = new Model(this);
		this.view = new View(this);
		this.controller = new Controller(this);
	}

	public MVC(Model model, View view, Controller controller) {
		this.model = model;
		this.view = view;
		this.controller = controller;
	}

	public MVC(String configPath) {
		this.model = new Model(this);
		this.controller = new Controller(this);
		this.view = new View(this, configPath);
	}

	@Override
	public void start() {
		Thread.startVirtualThread(() -> this.model.start());
		Thread.startVirtualThread(() -> this.controller.start());
		SwingUtilities.invokeLater(() -> this.view.start());
		this.initServer();
	}

	private void initServer() {
		ServerSocket serverSocket = null;
		try {
			// Create a server socket on port 1234
			serverSocket = new ServerSocket(Config.SERVER_PORT);
			this.logMessage("Server started.");
			int i = 0;
			while (true) {
				if (i == 10) {
					break;
				}
				// Wait for a client connection
				Socket clientSocket = serverSocket.accept();
				this.logMessage("Client connected.");

				// Get the input and output streams for the client socket
				ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

				Request req = (Request) inputStream.readObject();
				this.logMessage("Received from client: " + req.toString());

				ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				Response res = new Response();
				outputStream.writeObject(res);

				// Close the client socket
				clientSocket.close();
				this.logMessage("Client disconnected.");
				i++;
			}
		} catch (IOException | ClassNotFoundException ex) {
			this.logMessage(ex.getMessage());
			ex.printStackTrace();
		} finally {
			this.logMessage("Server stopped.");
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException ex) {
				this.logMessage(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		Thread.startVirtualThread(() -> this.model.stop());
		Thread.startVirtualThread(() -> this.controller.stop());
		SwingUtilities.invokeLater(() -> this.view.stop());
	}

	@Override
	public void requestHandler() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'requestHandler'");
	}

	@Override
	public void requestValidator(Request request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'requestValidator'");
	}

	@Override
	public void requestExecutor(Request request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'requestExecutor'");
	}

	@Override
	public Map<String, Service> requestMapLoader() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'requestMapLoader'");
	}

	private void logMessage(String message) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[SERVER]: {0}",
				new Object[] { message });
	}

}
