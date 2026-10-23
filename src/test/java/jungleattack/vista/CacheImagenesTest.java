package jungleattack.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import jungleattack.util.Constantes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheImagenesTest {

    @BeforeAll
    static void cargar() {
        CacheImagenes.cargarTodas();
    }

    /** True si la imagen tiene al menos un píxel que no es transparente. */
    private boolean tieneContenido(BufferedImage imagen) {
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                int alfa = (imagen.getRGB(x, y) >>> 24) & 0xFF;
                if (alfa > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    void cada_icono_se_carga_con_el_tamano_esperado_y_dibuja_algo() {
        for (String nombre : Constantes.IMAGENES_ICONOS) {
            BufferedImage imagen = CacheImagenes.obtener(nombre);

            assertNotNull(imagen, nombre);
            assertEquals(Constantes.TAMANO_IMAGEN, imagen.getWidth());
            assertTrue(tieneContenido(imagen), nombre);
        }
    }

    @Test
    void el_fondo_mide_lo_mismo_que_una_zona() {
        BufferedImage fondo = CacheImagenes.obtener(Constantes.IMAGEN_FONDO);

        assertNotNull(fondo);
        assertEquals(Constantes.ANCHO_ZONA, fondo.getWidth());
        assertEquals(Constantes.ALTO_ZONA, fondo.getHeight());
    }

    @Test
    void hay_un_icono_por_cada_especie() {
        for (String especie : Constantes.ESPECIES) {
            assertNotNull(CacheImagenes.obtener(especie.toLowerCase()), especie);
        }
    }

    @Test
    void una_imagen_que_no_existe_devuelve_null() {
        assertNull(CacheImagenes.obtener("dinosaurio"));
    }
}
