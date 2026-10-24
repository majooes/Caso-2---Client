package jungleattack.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jungleattack.util.Constantes;
import org.junit.jupiter.api.Test;

class TipoObjetoTest {

    @Test
    void buscar_devuelve_el_tipo_con_ese_nombre() {
        assertEquals(TipoObjeto.TAZA, TipoObjeto.buscar("TAZA"));
        assertEquals(TipoObjeto.BANDERA, TipoObjeto.buscar("BANDERA"));
    }

    @Test
    void buscar_devuelve_null_si_no_existe() {
        assertNull(TipoObjeto.buscar("ARBOL"));
        assertNull(TipoObjeto.buscar(null));
    }

    @Test
    void cada_tipo_sabe_su_imagen_y_su_tamano() {
        assertEquals(Constantes.IMAGEN_TAZA, TipoObjeto.TAZA.getImagen());
        assertEquals(Constantes.TAMANO_BANDERA, TipoObjeto.BANDERA.getTamano());
    }
}
