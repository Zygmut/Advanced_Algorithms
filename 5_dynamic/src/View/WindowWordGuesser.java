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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

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
		inputPanel.add(detect, BorderLayout.EAST);
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout());
		JLabel resultLabel = new JLabel("Introduce un fragmento de texto o una sola palabra y te diré a que idioma pertenece", SwingConstants.CENTER);
		resultPanel.add(resultLabel, BorderLayout.CENTER);
		detect.addActionListener(e -> {
            String input = inputField.getText();

			Body body = new Body(input);
			Request request = new Request(RequestCode.GUESS_LANG, this, body);
			this.view.sendRequest(request);
            // Crear el dataset con las probabilidades
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String[] languages = {"ESPAÑOL", "INGLES", "FRANCES", "ALEMAN", "ITALIANO", "PORTUGUES"};
            for (String language : languages) {
                Random rd = new Random();
                double probability = rd.nextDouble();
                dataset.addValue(probability, language, language);
            }

            // Crear el gráfico
            JFreeChart chart = ChartFactory.createBarChart("Language Probability", "Language", "Probability", dataset,
                    PlotOrientation.VERTICAL, false, true, false);

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

	public void show() {
		this.window.start();
	}
}
