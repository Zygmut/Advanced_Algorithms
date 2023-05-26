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
	 * Indicates where is the {@code View-Stats} configuration file located at.
	 */
	public static final String VIEW_STATS_WIN_CONFIG_PATH = "./src/view/settings/stats.json";
	/**
	 * Indicates where is the {@code View-User-Manual} configuration file located
	 * at.
	 */
	public static final String VIEW_USER_MANUAL_WIN_CONFIG_PATH = "./src/view/settings/usage.json";
	/**
	 * Indicates where is the {@code View-Word-Guesser} configuration file located
	 * at.
	 */
	public static final String VIEW_WORD_GUESSER_WIN_CONFIG_PATH = "./src/view/settings/word-guesser.json";
	/**
	 * Indicates where is the {@code MVC (Hub)} configuration file located at.
	 */
	public static final String MVC_CONFIG_PATH = "./src/master/settings/main.json";
	/**
	 * Indicates where is the content HTML file located at for the
	 * {@code View-Usage}.
	 */
	public static final String USER_MANUAL_CONTENT_FILE_PATH = "file:./assets/user-manual.html";
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
	 * Indicates the name of the database
	 */
	public static final String DB_NAME = "languages";

	/**
	 * Indicates the default path for all dictionaries
	 */
	public static final String DIC_PATH = "./assets/dictionaries/";

	/**
	 * Indicates the amount of inserts per instruction when populating the database.
	 */
	public static final int BATCH_SIZE = 50_000;
	/**
	 * Indicates the path to the icon of the flags.
	 */
	public static final String ICON_FLAGS_PATH = "./assets/flags/";
	/**
	 * Indicates the path to the Naive Bayes model.
	 */
	public static final String NAIVE_BAYES_MODEL_PATH = "./src/Model/naive-bayes-model.ser";
	/**
	 * Indicates the path to raw data for the Naive Bayes model.
	 */
	public static final String PATH_TO_RAW_DATA = "./assets/dictionaries/complete";
}
