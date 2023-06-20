package View;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Service;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import mesurament.Mesurament;
import utils.Config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
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

	private JLabel loadingFeedback;
	private JLabel loadingLabel;

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
			}
			case GET_FACTORS -> {
				Result result = (Result) request.body.content;
				logger.log(Level.INFO, "{0}, done in {1}ms",
						new Object[] { (Map<BigInteger, BigInteger>) result.result(), result.time().toMillis() });
			}
			case GET_MESURAMENT -> {
				String result = (String) request.body.content;
				JOptionPane.showMessageDialog(null, result, "Mesurament ratio", JOptionPane.INFORMATION_MESSAGE);
				this.loadingFeedback.setIcon(null);
				this.loadingLabel.setText("");
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
		this.window.addSection(
				this.body(),
				DirectionAndPosition.POSITION_CENTER,
				"Body");
		this.window.addSection(
				this.footer(),
				DirectionAndPosition.POSITION_BOTTOM,
				"Footer");
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

		JButton getMesurament = new JButton("Mesurament");
		getMesurament.setSize(200, 100);
		getMesurament
				.addActionListener(e -> {
					this.sendRequest(new Request(RequestCode.GET_MESURAMENT, this));
					this.loadingLabel.setText("Calculando...");
					ImageIcon loading = new ImageIcon(Config.PATH_TO_LOADING_ASSET);
					loading.setImage(loading.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
					this.loadingFeedback.setIcon(loading);
				});
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(getMesurament);
		actionsPanel.add(Box.createVerticalStrut(15));
		sideBar.add(actionsPanel);

		// Loading panel
		JPanel loadingPanel = new JPanel();
		loadingPanel.setBackground(Color.WHITE);
		this.loadingLabel = new JLabel("");
		this.loadingFeedback = new JLabel();
		loadingPanel.add(this.loadingLabel);
		loadingPanel.add(this.loadingFeedback);
		sideBar.add(loadingPanel);
		return sideBar;
	}

	// TODO: Move this to the controller

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
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);

		// TODO: Make this better
		JPanel todo = new JPanel();
		todo.setBackground(Color.WHITE);
		JTextField numberField = new JTextField(30);
		JButton primeButton = new JButton("is Prime?");
		primeButton.setSize(100, 100);
		primeButton.addActionListener(e -> {
			String inputText = numberField.getText();
			if (!inputText.isEmpty()) {
				this.sendRequest(new Request(RequestCode.CHECK_PRIMALITY, this, new Body(new Object[] {
						PrimalityFunction.TRIAL_DIVISION,
						BigInteger.valueOf(Long.parseLong(numberField.getText())) })));
			}
		});
		JButton factorsButton = new JButton("get Factors");
		factorsButton.addActionListener(e -> {
			this.sendRequest(new Request(RequestCode.GET_FACTORS, this, new Body(numberField.getText())));
		});
		todo.add(numberField);
		todo.add(primeButton);
		todo.add(factorsButton);

		content.add(todo, BorderLayout.CENTER);
		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section footer = new Section();
		this.buttons = new JButton[2];
		this.buttons[0] = new JButton("Generar Clave Pública");
		this.buttons[1] = new JButton("Generar Clave Privada");
		Section butons = new Section();
		butons.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
		JLabel cifrasLabel = new JLabel("Cifras RSA: ");
		JSpinner cifras = new JSpinner(new SpinnerNumberModel(300, 100, 600, 1));

		JPanel footerPanel = new JPanel();
		footerPanel.add(butons.getPanel());
		footerPanel.add(cifrasLabel);
		footerPanel.add(cifras);
		footer.createFreeSection(footerPanel);
		return footer;
	}

	/**
	 * This method creates the menu bar of the view.
	 *
	 * @return JMenuBar The menu bar of the view.
	 */
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
		db.add(load);
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
