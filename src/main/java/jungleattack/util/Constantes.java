package jungleattack.util;

/** Valores fijos del cliente. Solo atributos static final. */
public class Constantes {

    //  Conexión 
    public static final String HOST = "localhost";
    public static final int PUERTO = 5000;

    //  Ventana y dibujo 
    public static final int FPS = 30;
    public static final int ANCHO_VENTANA = 1000;
    public static final int ALTO_VENTANA = 700;
    public static final double RADIO_SELECCION = 20.0;
    public static final int ALTO_BARRA_ENERGIA = 5;
    public static final String RUTA_IMAGENES = "/imagenes/";

    //  Textos 
    public static final String TEXTO_CONEXION_PERDIDA = "Se perdió la conexión con el servidor";

    //  Claves del JSON (deben coincidir con las del servidor) 
    public static final String JSON_TIPO = "type";
    public static final String JSON_CARGA = "payload";
    public static final String JSON_CODIGO = "codigo";
    public static final String JSON_MENSAJE = "mensaje";
    public static final String JSON_DISPONIBLES = "disponibles";
    public static final String JSON_ZONA = "zona";
    public static final String JSON_ZONAS_CON_ANIMALES = "zonasConAnimales";
    public static final String JSON_CAPACIDAD_DANIO = "capacidadDanio";
    public static final String JSON_TAZAS = "tazasConsumidas";
    public static final String JSON_BOTS_ELIMINADOS = "botsEliminados";
    public static final String JSON_ANIMALES = "animales";
    public static final String JSON_OBJETOS = "objetos";
    public static final String JSON_ID = "id";
    public static final String JSON_ESPECIE = "especie";
    public static final String JSON_ES_MIO = "esMio";
    public static final String JSON_ES_BOT = "esBot";
    public static final String JSON_ENERGIA = "energia";
    public static final String JSON_X = "x";
    public static final String JSON_Y = "y";
    public static final String JSON_TIPO_OBJETO = "tipo";
    public static final String JSON_ANIMALES_PERDIDOS_A = "animalesPerdidosA";
    public static final String JSON_ANIMALES_PERDIDOS_B = "animalesPerdidosB";
    public static final String JSON_TAZAS_A = "tazasA";
    public static final String JSON_TAZAS_B = "tazasB";
    public static final String JSON_APODO_A = "apodoA";
    public static final String JSON_APODO_B = "apodoB";
    public static final String JSON_DURACION_MS = "duracionMs";
    public static final String JSON_GANADOR = "ganador";

}
