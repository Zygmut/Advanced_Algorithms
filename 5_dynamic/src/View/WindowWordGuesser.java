package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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
	private DefaultCategoryDataset datasetL;
	private DefaultCategoryDataset datasetB;
	private int index;
	private JFreeChart chartL;
	private JFreeChart chartB;

	public WindowWordGuesser(View view) {
		this.view = view;
		this.window = new Window(Config.VIEW_USER_MANUAL_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
		this.datasetL = new DefaultCategoryDataset();
		this.datasetB = new DefaultCategoryDataset();
		this.index = 0;
	}

	private void loadContent() {
		this.window.addSection(this.sectionUsage(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionUsage() {
		Section section = new Section();
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
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
			final String xAxisLabel = "Language";
			final String yAxisLabel = "Probability";
			chartL = this.createBarPlotPanel("Language Probability (Levenshtein)", xAxisLabel, yAxisLabel, datasetL);
			chartB = this.createBarPlotPanel("Language Probability (Bayes)", xAxisLabel, yAxisLabel, datasetB);

			JPanel aux = new JPanel();
			aux.setLayout(new GridLayout(1, 2));
			aux.add(new ChartPanel(chartL));
			aux.add(new ChartPanel(chartB));

			// Limpiar el panel y agregar el chartPanel al centro
			resultPanel.removeAll();
			resultPanel.add(aux, BorderLayout.CENTER);
			resultPanel.revalidate();
			resultPanel.repaint();
		});
		panel.add(resultPanel, BorderLayout.CENTER);

		return section;
	}

	private JFreeChart createBarPlotPanel(String title, String xAxisLabel, String yAxisLabel,
			DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(title,
				xAxisLabel,
				yAxisLabel,
				dataset,
				PlotOrientation.VERTICAL,
				false, true, false);

		for (int i = 0; i < 10; i++) {
			chart.getCategoryPlot().getRenderer().setSeriesPaint(i, Color.BLUE);
		}

		return chart;
	}

	public void addResult(String language, double distance) {
		// Devolvemos los lenguajes y sus probabilidades del View
		language = switch (language.toLowerCase()) {
			case "hr-custom" -> "Croatian";
			case "custom-pt" -> "Portuguese";
			case "en-custom" -> "English";
			case "it-custom" -> "Italian";
			case "es-custom" -> "Spanish";
			case "custom-de" -> "German";
			case "custom-ca" -> "Catalan";
			case "fr-custom" -> "French";
			case "custom-hu" -> "Hungarian";
			case "custom-da" -> "Danish";
			default -> "Unknown";
		};

		double probability = 1 / (distance + 1);
		datasetL.addValue(probability, language, language);
	}

	public int findMinValue(double[] probability) {
		double min = probability[0];
		for (int i = 0; i < probability.length; i++) {
			if (probability[i] < min) {
				min = probability[i];
				index = i;
			}
		}
		chartL.getCategoryPlot().getRenderer().setSeriesPaint(index, Color.RED);
		return index;
	}

	public void show() {
		this.window.start();
	}

	@SuppressWarnings("unchecked")
	public void addNaiveBayesResult(Object[] result) {
		// Object[0] -> labels
		// Object[1] -> probabilities
		String[] labels = getLabels((List<String>) result[0]);
		double[] probabilities = (double[]) result[1];

		for (int i = 0; i < labels.length; i++) {
			this.datasetB.addValue(probabilities[i], labels[i], labels[i]);
		}

		// Find the max value
		double max = probabilities[0];
		int aux = 0;
		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] > max) {
				max = probabilities[i];
				aux = i;
			}
		}

		chartB.getCategoryPlot().getRenderer().setSeriesPaint(aux, Color.RED);
	}

	private String[] getLabels(List<String> strings) {
		String[] labels = new String[strings.size()];
		for (int i = 0; i < strings.size(); i++) {
			labels[i] = toCommonLabel(strings.get(i));
		}
		return labels;
	}

	private String toCommonLabel(String label) {
		return switch (label) {
			case "ca" -> "Catalan";
			case "da" -> "Danish";
			case "de" -> "German";
			case "en" -> "English";
			case "es" -> "Spanish";
			case "fr" -> "French";
			case "hr" -> "Croatian";
			case "hu" -> "Hungarian";
			case "it" -> "Italian";
			case "pt" -> "Portuguese";
			default -> "Unknown";
		};
	}
}
