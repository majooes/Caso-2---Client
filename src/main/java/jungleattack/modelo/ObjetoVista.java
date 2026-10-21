package jungleattack.modelo;

/** Objeto del mapa que se dibuja: una taza de café o una bandera. */
public class ObjetoVista {

    private final String tipo;
    private final Punto posicion;

    public ObjetoVista(String tipo, Punto posicion) {
        this.tipo = tipo;
        this.posicion = posicion;
    }

    public String getTipo() { return tipo; }
    public Punto getPosicion() { return posicion; }
}
