package jungleattack.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import jungleattack.modelo.ModeloCliente;
import jungleattack.red.ConexionServidor;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControladorMouseTest {

    private ModeloCliente modelo;
    private ControladorMouse mouse;
    private final JPanel origen = new JPanel();

    @BeforeEach
    void preparar() {
        modelo = new ModeloCliente();
        // Sin conexión real: enviar() no hace nada si no hay socket
        ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());
        mouse = new ControladorMouse(controlador, modelo);

        JsonObject propio = animal(1, true, 50, 50);
        JsonObject rival = animal(2, false, 200, 200);
        JsonArray animales = new JsonArray();
        animales.add(propio);
        animales.add(rival);
        JsonObject carga = new JsonObject();
        carga.add("animales", animales);
        modelo.procesarMensaje(new Mensaje(TipoMensaje.ACTUALIZACION_ESTADO, carga));
    }

    private JsonObject animal(int id, boolean mio, double x, double y) {
        JsonObject a = new JsonObject();
        a.addProperty("id", id);
        a.addProperty("esMio", mio);
        a.addProperty("x", x);
        a.addProperty("y", y);
        return a;
    }

    private MouseEvent evento(int tipo, int boton, int x, int y) {
        int modificador = (boton == MouseEvent.BUTTON3) ? InputEvent.BUTTON3_DOWN_MASK : InputEvent.BUTTON1_DOWN_MASK;
        return new MouseEvent(origen, tipo, 0L, modificador, x, y, 1, boton == MouseEvent.BUTTON3, boton);
    }

    private void arrastrar(int x1, int y1, int x2, int y2) {
        mouse.mousePressed(evento(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1, x1, y1));
        mouse.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1, x2, y2));
    }

    @Test
    void arrastrar_selecciona_mis_animales_dentro_del_rectangulo() {
        arrastrar(0, 0, 100, 100);

        assertEquals(1, modelo.obtenerIdsSeleccionados().size());
        assertEquals(1, modelo.obtenerIdsSeleccionados().get(0));
    }

    @Test
    void clic_sobre_animal_propio_lo_selecciona() {
        arrastrar(50, 50, 50, 50);

        assertEquals(1, modelo.obtenerIdsSeleccionados().get(0));
    }

    @Test
    void clic_derecho_quita_la_seleccion() {
        arrastrar(0, 0, 100, 100);
        assertTrue(!modelo.obtenerIdsSeleccionados().isEmpty());

        mouse.mousePressed(evento(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON3, 300, 300));
        mouse.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON3, 300, 300));

        assertTrue(modelo.obtenerIdsSeleccionados().isEmpty());
    }

    @Test
    void soltar_sin_haber_presionado_no_hace_nada() {
        mouse.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1, 50, 50));

        assertTrue(modelo.obtenerIdsSeleccionados().isEmpty());
    }
}
