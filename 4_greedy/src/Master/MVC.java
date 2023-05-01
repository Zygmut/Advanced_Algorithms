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
	private Map<RequestCode, Service[]> reqMap;
	private Map<ResponseCode, Service[]> resMap;
	private Map<ResponseCode, RequestCode> resToReqMap;
	private boolean running = true;

	public MVC() {
		this.model = new Model();
		this.view = new View();
		this.controller = new Controller();
		this.reqMap = new EnumMap<>(RequestCode.class);
		this.resMap = new EnumMap<>(ResponseCode.class);
		this.resToReqMap = new EnumMap<>(ResponseCode.class);
	}

	public MVC(Model model, View view, Controller controller) {
		this.model = model;
		this.view = view;
		this.controller = controller;
		this.reqMap = new EnumMap<>(RequestCode.class);
		this.resMap = new EnumMap<>(ResponseCode.class);
		this.resToReqMap = new EnumMap<>(ResponseCode.class);
	}

	public MVC(String configPath) {
		this.model = new Model();
		this.controller = new Controller();
		this.view = new View(configPath);
		this.reqMap = new EnumMap<>(RequestCode.class);
		this.resMap = new EnumMap<>(ResponseCode.class);
		this.resToReqMap = new EnumMap<>(ResponseCode.class);
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

	/**
	 * This method initializes the server by loading the endpoints from the config
	 * and starting the server socket. Also it manages the requests and responses.
	 */
	private void initServer() {
		this.mapLoader();
		try (ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT)) {
			this.logMessage("Server started.");
			while (running) {
				// Wait for a client connection
				Socket clientSocket = serverSocket.accept();
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
					Response res = (Response) obj;
					this.responseHandler(res);
				}
				// Close the client socket
				clientSocket.close();
			}
		} catch (IOException | ClassNotFoundException ex) {
			this.logMessage(ex.getLocalizedMessage());
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
		return this.reqMap.containsKey(request.code);
	}

	@Override
	public void requestExecutor(Request request) {
		Service[] services = this.reqMap.get(request.code);
		for (Service service : services) {
			this.logMessage("Request sent to service: " + service.getClass().getSimpleName());
			service.notifyRequest(request);
		}
	}

	@Override
	public void mapLoader() {
		// Read the request map from the config file
		Gson gson = new Gson();
		try (BufferedReader br = new BufferedReader(new FileReader(Config.MVC_CONFIG_PATH))) {
			ConfigHolder config = gson.fromJson(br, ConfigHolder.class);
			for (Master.MVC.ConfigHolder.RequestMapHolder holder : config.requestMap) {
				reqMap.put(holder.code, this.stringToServices(holder.services));
			}
			for (Master.MVC.ConfigHolder.ResponseMapHolder holder : config.responseMap) {
				resMap.put(holder.code, this.stringToServices(holder.services));
			}
			for (ResponseToRequestMapHolder holder : config.resToReqMap) {
				resToReqMap.put(holder.responseCode, holder.requestCode);
			}
		} catch (Exception e) {
			throw new ConfigLoaderException("Error loading the request map from the config file.", e);
		}
	}

	@Override
	public void responseHandler(Response response) {
		this.logMessage("Received from client: " + response.toString());
		Thread.startVirtualThread(() -> this.responseExecutor(response));
		this.logMessage("Response processed.");
	}

	@Override
	public void responseExecutor(Response response) {
		Service[] services = this.resMap.get(response.code);
		for (Service service : services) {
			this.logMessage("Response sent to service: " + service.getClass().getSimpleName());
			service.notifyRequest(this.resToReq(response));
		}
	}

	/**
	 * This method converts a response to a request.
	 *
	 * @param response The response to convert.
	 * @return The converted request.
	 */
	private Request resToReq(Response response) {
		return new Request(this.resToReqMap.get(response.code), response.origin, response.body);
	}

	/**
	 * This method logs a message to the console using the logger.
	 *
	 * @param message The message to log.
	 */
	private void logMessage(String message) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[SERVER]: {0}",
				new Object[] { message });
	}

	/**
	 * This method converts a string array to a service array.
	 *
	 * @param names The names of the services to convert.
	 * @return The converted services.
	 */
	private Service[] stringToServices(String[] names) {
		Service[] services = new Service[names.length];
		for (int i = 0; i < names.length; i++) {
			services[i] = this.stringToService(names[i]);
		}
		return services;
	}

	/**
	 * This method converts a string to a service.
	 *
	 * @param name The name of the service to convert.
	 * @return The converted service.
	 */
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

	private class ConfigLoaderException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ConfigLoaderException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private record ConfigHolder(RequestMapHolder[] requestMap, ResponseMapHolder[] responseMap,
			ResponseToRequestMapHolder[] resToReqMap) {

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ConfigHolder)) {
				return false;
			}
			ConfigHolder other = (ConfigHolder) obj;
			if (this.requestMap.length != other.requestMap.length) {
				return false;
			}
			if (this.responseMap.length != other.responseMap.length) {
				return false;
			}
			for (int i = 0; i < this.requestMap.length; i++) {
				if (!this.requestMap[i].equals(other.requestMap[i])) {
					return false;
				}
			}
			for (int i = 0; i < this.responseMap.length; i++) {
				if (!this.responseMap[i].equals(other.responseMap[i])) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 0;
			for (RequestMapHolder holder : this.requestMap) {
				hash += holder.hashCode();
			}
			for (ResponseMapHolder holder : this.responseMap) {
				hash += holder.hashCode();
			}
			return hash;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Request map:\n");
			for (RequestMapHolder holder : this.requestMap) {
				sb.append(holder.toString());
				sb.append("\n");
			}
			sb.append("Response map:\n");
			for (ResponseMapHolder holder : this.responseMap) {
				sb.append(holder.toString());
				sb.append("\n");
			}
			return sb.toString();
		}

		private record RequestMapHolder(RequestCode code, String[] services) {
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof RequestMapHolder)) {
					return false;
				}
				RequestMapHolder other = (RequestMapHolder) obj;
				if (this.code != other.code) {
					return false;
				}
				if (this.services.length != other.services.length) {
					return false;
				}
				for (int i = 0; i < this.services.length; i++) {
					if (!this.services[i].equals(other.services[i])) {
						return false;
					}
				}
				return true;
			}

			@Override
			public int hashCode() {
				int hash = this.code.hashCode();
				for (String service : this.services) {
					hash += service.hashCode();
				}
				return hash;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("Request code: ");
				sb.append(this.code);
				sb.append("\n");
				sb.append("Services: ");
				for (String service : this.services) {
					sb.append(service);
					sb.append(", ");
				}
				return sb.toString();
			}
		}

		private record ResponseMapHolder(ResponseCode code, String[] services) {
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof ResponseMapHolder)) {
					return false;
				}
				ResponseMapHolder other = (ResponseMapHolder) obj;
				if (this.code != other.code) {
					return false;
				}
				if (this.services.length != other.services.length) {
					return false;
				}
				for (int i = 0; i < this.services.length; i++) {
					if (!this.services[i].equals(other.services[i])) {
						return false;
					}
				}
				return true;
			}

			@Override
			public int hashCode() {
				int hash = this.code.hashCode();
				for (String service : this.services) {
					hash += service.hashCode();
				}
				return hash;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("Response code: ");
				sb.append(this.code);
				sb.append("\n");
				sb.append("Services: ");
				for (String service : this.services) {
					sb.append(service);
					sb.append(", ");
				}
				return sb.toString();
			}
		}
	}

	private record ResponseToRequestMapHolder(ResponseCode responseCode, RequestCode requestCode) {
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ResponseToRequestMapHolder)) {
				return false;
			}
			ResponseToRequestMapHolder other = (ResponseToRequestMapHolder) obj;
			return this.responseCode == other.responseCode && this.requestCode == other.requestCode;
		}

		@Override
		public int hashCode() {
			return this.responseCode.hashCode() + this.requestCode.hashCode();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Response code: ");
			sb.append(this.responseCode);
			sb.append("\n");
			sb.append("Request code: ");
			sb.append(this.requestCode);
			return sb.toString();
		}
	}
}
