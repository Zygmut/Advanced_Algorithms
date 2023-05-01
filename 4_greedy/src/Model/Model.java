package Model;

import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Service;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Config;

public class Model implements Service {

	private Map map;
	private List<GeoPoint> path;

	public Model() {
		this.path = new ArrayList<>();
		this.map = new Map("", null);
	}

	@Override
	public void start() {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "Model stopped.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void notifyRequest(Request request) {
		switch (request.code) {
			case HELLO_WORLD -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO, "Hello World!");
			}
			case LOAD_MAP -> {
				this.map = (Map) request.body.content;
			}
			case SEND_GEOPOINTS -> {
				Object data = request.body.content;
				if (data instanceof List) {
					this.path.addAll((List<GeoPoint>) data);
				} else {
					Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Invalid data type.");
				}
			}
			default -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest(Request request) {
		try (
			Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)
		) {
			ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream()
			);
			out.writeObject(request);

			ObjectInputStream in = new ObjectInputStream(
				socket.getInputStream()
			);
			Response response = (Response) in.readObject();
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Response: {0}", response);
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.SEVERE, "Error while sending request.", e);
		}
	}

	@Override
	public void sendResponse(Response response) {
		try (
			Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)
		) {
			ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream()
			);
			out.writeObject(response);
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.SEVERE, "Error while sending response.", e);
		}
	}
}
