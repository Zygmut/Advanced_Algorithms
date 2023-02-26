package Controller;

import javax.swing.JButton;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Controller implements Notify {

    private MVC hub;
    private JButton[] buttons;

    public Controller(MVC mvc) { this.hub = mvc; }

    private void createButtons() {
        this.buttons = new JButton[5];
        buttons[0] = new JButton("Escalar");
        buttons[0].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Escalar_Product, this));
        });
        buttons[1] = new JButton("Mode NLogN");
        buttons[1].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Mode_O_nlogn, this));
        });
        buttons[2] = new JButton("Mode N");
        buttons[2].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Mode_O_n, this));
        });
        buttons[3] = new JButton("All");
        buttons[3].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.All_methods, this));
        });
        buttons[4] = new JButton("Stop");
        buttons[4].addActionListener(e -> {
            this.hub.notifyRequest(new Request(RequestCode.Stop_method, this));
        });

        this.hub.notifyRequest(new Request(RequestCode.Load_buttons, this));
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Create_buttons:
                this.createButtons();
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

    public JButton[] getButtons() {
        return buttons;
    }

}
