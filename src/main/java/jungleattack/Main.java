package jungleattack;

import javax.swing.SwingUtilities;
import jungleattack.controlador.ControladorJuego;
import jungleattack.modelo.ModeloCliente;
import jungleattack.red.ConexionServidor;
import jungleattack.vista.CacheImagenes;
import jungleattack.vista.VentanaPrincipal;

/** Punto de entrada del cliente de Jungle Attack. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Arranque());
    }

    /** Arma las piezas del juego (MVC) y abre la ventana. Corre en el hilo de Swing. */
    private static class Arranque implements Runnable {
        @Override
        public void run() {
            CacheImagenes.cargarTodas();
            ModeloCliente modelo = new ModeloCliente();
            ConexionServidor conexion = new ConexionServidor();
            ControladorJuego controlador = new ControladorJuego(modelo, conexion);
            VentanaPrincipal ventana = new VentanaPrincipal(modelo, controlador);
            ventana.setVisible(true);
            if (!controlador.conectar()) {
                ventana.mostrarErrorConexion();
            }
        }
    }
}
