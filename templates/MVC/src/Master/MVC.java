package Master;

import Model.Model;
import Request.Notify;
import Request.Request;
import View.View;
import Controller.Controller;

public class MVC implements Notify {

    private Model model;
    private View view;
    private Controller controller;

    public MVC() {
        this.model = new Model(this);
        this.view = new View(this);
        this.controller = new Controller(this);
    }

    public MVC(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    @Override
    public void notifyRequest(Request request) {
        switch(request.code){
            case None:
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
