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
		throw new IllegalStateException("Utility class");
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
	 * Indicates where is the {@code View-Stats} configuration file located at.
	 */
	public static final String VIEW_STATS_WIN_CONFIG_PATH = "./src/view/settings/stats.json";
	/**
	 * Indicates where is the {@code View-User-Manual} configuration file located
	 * at.
	 */
	public static final String VIEW_USER_MANUAL_WIN_CONFIG_PATH = "./src/view/settings/usage.json";
	/**
	 * Indicates where is the content HTML file located at for the
	 * {@code View-Usage}.
	 */
	public static final String USER_MANUAL_CONTENT_FILE_PATH = "file:./assets/user-manual.html";
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
	/**
	 * Indicates the path to the icon of the application.
	 */
	public static final String APP_UI_ICON_PATH = "./assets/icon.png";
	/**
	 * Indicates where is the database located at.
	 */
	public static final String PATH_TO_DB = "./src/Model/db/db.sqlite";
	/**
	 * Indicates the name of the public key file.
	 */
	public static final String PUBLIC_KEY_FILE_NAME = "key.pub";
	/**
	 * Indicates the name of the private key file.
	 */
	public static final String PRIVATE_KEY_FILE_NAME = "key.pri";
	/**
	 * Indicates the encrypted file extension
	 */
	public static final String ENCRYPTED_FILE_EXTENSION = ".crypt";
}
