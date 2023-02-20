/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc_esdeveniments;

import mvc_esdeveniments.control.Control;
import mvc_esdeveniments.model.Model;
import mvc_esdeveniments.vista.Vista.Vista;

/**
 *
 * @author mascport
 */
public class MVC_Esdeveniments implements PerEsdeveniments {

    private Model mod;    // Punter al Model del patró
    private Vista vis;    // Punter a la Vista del patró
    private Control con;  // punter al Control del patró

    /*
        Construcció de l'esquema MVC
     */
    private void inicio() {
        mod = new Model(this);
        con = null;
        vis = new Vista("Exemple de MVC + Patró Esdeveniments", this);
        vis.mostrar();
    }

    public static void main(String[] args) {
        (new MVC_Esdeveniments()).inicio();
    }

    /*
        Funció símple de la comunicació per Patró d'esdeveniments
     */
    @Override
    public void notificar(String s) {
        if (s.startsWith("Arrancar")) {
            if (con == null) {
                con = new Control(this);
                con.notificar(s);
            }
        } else if (s.startsWith("Parar")) {
            if (con != null) {
                con.notificar(s);
                con = null;
            }
        } else if (s.startsWith("Picat:")) {
            s = s.substring(s.indexOf(":") + 1);
            int x = Integer.parseInt(s.substring(0, s.indexOf(",")));
            int y = Integer.parseInt(s.substring(s.indexOf(",") + 1));
            mod.setXY(x, y);
        } else if (s.startsWith("Velocitat:")) {
            int v = Integer.parseInt(s.substring(s.indexOf(":") + 1));
            mod.setVel(v);
        }
    }

    /*
        Mètode public de retorn de la instància del model de dades
    */
    public Model getModel() {
        return mod;
    }
}
