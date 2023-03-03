package Master;

import Model.Model;
import Request.Notify;
import Request.Request;
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

    public void show(){
        EventQueue.invokeLater(() -> {
            this.view.start();
        });
    }

    @Override
    public void notifyRequest(Request request) {
        System.out.println("MVC received a " + request);
        switch (request.code) {
            case All_methods:
            case Escalar_Product:
            case Mode_O_n:
            case Mode_O_nlogn:
            case Resume_execution:
            case Pause_execution:
                this.controller.notifyRequest(request);
                break;
            case New_data:
                this.model.notifyRequest(request);
                break;
            case Show_data:
                this.view.notifyRequest(request);
                break;
            case Reset_data:
                this.controller.notifyRequest(request);
                this.model.notifyRequest(request);
                break;
            case Set_batchSize:
                System.out.println("MVC: TODO");
                break;
            case Time_To_Nanoseconds:
            case Time_To_Milliseconds:
            case Time_To_Seconds:
            case Time_To_Minutes:
            case Time_To_Hours:
            case Time_To_Days:
                this.model.notifyRequest(request);
                break;
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
