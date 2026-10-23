package jungleattack.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import jungleattack.controlador.ControladorJuego;
import jungleattack.controlador.ControladorMouse;
import jungleattack.modelo.ModeloCliente;
import jungleattack.red.ConexionServidor;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import jungleattack.util.Constantes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanelJuegoTest {

    private ModeloCliente modelo;
    private PanelJuego panel;

    @BeforeAll
    static void cargarImagenes() {
        CacheImagenes.cargarTodas();
    }

    @BeforeEach
    void preparar() {
        modelo = new ModeloCliente();
        ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());
        panel = new PanelJuego(modelo, controlador, new ControladorMouse(controlador, modelo));
        panel.setSize(Constantes.ANCHO_ZONA, Constantes.ALTO_ZONA);
        modelo.registrarObservador(panel);
    }

    private BufferedImage dibujar() {
        BufferedImage imagen = new BufferedImage(Constantes.ANCHO_ZONA, Constantes.ALTO_ZONA,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        panel.paint(g);
        g.dispose();
        return imagen;
    }

    private Mensaje snapshot(JsonArray zonas, JsonArray animales) {
        JsonObject carga = new JsonObject();
        carga.addProperty("zona", 5);
        carga.add("zonasConAnimales", zonas);
        carga.add("animales", animales);
        return new Mensaje(TipoMensaje.ACTUALIZACION_ESTADO, carga);
    }

    @Test
    void la_fraccion_de_interpolacion_depende_del_tiempo_desde_el_ultimo_snapshot() {
        assertEquals(0.0, PanelJuego.calcularFraccion(100, 100), 0.0001);
        assertEquals(0.5, PanelJuego.calcularFraccion(125, 100), 0.0001);
        assertEquals(1.0, PanelJuego.calcularFraccion(150, 100), 0.0001);
    }

    @Test
    void crea_un_boton_por_cada_zona_donde_hay_animales() {
        JsonArray zonas = new JsonArray();
        zonas.add(2);
        zonas.add(5);

        modelo.procesarMensaje(snapshot(zonas, new JsonArray()));

        assertEquals(2, panel.contarBotonesZona());
    }

    @Test
    void sin_zonas_con_animales_no_hay_botones() {
        modelo.procesarMensaje(snapshot(new JsonArray(), new JsonArray()));

        assertEquals(0, panel.contarBotonesZona());
    }

    @Test
    void dibujar_con_animales_cambia_la_imagen() {
        BufferedImage vacia = dibujar();

        JsonObject animal = new JsonObject();
        animal.addProperty("id", 1);
        animal.addProperty("especie", "JAGUAR");
        animal.addProperty("esMio", true);
        animal.addProperty("energia", 20);
        animal.addProperty("x", 200);
        animal.addProperty("y", 200);
        JsonArray animales = new JsonArray();
        animales.add(animal);
        modelo.procesarMensaje(snapshot(new JsonArray(), animales));
        BufferedImage conAnimal = dibujar();

        boolean hayDiferencia = false;
        for (int x = 170; x < 230 && !hayDiferencia; x++) {
            for (int y = 170; y < 230; y++) {
                if (vacia.getRGB(x, y) != conAnimal.getRGB(x, y)) {
                    hayDiferencia = true;
                    break;
                }
            }
        }
        assertTrue(hayDiferencia);
    }

    @Test
    void dibujar_sin_nada_no_falla_y_pinta_el_fondo() {
        BufferedImage imagen = dibujar();

        assertEquals(255, (imagen.getRGB(10, 10) >>> 24) & 0xFF);
    }
}
