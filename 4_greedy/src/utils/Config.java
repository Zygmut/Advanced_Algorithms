package utils;

/**
 * This class contains all the general configuration of the application. All the
 * constants that are shared between the application should be placed here.
 */
public class Config {

	/**
	 * This class should not be instantiated.
	 */
	private Config() {
	}

	/**
	 * Indicates whether the application is in debug mode or not.
	 */
	public static final boolean DEBUG = true;
	/**
	 * Indicates where is the {@code View} configuration file located at.
	 */
	public static final String VIEW_MAIN_WIN_CONFIG_PATH = "./src/view/settings/main.json";
	/**
	 * Indicates where is the {@code MVC (Hub)} configuration file located at.
	 */
	public static final String MVC_CONFIG_PATH = "./src/master/settings/main.json";
	/**
	 * Indicates what is the server port of the {@code MVC (Hub)}.
	 */
	public static final int SERVER_PORT = 6969;
	/**
	 * Indicates what is the server host of the {@code MVC (Hub)}.
	 */
	public static final String SERVER_HOST = "localhost";
}
