package Master;

import Model.Model;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import View.View;
import Controller.Controller;
import java.awt.EventQueue;

public class MVC implements Notify {

    private Model model;
    private View view;
    private Controller controller;

    public MVC() {
        this.model = new Model(this);
        this.controller = new Controller(this);
        this.view = new View(this);

        this.notifyRequest(new Request(RequestCode.Create_buttons, this));
        EventQueue.invokeLater(() -> {
            this.view.start();
        });
    }

    public MVC(String config_path) {
        this.model = new Model(this);
        this.controller = new Controller(this);
        this.view = new View(this, config_path);

        this.notifyRequest(new Request(RequestCode.Create_buttons, this));
        EventQueue.invokeLater(() -> {
            this.view.start();
        });
    }

    public MVC(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;

        this.notifyRequest(new Request(RequestCode.Create_buttons, this));
        EventQueue.invokeLater(() -> {
            this.view.start();
        });
    }

    @Override
    public void notifyRequest(Request request) {
        System.out.println("MVC received a " + request);
        switch (request.code) {
            case Set_batchSize:
            case All_methods:
            case Escalar_Product:
            case Mode_O_n:
            case Mode_O_nlogn:
            case Stop_method:
                this.model.notifyRequest(request);
                break;
            case Load_buttons:
            case New_data:
                this.view.notifyRequest(request);
                break;
            case Create_buttons:
                this.controller.notifyRequest(request);
            default:
                break;
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
