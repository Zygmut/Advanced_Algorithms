package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.HeadlessException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

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
     * The frame of the view that contains the container of the view.
     */
    private JFrame frame;
    /**
     * The container of the frame that contains all the elements of the view.
     */
    private Container container;
    /**
     * The configuration of the view.
     */
    private ConfigLoader config;
    /**
     * Indicates if the view is initialized.
     */
    private boolean isInitialized = false;
    /**
     * Indicates if the view is restarted.
     */
    private boolean isRestarted = false;
    /**
     * The path of the configuration file.
     */
    private String pathToConfig = null;
    /**
     * Copy of the frame of the view for allowing to get the config back.
     */
    private Container copyContainer = null;

    public View(MVC mvc) {
        this.hub = mvc;
    }

    /**
     * Allows to initialize the view. It loads the configuration of the view and
     * creates the frame of the view. It's important to call this method before any
     * other method of the view.
     *
     * @param path The path of the configuration file.
     */
    public void initConfig(String path) {
        this.isInitialized = true;
        config = new ConfigLoader(path);
        this.pathToConfig = path;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (config.lookAndFeel.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
        frame = new JFrame(config.title);
        container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(config.bgColor);
        if (config.iconPath != null && !config.iconPath.isEmpty()) {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(config.iconPath));
        }
        frame.setResizable(config.windowResizable);
        frame.setBounds(config.x, config.y, config.width, config.height);
        if (config.windowOnCenter && config.windowWidthCenter) {
            frame.setLocationRelativeTo(null);
        }
        frame.setDefaultCloseOperation(config.windowCloseOperation);
        frame.addKeyListener(new KeyActionManager());
    }

    /**
     * Allows to view the Frame. Basically it makes the frame visible. It's
     * important to initialize the view before calling this method.
     *
     * @see View#initConfig(String path)
     */
    public void start() {
        if (!this.isInitialized) {
            throw new IllegalStateException("The view is not initialized.");
        }
        if (this.isRestarted) {
            this.initConfig(this.pathToConfig);
            this.isRestarted = false;
        }
        frame.setVisible(config.windowOnLoadVisible);
    }

    /**
     * Allows to set the layout of the container of the view.
     *
     * @param layoutManager The layout manager of the container.
     */
    public void setContainerLayout(LayoutManager layoutManager) {
        container.setLayout(layoutManager);
    }

    /**
     * Allows to save the chart of the view to an image. Allowed formats: png. To
     * save an image it's necessary to override a file with the allowed format
     * extensions.
     */
    public void saveChart() {
        File file = selectFile();
        if (file == null) {
            return;
        }
        try {
            BufferedImage image = new BufferedImage(this.container.getWidth(),
                    this.container.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D temp = image.createGraphics();
            this.container.print(temp);
            temp.dispose();
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows to select a file.
     *
     * @return The file selected.
     */
    private File selectFile() {
        JFileChooser windowChooser = new JFileChooser();
        try {
            int state = windowChooser.showOpenDialog(windowChooser);
            File path = new File(System.getProperty("user.dir"));
            // Añadimos el directorio
            windowChooser.setCurrentDirectory(path);
            return state == JFileChooser.APPROVE_OPTION
                    ? windowChooser.getSelectedFile()
                    : null;
        } catch (HeadlessException error) {
            throw new RuntimeException(error);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * Allows to restart the view. Useful to make changes to the view without having
     * to restart the program.
     */
    public void reStart() {
        this.isRestarted = true;
        this.saveActualContentContainer();
        this.stop();
        this.start();
        this.reloadContentContainer();
    }

    /**
     * Allows to stop the view. Basically it closes the frame.
     */
    public void stop() {
        frame.dispose();
    }

    /**
     * Allows to reload the content of the container of the view.
     */
    private void reloadContentContainer() {
        Component[] components = this.copyContainer.getComponents();
        for (Component component : components) {
            this.container.add(component);
        }
    }

    /**
     * Allows to save the content of the container of the view.
     */
    private void saveActualContentContainer() {
        Component[] components = this.container.getComponents();
        this.copyContainer = new Container();
        for (Component component : components) {
            this.copyContainer.add(component);
        }
    }

    /**
     * Allows to add a panel to the container of the view.
     *
     * @param section The section to add to the view.
     */
    public void addSection(Section section) {
        container.add(section.getPanel());
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

    /**
     * Allows to manage the key events of the view. Mainly used for debugging and
     * development. The keys are:
     * <ul>
     * <li>Q: Quit the program.</li>
     * <li>R: Restart the view.</li>
     * <li>S: Stop the view.</li>
     * <li>V: Show or hide the view.</li>
     * </ul>
     */
    private class KeyActionManager implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            // Do nothing
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Q:
                    System.exit(0);
                    break;
                case KeyEvent.VK_R:
                    reStart();
                    break;
                case KeyEvent.VK_S:
                    stop();
                    break;
                case KeyEvent.VK_V:
                    config.windowOnLoadVisible = !config.windowOnLoadVisible;
                    frame.setVisible(config.windowOnLoadVisible);
                    break;
                case KeyEvent.VK_G:
                    saveChart();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Do nothing
        }

    }

    /**
     * Allows to load the configuration of the view. The configuration is loaded
     * from a file. By default the file is "config.txt" but it can be changed. Also
     * it has a default configuration. The configuration file can contain the
     * following parameters:
     * <ul>
     * <li>width: The width of the view.</li>
     * <li>height: The height of the view.</li>
     * <li>x: The x position of the view.</li>
     * <li>y: The y position of the view.</li>
     * <li>windowCloseOperation: The operation to do when the view is closed.</li>
     * <li>windowOnCenter: If the view is on the center of the screen.</li>
     * <li>windowResizable: If the view is resizable.</li>
     * <li>windowWidthCenter: If the view is centered on the width.</li>
     * <li>windowOnLoadVisible: If the view is visible on load.</li>
     * <li>title: The title of the view.</li>
     * <li>lookAndFeel: The look and feel of the view.</li>
     * <li>iconPath: The path of the icon of the view.</li>
     * <li>configPath: The path of the configuration file.</li>
     * <li>bgColor: The background color of the view.</li>
     * </ul>
     */
    private class ConfigLoader {
        private int width = 500;
        private int height = 500;
        private int x = 0;
        private int y = 0;
        private int windowCloseOperation = JFrame.EXIT_ON_CLOSE;
        private boolean windowOnCenter = true;
        private boolean windowResizable = true;
        private boolean windowWidthCenter = true;
        private boolean windowOnLoadVisible = true;
        private String title = "View";
        private String lookAndFeel = "Nimbus";
        private String iconPath = "";
        private String configPath = "config.txt";
        private Color bgColor = Color.BLACK;

        /**
         * Allows to load the configuration from the default configuration file.
         */
        public ConfigLoader(String configPath) {
            if (configPath != null) {
                this.configPath = configPath;
            }
            this.loadConfig();
        }

        /**
         * Allows to load the configuration from the configuration file. The file
         * structure is:
         *
         * <pre>
         * Use # to comment the line
         * parameter: value
         * </pre>
         *
         * Example:
         *
         * <pre>
         * {@code
         * bgColor: "white"
         * width: 500
         * }
         * </pre>
         */
        private void loadConfig() {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.configPath));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(":");
                    parts[0] = parts[0].trim();
                    parts[1] = parts[1].trim();
                    switch (parts[0]) {
                        case "width" -> this.width = Integer.parseInt(parts[1]);
                        case "height" -> this.height = Integer.parseInt(parts[1]);
                        case "x" -> this.x = Integer.parseInt(parts[1]);
                        case "y" -> this.y = Integer.parseInt(parts[1]);
                        case "title" -> this.title = parts[1].replaceAll("^\"|\"$", "");
                        case "lookAndFeel" -> this.lookAndFeel = parts[1].replaceAll("^\"|\"$", "");
                        case "windowOnCenter" -> {
                            this.windowOnCenter = Boolean.parseBoolean(parts[1]);
                            if (this.windowOnCenter) {
                                this.x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
                                        - (this.width / 2);
                                this.y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
                                        - (this.height / 2);
                            }
                        }
                        case "windowResizable" -> this.windowResizable = Boolean.parseBoolean(parts[1]);
                        case "windowCloseOperation" -> this.windowCloseOperation = getWindowCloseOperation(parts[1]);
                        case "windowIcon" -> this.iconPath = parts[1].replaceAll("^\"|\"$", "");
                        case "windowWidthCenter" -> {
                            this.windowWidthCenter = Boolean.parseBoolean(parts[1]);
                            if (this.windowWidthCenter) {
                                this.width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                                        / 2);
                                this.height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                                        / 2);
                            }
                        }
                        case "windowOnLoadVisible" -> this.windowOnLoadVisible = Boolean.parseBoolean(parts[1]);
                        case "bgColor" -> this.bgColor = getColor(parts[1].replaceAll("^\"|\"$", "").toUpperCase());
                        default -> {
                            // Do nothing
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error loading the configuration file.\nThe file was not found", e);
            }
        }

        private int getWindowCloseOperation(String windowCloseOperation) {
            switch (windowCloseOperation) {
                case "DO_NOTHING_ON_CLOSE":
                    return JFrame.DO_NOTHING_ON_CLOSE;
                case "HIDE_ON_CLOSE":
                    return JFrame.HIDE_ON_CLOSE;
                case "DISPOSE_ON_CLOSE":
                    return JFrame.DISPOSE_ON_CLOSE;
                case "EXIT_ON_CLOSE":
                    return JFrame.EXIT_ON_CLOSE;
                default:
                    return JFrame.EXIT_ON_CLOSE;
            }
        }

        private Color getColor(String color) {
            switch (color) {
                case "BLACK":
                    return Color.BLACK;
                case "BLUE":
                    return Color.BLUE;
                case "CYAN":
                    return Color.CYAN;
                case "DARK_GRAY":
                    return Color.DARK_GRAY;
                case "GRAY":
                    return Color.GRAY;
                case "GREEN":
                    return Color.GREEN;
                case "LIGHT_GRAY":
                    return Color.LIGHT_GRAY;
                case "MAGENTA":
                    return Color.MAGENTA;
                case "ORANGE":
                    return Color.ORANGE;
                case "PINK":
                    return Color.PINK;
                case "RED":
                    return Color.RED;
                case "WHITE":
                    return Color.WHITE;
                case "YELLOW":
                    return Color.YELLOW;
                default:
                    return Color.WHITE;
            }
        }
    }
}
