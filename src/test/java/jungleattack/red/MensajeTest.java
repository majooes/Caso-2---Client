package jungleattack.red;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensajeTest {

    @Test
    void ida_y_vuelta_conserva_tipo_y_carga() {
        JsonObject carga = new JsonObject();
        carga.addProperty("apodo", "Ana");
        Mensaje original = new Mensaje(TipoMensaje.CREAR_PARTIDA, carga);

        Mensaje copia = Mensaje.desdeJson(original.aJson());

        assertEquals(TipoMensaje.CREAR_PARTIDA, copia.getTipo());
        assertEquals("Ana", copia.getCarga().get("apodo").getAsString());
    }

    @Test
    void json_invalido_devuelve_null() {
        assertNull(Mensaje.desdeJson("esto no es json"));
        assertNull(Mensaje.desdeJson(null));
    }

    @Test
    void tipo_desconocido_devuelve_null() {
        assertNull(Mensaje.desdeJson("{\"type\":\"INVENTADO\",\"payload\":{}}"));
    }

    @Test
    void sin_carga_usa_objeto_vacio() {
        Mensaje m = Mensaje.desdeJson("{\"type\":\"ERROR\"}");
        assertNotNull(m.getCarga());
        assertEquals(0, m.getCarga().size());
    }
}
