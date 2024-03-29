package Model;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Statistics.Statistics;

public class Model implements Service {

	private Map map;
	private List<GeoPoint> path;
	private Path solution;
	private Statistics statistics;

	public Model() {
		this.path = new ArrayList<>();
		this.map = new Map("", null);
		this.solution = new Path(null, 0.0);
		this.statistics = null;
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
			case GET_MAP -> {
				this.sendResponse(
						new Response(
								ResponseCode.GET_MAP,
								this,
								new Body(this.map)));
			}
			case SOLUTION -> {
				this.solution = (Path) request.body.content;
			}
			case STORE_STATISTICS -> {
				this.statistics = (Statistics) request.body.content;
			}
			case GET_STATS -> {
				this.sendResponse(
						new Response(
								ResponseCode.SEND_STATS,
								this,
								new Body(this.statistics)));
			}
			default -> {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}
}
