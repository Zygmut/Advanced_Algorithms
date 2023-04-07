package View;

import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Request.Notify;
import Request.Request;
import betterSwing.Window;

public class View implements Notify {

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
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		//throw new RuntimeException("Not implemented");
	}

	/**
	 * Returns the window of the view.
	 *
	 * @return The window of the view.
	 */
	public Window getWindow() {
		return this.window;
	}

}
