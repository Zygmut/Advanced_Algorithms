package Master;

import Model.Model;
import Request.Request;
import Request.Service;
import View.View;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import Controller.Controller;

public class MVC implements Service {

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
		SwingUtilities.invokeLater(() -> this.view.getWindow().start());
	}

	@Override
	public void stop() {
		Thread.startVirtualThread(() -> this.model.stop());
		Thread.startVirtualThread(() -> this.controller.stop());
		SwingUtilities.invokeLater(() -> this.view.getWindow().stop());
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
