package jungleattack.modelo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jungleattack.red.Mensaje;
import jungleattack.util.Constantes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estado del juego en el cliente (sujeto del patrón Observer).
 * Solo se modifica desde el hilo de Swing (vía ActualizadorModelo), por eso no usa sincronización.
 *
 * Formato esperado de las cargas del servidor:
 *  PARTIDA_CREADA / PARTIDA_UNIDA : {codigo}
 *  ERROR                          : {mensaje}
 *  SELECCION_ANIMALES             : {disponibles: [especie, ...]}
 *  PARTIDA_INICIADA               : {zona}
 *  ACTUALIZACION_ESTADO           : {zona, zonasConAnimales:[int], capacidadDanio, tazasConsumidas,
 *                                    botsEliminados, animales:[{id, especie, esMio, esBot, energia, x, y}],
 *                                    objetos:[{tipo, x, y}]}
 *  FIN_PARTIDA                    : {botsEliminados, animalesPerdidosA, animalesPerdidosB, tazasA, tazasB,
 *                                    duracionMs, apodoA, apodoB, ganador}
 */
public class ModeloCliente {

    private String apodo;
    private String codigoPartida;
    private EstadoPantalla estado = EstadoPantalla.LOBBY;
    private String mensajeError;
    private List<String> especiesDisponibles = new ArrayList<>();
    private String miEspecie;
    private int zonaActual;
    private List<Integer> zonasConAnimales = new ArrayList<>();
    private List<AnimalVista> animales = new ArrayList<>();
    private List<ObjetoVista> objetos = new ArrayList<>();
    private int capacidadDanio;
    private int tazasConsumidas;
    private int botsEliminados;
    private ResumenPartida resumen;
    private final List<ObservadorModelo> observadores = new ArrayList<>();

    // ---------- Observer ----------

    public void registrarObservador(ObservadorModelo o) {
        observadores.add(o);
    }

    public void notificarObservadores() {
        for (ObservadorModelo o : observadores) {
            o.modeloActualizado();
        }
    }

    // ---------- Mensajes del servidor ----------

    /** Actualiza el modelo según el mensaje recibido y avisa a las vistas. */
    public void procesarMensaje(Mensaje m) {
        if (m == null) {
            return;
        }
        JsonObject c = m.getCarga();
        switch (m.getTipo()) {
            case PARTIDA_CREADA, PARTIDA_UNIDA -> {
                codigoPartida = leerTexto(c, Constantes.JSON_CODIGO, codigoPartida);
                mensajeError = null;
            }
            case ERROR -> mensajeError = leerTexto(c, Constantes.JSON_MENSAJE, "Error desconocido");
            case SELECCION_ANIMALES -> {
                especiesDisponibles = leerListaTexto(c, Constantes.JSON_DISPONIBLES);
                estado = EstadoPantalla.SELECCION;
                mensajeError = null;
            }
            case PARTIDA_INICIADA -> {
                zonaActual = leerEntero(c, Constantes.JSON_ZONA, 0);
                estado = EstadoPantalla.JUEGO;
            }
            case ACTUALIZACION_ESTADO -> actualizarEstadoJuego(c);
            case FIN_PARTIDA -> {
                resumen = new ResumenPartida(
                        leerEntero(c, Constantes.JSON_BOTS_ELIMINADOS, 0),
                        leerEntero(c, Constantes.JSON_ANIMALES_PERDIDOS_A, 0),
                        leerEntero(c, Constantes.JSON_ANIMALES_PERDIDOS_B, 0),
                        leerEntero(c, Constantes.JSON_TAZAS_A, 0),
                        leerEntero(c, Constantes.JSON_TAZAS_B, 0),
                        leerLong(c, Constantes.JSON_DURACION_MS, 0),
                        leerTexto(c, Constantes.JSON_APODO_A, ""),
                        leerTexto(c, Constantes.JSON_APODO_B, ""),
                        leerTexto(c, Constantes.JSON_GANADOR, ""));
                estado = EstadoPantalla.FIN;
            }
            default -> { } // los demás tipos solo los envía el cliente
        }
        notificarObservadores();
    }

    /** Aplica un snapshot del servidor. */
    private void actualizarEstadoJuego(JsonObject c) {
        zonaActual = leerEntero(c, Constantes.JSON_ZONA, zonaActual);
        capacidadDanio = leerEntero(c, Constantes.JSON_CAPACIDAD_DANIO, capacidadDanio);
        tazasConsumidas = leerEntero(c, Constantes.JSON_TAZAS, tazasConsumidas);
        botsEliminados = leerEntero(c, Constantes.JSON_BOTS_ELIMINADOS, botsEliminados);

        zonasConAnimales = new ArrayList<>();
        for (JsonElement e : leerArreglo(c, Constantes.JSON_ZONAS_CON_ANIMALES)) {
            zonasConAnimales.add(e.getAsInt());
        }

        objetos = new ArrayList<>();
        for (JsonElement e : leerArreglo(c, Constantes.JSON_OBJETOS)) {
            JsonObject o = e.getAsJsonObject();
            objetos.add(new ObjetoVista(
                    leerTexto(o, Constantes.JSON_TIPO_OBJETO, ""),
                    new Punto(leerDouble(o, Constantes.JSON_X), leerDouble(o, Constantes.JSON_Y))));
        }

        actualizarAnimales(leerArreglo(c, Constantes.JSON_ANIMALES));
    }

