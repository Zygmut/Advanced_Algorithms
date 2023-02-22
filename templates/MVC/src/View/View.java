package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Color;

public class View implements Notify {

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

    public View(MVC mvc) {
        this.hub = mvc;
    }

    public View() {
        this.hub = null;
    }

    /**
     * Allows to create and start the view.
     */
    public void start() {
        config = new ConfigLoader();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (config.lookAndFeel.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
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
     * Allows to restart the view. Useful to make changes to the view without having
     * to restart the program.
     */
    public void reStart() {
        this.stop();
        this.start();
    }

    /**
     * Allows to stop the view. Basically it closes the frame.
     */
    public void stop() {
        frame.dispose();
    }

    /**
     * Allows to add a panel to the container of the view.
     * 
     * @param panel The panel to add.
     */
    public void addPanels(JPanel panel) {
        container.add(panel);
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

    // TODO: Finish the Section class implementation
    private class Section extends JPanel {

        public Section() {
            super();
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

        public ConfigLoader() {
            this.loadConfig();
        }

        /**
         * Allows to load the configuration from the configuration file. The file
         * structure is:
         * 
         * <pre>
         * # Use # to comment the line
         * parameter: value
         * </pre>
         * 
         * Example:
         * 
         * <pre>
         * bgColor: "white"
         * width: 500
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
                        case "width":
                            this.width = Integer.parseInt(parts[1]);
                            break;
                        case "height":
                            this.height = Integer.parseInt(parts[1]);
                            break;
                        case "x":
                            this.x = Integer.parseInt(parts[1]);
                            break;
                        case "y":
                            this.y = Integer.parseInt(parts[1]);
                            break;
                        case "title":
                            this.title = parts[1].replaceAll("^\"|\"$", "");
                            break;
                        case "lookAndFeel":
                            this.lookAndFeel = parts[1].replaceAll("^\"|\"$", "");
                            break;
                        case "windowOnCenter":
                            this.windowOnCenter = Boolean.parseBoolean(parts[1]);
                            if (this.windowOnCenter) {
                                this.x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
                                        - (this.width / 2);
                                this.y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
                                        - (this.height / 2);
                            }
                            break;
                        case "windowResizable":
                            this.windowResizable = Boolean.parseBoolean(parts[1]);
                            break;
                        case "windowCloseOperation":
                            switch (parts[1]) {
                                case "EXIT_ON_CLOSE":
                                    this.windowCloseOperation = JFrame.EXIT_ON_CLOSE;
                                    break;
                                case "DISPOSE_ON_CLOSE":
                                    this.windowCloseOperation = JFrame.DISPOSE_ON_CLOSE;
                                    break;
                                case "HIDE_ON_CLOSE":
                                    this.windowCloseOperation = JFrame.HIDE_ON_CLOSE;
                                    break;
                                case "DO_NOTHING_ON_CLOSE":
                                    this.windowCloseOperation = JFrame.DO_NOTHING_ON_CLOSE;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "windowIcon":
                            this.iconPath = parts[1].replaceAll("^\"|\"$", "");
                            break;
                        case "windowWidthCenter":
                            this.windowWidthCenter = Boolean.parseBoolean(parts[1]);
                            if (this.windowWidthCenter) {
                                this.width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                                        / 2);
                                this.height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                                        / 2);
                            }
                            break;
                        case "windowOnLoadVisible":
                            this.windowOnLoadVisible = Boolean.parseBoolean(parts[1]);
                            break;
                        case "bgColor":
                            switch (parts[1].replaceAll("^\"|\"$", "").toUpperCase()) {
                                case "BLACK":
                                    this.bgColor = Color.BLACK;
                                    break;
                                case "BLUE":
                                    this.bgColor = Color.BLUE;
                                    break;
                                case "CYAN":
                                    this.bgColor = Color.CYAN;
                                    break;
                                case "DARK_GRAY":
                                    this.bgColor = Color.DARK_GRAY;
                                    break;
                                case "GRAY":
                                    this.bgColor = Color.GRAY;
                                    break;
                                case "GREEN":
                                    this.bgColor = Color.GREEN;
                                    break;
                                case "LIGHT_GRAY":
                                    this.bgColor = Color.LIGHT_GRAY;
                                    break;
                                case "MAGENTA":
                                    this.bgColor = Color.MAGENTA;
                                    break;
                                case "ORANGE":
                                    this.bgColor = Color.ORANGE;
                                    break;
                                case "PINK":
                                    this.bgColor = Color.PINK;
                                    break;
                                case "RED":
                                    this.bgColor = Color.RED;
                                    break;
                                case "WHITE":
                                    this.bgColor = Color.WHITE;
                                    break;
                                case "YELLOW":
                                    this.bgColor = Color.YELLOW;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
