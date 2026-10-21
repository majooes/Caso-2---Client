package jungleattack.modelo;

/** Coordenada 2D inmutable. */
public class Punto {

    private final double x;
    private final double y;

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /** Distancia en línea recta hasta otro punto. */
    public double distanciaA(Punto otro) {
        return Math.hypot(x - otro.x, y - otro.y);
    }
}
