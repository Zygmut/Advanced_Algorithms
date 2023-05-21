package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowWordGuesser {

	private Window window;
	private View view;

	public WindowWordGuesser(View view) {
		this.view = view;
		this.window = new Window(Config.VIEW_USER_MANUAL_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionUsage(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionUsage() {
		Section section = new Section();
		JPanel panel = new JPanel();
		section.createFreeSection(panel);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Word Guesser", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
		panel.add(titleLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		JLabel inputLabel = new JLabel("Input text:", SwingConstants.CENTER);
		JTextField inputField = new JTextField();
		inputPanel.add(inputLabel, BorderLayout.NORTH);
		inputPanel.add(inputField, BorderLayout.CENTER);
		panel.add(inputPanel, BorderLayout.SOUTH);
		JButton detect = new JButton("Detect Language");
		detect.setBounds(new Rectangle(new Dimension(10, 10)));
		// Log panel

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		// Wrap a scrollpane around it.
		rightPanel.add(detect, BorderLayout.SOUTH);
		panel.add(rightPanel, BorderLayout.EAST);
		detect.addActionListener(e -> {
			String input = inputField.getText();
			System.out.println("La palabra que buscamos es: " + input);
			Body body = new Body(input);
			this.view.sendRequest(new Request(RequestCode.GUESS_LANG, this, body));
			// Si input existe, se muestra el resultado
			JPanel resultPanel = new JPanel();
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			String[] languages = { "ESPAÑOL", "INGLES", "FRANCES", "ALEMAN", "ITALIANO", "PORTUGUES" };
			for (String language : languages) {
				Random rd = new Random();
				double probability = rd.nextDouble(1);
				dataset.addValue(probability, language, language);
			}
			// Create chart
			JFreeChart chart = ChartFactory.createBarChart("Probabilidad de idioma", "Idioma", "Probabilidad", dataset,
					PlotOrientation.VERTICAL, false, true, false);
			ChartFrame frame = new ChartFrame("Language Probability", chart);
			frame.pack();
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			resultPanel.add(frame);
			panel.add(resultPanel, BorderLayout.CENTER);
		});

		return section;
	}

	public void show() {
		this.window.start();
	}
}
