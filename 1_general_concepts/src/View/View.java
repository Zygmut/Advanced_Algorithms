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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
    private boolean isInitialized;
    /**
     * Indicates if the view is restarted.
     */
    private boolean isRestarted;
    /**
     * The path of the configuration file.
     */
    private String pathToConfig;
    /**
     * Copy of the frame of the view for allowing to get the config back.
     */
    private Container copyContainer;
    /**
     * The index of the panels that are being added to the view.
     */
    private ArrayList<String> viewIndexPanels;
    /**
     * Label that shows the actual iteration the algorithms.
     */
    private JLabel actualRelativeIteration;
    /**
     * Time selected by the user.
     */
    private String selectedTime;
    /**
     * The actual iteration step of the algorithms.
     */
    private int iterationStep;
    /**
     * The actual batch size of the algorithms.
     */
    private int batchSize;
    /**
     * The progress bar of the view.
     */
    private JProgressBar progressBar;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.selectedTime = "Nanosegundos";
        this.isRestarted = false;
        this.isInitialized = false;
        this.iterationStep = this.hub.getModel().getIterationStep();
        this.batchSize = this.hub.getModel().getBatchSize();
        this.viewIndexPanels = new ArrayList<>();
        this.progressBar = new JProgressBar();
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
        this.selectedTime = "Nanosegundos";
        this.isRestarted = false;
        this.isInitialized = false;
        this.iterationStep = this.hub.getModel().getIterationStep();
        this.batchSize = this.hub.getModel().getBatchSize();
        this.viewIndexPanels = new ArrayList<>();
        this.progressBar = new JProgressBar();
        this.initConfig(configPath);
        this.loadContent();
    }

    /**
     * Loads all the view content
     */
    private void loadContent() {
        this.addSection(this.createButtons(), DirectionAndPosition.POSITION_TOP, "Buttons");
        this.addSection(this.updateChart(), DirectionAndPosition.POSITION_CENTER, "Chart");
        this.addSection(this.footer(), DirectionAndPosition.POSITION_BOTTOM, "Menu");
    }

    private Section createButtons() {
        Section buttonSection = new Section();
        JButton[] buttons = new JButton[5];
        buttons[0] = new JButton("Escalar");
        buttons[0].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
            this.hub.notifyRequest(new Request(RequestCode.Escalar_Product, this));
            buttons[4].setText("Pausar");
        });
        buttons[1] = new JButton("Moda NLogN");
        buttons[1].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
            this.hub.notifyRequest(new Request(RequestCode.Mode_O_nlogn, this));
            buttons[4].setText("Pausar");
        });
        buttons[2] = new JButton("Moda N");
        buttons[2].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
            this.hub.notifyRequest(new Request(RequestCode.Mode_O_n, this));
            buttons[4].setText("Pausar");
        });
        buttons[3] = new JButton("Todos");
        buttons[3].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
            this.hub.notifyRequest(new Request(RequestCode.All_methods, this));
            buttons[4].setText("Pausar");
        });
        buttons[4] = new JButton("Pausar");
        buttons[4].addActionListener(e -> {
            String btnText = buttons[4].getText();
            String newBtnText = btnText.equals("Pausar") ? "Reanudar" : "Pausar";
            buttons[4].setText(newBtnText);
            RequestCode code;
            if (btnText.equals("Pausar")) {
                code = RequestCode.Pause_execution;
            } else {
                code = RequestCode.Resume_execution;
            }
            this.hub.notifyRequest(new Request(code, this));
        });

        buttonSection.addButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
        return buttonSection;
    }

    /**
     * Creates the menu for the seconds and the batch size.
     *
     * @return Section The menu section.
     */
    private Section footer() {
        String[] timeOptions = { "Nanosegundos", "Milisegundos", "Segundos", "Minutos", "Horas", "Días" };
        JComboBox<String> timeMenu = new JComboBox<>(timeOptions);
        timeMenu.addActionListener(e -> {
            String selected = String.valueOf(timeMenu.getSelectedItem());
            this.selectedTime = selected;
            switch (selected) {
                case "Nanosegundos" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Nanoseconds, this));
                }
                case "Milisegundos" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Milliseconds, this));
                }
                case "Segundos" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Seconds, this));
                }
                case "Minutos" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Minutes, this));
                }
                case "Horas" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Hours, this));
                }
                case "Días" -> {
                    this.hub.notifyRequest(new Request(RequestCode.Time_To_Days, this));
                }
                default -> {
                    // Do nothing
                }
            }
        });

        JLabel batchLabel = new JLabel("Tamaño del lote: ");
        JLabel timeLabel = new JLabel("Representación: ");

        SpinnerNumberModel model = new SpinnerNumberModel(this.hub.getModel().getBatchSize(), 1, 500, 1);
        JSpinner batchSpinner = new JSpinner(model);
        batchSpinner.addChangeListener(e -> {
            this.batchSize = (int) batchSpinner.getValue();
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
        });

        SpinnerNumberModel iterationModel = new SpinnerNumberModel(this.hub.getModel().getIterationStep(), 1, 1000, 1);
        JSpinner spIteration = new JSpinner(iterationModel);
        spIteration.addChangeListener(e -> {
            this.iterationStep = (int) spIteration.getValue();
            this.hub.notifyRequest(new Request(RequestCode.Reset_data, this));
        });

        String labelText = String.format("Iteración: %d x ", this.hub.getModel().getIteration());
        this.actualRelativeIteration = new JLabel(labelText);

        JPanel centralPanel = new JPanel();
        centralPanel.add(batchLabel);
        centralPanel.add(batchSpinner);
        centralPanel.add(timeLabel);
        centralPanel.add(timeMenu);
        centralPanel.add(this.actualRelativeIteration);
        centralPanel.add(spIteration);

        JPanel feedBackPanel = new JPanel();
        this.progressBar.setStringPainted(true);
        this.progressBar.setValue(0);
        this.progressBar.setForeground(new Color(70, 130, 180));
        feedBackPanel.add(progressBar);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(centralPanel, BorderLayout.CENTER);
        panel.add(feedBackPanel, BorderLayout.EAST);
        Section section = new Section();
        section.createSectionOnSection(panel);
        return section;
    }

    /**
     * Returns the iteration step.
     *
     * @return int The iteration step.
     */
    public int getIterationStep() {
        return this.iterationStep;
    }

    /**
     * Returns the batch size
     *
     * @return int The batch size
     */
    public int getBatchSize() {
        return this.batchSize;
    }

    /**
     * Creates and updates a chart view of the model data. Returns the newly created
     * chart.
     *
     * @return Section The updated chart Section.
     */
    private Section updateChart() {
        String[] labels = { "Iteración", "Tiempo" };
        String chartColumnLabels[] = { "Escalar", "Moda LogN", "Moda N" };
        Color chartColors[] = {
                Color.RED,
                Color.BLUE,
                Color.BLACK,
        };
        Section chartSection = new Section();

        long[][] data = this.hub.getModel().getData();
        // Tiempo (ns) por Iteración ns ms s min h d y
        chartSection.createLineChart(labels, data, chartColors, chartColumnLabels,
                String.format("Tiempo (%s) por Iteración", abreviateTime(this.selectedTime)));
        return chartSection;
    }

    private String abreviateTime(String time) {
        return switch (time) {
            case "Nanosegundos" -> "ns";
            case "Milisegundos" -> "ms";
            case "Segundos" -> "s";
            case "Minutos" -> "min";
            case "Horas" -> "h";
            case "Días" -> "d";
            default -> throw new IllegalStateException("Unexpected value: " + time);
        };
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
            case Show_data:
                // TODO: Cambiar 'this.hub.getModel().getIteration() del progressBar
                // por algo que indique el progreso de cada algoritmo en la iteración
                this.progressBar.setValue(this.hub.getModel().getIteration());
                String labelText = String.format("Iteración: %d x ", this.hub.getModel().getIteration());
                this.actualRelativeIteration.setText(labelText);
                this.updateSection(this.updateChart(), "Chart", DirectionAndPosition.POSITION_CENTER);
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
