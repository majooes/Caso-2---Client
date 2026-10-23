package jungleattack.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jungleattack.controlador.ControladorJuego;
import jungleattack.modelo.ModeloCliente;
import jungleattack.red.ConexionServidor;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import jungleattack.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanelSeleccionTest {

    private ModeloCliente modelo;
    private PanelSeleccion panel;

    @BeforeEach
    void preparar() {
        modelo = new ModeloCliente();
        panel = new PanelSeleccion(modelo, new ControladorJuego(modelo, new ConexionServidor()));
        modelo.registrarObservador(panel);
    }

    private void llegaronLibres(String... especies) {
        JsonArray libres = new JsonArray();
        for (String e : especies) {
            libres.add(e);
        }
        JsonObject carga = new JsonObject();
        carga.add("disponibles", libres);
        modelo.procesarMensaje(new Mensaje(TipoMensaje.SELECCION_ANIMALES, carga));
    }

    @Test
    void tiene_un_boton_por_especie() {
        for (String especie : Constantes.ESPECIES) {
            assertTrue(panel.getBoton(especie) != null, especie);
        }
    }

    @Test
    void al_inicio_ninguna_especie_esta_habilitada() {
        assertFalse(panel.getBoton("JAGUAR").isEnabled());
    }

    @Test
    void solo_se_habilitan_las_especies_libres() {
        llegaronLibres("JAGUAR", "TUCAN");

        assertTrue(panel.getBoton("JAGUAR").isEnabled());
        assertTrue(panel.getBoton("TUCAN").isEnabled());
        assertFalse(panel.getBoton("RANA").isEnabled());
    }

    @Test
    void al_elegir_se_bloquean_todos_y_espera_al_rival() {
        llegaronLibres("JAGUAR", "TUCAN");

        panel.getBoton("JAGUAR").doClick();

        assertEquals("JAGUAR", modelo.getMiEspecie());
        assertFalse(panel.getBoton("TUCAN").isEnabled());
        assertEquals(Constantes.TEXTO_ESPERANDO_RIVAL, panel.getTextoEtiqueta());
    }

    @Test
    void si_el_servidor_rechaza_la_especie_se_puede_elegir_otra() {
        llegaronLibres("JAGUAR", "TUCAN");
        panel.getBoton("JAGUAR").doClick();
        JsonObject carga = new JsonObject();
        carga.addProperty("mensaje", "Especie ocupada");

        modelo.procesarMensaje(new Mensaje(TipoMensaje.ERROR, carga));

        assertTrue(panel.getBoton("TUCAN").isEnabled());
        assertEquals("Especie ocupada", panel.getTextoEtiqueta());
    }
}
