package jungleattack.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalVistaTest {

    @Test
    void interpolacion_a_la_mitad_queda_en_el_medio() {
        AnimalVista a = new AnimalVista(1, "JAGUAR", true, false, 20, new Punto(0, 0));
        a.actualizarPosicion(new Punto(100, 50));

        Punto p = a.posicionInterpolada(0.5);

        assertEquals(50.0, p.getX(), 0.001);
        assertEquals(25.0, p.getY(), 0.001);
    }

    @Test
    void interpolacion_limita_t_entre_0_y_1() {
        AnimalVista a = new AnimalVista(1, "JAGUAR", true, false, 20, new Punto(0, 0));
        a.actualizarPosicion(new Punto(10, 10));

        assertEquals(10.0, a.posicionInterpolada(5.0).getX(), 0.001);
        assertEquals(0.0, a.posicionInterpolada(-2.0).getX(), 0.001);
    }

    @Test
    void contiene_depende_del_radio_de_seleccion() {
        AnimalVista a = new AnimalVista(1, "TUCAN", true, false, 20, new Punto(100, 100));

        assertTrue(a.contiene(105, 105));
        assertFalse(a.contiene(300, 300));
    }
}
