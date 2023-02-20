import Model.Model;
import View.View;
import Controller.Controller;

public class MVC {

    private Model model;
    private View view;
    private Controller controller;

    public MVC() {
        this.model = new Model();
        this.view = new View();
        this.controller = new Controller();
    }

    public MVC(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

}
