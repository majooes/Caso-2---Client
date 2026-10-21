package jungleattack.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.Punto;
import jungleattack.red.ConexionServidor;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Usa un servidor falso para ver exactamente qué mensajes manda el controlador. */
class ControladorJuegoTest {

    private ServerSocket servidor;
    private Socket ladoServidor;
    private BufferedReader lector;
    private ConexionServidor conexion;
    private ModeloCliente modelo;
    private ControladorJuego controlador;

    @BeforeEach
    void preparar() throws Exception {
        servidor = new ServerSocket(0);
        conexion = new ConexionServidor();
        assertTrue(conexion.conectar("localhost", servidor.getLocalPort()));
        ladoServidor = servidor.accept();
        lector = new BufferedReader(
                new InputStreamReader(ladoServidor.getInputStream(), StandardCharsets.UTF_8));
        modelo = new ModeloCliente();
        controlador = new ControladorJuego(modelo, conexion);
    }

    @AfterEach
    void limpiar() throws Exception {
        conexion.cerrar();
        ladoServidor.close();
        servidor.close();
    }

    private Mensaje siguienteMensaje() throws Exception {
        Mensaje m = Mensaje.desdeJson(lector.readLine());
        assertNotNull(m);
        return m;
    }

    /** Deja un animal mío con id 5 en el modelo, ya seleccionado. */
    private void seleccionarAnimalCinco() {
        JsonObject animal = new JsonObject();
        animal.addProperty("id", 5);
        animal.addProperty("esMio", true);
        animal.addProperty("x", 10);
        animal.addProperty("y", 10);
        JsonArray animales = new JsonArray();
        animales.add(animal);
        JsonObject carga = new JsonObject();
        carga.add("animales", animales);
        modelo.procesarMensaje(new Mensaje(TipoMensaje.ACTUALIZACION_ESTADO, carga));
        modelo.seleccionarAnimales(new Punto(0, 0), new Punto(20, 20));
    }

    @Test
    void crear_partida_envia_el_apodo() throws Exception {
        controlador.crearPartida("Ana");

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.CREAR_PARTIDA, m.getTipo());
        assertEquals("Ana", m.getCarga().get("apodo").getAsString());
        assertEquals("Ana", modelo.getApodo());
    }

    @Test
    void unirse_partida_envia_apodo_y_codigo() throws Exception {
        controlador.unirsePartida("Luis", "A7K2");

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.UNIRSE_PARTIDA, m.getTipo());
        assertEquals("A7K2", m.getCarga().get("codigo").getAsString());
    }

    @Test
    void elegir_especie_envia_la_especie_y_la_guarda() throws Exception {
        controlador.elegirEspecie("TUCAN");

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.ELEGIR_ANIMAL, m.getTipo());
        assertEquals("TUCAN", m.getCarga().get("especie").getAsString());
        assertEquals("TUCAN", modelo.getMiEspecie());
    }

    @Test
    void mover_envia_ids_seleccionados_y_destino() throws Exception {
        seleccionarAnimalCinco();

        controlador.ordenarMover(new Punto(300, 150));

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.MOVER_ANIMALES, m.getTipo());
        assertEquals(5, m.getCarga().getAsJsonArray("animalIds").get(0).getAsInt());
        assertEquals(300.0, m.getCarga().get("targetX").getAsDouble(), 0.001);
        assertEquals(150.0, m.getCarga().get("targetY").getAsDouble(), 0.001);
    }

    @Test
    void atacar_envia_ids_seleccionados_y_objetivo() throws Exception {
        seleccionarAnimalCinco();

        controlador.ordenarAtacar(9);

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.ATACAR, m.getTipo());
        assertEquals(9, m.getCarga().get("objetivoId").getAsInt());
    }

    @Test
    void sin_seleccion_mover_y_atacar_no_envian_nada() throws Exception {
        controlador.ordenarMover(new Punto(1, 1));
        controlador.ordenarAtacar(3);
        controlador.cambiarZona(2); // este sí se envía, y debe ser el primero en llegar

        Mensaje m = siguienteMensaje();
        assertEquals(TipoMensaje.VER_ZONA, m.getTipo());
        assertEquals(2, m.getCarga().get("zona").getAsInt());
    }
}
