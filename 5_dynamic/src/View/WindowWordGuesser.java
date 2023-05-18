package View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowWordGuesser {

	private Window window;

	public WindowWordGuesser() {
		this.window = new Window(Config.VIEW_USER_MANUAL_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionUsage(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionUsage() {
		Section section = new Section();
		return section;
	}

	public void show() {
		this.window.start();
	}
}
