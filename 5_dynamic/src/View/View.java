package View;

import Services.Comunication.Request.Request;
import Services.Service;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import Model.Language;

public class View implements Service {

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
	/**
	 * The text area of the view to display logs.
	 */
	private JTextArea textArea;
	/**
	 * The list of dictionaries of the view.
	 */
	private List<String> optionsAnalysisMode;
	/**
	 * The list of dictionaries of the view.
	 */
	private List<String> optionsDictionary;

	/**
	 * This constructor creates a view with the MVC hub without any configuration
	 *
	 * @param mvc The MVC hub of the view.
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
	 */
	public View(String configPath) {
		this.window = new Window(configPath);
		this.window.initConfig();
	}

	@Override
	public void start() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View started.");
		this.loadContent();
		this.window.start();
	}

	@Override
	public void stop() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "View stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
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

		JLabel analysisMode = new JLabel("Modo de análisis: ");
		analysisMode.setFont(new Font("Arial", Font.ITALIC, 14));
		this.optionsAnalysisMode = new ArrayList<>();
		JCheckBox statsMode = new JCheckBox("Aplicar estadísticas");
		statsMode.addActionListener(e -> {
			if (statsMode.isSelected()) {
				this.optionsAnalysisMode.add("stats");
			} else {
				this.optionsAnalysisMode.remove("stats");
			}
		});
		JCheckBox paralel = new JCheckBox("Paralelizar");
		paralel.addActionListener(e -> {
			if (paralel.isSelected()) {
				this.optionsAnalysisMode.add("paralel");
			} else {
				this.optionsAnalysisMode.remove("paralel");
			}
		});
		JCheckBox sorted = new JCheckBox("Ordenar");
		sorted.addActionListener(e -> {
			if (sorted.isSelected()) {
				this.optionsAnalysisMode.add("sorted");
			} else {
				this.optionsAnalysisMode.remove("sorted");
			}
		});

		actionsPanel.add(Box.createVerticalStrut(10));
		actionsPanel.add(analysisMode);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(statsMode);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(paralel);
		actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(sorted);
		actionsPanel.add(Box.createVerticalStrut(5));

		sideBar.add(actionsPanel);

		// Log panel
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		infoPanel.setLayout(new BorderLayout());
		this.textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText("Logs: \n");

		// Wrap a scrollpane around it.
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		infoPanel.add(scrollPane, BorderLayout.CENTER);
		sideBar.add(infoPanel);

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
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);

		String[] dictLanguages = Arrays.stream(Language.values())
				.map(Language::toString)
				.toArray(String[]::new);

		JPanel dictPanel = new JPanel();
		dictPanel.setBackground(Color.WHITE);
		dictPanel.setLayout(new GridLayout(4, 3));

		this.optionsDictionary = new ArrayList<>();
		for (int i = 0; i < dictLanguages.length; i++) {
			JButton dict = new JButton(dictLanguages[i]);
			dict.setBackground(Color.LIGHT_GRAY);
			dict.addActionListener(e -> {
				dict.setSelected(!dict.isSelected());
				if (dict.isSelected()) {
					dict.setBackground(Color.LIGHT_GRAY.darker());
					this.optionsDictionary.add(dict.getText());
				} else {
					this.optionsDictionary.remove(dict.getText());
					dict.setBackground(Color.LIGHT_GRAY);
				}
				System.out.println(this.optionsDictionary);
			});
			if (i == dictLanguages.length - 1) {
				JPanel spacingPanel = new JPanel();
				spacingPanel.setBackground(Color.WHITE);
				dictPanel.add(spacingPanel);
			}
			dictPanel.add(dict);
		}
		content.add(dictPanel, BorderLayout.CENTER);

		splitPane.setLeftComponent(content);
		Section body = new Section();
		body.createJSplitPaneSection(splitPane);
		return body;
	}

	private Section footer() {
		Section buttonSection = new Section();
		this.buttons = new JButton[4];

		this.buttons[0] = new JButton("");
		this.buttons[1] = new JButton("");
		this.buttons[2] = new JButton("");
		this.buttons[3] = new JButton("Inciar");

		buttonSection.createButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
		return buttonSection;
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
		alg.addActionListener(e -> {
			// TODO
		});
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
