package View;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import Controller.PrimalityFunction;
import Master.MVC;
import Model.Result;
import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;

public class View implements Service {

	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
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
	public View() {
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
	public View(String configPath) {
		this.window = new Window(configPath);
		this.window.initConfig();
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View started.");
		this.loadContent();
		this.window.start();
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CHECK_PRIMALITY -> {
				Result result = (Result) request.body.content;
				logger.log(Level.INFO, "{0}, done in {1}ns", new Object[]{(boolean) result.result() ? "yes" : "no", result.time().toNanos()});
			}
			case GREET -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Hi {0}!.", request.origin);
			}
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
		Section demoSection = new Section();
		JButton demoButton = new JButton("Greet the boys!");
		demoButton.addActionListener(e -> {
			Request request = new Request(RequestCode.GREET, this, new Body("Anybody there?!"));
			this.sendRequest(request);
		});
		demoSection.createButtons(new JButton[] { demoButton }, DirectionAndPosition.DIRECTION_ROW);
		Section primality = new Section();
		JPanel prime = new JPanel();
		JTextField numberField = new JTextField(30);
		JButton primeButton = new JButton("is Prime?");
		primeButton.addActionListener(e -> {
			this.sendRequest(new Request(RequestCode.CHECK_PRIMALITY, this, new Body(new Object[] {
					PrimalityFunction.TRIAL_DIVISION, BigInteger.valueOf(Long.parseLong(numberField.getText())) })));
		});
		prime.add(numberField);
		prime.add(primeButton);
		primality.createFreeSection(prime);
		this.window.addSection(demoSection, DirectionAndPosition.POSITION_TOP, "Demo");
		this.window.addSection(primality, DirectionAndPosition.POSITION_CENTER, "Primality");
	}

}
