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

public class View implements Notify {
    private MVC hub;
    private JFrame frame;

    public View(MVC mvc) {
        this.hub = mvc;
    }

    public View() {
        this.hub = null;
    }

    public void start() {
        ConfigLoader config = new ConfigLoader();
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
        frame.setBounds(config.x, config.y, config.width, config.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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

    private class ConfigLoader {

        private int width = 500;
        private int height = 500;
        private int x = 0;
        private int y = 0;
        private boolean windowOnCenter = true;
        private boolean windowResizable = true;
        private boolean windowWidthCenter = true;
        private boolean windowOnLoadVisible = true;
        private String title = "View";
        private String lookAndFeel = "Nimbus";
        private String iconPath = "icon.png";
        private String windowCloseOperation = "EXIT_ON_CLOSE";
        private String configPath = "config.txt";

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
                            this.windowCloseOperation = parts[1].replaceAll("^\"|\"$", "");
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
