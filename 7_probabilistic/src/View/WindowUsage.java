package View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import betterSwing.Section;
import betterSwing.Window;
import betterSwing.utils.DirectionAndPosition;
import utils.Config;

public class WindowUsage {

	private Window window;

	public WindowUsage() {
		this.window = new Window(Config.VIEW_USER_MANUAL_WIN_CONFIG_PATH);
		this.window.initConfig();
		this.loadContent();
	}

	private void loadContent() {
		this.window.addSection(this.sectionUsage(), DirectionAndPosition.POSITION_CENTER, "Body");
	}

	private Section sectionUsage() {
		Section section = new Section();
		URL url = null;
		try {
			url = new URL(Config.USER_MANUAL_CONTENT_FILE_PATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (Objects.isNull(url)) {
			throw new RuntimeException("URL is null");
		}
		section.createSectionFromHTMLFile(url);
		return section;
	}

	public void show() {
		this.window.start();
	}

}
