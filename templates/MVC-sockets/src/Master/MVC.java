package Master;

import Model.Model;
import Controller.Controller;
import Services.Server;
import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Comunication.Response.ResponseStatus;
import View.View;
import utils.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;

public class MVC implements Server {

	private Model model;
	private View view;
	private Controller controller;
	private Map<RequestCode, Service[]> requestMap;
	private boolean running = true;

	public MVC() {
		this.model = new Model();
		this.view = new View();
		this.controller = new Controller();
	}

	public MVC(Model model, View view, Controller controller) {
		this.model = model;
		this.view = view;
		this.controller = controller;
	}

	public MVC(String configPath) {
		this.model = new Model();
		this.controller = new Controller();
		this.view = new View(configPath);
	}

	@Override
	public void start() {
		SwingUtilities.invokeLater(() -> this.view.start());
		this.initServer();
	}

	@Override
	public void stop() {
		SwingUtilities.invokeLater(() -> this.view.stop());
		this.running = false;
	}

	private void initServer() {
		this.requestMap = this.requestMapLoader();
		try (ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT)) {
			this.logMessage("Server started.");
			while (running) {
				// Wait for a client connection
				Socket clientSocket = serverSocket.accept();
				this.logMessage("Client connected." + clientSocket.getInetAddress().toString());
				// Get the input and output streams for the client socket
				ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
				// If is a request
				Object obj = inputStream.readObject();
				if (obj instanceof Request) {
					Request req = (Request) obj;
					ObjectOutputStream outputStream;
					if (this.requestValidator(req)) {
						// Responer
						outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
						Response res = new Response(ResponseCode.PROMISE, this);
						outputStream.writeObject(res);
						// Realizar request
						this.requestHandler(req);
					} else {
						// Responder
						outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
						Response res = new Response(ResponseCode.PROMISE, this, ResponseStatus.NOT_VALID);
						outputStream.writeObject(res);
					}
				} else {
					// If is a response
					Response res = (Response) inputStream.readObject();
					this.responseHandler(res);
				}
				// Close the client socket
				clientSocket.close();
				this.logMessage("Client disconnected." + clientSocket.getInetAddress().toString());
			}
		} catch (IOException | ClassNotFoundException ex) {
			this.logMessage(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void requestHandler(Request request) {
		this.logMessage("Received from client: " + request.toString());
		Thread.startVirtualThread(() -> this.requestExecutor(request));
		this.logMessage("Request processed.");
	}

	@Override
	public boolean requestValidator(Request request) {
		return this.requestMap.containsKey(request.code);
	}

	@Override
	public void requestExecutor(Request request) {
		Service[] services = this.requestMap.get(request.code);
		for (Service service : services) {
			this.logMessage("Request sent to service: " + service.getClass().getSimpleName());
			service.notifyRequest(request);
		}
	}

	@Override
	public Map<RequestCode, Service[]> requestMapLoader() {
		Map<RequestCode, Service[]> map = new EnumMap<>(RequestCode.class);
		// Read the request map from the config file
		Gson gson = new Gson();
		try (BufferedReader br = new BufferedReader(new FileReader(Config.MVC_CONFIG_PATH))) {
			ConfigHolder config = gson.fromJson(br, ConfigHolder.class);
			for (RequestMapHolder holder : config.requestMap) {
				map.put(holder.code, this.stringToServices(holder.services));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading the request map from the config file.", e);
		}
		return map;
	}

	@Override
	public void responseHandler(Response response) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'responseHandler'");
	}

	private void logMessage(String message) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[SERVER]: {0}",
				new Object[] { message });
	}

	private class ConfigHolder {
		private RequestMapHolder[] requestMap;
		private ResponseMapHolder[] responseMap;
	}

	private class RequestMapHolder {
		private RequestCode code;
		private String[] services;
	}

	private class ResponseMapHolder {
		private ResponseCode code;
		private String[] services;
	}

	private Service[] stringToServices(String[] names) {
		Service[] services = new Service[names.length];
		for (int i = 0; i < names.length; i++) {
			services[i] = this.stringToService(names[i]);
		}
		return services;
	}

	private Service stringToService(String name) {
		// The name should be all in lower case execept the first letter
		name = name.toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return switch (name) {
			case "Model" -> this.model;
			case "View" -> this.view;
			case "Controller" -> this.controller;
			default -> null;
		};
	}

}
