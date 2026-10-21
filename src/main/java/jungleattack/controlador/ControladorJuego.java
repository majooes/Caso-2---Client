package jungleattack.controlador;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.Punto;
import jungleattack.red.ConexionServidor;
import jungleattack.red.HiloLector;
import jungleattack.red.Mensaje;
import jungleattack.red.TipoMensaje;
import jungleattack.util.Constantes;

/** Convierte las acciones del jugador en mensajes para el servidor. No dibuja nada. */
public class ControladorJuego {

    private final ModeloCliente modelo;
    private final ConexionServidor conexion;

    public ControladorJuego(ModeloCliente modelo, ConexionServidor conexion) {
        this.modelo = modelo;
        this.conexion = conexion;
    }

    /** Se conecta al servidor y arranca el hilo que lee sus mensajes. */
    public boolean conectar() {
        boolean conectado = conexion.conectar(Constantes.HOST, Constantes.PUERTO);
        if (conectado) {
            new HiloLector(conexion, modelo).iniciar();
        }
        return conectado;
    }

    public void crearPartida(String apodo) {
        modelo.setApodo(apodo);
        JsonObject carga = new JsonObject();
        carga.addProperty(Constantes.JSON_APODO, apodo);
        conexion.enviar(new Mensaje(TipoMensaje.CREAR_PARTIDA, carga));
    }

    public void unirsePartida(String apodo, String codigo) {
        modelo.setApodo(apodo);
        JsonObject carga = new JsonObject();
        carga.addProperty(Constantes.JSON_APODO, apodo);
        carga.addProperty(Constantes.JSON_CODIGO, codigo);
        conexion.enviar(new Mensaje(TipoMensaje.UNIRSE_PARTIDA, carga));
    }

    public void elegirEspecie(String especie) {
        modelo.setMiEspecie(especie);
        JsonObject carga = new JsonObject();
        carga.addProperty(Constantes.JSON_ESPECIE, especie);
        conexion.enviar(new Mensaje(TipoMensaje.ELEGIR_ANIMAL, carga));
    }

    /** Manda a los animales seleccionados hacia el destino. Sin selección no hace nada. */
    public void ordenarMover(Punto destino) {
        List<Integer> ids = modelo.obtenerIdsSeleccionados();
        if (ids.isEmpty()) {
            return;
        }
        JsonObject carga = new JsonObject();
        carga.add(Constantes.JSON_ANIMAL_IDS, convertirIds(ids));
        carga.addProperty(Constantes.JSON_DESTINO_X, destino.getX());
        carga.addProperty(Constantes.JSON_DESTINO_Y, destino.getY());
        conexion.enviar(new Mensaje(TipoMensaje.MOVER_ANIMALES, carga));
    }

    /** Los animales seleccionados atacan al objetivo. Sin selección no hace nada. */
    public void ordenarAtacar(int idObjetivo) {
        List<Integer> ids = modelo.obtenerIdsSeleccionados();
        if (ids.isEmpty()) {
            return;
        }
        JsonObject carga = new JsonObject();
        carga.add(Constantes.JSON_ANIMAL_IDS, convertirIds(ids));
        carga.addProperty(Constantes.JSON_OBJETIVO_ID, idObjetivo);
        conexion.enviar(new Mensaje(TipoMensaje.ATACAR, carga));
    }

    public void cambiarZona(int idZona) {
        JsonObject carga = new JsonObject();
        carga.addProperty(Constantes.JSON_ZONA, idZona);
        conexion.enviar(new Mensaje(TipoMensaje.VER_ZONA, carga));
    }

    private JsonArray convertirIds(List<Integer> ids) {
        JsonArray arreglo = new JsonArray();
        for (Integer id : ids) {
            arreglo.add(id);
        }
        return arreglo;
    }
}
