package master;

import controller.Controller;
import model.Model;
import request.Notify;
import request.Request;
import view.View;

import java.util.logging.Level;
import java.util.logging.Logger;

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
            case START, RESUME, RESTART, NEXT, STOP:
                this.controller.notifyRequest(request);
                break;
            case UPDATEDBOARD, CHANGEDTABLESIZE, HASFINISHED:
                this.model.notifyRequest(request);
                this.view.notifyRequest(request);
                break;
            case CHANGEDPIECE, DELETEDPIECE:
                this.model.notifyRequest(request);
                break;
            default:
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
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