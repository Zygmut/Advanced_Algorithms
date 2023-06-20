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
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToIntFunction;
import java.util.function.ToLongFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;

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
	 * Displays the result of the primality test.
	 */
	private JLabel resultLabel;
	/**
	 * Displays the time taken to perform encryption/decryption.
	 */
	private JLabel timeTaken;
	/**
	 * Holds the last file selected by the user.
	 */
	private File selectedFile;
	/**
	 * The dialog that displays the mesurament.
	 */
	private JDialog mesuramentDialog;

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
				final String time = getTimeString(result.time());
				final String text = "<html>Resultado: <br/> El número es "
						+ ((boolean) result.result() ? "primo" : "compuesto") + "<br/> Calculado en: "
						+ time + "</html>";
				this.resultLabel.setText(text);
			}
			case GET_FACTORS -> {
				Result result = (Result) request.body.content;
				logger.log(Level.INFO, "{0}, done in {1}ms",
						new Object[] { (Map<BigInteger, BigInteger>) result.result(), result.time().toMillis() });
				String text = "";
				String time = getTimeString(result.time());
				Map<BigInteger, BigInteger> factors = (Map<BigInteger, BigInteger>) result.result();
				if (factors.isEmpty()) {
					text = "<html>Resultado: <br/> No se ha calculado. <br/>" +
							"Motivo: Tiempo aprox. de cálculo " + time + ".</html>";
				} else {
					text = "<html>Resultado: <br/> Los factores son: " + result.result()
							+ "<br/> Calculado en: " + time + "</html>";
				}
				this.resultLabel.setText(text);
			}
			case GET_MESURAMENT -> {
				String result = (String) request.body.content;
				logger.log(Level.INFO, "{0}", result);
				JOptionPane pane = new JOptionPane(result, JOptionPane.INFORMATION_MESSAGE,
						JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
				this.mesuramentDialog.dispose();
				this.mesuramentDialog = pane.createDialog("Ratio mesurament");
				this.mesuramentDialog.setVisible(true);
			}
			case FETCH_STATS -> {
				final Object[] content = (Object[]) request.body.content;
				final Result[] results = (Result[]) content[0];
				final long[] values = (long[]) content[1];
				WindowStats stats = new WindowStats(results, values);
				stats.show();
			}
			case DECRYPT_FILE -> {
				logger.info("File decrypted.");
			}
			case ENCRYPT_FILE -> {
				logger.info("File encrypted.");
			}
			case GENERATE_RSA_KEYS -> {
				logger.info("RSA keys generated.");
			}
			default -> {
				logger.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getTimeString(Duration time) {
		final ToLongFunction<Duration>[] precision = new ToLongFunction[] {
				value -> ((Duration) value).toMillis(),
				value -> ((Duration) value).toSeconds(),
				value -> ((Duration) value).toMinutes(),
				value -> ((Duration) value).toHours(),
				value -> ((Duration) value).toDays(),
		};
		final String[] unit = new String[] { "ms", "s", "m", "h", "d" };

		final LongToIntFunction getNumerOfDigits = value -> {
			int digits = 0;
			while (value > 0) {
				value /= 10;
				digits++;
			}
			return digits;
		};

		int i = 0;
		for (i = 0; getNumerOfDigits.applyAsInt(precision[i].applyAsLong(time)) > 5 && i < precision.length - 1; i++) {
			// find index, so no code is necesary
		}

		return String.format("%d %s", precision[i].applyAsLong(time), unit[i]);
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
		final String fontName = "Arial";

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
		rsaDigitsLabel.setFont(new Font(fontName, Font.ITALIC, 14));
		rsaDigits.add(rsaDigitsLabel);
		JSpinner cifras = new JSpinner(new SpinnerNumberModel(300, 100, 600, 1));
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
		final String fontName = "Arial";

		JPanel left = new JPanel();
		left.setBackground(Color.WHITE);
		left.setLayout(new BorderLayout());
		// Title
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.WHITE);
		JLabel title = new JLabel("Primalidad");
		title.setFont(new Font(fontName, Font.BOLD, 24));
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
		textArea.setFont(new Font(fontName, Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
		resultLabel.setFont(new Font(fontName, Font.BOLD, 14));
		row3.add(resultLabel);
		contentPanel.add(row3);
		left.add(contentPanel, BorderLayout.CENTER);

		JPanel right = new JPanel();
		right.setBackground(Color.GRAY);
		right.setLayout(new BorderLayout(0, 0));
		// Title
		JPanel titlePanel2 = new JPanel();
		titlePanel2.setBackground(Color.WHITE);
		JLabel title2 = new JLabel("RSA con ficheros");
		title2.setFont(new Font(fontName, Font.BOLD, 24));
		titlePanel2.add(title2);
		right.add(titlePanel2, BorderLayout.NORTH);
		// Content
		JPanel contentPanel2 = new JPanel();
		contentPanel2.setBackground(Color.WHITE);
		contentPanel2.setLayout(new GridLayout(3, 1));
		// Row 1
		JPanel row12 = new JPanel();
		row12.setPreferredSize(new Dimension(0, 0));
		row12.setLayout(new BorderLayout());
		// Title
		JLabel title12 = new JLabel("Entrada: ");
		title12.setAlignmentX(Component.CENTER_ALIGNMENT);
		title12.setFont(new Font(fontName, Font.BOLD, 14));
		row12.add(title12, BorderLayout.NORTH);
		row12.setPreferredSize(new Dimension(0, 0));
		row12.setBackground(Color.WHITE);
		JTextArea textArea2 = new JTextArea(7, 30);
		textArea2.setLineWrap(true);
		textArea2.setWrapStyleWord(true);
		textArea2.setEditable(true);
		textArea2.setFont(new Font(fontName, Font.PLAIN, 14));
		JScrollPane scrollPane2 = new JScrollPane(textArea2);
		scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		row12.add(scrollPane2, BorderLayout.CENTER);
		contentPanel2.add(row12);
		// Row 2
		JPanel row22 = new JPanel();
		row22.setBackground(Color.WHITE);
		row22.setLayout(new BorderLayout());
		// Title
		JLabel title22 = new JLabel("Salida: ");
		title22.setAlignmentX(Component.CENTER_ALIGNMENT);
		title22.setFont(new Font(fontName, Font.BOLD, 14));
		row22.add(title22, BorderLayout.NORTH);
		JTextArea textArea3 = new JTextArea(7, 30);
		textArea3.setLineWrap(true);
		textArea3.setWrapStyleWord(true);
		textArea3.setEditable(false);
		textArea3.setFont(new Font(fontName, Font.PLAIN, 14));
		JScrollPane scrollPane3 = new JScrollPane(textArea3);
		scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		row22.add(scrollPane3);
		contentPanel2.add(row22);
		// Row 3
		JPanel row32 = new JPanel();
		row32.setBackground(Color.WHITE);
		row32.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		buttons.setBackground(Color.WHITE);
		JButton encrypt = new JButton("Encriptar");
		encrypt.addActionListener(e -> {
			if (Objects.isNull(this.selectedFile)) {
				JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún fichero", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.timeTaken.setText("Calculando...");
			this.sendRequest(new Request(RequestCode.ENCRYPT_FILE, this, new Body(this.selectedFile)));
			this.selectedFile = null;
		});
		JButton decrypt = new JButton("Desencriptar");
		decrypt.addActionListener(e -> {
			if (Objects.isNull(this.selectedFile)) {
				JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún fichero", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.timeTaken.setText("Calculando...");
			this.sendRequest(new Request(RequestCode.DECRYPT_FILE, this, new Body(this.selectedFile)));
			this.selectedFile = null;
		});
		JButton load = new JButton("Cargar");
		load.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Selecciona un fichero");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				this.selectedFile = file;
				try {
					textArea2.setText(new String(Files.readAllBytes(file.toPath())));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton save = new JButton("Guardar");
		save.addActionListener(e -> {
			if (textArea3.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "No hay nada que guardar", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Selecciona un fichero");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				this.selectedFile = file;
				try {
					Files.write(file.toPath(), textArea3.getText().getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		buttons.add(encrypt);
		buttons.add(decrypt);
		buttons.add(load);
		buttons.add(save);
		row32.add(buttons, BorderLayout.NORTH);
		timeTaken = new JLabel("");
		timeTaken.setFont(new Font(fontName, Font.PLAIN, 14));
		row32.add(timeTaken, BorderLayout.CENTER);
		contentPanel2.add(row32);
		right.add(contentPanel2, BorderLayout.CENTER);

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
		load.addActionListener(e -> this.sendRequest(new Request(RequestCode.CREATE_DB, this)));
		JMenuItem mesu = new JMenuItem("Mesurament");
		mesu.addActionListener(e -> {
			this.sendRequest(new Request(RequestCode.GET_MESURAMENT, this));
			JOptionPane pane = new JOptionPane("Calculando...", JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
			mesuramentDialog = pane.createDialog("Ratio mesurament");
			mesuramentDialog.setModal(false);
			mesuramentDialog.setVisible(true);
		});
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

		return menuBar;
	}

}
