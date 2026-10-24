package jungleattack.modelo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModeloClienteTest {

    private ModeloCliente modelo;

    @BeforeEach
    void preparar() {
        modelo = new ModeloCliente();
    }

    /** Crea un animal en formato JSON como lo mandaría el servidor. */
    private JsonObject animalJson(int id, boolean mio, double x, double y) {
        JsonObject a = new JsonObject();
        a.addProperty("id", id);
        a.addProperty("especie", "JAGUAR");
        a.addProperty("esMio", mio);
        a.addProperty("esBot", false);
        a.addProperty("energia", 20);
        a.addProperty("x", x);
        a.addProperty("y", y);
        return a;
    }

    private Mensaje snapshot(JsonObject... animales) {
        JsonArray arreglo = new JsonArray();
        for (JsonObject a : animales) {
            arreglo.add(a);
        }
        JsonObject carga = new JsonObject();
        carga.add("animales", arreglo);
        carga.addProperty("capacidadDanio", 3);
        return new Mensaje(TipoMensaje.ACTUALIZACION_ESTADO, carga);
    }

    @Test
    void partida_creada_guarda_el_codigo() {
        JsonObject carga = new JsonObject();
        carga.addProperty("codigo", "A7K2");

        modelo.procesarMensaje(new Mensaje(TipoMensaje.PARTIDA_CREADA, carga));

        assertEquals("A7K2", modelo.getCodigoPartida());
        assertEquals(EstadoPantalla.LOBBY, modelo.getEstado());
    }

    @Test
    void error_guarda_el_mensaje() {
        JsonObject carga = new JsonObject();
        carga.addProperty("mensaje", "Código inválido");

        modelo.procesarMensaje(new Mensaje(TipoMensaje.ERROR, carga));

        assertEquals("Código inválido", modelo.getMensajeError());
    }

    @Test
    void error_en_la_seleccion_permite_elegir_otra_especie() {
        modelo.procesarMensaje(new Mensaje(TipoMensaje.SELECCION_ANIMALES));
        modelo.setMiEspecie("JAGUAR");

        modelo.procesarMensaje(new Mensaje(TipoMensaje.ERROR));

        assertNull(modelo.getMiEspecie());
    }

    @Test
    void seleccion_animales_cambia_a_pantalla_de_seleccion() {
        JsonArray libres = new JsonArray();
        libres.add("JAGUAR");
        libres.add("TUCAN");
        JsonObject carga = new JsonObject();
        carga.add("disponibles", libres);

        modelo.procesarMensaje(new Mensaje(TipoMensaje.SELECCION_ANIMALES, carga));

        assertEquals(EstadoPantalla.SELECCION, modelo.getEstado());
        assertEquals(2, modelo.getEspeciesDisponibles().size());
    }

    @Test
    void snapshot_crea_animales_y_actualiza_datos() {
        modelo.procesarMensaje(snapshot(animalJson(1, true, 10, 10), animalJson(2, false, 50, 50)));

        assertEquals(2, modelo.getAnimales().size());
        assertEquals(3, modelo.getCapacidadDanio());
    }

    @Test
    void snapshot_conserva_la_seleccion_de_un_animal_existente() {
        modelo.procesarMensaje(snapshot(animalJson(1, true, 10, 10)));
        modelo.seleccionarAnimales(new Punto(0, 0), new Punto(20, 20));

        modelo.procesarMensaje(snapshot(animalJson(1, true, 15, 15)));

        assertEquals(1, modelo.obtenerIdsSeleccionados().size());
    }

    @Test
    void seleccion_por_rectangulo_solo_toma_animales_mios() {
        modelo.procesarMensaje(snapshot(animalJson(1, true, 10, 10), animalJson(2, false, 12, 12)));

        // Las esquinas pueden venir en cualquier orden
        modelo.seleccionarAnimales(new Punto(30, 30), new Punto(0, 0));

        assertEquals(1, modelo.obtenerIdsSeleccionados().size());
        assertEquals(1, modelo.obtenerIdsSeleccionados().get(0));
    }

    @Test
    void limpiar_seleccion_deja_la_lista_vacia() {
        modelo.procesarMensaje(snapshot(animalJson(1, true, 10, 10)));
        modelo.seleccionarAnimales(new Punto(0, 0), new Punto(20, 20));

        modelo.limpiarSeleccion();

        assertTrue(modelo.obtenerIdsSeleccionados().isEmpty());
    }

    @Test
    void los_objetos_del_snapshot_se_convierten_a_su_tipo_y_se_ignoran_los_desconocidos() {
        JsonObject taza = new JsonObject();
        taza.addProperty("tipo", "TAZA");
        taza.addProperty("x", 10);
        taza.addProperty("y", 20);
        JsonObject raro = new JsonObject();
        raro.addProperty("tipo", "ARBOL");
        JsonArray objetos = new JsonArray();
        objetos.add(taza);
        objetos.add(raro);
        JsonObject carga = new JsonObject();
        carga.add("objetos", objetos);

        modelo.procesarMensaje(new Mensaje(TipoMensaje.ACTUALIZACION_ESTADO, carga));

        assertEquals(1, modelo.getObjetos().size());
        assertEquals(TipoObjeto.TAZA, modelo.getObjetos().get(0).getTipo());
    }

    @Test
    void buscar_animal_en_un_punto() {
        modelo.procesarMensaje(snapshot(animalJson(7, false, 100, 100)));

        assertEquals(7, modelo.buscarAnimalEn(102, 98).getId());
        assertNull(modelo.buscarAnimalEn(500, 500));
    }

    @Test
    void fin_partida_guarda_resumen_y_cambia_estado() {
        JsonObject carga = new JsonObject();
        carga.addProperty("botsEliminados", 4);
        carga.addProperty("animalesPerdidosA", 3);
        carga.addProperty("animalesPerdidosB", 5);
        carga.addProperty("tazasA", 2);
        carga.addProperty("tazasB", 1);
        carga.addProperty("duracionMs", 90000);
        carga.addProperty("apodoA", "Ana");
        carga.addProperty("apodoB", "Luis");
        carga.addProperty("ganador", "Ana");

        modelo.procesarMensaje(new Mensaje(TipoMensaje.FIN_PARTIDA, carga));

        assertEquals(EstadoPantalla.FIN, modelo.getEstado());
        assertEquals(4, modelo.getResumen().getBotsEliminados());
        assertEquals(5, modelo.getResumen().getAnimalesPerdidosB());
        assertEquals(2, modelo.getResumen().getTazasA());
        assertEquals("Ana", modelo.getResumen().getGanador());
    }

    @Test
    void cada_mensaje_notifica_a_los_observadores() {
        ObservadorContador observador = new ObservadorContador();
        modelo.registrarObservador(observador);

        modelo.procesarMensaje(new Mensaje(TipoMensaje.ERROR));

        assertEquals(1, observador.getLlamadas());
    }

    /** Observador de prueba: solo cuenta cuántas veces lo avisaron. */
    private static class ObservadorContador implements ObservadorModelo {
        private int llamadas;

        @Override
        public void modeloActualizado() {
            llamadas++;
        }

        int getLlamadas() {
            return llamadas;
        }
    }
}
