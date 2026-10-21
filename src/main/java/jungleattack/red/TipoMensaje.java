package jungleattack.red;

/** Tipos de mensaje que viajan entre cliente y servidor. */
public enum TipoMensaje {
    // Cliente -> Servidor
    CREAR_PARTIDA,
    UNIRSE_PARTIDA,
    ELEGIR_ANIMAL,
    MOVER_ANIMALES,
    ATACAR,
    VER_ZONA,

    // Servidor -> Cliente
    PARTIDA_CREADA,
    PARTIDA_UNIDA,
    ERROR,
    SELECCION_ANIMALES,
    PARTIDA_INICIADA,
    ACTUALIZACION_ESTADO,
    FIN_PARTIDA;

    /** Devuelve el tipo con ese nombre, o null si no existe (así no hace falta una excepción). */
    public static TipoMensaje buscar(String nombre) {
        for (TipoMensaje t : values()) {
            if (t.name().equals(nombre)) {
                return t;
            }
        }
        return null;
    }
}
