/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc_esdeveniments.model;

import mvc_esdeveniments.MVC_Esdeveniments;
import mvc_esdeveniments.PerEsdeveniments;

/**
 *
 * @author mascport
 */
public class Model implements PerEsdeveniments {

    private MVC_Esdeveniments prog;
    private int x;
    private int y;
    private double grau;
    private final int radi = 50;
    private int vel;
    private int masa;

    public Model(MVC_Esdeveniments p) {
        prog = p;
        x = y = 0;
        grau = 0.0;
        vel = 7;
        masa = 18;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void incGrau() {
        double inc = (Math.PI / 180.0) * vel;
        grau += inc;
        if (grau > (2.0 * Math.PI)) {
            grau = 0.0;
        }
    }

    public int getRadi() {
        return radi;
    }

    public double getGrau() {
        return grau;
    }

    public int getVel() {
        return vel;
    }

    public void setVel(int v) {
        vel = v;
    }

    public int getMasa() {
        return masa;
    }

    @Override
    public void notificar(String s) {
        if (s.startsWith("IncGrau")) {
            this.incGrau();
        }
    }
}
