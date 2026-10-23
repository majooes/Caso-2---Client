package jungleattack.vista;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import jungleattack.controlador.ControladorJuego;
import jungleattack.controlador.ControladorMouse;
import jungleattack.modelo.EstadoPantalla;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.ObservadorModelo;
import jungleattack.util.Constantes;

/** Ventana del juego. Muestra una pantalla a la vez según el estado del modelo. */
public class VentanaPrincipal extends JFrame implements ObservadorModelo {

    private final ModeloCliente modelo;
    private final CardLayout layout = new CardLayout();
    private final JPanel contenedor = new JPanel(layout);
    private final PanelLobby panelLobby;
    private final PanelSeleccion panelSeleccion;
    private final PanelJuego panelJuego;
    private final PanelResumen panelResumen;

    public VentanaPrincipal(ModeloCliente modelo, ControladorJuego controlador) {
        super(Constantes.TITULO_VENTANA);
        this.modelo = modelo;
        ControladorMouse mouse = new ControladorMouse(controlador, modelo);
        panelLobby = new PanelLobby(controlador);
        panelSeleccion = new PanelSeleccion(modelo, controlador);
        panelJuego = new PanelJuego(modelo, controlador, mouse);
        panelResumen = new PanelResumen();

        contenedor.add(panelLobby, EstadoPantalla.LOBBY.name());
        contenedor.add(panelSeleccion, EstadoPantalla.SELECCION.name());
        contenedor.add(panelJuego, EstadoPantalla.JUEGO.name());
        contenedor.add(panelResumen, EstadoPantalla.FIN.name());
        add(contenedor);

        modelo.registrarObservador(this);
        modelo.registrarObservador(panelSeleccion);
        modelo.registrarObservador(panelJuego);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    /** Cambia de pantalla y actualiza los mensajes según lo que cambió en el modelo. */
    @Override
    public void modeloActualizado() {
        EstadoPantalla estado = modelo.getEstado();
        if (estado == EstadoPantalla.LOBBY) {
            if (modelo.getMensajeError() != null) {
                panelLobby.mostrarError(modelo.getMensajeError());
            } else if (modelo.getCodigoPartida() != null) {
                panelLobby.mostrarCodigo(modelo.getCodigoPartida());
            }
        } else if (estado == EstadoPantalla.FIN) {
            panelResumen.mostrarResumen(modelo.getResumen());
        }
        layout.show(contenedor, estado.name());
    }

    public void mostrarErrorConexion() {
        panelLobby.mostrarError(Constantes.TEXTO_SIN_SERVIDOR);
    }
}