    /** Reutiliza los animales que ya existían (para no perder la selección ni la interpolación). */
    private void actualizarAnimales(JsonArray arreglo) {
        List<AnimalVista> nuevos = new ArrayList<>();
        for (JsonElement e : arreglo) {
            JsonObject o = e.getAsJsonObject();
            int id = leerEntero(o, Constantes.JSON_ID, -1);
            int energia = leerEntero(o, Constantes.JSON_ENERGIA, 0);
            Punto pos = new Punto(leerDouble(o, Constantes.JSON_X), leerDouble(o, Constantes.JSON_Y));

            AnimalVista a = buscarPorId(id);
            if (a == null) {
                a = new AnimalVista(id,
                        leerTexto(o, Constantes.JSON_ESPECIE, ""),
                        leerBoolean(o, Constantes.JSON_ES_MIO),
                        leerBoolean(o, Constantes.JSON_ES_BOT),
                        energia, pos);
            } else {
                a.actualizarPosicion(pos);
                a.setEnergia(energia);
            }
            nuevos.add(a);
        }
        animales = nuevos; // los que ya no vienen en el snapshot desaparecen
    }

    // ---------- Selección ----------

    /** Selecciona mis animales que estén dentro del rectángulo formado por las dos esquinas. */
    public void seleccionarAnimales(Punto esquina1, Punto esquina2) {
        double minX = Math.min(esquina1.getX(), esquina2.getX());
        double maxX = Math.max(esquina1.getX(), esquina2.getX());
        double minY = Math.min(esquina1.getY(), esquina2.getY());
        double maxY = Math.max(esquina1.getY(), esquina2.getY());

        for (AnimalVista a : animales) {
            Punto p = a.getPosicionActual();
            boolean dentro = p.getX() >= minX && p.getX() <= maxX
                          && p.getY() >= minY && p.getY() <= maxY;
            a.setSeleccionado(a.isEsMio() && dentro);
        }
    }

    public void limpiarSeleccion() {
        for (AnimalVista a : animales) {
            a.setSeleccionado(false);
        }
    }

    public List<Integer> obtenerIdsSeleccionados() {
        List<Integer> ids = new ArrayList<>();
        for (AnimalVista a : animales) {
            if (a.isSeleccionado()) {
                ids.add(a.getId());
            }
        }
        return ids;
    }

    /** Animal (mío, del rival o bot) bajo el punto (x, y), o null si no hay ninguno. */
    public AnimalVista buscarAnimalEn(double x, double y) {
        for (AnimalVista a : animales) {
            if (a.contiene(x, y)) {
                return a;
            }
        }
        return null;
    }

    private AnimalVista buscarPorId(int id) {
        for (AnimalVista a : animales) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    // ---------- Lectura segura del JSON (si falta un campo, usa un valor por defecto) ----------

    private static String leerTexto(JsonObject o, String clave, String defecto) {
        JsonElement e = o.get(clave);
        return (e != null && !e.isJsonNull()) ? e.getAsString() : defecto;
    }

    private static int leerEntero(JsonObject o, String clave, int defecto) {
        JsonElement e = o.get(clave);
        return (e != null && !e.isJsonNull()) ? e.getAsInt() : defecto;
    }

    private static long leerLong(JsonObject o, String clave, long defecto) {
        JsonElement e = o.get(clave);
        return (e != null && !e.isJsonNull()) ? e.getAsLong() : defecto;
    }

    private static double leerDouble(JsonObject o, String clave) {
        JsonElement e = o.get(clave);
        return (e != null && !e.isJsonNull()) ? e.getAsDouble() : 0.0;
    }

    private static boolean leerBoolean(JsonObject o, String clave) {
        JsonElement e = o.get(clave);
        return e != null && !e.isJsonNull() && e.getAsBoolean();
    }

    private static JsonArray leerArreglo(JsonObject o, String clave) {
        JsonElement e = o.get(clave);
        return (e != null && e.isJsonArray()) ? e.getAsJsonArray() : new JsonArray();
    }

    private static List<String> leerListaTexto(JsonObject o, String clave) {
        List<String> lista = new ArrayList<>();
        for (JsonElement e : leerArreglo(o, clave)) {
            lista.add(e.getAsString());
        }
        return lista;
    }

    // ---------- Getters y setters ----------

    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }
    public String getCodigoPartida() { return codigoPartida; }
    public EstadoPantalla getEstado() { return estado; }
    public String getMensajeError() { return mensajeError; }
    public List<String> getEspeciesDisponibles() { return Collections.unmodifiableList(especiesDisponibles); }
    public String getMiEspecie() { return miEspecie; }
    public void setMiEspecie(String miEspecie) { this.miEspecie = miEspecie; }
    public int getZonaActual() { return zonaActual; }
    public List<Integer> getZonasConAnimales() { return Collections.unmodifiableList(zonasConAnimales); }
    public List<AnimalVista> getAnimales() { return Collections.unmodifiableList(animales); }
    public List<ObjetoVista> getObjetos() { return Collections.unmodifiableList(objetos); }
    public int getCapacidadDanio() { return capacidadDanio; }
    public int getTazasConsumidas() { return tazasConsumidas; }
    public int getBotsEliminados() { return botsEliminados; }
    public ResumenPartida getResumen() { return resumen; }
}
