package jungleattack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConstantesTest {

    @Test
    void hay_siete_especies_y_cada_una_tiene_su_icono() {
        assertEquals(7, Constantes.ESPECIES.length);
        for (String especie : Constantes.ESPECIES) {
            boolean tieneIcono = false;
            for (String icono : Constantes.IMAGENES_ICONOS) {
                if (icono.equals(especie.toLowerCase())) {
                    tieneIcono = true;
                }
            }
            assertTrue(tieneIcono, especie);
        }
    }

    @Test
    void el_mapa_tiene_doce_zonas() {
        assertEquals(12, Constantes.COLUMNAS_MAPA * Constantes.FILAS_MAPA);
    }

    @Test
    void el_codigo_de_partida_tiene_cuatro_caracteres() {
        assertEquals(4, Constantes.LONGITUD_CODIGO);
    }

    @Test
    void los_tiempos_de_dibujo_son_positivos() {
        assertTrue(Constantes.FPS > 0);
        assertTrue(Constantes.INTERVALO_SNAPSHOT_MS > 0);
    }
}
