package jungleattack.red;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import jungleattack.util.Constantes;

/** Mensaje con formato {"type": "...", "payload": {...}}. Se envía una línea por mensaje. */
public class Mensaje {

    private final TipoMensaje tipo;
    private final JsonObject carga;

    public Mensaje(TipoMensaje tipo, JsonObject carga) {
        this.tipo = tipo;
        this.carga = (carga != null) ? carga : new JsonObject();
    }

    /** Mensaje sin datos extra. */
    public Mensaje(TipoMensaje tipo) {
        this(tipo, new JsonObject());
    }

    public TipoMensaje getTipo() { return tipo; }
    public JsonObject getCarga() { return carga; }

    /** Convierte el mensaje a una línea de JSON. */
    public String aJson() {
        JsonObject sobre = new JsonObject();
        sobre.addProperty(Constantes.JSON_TIPO, tipo.name());
        sobre.add(Constantes.JSON_CARGA, carga);
        return sobre.toString();
    }

    /** Lee un mensaje desde JSON. Si el texto es inválido devuelve null (sin excepciones). */
    public static Mensaje desdeJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            JsonObject sobre = JsonParser.parseString(json).getAsJsonObject();
            JsonElement elementoTipo = sobre.get(Constantes.JSON_TIPO);
            if (elementoTipo == null) {
                return null;
            }
            TipoMensaje tipo = TipoMensaje.valueOf(elementoTipo.getAsString());
            JsonElement elementoCarga = sobre.get(Constantes.JSON_CARGA);
            JsonObject carga = (elementoCarga != null && elementoCarga.isJsonObject())
                    ? elementoCarga.getAsJsonObject()
                    : new JsonObject();
            return new Mensaje(tipo, carga);
        } catch (JsonParseException | IllegalStateException | IllegalArgumentException e) {
            return null; // JSON roto o tipo desconocido
        }
    }
}
