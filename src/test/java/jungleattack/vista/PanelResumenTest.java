package jungleattack.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jungleattack.modelo.ResumenPartida;
import org.junit.jupiter.api.Test;

class PanelResumenTest {

    @Test
    void formatea_la_duracion_en_minutos_y_segundos() {
        assertEquals("1:30", PanelResumen.formatearDuracion(90000));
        assertEquals("0:05", PanelResumen.formatearDuracion(5000));
        assertEquals("12:00", PanelResumen.formatearDuracion(720000));
    }

    @Test
    void el_resumen_muestra_ganador_y_estadisticas() {
        PanelResumen panel = new PanelResumen();
        ResumenPartida r = new ResumenPartida(4, 3, 5, 2, 1, 90000, "Ana", "Luis", "Ana");

        panel.mostrarResumen(r);

        String texto = panel.getTexto();
        assertTrue(texto.contains("Ganador: Ana"));
        assertTrue(texto.contains("Bots eliminados: 4"));
        assertTrue(texto.contains("Animales eliminados de Luis: 5"));
        assertTrue(texto.contains("Tazas de café de Ana: 2"));
        assertTrue(texto.contains("Duración: 1:30"));
    }

    @Test
    void un_resumen_nulo_no_cambia_el_texto() {
        PanelResumen panel = new PanelResumen();

        panel.mostrarResumen(null);

        assertEquals("", panel.getTexto());
    }
}
