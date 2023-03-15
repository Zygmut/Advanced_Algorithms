package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import betterSwing.Window;

/**
 * The view of the MVC pattern. It's the class that manages the view of the
 * program and the user interface. Also, this class implements the Notify
 * interface.
 * <blockquote>
 * Considerations: It's important to initialize the view before any other
 * method of the view.
 * </blockquote>
 *
 * Example:
 *
 * <pre>
 * {@code
 * View view = new View(); // or View view = new View(mvc);
 * // If the parameter is null, the view will load the default configuration.
 * // It will search for a file named "config.txt" in the base directory of the
 * // project.
 * view.initConfig("config.txt"); // Init config
 * view.start(); // Start the view
 * }
 * </pre>
 *
 * Also, this class allows hot reloading the window. For more information, see
 * the KeyActionManager class.
 *
 * @see View#initConfig(String path)
 * @see View#start()
 * @see View#KeyActionManager
 * @see Notify
 */
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
        this.loadContent();
    }

    public Window getWindow() {
        return this.window;
    }

    /**
     * Loads all the view content
     */
    private void loadContent() {
        // TODO: Add graphics content here
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }
}
