package jungleattack.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TipoMensajeTest {

    @Test
    void buscar_devuelve_el_tipo_con_ese_nombre() {
        assertEquals(TipoMensaje.ATACAR, TipoMensaje.buscar("ATACAR"));
    }

    @Test
    void buscar_devuelve_null_si_no_existe() {
        assertNull(TipoMensaje.buscar("INVENTADO"));
        assertNull(TipoMensaje.buscar(null));
    }
}
