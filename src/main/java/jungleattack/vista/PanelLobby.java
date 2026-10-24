package jungleattack.vista;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import jungleattack.controlador.ControladorJuego;
import jungleattack.util.Constantes;

/** Pantalla inicial: elegir apodo y crear una partida o unirse con un código. */
public class PanelLobby extends JPanel {

    private final ControladorJuego controlador;
    private final JTextField campoApodo = new JTextField(Constantes.COLUMNAS_CAMPO_APODO);
    private final JTextField campoCodigo = new JTextField(Constantes.COLUMNAS_CAMPO_CODIGO);
    private final JButton botonCrear = new JButton(Constantes.TEXTO_CREAR);
    private final JButton botonUnirse = new JButton(Constantes.TEXTO_UNIRSE);
    private final JLabel etiquetaMensaje = new JLabel(" ", SwingConstants.CENTER);

    public PanelLobby(ControladorJuego controlador) {
        this.controlador = controlador;
        setLayout(new GridLayout(0, 1, Constantes.ESPACIO_LOBBY, Constantes.ESPACIO_LOBBY));
        setBorder(BorderFactory.createEmptyBorder(Constantes.MARGEN_LOBBY_VERTICAL,
                Constantes.MARGEN_LOBBY_HORIZONTAL, Constantes.MARGEN_LOBBY_VERTICAL,
                Constantes.MARGEN_LOBBY_HORIZONTAL));

        JLabel titulo = new JLabel(Constantes.TITULO_VENTANA, SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Constantes.FUENTE_TITULO));
        add(titulo);

        JPanel filaApodo = new JPanel(new FlowLayout());
        filaApodo.add(new JLabel(Constantes.TEXTO_APODO));
        filaApodo.add(campoApodo);
        add(filaApodo);

        add(botonCrear);

        JPanel filaCodigo = new JPanel(new FlowLayout());
        filaCodigo.add(new JLabel(Constantes.TEXTO_CODIGO));
        filaCodigo.add(campoCodigo);
        add(filaCodigo);

        add(botonUnirse);
        add(etiquetaMensaje);

        botonCrear.addActionListener(new AccionCrear());
        botonUnirse.addActionListener(new AccionUnirse());
    }

    /** Crea una partida nueva con el apodo escrito. */
    void crear() {
        String apodo = campoApodo.getText().trim();
        if (apodo.isEmpty()) {
            mostrarError(Constantes.TEXTO_APODO_VACIO);
        } else {
            controlador.crearPartida(apodo);
        }
    }

    /** Se une a la partida con el código escrito (sin importar mayúsculas). */
    void unirse() {
        String apodo = campoApodo.getText().trim();
        String codigo = campoCodigo.getText().trim().toUpperCase();
        if (apodo.isEmpty()) {
            mostrarError(Constantes.TEXTO_APODO_VACIO);
        } else if (codigo.length() != Constantes.LONGITUD_CODIGO) {
            mostrarError(Constantes.TEXTO_CODIGO_INVALIDO);
        } else {
            controlador.unirsePartida(apodo, codigo);
        }
    }

    public void mostrarCodigo(String codigo) {
        etiquetaMensaje.setForeground(Constantes.COLOR_TEXTO);
        etiquetaMensaje.setText(String.format(Constantes.TEXTO_ESPERANDO_JUGADOR, codigo));
    }

    public void mostrarError(String texto) {
        etiquetaMensaje.setForeground(Constantes.COLOR_ERROR);
        etiquetaMensaje.setText(texto);
    }

    String getTextoMensaje() {
        return etiquetaMensaje.getText();
    }

    void setApodo(String apodo) { campoApodo.setText(apodo); }
    void setCodigo(String codigo) { campoCodigo.setText(codigo); }

    private class AccionCrear implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            crear();
        }
    }

    private class AccionUnirse implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            unirse();
        }
    }
}
