package jungleattack.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jungleattack.controlador.ControladorJuego;
import jungleattack.modelo.ModeloCliente;
import jungleattack.red.ConexionServidor;
import jungleattack.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanelLobbyTest {

    private ModeloCliente modelo;
    private PanelLobby panel;

    @BeforeEach
    void preparar() {
        modelo = new ModeloCliente();
        // Sin conexión real: enviar() no hace nada
        panel = new PanelLobby(new ControladorJuego(modelo, new ConexionServidor()));
    }

    @Test
    void crear_sin_apodo_muestra_error_y_no_guarda_apodo() {
        panel.crear();

        assertEquals(Constantes.TEXTO_APODO_VACIO, panel.getTextoMensaje());
        assertNull(modelo.getApodo());
    }

    @Test
    void crear_con_apodo_lo_guarda_en_el_modelo() {
        panel.setApodo("  Ana ");

        panel.crear();

        assertEquals("Ana", modelo.getApodo());
    }

    @Test
    void unirse_con_codigo_de_largo_incorrecto_muestra_error() {
        panel.setApodo("Luis");
        panel.setCodigo("AB");

        panel.unirse();

        assertEquals(Constantes.TEXTO_CODIGO_INVALIDO, panel.getTextoMensaje());
        assertNull(modelo.getApodo());
    }

    @Test
    void unirse_con_apodo_y_codigo_validos_guarda_el_apodo() {
        panel.setApodo("Luis");
        panel.setCodigo("a7k2");

        panel.unirse();

        assertEquals("Luis", modelo.getApodo());
    }

    @Test
    void mostrar_codigo_incluye_el_codigo_en_el_mensaje() {
        panel.mostrarCodigo("A7K2");

        assertTrue(panel.getTextoMensaje().contains("A7K2"));
    }

    @Test
    void mostrar_error_cambia_el_texto() {
        panel.mostrarError("Partida llena");

        assertEquals("Partida llena", panel.getTextoMensaje());
    }
}
