package View;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Service;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import Controller.PrimalityFunction;
import Master.MVC;
import Model.Result;

public class View implements Service {

	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	/**
	 * The window of the view.
	 */
	private Window window;
	/**
	 * The split pane of the view.
	 */
	private JSplitPane splitPane;
	/**
	 * The list of buttons of the view.
	 */
	private JButton[] buttons;
	private JSpinner cifras;
	private JLabel resultLabel;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
	 * @see MVC
	 */
	public View() {
		this.window = new Window();
		this.window.initConfig();
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
	public View(String configPath) {
		this.window = new Window(configPath);
		this.window.initConfig();
	}

	@Override
	public void start() {
		logger.log(Level.INFO, "View started.");
		this.loadContent();
		this.window.start();
	}

	@Override
	public void stop() {
		logger.log(Level.INFO, "View stopped.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CHECK_PRIMALITY -> {
				Result result = (Result) request.body.content;
				logger.log(Level.INFO, "{0}, done in {1}ns",
						new Object[] { (boolean) result.result() ? "yes" : "no", result.time().toNanos() });
				final String text = "<html>Resultado: <br/> El número es "
						+ ((boolean) result.result() ? "primo" : "compuesto") + "<br/> Calculado en: "
						+ result.time().toNanos() + "ns</html>";
				this.resultLabel.setText(text);
			}
			case GET_FACTORS -> {
				Result result = (Result) request.body.content;
				logger.log(Level.INFO, "{0}, done in {1}ms",
						new Object[] { (Map<BigInteger, BigInteger>) result.result(), result.time().toMillis() });
				String text = "";
				if (Objects.isNull(result.result())) {
					text = "<html>Resultado: <br/> No se ha calculado. <br/>" +
							"Motivo: Tiempo aprox. de cálculo " + result.time().toHours() + "horas.</html>";
				} else {
					text = "<html>Resultado: <br/> Los factores son: " + result.result()
							+ "<br/> Calculado en: " + result.time().toMillis() + "ms</html>";
				}
				this.resultLabel.setText(text);
			}
			case GET_MESURAMENT -> {
				String result = (String) request.body.content;
				JOptionPane.showMessageDialog(null, result, "Ratio mesurament", JOptionPane.INFORMATION_MESSAGE);
			}
			case FETCH_STATS -> {
				final Object[] content = (Object[]) request.body.content;
				final Result[] results = (Result[]) content[0];
				final long[] values = (long[]) content[1];
				WindowStats stats = new WindowStats(results, values);
				stats.show();
			}
			default -> {
				logger.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	/**
	 * Loads all the view content.
	 *
	 */
	private void loadContent() {
		this.window.addMenuBar(this.menu());
		this.initSplitPane();
		this.splitPane.setRightComponent(this.sideBar());
		this.window.addSection(this.body(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private JPanel sideBar() {
		JPanel sideBar = new JPanel();
		sideBar.setBackground(Color.WHITE);
		sideBar.setLayout(new GridLayout(3, 1));

		// Logo panel
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.WHITE);
		JPanel iconPanel = new JPanel();
		iconPanel.setBackground(Color.WHITE);
		JLabel icon = new JLabel();
		icon.setBackground(Color.WHITE);
		icon.setIcon(this.escalateImageIcon(Config.APP_UI_ICON_PATH, 128, 128));
		iconPanel.add(icon);
		logoPanel.add(iconPanel);
		sideBar.add(logoPanel);

		// Actions panel
		JPanel actionsPanel = new JPanel();
		actionsPanel.setBackground(Color.WHITE);
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
		actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

		JPanel rsaDigits = new JPanel();
		rsaDigits.setBackground(Color.WHITE);
		rsaDigits.setLayout(new BoxLayout(rsaDigits, BoxLayout.Y_AXIS));
		JLabel rsaDigitsLabel = new JLabel("Número de cifras:");
		rsaDigitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		rsaDigitsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
		rsaDigits.add(rsaDigitsLabel);
		cifras = new JSpinner(new SpinnerNumberModel(300, 100, 600, 1));
		cifras.setMaximumSize(new Dimension(100, 30));
		rsaDigits.add(cifras);

		JPanel rsaActions = new JPanel();
		rsaActions.setBackground(Color.WHITE);
		rsaActions.setLayout(new BoxLayout(rsaActions, BoxLayout.X_AXIS));
		JButton rsaCheckPrimal = new JButton("Generar claves");
		rsaCheckPrimal.addActionListener(
				e -> this.notifyRequest(new Request(RequestCode.GENERATE_RSA_KEYS, new Body((int) cifras.getValue()))));
		rsaActions.add(rsaCheckPrimal);

		actionsPanel.add(Box.createVerticalStrut(30));
		actionsPanel.add(rsaDigits);
		actionsPanel.add(Box.createVerticalStrut(15));
		actionsPanel.add(rsaActions);
		actionsPanel.add(Box.createVerticalStrut(15));
		sideBar.add(actionsPanel);

		return sideBar;
	}

	private ImageIcon escalateImageIcon(String iconPath, int width, int height) {
		Image image = new ImageIcon(iconPath).getImage();
		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	private void initSplitPane() {
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setDividerLocation(0.9);
		this.splitPane.setResizeWeight(1.0);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setBorder(null);
	}

	private Section body() {
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(1, 2));
		content.setBackground(Color.WHITE);

		JPanel left = new JPanel();
		left.setBackground(Color.WHITE);
		left.setLayout(new BorderLayout());
		// Title
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.WHITE);
		JLabel title = new JLabel("Primalidad");
		title.setFont(new Font("Arial", Font.BOLD, 24));
		titlePanel.add(title);
		left.add(titlePanel, BorderLayout.NORTH);
		// Content
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(new GridLayout(3, 1));
		// Row 1
		JPanel row1 = new JPanel();
		row1.setPreferredSize(new Dimension(0, 0));
		row1.setBackground(Color.WHITE);
		JTextArea textArea = new JTextArea(7, 30);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(true);
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		row1.add(scrollPane);
		contentPanel.add(row1);
		// Row 2
		JPanel row2 = new JPanel();
		row2.setBackground(Color.WHITE);
		JComboBox<String> primalityFunction = new JComboBox<>();
		for (PrimalityFunction function : PrimalityFunction.values()) {
			primalityFunction.addItem(function.toString());
		}
		primalityFunction.setSelectedItem(PrimalityFunction.TRIAL_DIVISION);
		JButton checkPrimal = new JButton("Comprobar primalidad");
		checkPrimal.addActionListener(e -> {
			String inputText = textArea.getText();
			if (!inputText.isEmpty()) {
				resultLabel.setText("Calculando...");
				this.sendRequest(new Request(RequestCode.CHECK_PRIMALITY, this, new Body(new Object[] {
						PrimalityFunction.TRIAL_DIVISION, inputText })));
			}
		});
		JButton getFactors = new JButton("Obtener factores");
		getFactors.addActionListener(e -> {
			String inputText = textArea.getText();
			if (!inputText.isEmpty()) {
				resultLabel.setText("Calculando...");
				this.sendRequest(new Request(RequestCode.GET_FACTORS, this, new Body(inputText)));
			}
		});
		row2.add(primalityFunction);
		row2.add(checkPrimal);
		row2.add(getFactors);
		contentPanel.add(row2);
		// Row 3
		JPanel row3 = new JPanel();
		row3.setBackground(Color.WHITE);
		resultLabel = new JLabel("<html>Resultado:<br/>Ejecuta el algoritmo para ver los resultados.</html>");
		resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
		row3.add(resultLabel);
		contentPanel.add(row3);
		left.add(contentPanel, BorderLayout.CENTER);

		JPanel right = new JPanel();
		right.setBackground(Color.GRAY);

		content.add(left);
		content.add(right);
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private JMenuBar menu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu options = new JMenu("Opciones");
		JMenu stats = new JMenu("Estadisticas");
		JMenuItem alg = new JMenuItem("Algoritmos");
		alg.addActionListener(e -> this.sendRequest(new Request(RequestCode.FETCH_STATS, this)));
		JMenuItem jvm = new JMenuItem("JVM");
		jvm.addActionListener(e -> {
			WindowJVMStats jvmStats = new WindowJVMStats();
			jvmStats.show();
			jvmStats.start();
		});

		JMenuItem exit = new JMenuItem("Salir");
		exit.addActionListener(e -> System.exit(0));
		stats.add(alg);
		stats.add(jvm);
		options.add(stats);

		options.addSeparator();
		options.add(exit);
		menuBar.add(options);

		JMenu db = new JMenu("Datos");
		JMenuItem load = new JMenuItem("Cargar BD");
		load.addActionListener(e -> {
			this.sendRequest(new Request(RequestCode.CREATE_DB, this));
		});
		JMenuItem mesu = new JMenuItem("Mesurament");
		mesu.addActionListener(e -> this.sendRequest(new Request(RequestCode.GET_MESURAMENT, this)));
		db.add(load);
		db.add(mesu);
		menuBar.add(db);

		JMenu help = new JMenu("Ayuda");
		JMenuItem about = new JMenuItem("Manual de Usuario");
		about.addActionListener(e -> {
			WindowUsage usage = new WindowUsage();
			usage.show();
		});
		help.add(about);
		menuBar.add(help);

		JMenu encriptacion = new JMenu("Encriptacion RSA");
		JMenuItem encriptar = new JMenuItem("Encriptar fichero");
		JMenuItem desencriptar = new JMenuItem("Desencriptar fichero");
		encriptar.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Selección fichero a encriptar");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Ficheros de texto", "txt"));
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				this.sendRequest(new Request(RequestCode.ENCRYPT_FILE, this, new Body(selectedFile)));
			}
		});

		desencriptar.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Selección fichero a desencriptar");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Ficheros de texto", "txt"));
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				this.sendRequest(new Request(RequestCode.DECRYPT_FILE, this, new Body(selectedFile)));
			}
		});

		encriptacion.add(encriptar);
		encriptacion.add(desencriptar);
		menuBar.add(encriptacion);

		return menuBar;
	}

}
