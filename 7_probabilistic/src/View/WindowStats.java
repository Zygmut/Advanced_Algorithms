package View;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import javax.swing.JPanel;
import utils.Config;

public class WindowStats {

	private Window window;

	public WindowStats() {
		this.window = new Window(Config.VIEW_STATS_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(
				this.sectionStats(),
				DirectionAndPosition.POSITION_CENTER,
				"Body");
	}

	private Section sectionStats() {
		Section section = new Section();
		section.createFreeSection(this.panelStats());
		return section;
	}

	private JPanel panelStats() {
		JPanel panel = new JPanel();
		// TODO
		return panel;
	}

	public void show() {
		this.window.start();
	}
}
