package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import View.Section.DirectionAndPosition;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
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
    /**
     * The index of the panels that are being added to the view.
     */
    private ArrayList<String> viewIndexPanels = null;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.viewIndexPanels = new ArrayList<>();
        this.initConfig(null);
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
        this.viewIndexPanels = new ArrayList<>();
        this.initConfig(configPath);
        this.loadContent();
    }

    /**
     * Loads all the view content
     */
    private void loadContent() {
        // Leyend
        Section legendSection = new Section();
        String leyendColumnLabels[] = { "Escalar", "Mode NLogN", "Mode N" };
        Color leyendColors[] = {
                Color.RED,
                Color.YELLOW,
                Color.BLUE,
        };
        legendSection.addLegend(leyendColumnLabels, leyendColors, DirectionAndPosition.DIRECTION_COLUMN, -1);
        this.addSection(legendSection, DirectionAndPosition.POSITION_RIGHT, "Legend");
        this.addSection(this.updateChart(), DirectionAndPosition.POSITION_CENTER, "Chart");
        this.addSection(this.menuSecondsAndBatch(), DirectionAndPosition.POSITION_BOTTOM, "Menu");
    }

    private Section menuSecondsAndBatch() {
        String[] opcionesTiempo = { "Nanoseconds", "Miliseconds", "Seconds" };
        JComboBox<String> menuTiempo = new JComboBox<String>(opcionesTiempo);
        menuTiempo.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String selected = (String) cb.getSelectedItem();
            switch (selected) { //
                case "Nanoseconds":
                    System.out.println("Nanoseconds");
                    break;
                case "Miliseconds":
                    System.out.println("Miliseconds");
                    break;
                case "Seconds":
                    System.out.println("Seconds");
                    break;
            }
        });
        JPanel panel = new JPanel();
        SpinnerListModel model = new SpinnerListModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" });
        JSpinner spinner = new JSpinner(model);
        panel.add(spinner);
        panel.add(menuTiempo);
        Section section = new Section();
        section.createSectionOnSection(panel);
        return section;
    }


    /**
     * Creates and updates a chart view of the model data. Returns the newly created
     * chart
     *
     * @return Section Newly created chart section
     */
    private Section updateChart() {
        Section chartSection = new Section();
        String chartColumnLabels[] = { "A", "B", "C" };
        String shh[] = { "A", "B" };
        Color chartColors[] = {
                Color.RED,
                Color.BLUE,
                Color.BLACK,
        };
        long[][] data = this.hub.getModel().getData();
        chartSection.createLineChart(shh, data, chartColors, chartColumnLabels);
        return chartSection;
    }

    /**
     * Allows to initialize the view. It loads the configuration of the view and
     * creates the frame of the view. It's important to call this method before any
     * other method of the view.
     *
     * @param path The path of the configuration file.
     */
    private void initConfig(String path) {
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
     * Allows to repaint an specific component of the view.
     *
     * @param component The component to repaint.
     */
    public void repaintComponent(String component) {
        int index = this.viewIndexPanels.indexOf(component);
        if (index == -1) {
            throw new IllegalArgumentException("The component does not exist.");
        }
        try {
            this.container.getComponent(index).repaint();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows to repaint all the view.
     */
    public void repaintAllComponents() {
        for (Component component : this.container.getComponents()) {
            component.repaint();
        }
    }

    /**
     * Allows to delete an specific component of the view.
     *
     * @param component The component to delete.
     */
    public void deleteComponent(String component) {
        int index = this.viewIndexPanels.indexOf(component);
        if (index == -1) {
            throw new IllegalArgumentException("The component does not exist.");
        }
        this.viewIndexPanels.remove(component);

        try {
            this.container.remove(index);
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
     * Allows to add a panel to the container of the view. The posible positions are
     * from the class DirectionAndPosition, see more in the documentation of the
     * class.
     *
     * @param section  The section to add to the view.
     * @param position The position of the section in the view.
     *
     * @see DirectionAndPosition
     */
    public void addSection(Section section, int position, String name) {
        this.viewIndexPanels.add(name);
        container.add(section.isHTML() ? section.getComponent() : section.getPanel(),
                DirectionAndPosition.getPosition(position));
    }

    /**
     * Allows to update a panel from the container of the view. The posible
     * positions are from the class DirectionAndPosition, see more in the
     * documentation of the class.
     *
     * @param oldSection The section to add to the view.
     * @param newSection The section to add to the view.
     * @param position   The position of the section in the view.
     *
     * @see DirectionAndPosition
     */
    public void updateSection(Section newSection, String sectionName, int position) {
        this.deleteComponent(sectionName);
        this.addSection(newSection, position, sectionName);
        frame.validate();
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case New_data:
                this.updateSection(this.updateChart(), "Chart", DirectionAndPosition.POSITION_CENTER);
                break;
            case Load_buttons:
                Section buttonsSection = new Section();
                buttonsSection.addButtons(this.hub.getController().getButtons(), DirectionAndPosition.DIRECTION_ROW);
                this.addSection(buttonsSection, DirectionAndPosition.POSITION_TOP, "Buttons");
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
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
            return switch (windowCloseOperation) {
                case "DO_NOTHING_ON_CLOSE" -> JFrame.DO_NOTHING_ON_CLOSE;
                case "HIDE_ON_CLOSE" -> JFrame.HIDE_ON_CLOSE;
                case "DISPOSE_ON_CLOSE" -> JFrame.DISPOSE_ON_CLOSE;
                case "EXIT_ON_CLOSE" -> JFrame.EXIT_ON_CLOSE;
                default -> JFrame.EXIT_ON_CLOSE;
            };
        }

        private Color getColor(String color) {
            return switch (color) {
                case "BLACK" -> Color.BLACK;
                case "BLUE" -> Color.BLUE;
                case "CYAN" -> Color.CYAN;
                case "DARK_GRAY" -> Color.DARK_GRAY;
                case "GRAY" -> Color.GRAY;
                case "GREEN" -> Color.GREEN;
                case "LIGHT_GRAY" -> Color.LIGHT_GRAY;
                case "MAGENTA" -> Color.MAGENTA;
                case "ORANGE" -> Color.ORANGE;
                case "PINK" -> Color.PINK;
                case "RED" -> Color.RED;
                case "WHITE" -> Color.WHITE;
                case "YELLOW" -> Color.YELLOW;
                default -> Color.WHITE;
            };
        }
    }
}
