package jungleattack.modelo;

import jungleattack.util.Constantes;

/** Lo que el cliente sabe de un animal: solo datos para dibujar, sin lógica de juego. */
public class AnimalVista {

    private final int id;
    private final String especie;
    private final boolean esMio;
    private final boolean esBot;
    private int energia;
    private boolean seleccionado;
    private Punto posicionAnterior;
    private Punto posicionActual;

    public AnimalVista(int id, String especie, boolean esMio, boolean esBot,
                       int energia, Punto posicion) {
        this.id = id;
        this.especie = especie;
        this.esMio = esMio;
        this.esBot = esBot;
        this.energia = energia;
        // Al inicio no hay movimiento: anterior y actual son la misma
        this.posicionAnterior = posicion;
        this.posicionActual = posicion;
    }

    /** Guarda la posición vieja y recibe la nueva del servidor. */
    public void actualizarPosicion(Punto nueva) {
        posicionAnterior = posicionActual;
        posicionActual = nueva;
    }

    /** Posición entre los dos últimos snapshots. t va de 0 (anterior) a 1 (actual). */
    public Punto posicionInterpolada(double t) {
        double f = Math.max(0.0, Math.min(1.0, t));
        double x = posicionAnterior.getX() + (posicionActual.getX() - posicionAnterior.getX()) * f;
        double y = posicionAnterior.getY() + (posicionActual.getY() - posicionAnterior.getY()) * f;
        return new Punto(x, y);
    }

    /** True si el punto (x, y) cae dentro del radio de selección del animal. */
    public boolean contiene(double x, double y) {
        return posicionActual.distanciaA(new Punto(x, y)) <= Constantes.RADIO_SELECCION;
    }

    public int getId() { return id; }
    public String getEspecie() { return especie; }
    public boolean isEsMio() { return esMio; }
    public boolean isEsBot() { return esBot; }
    public int getEnergia() { return energia; }
    public void setEnergia(int energia) { this.energia = energia; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
    public Punto getPosicionAnterior() { return posicionAnterior; }
    public Punto getPosicionActual() { return posicionActual; }
}
