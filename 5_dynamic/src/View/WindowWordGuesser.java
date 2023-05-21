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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtils;

import Services.Service;
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
	private DefaultCategoryDataset dataset;
	private int index;
	private JFreeChart chart;

	public WindowWordGuesser(View view) {
		this.view = view;
		this.window = new Window(Config.VIEW_USER_MANUAL_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
		this.dataset = new DefaultCategoryDataset();
		this.index = 0;
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
		inputPanel.add(detect, BorderLayout.EAST);
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout());
		JLabel resultLabel = new JLabel(
				"Introduce un fragmento de texto o una sola palabra y te diré a que idioma pertenece",
				SwingConstants.CENTER);
		resultPanel.add(resultLabel, BorderLayout.CENTER);
		detect.addActionListener(e -> {
			String input = inputField.getText();
			Body body = new Body(input);
			Request request = new Request(RequestCode.GUESS_LANG, this, body);
			this.view.sendRequest(request);

			// Crear el gráfico
			chart = ChartFactory.createBarChart("Language Probability", "Language", "Probability", dataset,
					PlotOrientation.VERTICAL, false, true, false);
			for (int i = 0; i < 10; i++) {
				chart.getCategoryPlot().getRenderer().setSeriesPaint(i, Color.BLUE);
			}

			// Crear el panel del gráfico
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new Dimension(400, 300));
			// Limpiar el panel y agregar el chartPanel al centro
			resultPanel.removeAll();
			resultPanel.add(chartPanel, BorderLayout.CENTER);
			resultPanel.revalidate();
			resultPanel.repaint();
		});
		panel.add(resultPanel, BorderLayout.CENTER);
		return section;
	}

	public void addResult(String language, double distance) {
		// Devolvemos los lenguajes y sus probabilidades del View
		switch (language) {
			case "HU-CUSTOM":
				language = "Hungarian";
				break;
			case "HR-CUSTOM":
				language = "Croatian";
				break;
			case "EN-CUSTOM":
				language = "English";
				break;
			case "CUSTOM-ES":
				language = "Spanish";
				break;
			case "CUSTOM-CA":
				language = "Catalan";
				break;
			case "CUSTOM-FR":
				language = "French";
				break;
			case "IT-CUSTOM":
				language = "Italian";
				break;
			case "DE-CUSTOM":
				language = "German";
				break;
			case "PT-CUSTOM":
				language = "Portuguese";
				break;
			case "CUSTOM-DA":
				language = "Danish";
				break;
		}
		double probability = 1 / (distance + 1);
		dataset.addValue(probability, language, language);
	}

	public int findMinValue(String[] language, double[] probability) {
		double min = probability[0];
		for (int i = 0; i < probability.length; i++) {
			if (probability[i] < min) {
				min = probability[i];
				index = i;
			}
		}
		System.out.println("Index min " + index);
		chart.getCategoryPlot().getRenderer().setSeriesPaint(index, Color.RED);
		return index;
	}

	public void show() {
		this.window.start();
	}
}
