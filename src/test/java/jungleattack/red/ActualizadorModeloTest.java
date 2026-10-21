package jungleattack.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import jungleattack.modelo.ModeloCliente;
import org.junit.jupiter.api.Test;

class ActualizadorModeloTest {

    @Test
    void run_aplica_el_mensaje_al_modelo() {
        ModeloCliente modelo = new ModeloCliente();
        JsonObject carga = new JsonObject();
        carga.addProperty("codigo", "B3X9");

        new ActualizadorModelo(modelo, new Mensaje(TipoMensaje.PARTIDA_CREADA, carga)).run();

        assertEquals("B3X9", modelo.getCodigoPartida());
    }
}
