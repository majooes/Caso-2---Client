package jungleattack.util;

import java.awt.Color;

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
    public static final double UMBRAL_ARRASTRE = 5.0;

    //  Mapa (debe coincidir con el servidor) 
    public static final int COLUMNAS_MAPA = 4;
    public static final int FILAS_MAPA = 3;
    public static final int ANCHO_ZONA = 800;
    public static final int ALTO_ZONA = 600;

    //  Imágenes SVG 
    public static final String EXTENSION_IMAGEN = ".svg";
    public static final int TAMANO_IMAGEN = 128;
    public static final String IMAGEN_FONDO = "fondo";
    public static final String IMAGEN_TAZA = "taza";
    public static final String IMAGEN_BANDERA = "bandera";
    public static final String[] ESPECIES = {
        "JAGUAR", "PEREZOSO", "TUCAN", "LAPA", "MONO", "TAPIR", "RANA"
    };
    public static final String[] IMAGENES_ICONOS = {
        "jaguar", "perezoso", "tucan", "lapa", "mono", "tapir", "rana", "taza", "bandera"
    };

    //  Textos 
    public static final String TEXTO_CONEXION_PERDIDA = "Se perdió la conexión con el servidor";
    public static final String TEXTO_SIN_SERVIDOR = "No se pudo conectar con el servidor";
    public static final String TITULO_VENTANA = "Jungle Attack";
    public static final String TEXTO_APODO = "Apodo:";
    public static final String TEXTO_CODIGO = "Código de la partida:";
    public static final String TEXTO_CREAR = "Crear partida";
    public static final String TEXTO_UNIRSE = "Unirse a partida";
    public static final String TEXTO_APODO_VACIO = "Escribe un apodo para jugar";
    public static final String TEXTO_CODIGO_INVALIDO = "El código tiene 4 letras o números";
    public static final String TEXTO_ESPERANDO_JUGADOR = "Código de la partida: %s. Esperando al segundo jugador...";
    public static final String TEXTO_ELEGIR_ANIMAL = "Elige tu animal";
    public static final String TEXTO_ESPERANDO_RIVAL = "Esperando al otro jugador...";
    public static final int LONGITUD_CODIGO = 4;
    public static final int TAMANO_ICONO_BOTON = 64;
    public static final Color COLOR_ERROR = new Color(190, 30, 30);
    public static final Color COLOR_TEXTO = new Color(30, 30, 30);

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

    //  Claves de los mensajes que envía el cliente 
    public static final String JSON_APODO = "apodo";
    public static final String JSON_ANIMAL_IDS = "animalIds";
    public static final String JSON_DESTINO_X = "targetX";
    public static final String JSON_DESTINO_Y = "targetY";
    public static final String JSON_OBJETIVO_ID = "objetivoId";
}
