package jungleattack.modelo;

import jungleattack.util.Constantes;

/** Objetos del mapa que se dibujan. Cada tipo sabe qué imagen y qué tamaño usa. */
public enum TipoObjeto {
    TAZA(Constantes.IMAGEN_TAZA, Constantes.TAMANO_TAZA),
    BANDERA(Constantes.IMAGEN_BANDERA, Constantes.TAMANO_BANDERA);

    private final String imagen;
    private final int tamano;

    TipoObjeto(String imagen, int tamano) {
        this.imagen = imagen;
        this.tamano = tamano;
    }

    public String getImagen() {
        return imagen;
    }

    public int getTamano() {
        return tamano;
    }

    /** Devuelve el tipo con ese nombre, o null si no existe. */
    public static TipoObjeto buscar(String nombre) {
        for (TipoObjeto t : values()) {
            if (t.name().equals(nombre)) {
                return t;
            }
        }
        return null;
    }
}
