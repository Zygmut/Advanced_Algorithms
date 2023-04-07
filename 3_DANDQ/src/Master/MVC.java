package Master;

import Model.Model;
import Request.Notify;
import Request.Request;
import View.View;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import Controller.Controller;

public class MVC implements Notify {

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

	public void show() {
		SwingUtilities.invokeLater(() -> this.view.getWindow().start());
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case GENERATE_UNIFORM_DATA, GENERATE_GAUSSIAN_DATA -> {
				this.controller.notifyRequest(request);
			}
			case NEW_DATA -> {
				this.model.notifyRequest(request);
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}

	}

	public Model getModel() {
		return model;
	}

	public View getView() {
		return view;
	}

	public Controller getController() {
		return controller;
	}

}
