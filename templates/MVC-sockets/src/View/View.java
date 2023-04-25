package View;

import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Services.Service;
import Services.Comunication.Request.Request;
import betterSwing.Window;

public class View implements Service {

	/**
	 * The MVC hub of the view.
	 */
	private MVC hub;
	/**
	 * The window of the view.
	 */
	private Window window;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 * @see MVC
	 */
	public View(MVC mvc) {
		this.hub = mvc;
		this.window = new Window();
		this.window.initConfig();
		this.loadContent();
	}

	/**
	 * This constructor creates a view with the MVC hub and configures itself given
	 * a config path.
	 *
	 * @param mvc        The MVC hub of the view.
	 * @param configPath The path to its config.
	 * @see MVC
	 */
	public View(MVC mvc, String configPath) {
		this.hub = mvc;
		this.window = new Window(configPath);
		this.window.initConfig();
		this.loadContent();
	}

	@Override
	public void start() {
		// this.window.start();
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View stopped.");
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

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Loading content...");
	}

}
