import java.awt.Dimension;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Factor {

	private static ChartPanel createChartPanel(XYSeriesCollection dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Execution time", "Number of digits", "Time (hour)",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		return new ChartPanel(chart);
	}

	private static XYSeriesCollection createDataset(long[] vals) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Execution time");
		XYSeries linear = new XYSeries("Regression line");
		for (int i = 0; i < vals.length; i++) {
			series.add(i + 1, vals[i]);
		}

		// Calculate linear regression
		double n = vals.length;
		double sumX = n * (n + 1) / 2;
		double sumY = 0;
		double sumXY = 0;
		double sumXX = 0;
		for (int i = 0; i < vals.length; i++) {
			sumY += vals[i];
			sumXY += (i + 1) * vals[i];
			sumXX += (i + 1) * (i + 1);
		}
		double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
		double intercept = (sumY - slope * sumX) / n;
		for (int i = 0; i < vals.length; i++) {
			linear.add(i + 1, slope * (i + 1) + intercept);
		}

		// Calculate R^2
		double mean = sumY / n;
		double ssTot = 0;
		double ssRes = 0;
		for (int i = 0; i < vals.length; i++) {
			ssTot += Math.pow(vals[i] - mean, 2);
			ssRes += Math.pow(vals[i] - (slope * (i + 1) + intercept), 2);
		}
		double rSquared = 1 - ssRes / ssTot;
		System.out.println("R^2 = " + rSquared);

		dataset.addSeries(series);
		dataset.addSeries(linear);
		return dataset;
	}

	private static void displayChart(XYSeriesCollection dataset) {
		ChartPanel chartPanel = createChartPanel(dataset);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		JFrame frame = new JFrame("Chart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		final int TOP_NUM_OF_DIGITS = 2000;
		long[] numbers = new long[TOP_NUM_OF_DIGITS];
		for (int i = 1; i <= TOP_NUM_OF_DIGITS; i++) {
			numbers[i - 1] = getEstimatedTime(i).toHours();
		}
		// saveToDB(numbers);
		displayChart(createDataset(numbers));
	}

	private static void saveToDB(long[] numbers) {
		final String DB_NAME = "jdbc:sqlite:db.sqlite";
		try (Connection conn = DriverManager.getConnection(DB_NAME);
				Statement stmt = conn.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS expected_hours");
			stmt.execute("CREATE TABLE expected_hours (id INTEGER PRIMARY KEY, time INTEGER)");
			// Create a single string with all the values
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < numbers.length; i++) {
				sb.append("(" + (i + 1) + "," + numbers[i] + "),");
			}
			sb.deleteCharAt(sb.length() - 1);
			stmt.execute("INSERT INTO expected_hours VALUES " + sb.toString());
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// https://planetcalc.com/9023/?xy=5%2010%0A7%2023%0A8%2061%0A10%20165%0A11%20428%0A14%2016596%0A15%2037864&interpolate=10%2020%2030%2040
	private static Duration getEstimatedTime(int length) {
		final BigInteger x = BigInteger.valueOf(length);
		BigInteger numerator1 = new BigInteger("5939");
		BigInteger denominator1 = new BigInteger("907200");
		BigInteger term1 = numerator1.multiply(x.pow(6)).divide(denominator1);

		BigInteger numerator2 = new BigInteger("409711");
		BigInteger denominator2 = new BigInteger("181440");
		BigInteger term2 = numerator2.multiply(x.pow(5)).divide(denominator2);

		BigInteger numerator3 = new BigInteger("-17477987");
		BigInteger denominator3 = new BigInteger("181440");
		BigInteger term3 = numerator3.multiply(x.pow(4)).divide(denominator3);

		BigInteger numerator4 = new BigInteger("40090411");
		BigInteger denominator4 = new BigInteger("25920");
		BigInteger term4 = numerator4.multiply(x.pow(3)).divide(denominator4);

		BigInteger numerator5 = new BigInteger("-2739635491");
		BigInteger denominator5 = new BigInteger("226800");
		BigInteger term5 = numerator5.multiply(x.pow(2)).divide(denominator5);

		BigInteger numerator6 = new BigInteger("2092430983");
		BigInteger denominator6 = new BigInteger("45360");
		BigInteger term6 = numerator6.multiply(x).divide(denominator6);

		BigInteger numerator7 = new BigInteger("-3722729");
		BigInteger denominator7 = new BigInteger("54");
		BigInteger term7 = numerator7.divide(denominator7);

		BigInteger result = term1.add(term2).add(term3).add(term4)
				.add(term5).add(term6).add(term7);

		return Duration.ofMillis(result.longValue());
	}
}