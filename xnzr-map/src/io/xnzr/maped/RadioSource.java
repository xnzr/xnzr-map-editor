package io.xnzr.maped;

/**
 * Model for radio source
 */
public class RadioSource {
    public RadioSource(double x, double y) {
        this.x = x;
        this.y = y;
    }
    private double x;
    private double y;

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
