/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc_esdeveniments.control;

import mvc_esdeveniments.MVC_Esdeveniments;
import mvc_esdeveniments.MeuError;
import mvc_esdeveniments.PerEsdeveniments;

/**
 *
 * @author mascport
 */
public class Control extends Thread implements PerEsdeveniments {

    private MVC_Esdeveniments prog;
    private boolean seguir;

    public Control(MVC_Esdeveniments p) {
        prog = p;
    }

    public void run() {
        seguir = true;
        while (seguir) {
            prog.getModel().notificar("IncGrau");
            espera(1000 / 25, 0);
        }
    }

    private void espera(long m, int n) {
        try {
            Thread.sleep(m, n);
        } catch (Exception e) {
            MeuError.informaError(e);
        }
    }

    @Override
    public void notificar(String s) {
        if (s.startsWith("Parar")) {
            seguir = false;
        } else if (s.startsWith("Arrancar")) {
            this.start();
        }
    }
}
