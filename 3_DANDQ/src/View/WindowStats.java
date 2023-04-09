package View;

import java.awt.Color;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowStats {

	private Window window;
	private String[] labels;
	private int[] values;
	private Color[] color;

	public WindowStats(String[] labels, int[] values, Color[] color) {
		this.labels = labels;
		this.values = values;
		this.color = color;
		this.window = new Window(Config.CONFIG_PATH_TO_STATS_WINDOW);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionStats(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionStats() {
		// TODO: #37 Create a section with the stats.
		Section section = new Section();
		section.createHistogramChart(labels, values, color);
		return section;
	}

	public void show() {
		this.window.start();
	}

}
