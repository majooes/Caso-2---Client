package jungleattack.modelo;

/** Objeto del mapa que se dibuja: una taza de café o una bandera. */
public class ObjetoVista {

    private final TipoObjeto tipo;
    private final Punto posicion;

    public ObjetoVista(TipoObjeto tipo, Punto posicion) {
        this.tipo = tipo;
        this.posicion = posicion;
    }

    public TipoObjeto getTipo() { return tipo; }
    public Punto getPosicion() { return posicion; }
}
