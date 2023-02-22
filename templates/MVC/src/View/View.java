package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Color;

public class View implements Notify {

    private MVC hub;
    private JFrame frame;
    private ConfigLoader config;
    private Container container;

    public View(MVC mvc) {
        this.hub = mvc;
    }

    public View() {
        this.hub = null;
    }

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

    public void setContainerLayout(LayoutManager mgr) {
        container.setLayout(mgr);
    }

    public void reStart() {
        this.stop();
        this.start();
    }

    public void stop() {
        frame.dispose();
    }

    public void addPanels(JPanel panel) {
        frame.add(panel);
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

    private class KeyActionManager implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
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
        }

    }

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
