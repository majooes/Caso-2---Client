package jungleattack.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Usa un servidor falso en el puerto libre que asigne el sistema (puerto 0). */
class ConexionServidorTest {

    @Test
    void conectar_devuelve_false_si_no_hay_servidor() throws Exception {
        ServerSocket temporal = new ServerSocket(0);
        int puertoLibre = temporal.getLocalPort();
        temporal.close(); // ahora nadie escucha en ese puerto

        ConexionServidor conexion = new ConexionServidor();

        assertFalse(conexion.conectar("localhost", puertoLibre));
        assertFalse(conexion.estaConectado());
    }

    @Test
    void enviar_escribe_una_linea_con_el_mensaje() throws Exception {
        ServerSocket servidor = new ServerSocket(0);
        ConexionServidor conexion = new ConexionServidor();
        assertTrue(conexion.conectar("localhost", servidor.getLocalPort()));
        Socket ladoServidor = servidor.accept();
        BufferedReader lector = new BufferedReader(
                new InputStreamReader(ladoServidor.getInputStream(), StandardCharsets.UTF_8));

        conexion.enviar(new Mensaje(TipoMensaje.CREAR_PARTIDA));

        Mensaje recibido = Mensaje.desdeJson(lector.readLine());
        assertNotNull(recibido);
        assertEquals(TipoMensaje.CREAR_PARTIDA, recibido.getTipo());
        conexion.cerrar();
        ladoServidor.close();
        servidor.close();
    }

    @Test
    void leer_ignora_lineas_invalidas_y_devuelve_el_primer_mensaje_valido() throws Exception {
        ServerSocket servidor = new ServerSocket(0);
        ConexionServidor conexion = new ConexionServidor();
        assertTrue(conexion.conectar("localhost", servidor.getLocalPort()));
        Socket ladoServidor = servidor.accept();
        PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(ladoServidor.getOutputStream(), StandardCharsets.UTF_8), true);

        escritor.println("esto no es json");
        escritor.println(new Mensaje(TipoMensaje.FIN_PARTIDA).aJson());

        Mensaje leido = conexion.leer();
        assertNotNull(leido);
        assertEquals(TipoMensaje.FIN_PARTIDA, leido.getTipo());
        conexion.cerrar();
        ladoServidor.close();
        servidor.close();
    }

    @Test
    void leer_devuelve_null_cuando_el_servidor_cierra() throws Exception {
        ServerSocket servidor = new ServerSocket(0);
        ConexionServidor conexion = new ConexionServidor();
        assertTrue(conexion.conectar("localhost", servidor.getLocalPort()));
        Socket ladoServidor = servidor.accept();

        ladoServidor.close();

        assertNull(conexion.leer());
        conexion.cerrar();
        servidor.close();
    }
}
