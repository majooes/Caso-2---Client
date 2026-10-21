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
    FIN_PARTIDA
}
