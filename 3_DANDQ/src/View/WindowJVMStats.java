package View;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowJVMStats {

	private Window window;
	private ArrayList<JVMStatsData> memoryUsed;
	private ArrayList<JVMStatsData> memoryFree;
	private ArrayList<JVMStatsData> memoryTotal;
	private static final int REFRESH_INTERVAL = 1000; // milliseconds
	private static final int DELAY = 0; // milliseconds

	public WindowJVMStats() {
		this.window = new Window(Config.CONFIG_PATH_TO_STATS_WINDOW);
		this.window.initConfig();
		this.memoryFree = new ArrayList<>();
		this.memoryUsed = new ArrayList<>();
		this.memoryTotal = new ArrayList<>();
	}

	private void calcStats() {
		Runtime runtime = Runtime.getRuntime();
		long memoryFree = runtime.freeMemory();
		long memoryTotal = runtime.totalMemory();
		LocalDateTime currentDateTime = LocalDateTime.now();
		Function<Long, Long> toMB = n -> n / 1024 / 1024;
		this.memoryUsed.add(new JVMStatsData(currentDateTime, toMB.apply(memoryTotal - memoryFree)));
		this.memoryFree.add(new JVMStatsData(currentDateTime, toMB.apply(memoryFree)));
		this.memoryTotal.add(new JVMStatsData(currentDateTime, toMB.apply(memoryTotal)));
	}

	public void start() {
		this.calcStats();
		Section section = new Section();
		// Create the dataset for a line chart
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries series = new TimeSeries("Memoria usada");
		TimeSeries series2 = new TimeSeries("Memoria libre");
		TimeSeries series3 = new TimeSeries("Memoria total");

		for (int i = 0; i < this.memoryUsed.size(); i++) {
			series.add(
					new Second(this.memoryUsed.get(i).time().getSecond(), this.memoryUsed.get(i).time().getMinute(),
							this.memoryUsed.get(i).time().getHour(), this.memoryUsed.get(i).time().getDayOfMonth(),
							this.memoryUsed.get(i).time().getMonthValue(), this.memoryUsed.get(i).time().getYear()),
					this.memoryUsed.get(i).data());
			series2.add(
					new Second(this.memoryFree.get(i).time().getSecond(), this.memoryFree.get(i).time().getMinute(),
							this.memoryFree.get(i).time().getHour(), this.memoryFree.get(i).time().getDayOfMonth(),
							this.memoryFree.get(i).time().getMonthValue(), this.memoryFree.get(i).time().getYear()),
					this.memoryFree.get(i).data());
			series3.add(
					new Second(this.memoryTotal.get(i).time().getSecond(), this.memoryTotal.get(i).time().getMinute(),
							this.memoryTotal.get(i).time().getHour(), this.memoryTotal.get(i).time().getDayOfMonth(),
							this.memoryTotal.get(i).time().getMonthValue(), this.memoryTotal.get(i).time().getYear()),
					this.memoryTotal.get(i).data());
		}
		dataset.addSeries(series);
		dataset.addSeries(series2);
		dataset.addSeries(series3);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Estadísticas JVM (Java Virtual Machine)", // chart title
				"Tiempo", // x axis label
				"Espacio (MB)", // y axis label
				dataset // data
		);

		ChartPanel chartPanel = new ChartPanel(chart);
		section.createFreeSection(chartPanel);
		this.window.addSection(section, DirectionAndPosition.POSITION_CENTER, "Chart");
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				calcStats();
				series.addOrUpdate(
						new Second(memoryUsed.get(memoryUsed.size() - 1).time().getSecond(),
								memoryUsed.get(memoryUsed.size() - 1).time().getMinute(),
								memoryUsed.get(memoryUsed.size() - 1).time().getHour(),
								memoryUsed.get(memoryUsed.size() - 1).time().getDayOfMonth(),
								memoryUsed.get(memoryUsed.size() - 1).time().getMonthValue(),
								memoryUsed.get(memoryUsed.size() - 1).time().getYear()),
						memoryUsed.get(memoryUsed.size() - 1).data());
				series2.addOrUpdate(
						new Second(memoryFree.get(memoryFree.size() - 1).time().getSecond(),
								memoryFree.get(memoryFree.size() - 1).time().getMinute(),
								memoryFree.get(memoryFree.size() - 1).time().getHour(),
								memoryFree.get(memoryFree.size() - 1).time().getDayOfMonth(),
								memoryFree.get(memoryFree.size() - 1).time().getMonthValue(),
								memoryFree.get(memoryFree.size() - 1).time().getYear()),
						memoryFree.get(memoryFree.size() - 1).data());
				series3.addOrUpdate(
						new Second(memoryTotal.get(memoryTotal.size() - 1).time().getSecond(),
								memoryTotal.get(memoryTotal.size() - 1).time().getMinute(),
								memoryTotal.get(memoryTotal.size() - 1).time().getHour(),
								memoryTotal.get(memoryTotal.size() - 1).time().getDayOfMonth(),
								memoryTotal.get(memoryTotal.size() - 1).time().getMonthValue(),
								memoryTotal.get(memoryTotal.size() - 1).time().getYear()),
						memoryTotal.get(memoryTotal.size() - 1).data());
			}
		}, DELAY, REFRESH_INTERVAL);
	}

	public void show() {
		this.window.start();
	}

	private record JVMStatsData(LocalDateTime time, long data) {
	}

}
