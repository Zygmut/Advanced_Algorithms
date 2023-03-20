package Master;

import Model.Model;
import Request.Notify;
import Request.Request;
import View.View;
import Controller.Controller;

import javax.swing.SwingUtilities;

public class MVC implements Notify {

    private Model model;
    private View view;
    private Controller controller;

    public MVC() {
        this.model = new Model(this);
        this.controller = new Controller(this);
        this.view = new View(this);
    }

    public MVC(String config_path) {
        this.model = new Model(this);
        this.controller = new Controller(this);
        this.view = new View(this, config_path);
    }

    public MVC(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> this.view.getWindow().start());
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Start:
                this.controller.notifyRequest(request);
                break;
            case UpdateBoard:
                this.model.notifyRequest(request);
                this.view.notifyRequest(request);
                break;
            case Error:
                System.out.println(request);
                System.exit(1);
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
    }

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

    public Controller getController() {
        return controller;
    }

}
