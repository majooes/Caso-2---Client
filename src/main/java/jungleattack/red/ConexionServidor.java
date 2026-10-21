package jungleattack.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Conexión por socket con el servidor. Un mensaje por línea, en UTF-8. */
public class ConexionServidor {

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    /** Intenta conectarse. Devuelve false si no se pudo (servidor apagado, host incorrecto...). */
    public boolean conectar(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            return true;
        } catch (IOException e) {
            cerrar();
            return false;
        }
    }

    public boolean estaConectado() {
        return socket != null && !socket.isClosed();
    }

    /** Envía un mensaje. Si no hay conexión, no hace nada. */
    public void enviar(Mensaje m) {
        if (salida != null) {
            salida.println(m.aJson());
        }
    }

    /**
     * Espera el siguiente mensaje válido. Las líneas inválidas se ignoran.
     * Devuelve null cuando la conexión se cerró.
     */
    public Mensaje leer() {
        if (entrada == null) {
            return null;
        }
        try {
            String linea = entrada.readLine();
            while (linea != null) {
                Mensaje m = Mensaje.desdeJson(linea);
                if (m != null) {
                    return m;
                }
                linea = entrada.readLine();
            }
            return null;
        } catch (IOException e) {
            return null; // se cayó la conexión
        }
    }

    public void cerrar() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Ya se estaba cerrando; no hay nada más que hacer.
            }
        }
    }
}
