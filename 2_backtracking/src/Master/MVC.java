package Master;

import Controller.Controller;
import Model.Model;
import Request.Notify;
import Request.Request;
import View.View;

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
            case START, RESUME, NEXT, STOP -> {
                this.controller.notifyRequest(request);
            }
            case RESTART -> {
                this.model.notifyRequest(request);
                this.controller.notifyRequest(request);
                this.view.notifyRequest(request);
            }
            case UPDATEDBOARD, CHANGEDTABLESIZE, HASFINISHED -> {
                this.model.notifyRequest(request);
                this.view.notifyRequest(request);
            }
            case CHANGEDPIECE, DELETEDPIECE, UPDATEMEMORYBOARD -> {
                this.model.notifyRequest(request);
            }
            default -> {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
            }
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