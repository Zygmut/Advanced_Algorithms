package View;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import betterSwing.DirectionAndPosition;
import betterSwing.Section;
import betterSwing.Window;

public class View implements Notify {

    /**
     * The MVC hub of the view.
     */
    private MVC hub;
    /**
     * The window of the view.
     */
    private Window window;

    private int sizeTable;

    /**
     * This constructor creates a view with the MVC hub without any configuration
     *
     * @param mvc The MVC hub of the view.
     * @see MVC
     */
    public View(MVC mvc) {
        this.hub = mvc;
        this.window = new Window();
        this.loadContent();
    }

    /**
     * This constructor creates a view with the MVC hub and configures itself given
     * a config path.
     *
     * @param mvc        The MVC hub of the view.
     * @param configPath The path to its config.
     * @see MVC
     */
    public View(MVC mvc, String configPath) {
        this.hub = mvc;
        this.window = new Window(configPath);
        this.loadContent();
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

    /**
     * Loads all the view content.
     * 
     * @see #headerSection()
     * @see #mainSection()
     * @see #sideBarSection()
     * @see #footerSection()
     */
    private void loadContent() {
        this.window.addSection(this.headerSection(), DirectionAndPosition.POSITION_TOP, "Header");
        this.window.addSection(this.mainSection(), DirectionAndPosition.POSITION_CENTER, "MainContent");
        this.window.addSection(this.sideBarSection(), DirectionAndPosition.POSITION_RIGHT, "SideBar");
        this.window.addSection(this.footerSection(), DirectionAndPosition.POSITION_BOTTOM, "Footer");
    }

    /**
     * Creates and returns the header section of the view. This section is mainly
     * used for allowing the user to select the piece to play and the game mode.
     * 
     * @return The header section of the view.
     */
    private Section headerSection() {
        // TODO: Implement this method.
        Section header = new Section();
        JPanel headerContent = new JPanel();
        headerContent.setBackground(Color.RED);
        header.createFreeSection(headerContent);
        return header;
    }

    /**
     * Creates and returns the main section of the view. This section is mainly used
     * for showing the game board.
     * 
     * @return The main section of the view.
     */
    private Section mainSection() {
        // TODO: Implement this method.
        Section main = new Section();
        JPanel mainContent = new JPanel();
        mainContent.setBackground(Color.BLUE);
        main.createFreeSection(mainContent);
        return main;
    }

    /**
     * Creates and returns the side bar section of the view. This section is mainly
     * used for showing the stats of the game.
     * 
     * @return The side bar section of the view.
     */
    private Section sideBarSection() {
        // TODO: Implement this method.
        Section sideBar = new Section();
        JPanel sideBarContent = new JPanel();
        sideBarContent.setBackground(Color.GREEN);
        sideBar.createFreeSection(sideBarContent);
        return sideBar;
    }

    /**
     * Creates and returns the footer section of the view. This section is mainly
     * used for allowing the user to navigate through different views.
     * 
     * @return The footer section of the view.
     */
    private Section footerSection() {
        // TODO: Implement this method.
        Section footer = new Section();
        JPanel footerContent = new JPanel();
        JLabel tableSize = new JLabel("Tamaño del tablero: ");
        //Falta pasar por parámetro el tamaño del tablero inicial y el tamaño del tablero cuando se cambie
        SpinnerNumberModel size = new SpinnerNumberModel(8, 8, 20, 1);
        JSpinner tableSizeSpinner = new JSpinner(size);
        tableSizeSpinner.addChangeListener(e -> {
            this.sizeTable = (int) tableSizeSpinner.getValue();
            this.hub.notifyRequest(new Request(RequestCode.ChangedTableSize, this));
        });
        footerContent.add(tableSize);
        footerContent.add(tableSizeSpinner);
        footerContent.setBackground(Color.YELLOW);
        footer.createFreeSection(footerContent);
        return footer;
    }



    

    /**
     * Returns the window of the view.
     * 
     * @return The window of the view.
     */
    public Window getWindow() {
        return this.window;
    }

}
