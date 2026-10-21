package jungleattack.red;

import com.google.gson.JsonObject;
import javax.swing.SwingUtilities;
import jungleattack.modelo.ModeloCliente;
import jungleattack.util.Constantes;

/** Lee mensajes del servidor en un hilo aparte, sin trabar la ventana. */
public class HiloLector implements Runnable {

    private final ConexionServidor conexion;
    private final ModeloCliente modelo;

    public HiloLector(ConexionServidor conexion, ModeloCliente modelo) {
        this.conexion = conexion;
        this.modelo = modelo;
    }

    /** Crea el hilo y lo arranca. Es "daemon": no impide cerrar el programa. */
    public void iniciar() {
        Thread hilo = new Thread(this);
        hilo.setDaemon(true);
        hilo.start();
    }

    @Override
    public void run() {
        Mensaje m = conexion.leer();
        while (m != null) {
            SwingUtilities.invokeLater(new ActualizadorModelo(modelo, m));
            m = conexion.leer();
        }
        // leer() devolvió null: la conexión terminó
        JsonObject carga = new JsonObject();
        carga.addProperty(Constantes.JSON_MENSAJE, Constantes.TEXTO_CONEXION_PERDIDA);
        SwingUtilities.invokeLater(
                new ActualizadorModelo(modelo, new Mensaje(TipoMensaje.ERROR, carga)));
    }
}
