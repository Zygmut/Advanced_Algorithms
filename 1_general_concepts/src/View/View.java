package View;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import betterSwing.Window;
import betterSwing.Section;
import betterSwing.DirectionAndPosition;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Color;

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
    private JProgressBar[] progressBarList;
    /**
     * The number of progress bars in the view.
     */
    private final int PROGRESS_OPTIONS = 3;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.selectedTime = "Nanosegundos";
        this.iterationStep = this.hub.getModel().getIterationStep();
        this.batchSize = this.hub.getModel().getBatchSize();
        this.createProgressBarToTimeOut();
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
        this.selectedTime = "Nanosegundos";
        this.iterationStep = this.hub.getModel().getIterationStep();
        this.batchSize = this.hub.getModel().getBatchSize();
        this.createProgressBarToTimeOut();
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
        this.window.addSection(this.createButtons(), DirectionAndPosition.POSITION_TOP, "Buttons");
        this.window.addSection(this.updateChart(), DirectionAndPosition.POSITION_CENTER, "Chart");
        this.window.addSection(this.footer(), DirectionAndPosition.POSITION_BOTTOM, "Menu");
    }

    /**
     * Shows the progress of the algorithm to time out.
     */
    private void createProgressBarToTimeOut() {
        this.progressBarList = new JProgressBar[PROGRESS_OPTIONS];
        JProgressBar progressBar;
        for (int i = 0; i < PROGRESS_OPTIONS; i++) {
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            progressBar.setValue(0);
            this.progressBarList[i] = progressBar;
        }
        this.progressBarList[0].setForeground(Color.RED);
        this.progressBarList[1].setForeground(Color.BLUE);
        this.progressBarList[2].setForeground(Color.GREEN);
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
        feedBackPanel.add(this.progressBarList[0]);
        feedBackPanel.add(this.progressBarList[1]);
        feedBackPanel.add(this.progressBarList[2]);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(centralPanel);
        panel.add(feedBackPanel);
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

    private int getProgress(long value) {
        return (int) ((double) value / this.hub.getModel().getParsedTimeout() * 100);
    }

    /**
     * Creates and updates a chart view of the model data. Returns the newly created
     * chart.
     *
     * @return Section The updated chart Section.
     */
    private Section updateChart() {
        String[] labels = { "Iteración", "Tiempo" };
        String chartColumnLabels[] = { "Escalar", "Moda NLogN", "Moda N" };
        Color chartColors[] = {
                Color.RED,
                Color.BLUE,
                Color.BLACK,
        };
        Section chartSection = new Section();

        long[][] data = this.hub.getModel().getData();

        this.progressBarList[0].setValue(getProgress(data[0][data[0].length - 1]));
        this.progressBarList[1].setValue(getProgress(data[1][data[1].length - 1]));
        this.progressBarList[2].setValue(getProgress(data[2][data[2].length - 1]));

        if (data[0].length == 1 && data[1].length == 1 && data[2].length == 1){
            data[0] = new long[]{};
            data[1] = new long[]{};
            data[2] = new long[]{};
        }

        chartSection.createLineChart(labels, data , chartColors, chartColumnLabels,
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

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Show_data:
                String labelText = String.format("Iteración: %d x ", this.hub.getModel().getIteration());
                this.actualRelativeIteration.setText(labelText);
                this.window.updateSection(this.updateChart(), "Chart", DirectionAndPosition.POSITION_CENTER);
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }
}
