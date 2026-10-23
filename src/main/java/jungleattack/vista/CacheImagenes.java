package jungleattack.vista;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import jungleattack.util.Constantes;

/**
 * Guarda las imágenes ya dibujadas. Java no dibuja SVG por sí solo, así que cada SVG se
 * convierte una sola vez a BufferedImage al iniciar (no en cada cuadro del juego).
 */
public class CacheImagenes {

    private static final Map<String, BufferedImage> imagenes = new HashMap<>();

    private CacheImagenes() {
        // Solo métodos estáticos.
    }

    /** Carga el fondo y los íconos de animales, taza y bandera. */
    public static void cargarTodas() {
        cargar(Constantes.IMAGEN_FONDO, Constantes.ANCHO_ZONA, Constantes.ALTO_ZONA);
        for (String nombre : Constantes.IMAGENES_ICONOS) {
            cargar(nombre, Constantes.TAMANO_IMAGEN, Constantes.TAMANO_IMAGEN);
        }
    }

    /** Devuelve la imagen con ese nombre, o null si no se cargó. */
    public static BufferedImage obtener(String nombre) {
        return imagenes.get(nombre);
    }

    /** Convierte un SVG de recursos en imagen. Si falta o está dañado, no se guarda. */
    private static void cargar(String nombre, int ancho, int alto) {
        URL url = CacheImagenes.class.getResource(
                Constantes.RUTA_IMAGENES + nombre + Constantes.EXTENSION_IMAGEN);
        if (url == null) {
            return;
        }
        SVGDocument documento = new SVGLoader().load(url);
        if (documento == null) {
            return;
        }
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        documento.render(null, g, new ViewBox(0, 0, ancho, alto));
        g.dispose();
        imagenes.put(nombre, imagen);
    }
}
