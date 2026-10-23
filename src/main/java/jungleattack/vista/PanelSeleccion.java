package jungleattack.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jungleattack.controlador.ControladorJuego;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.ObservadorModelo;
import jungleattack.util.Constantes;

/** Pantalla para elegir el animal. Solo se pueden elegir las especies que siguen libres. */
public class PanelSeleccion extends JPanel implements ObservadorModelo {

    private final ModeloCliente modelo;
    private final ControladorJuego controlador;
    private final Map<String, JButton> botones = new LinkedHashMap<>();
    private final JLabel etiqueta = new JLabel(Constantes.TEXTO_ELEGIR_ANIMAL, SwingConstants.CENTER);

    public PanelSeleccion(ModeloCliente modelo, ControladorJuego controlador) {
        this.modelo = modelo;
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 30));
        setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        etiqueta.setFont(etiqueta.getFont().deriveFont(24f));
        add(etiqueta, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        add(centro, BorderLayout.CENTER);
        crearBotonesEspecies(centro);
        modeloActualizado();
    }

    /** Un botón por cada especie, con su ícono. */
    private void crearBotonesEspecies(JPanel destino) {
        for (String especie : Constantes.ESPECIES) {
            JButton boton = new JButton(nombreBonito(especie));
            BufferedImage imagen = CacheImagenes.obtener(especie.toLowerCase());
            if (imagen != null) {
                Image icono = imagen.getScaledInstance(Constantes.TAMANO_ICONO_BOTON,
                        Constantes.TAMANO_ICONO_BOTON, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(icono));
            }
            boton.setVerticalTextPosition(SwingConstants.BOTTOM);
            boton.setHorizontalTextPosition(SwingConstants.CENTER);
            boton.addActionListener(new AccionEspecie(especie));
            botones.put(especie, boton);
            destino.add(boton);
        }
    }

    private String nombreBonito(String especie) {
        return especie.charAt(0) + especie.substring(1).toLowerCase();
    }

    /** Habilita solo las especies libres; si ya elegí, espero al otro jugador. */
    @Override
    public void modeloActualizado() {
        boolean yaElegi = modelo.getMiEspecie() != null;
        List<String> libres = modelo.getEspeciesDisponibles();
        for (Map.Entry<String, JButton> par : botones.entrySet()) {
            par.getValue().setEnabled(!yaElegi && libres.contains(par.getKey()));
        }
        if (modelo.getMensajeError() != null) {
            etiqueta.setForeground(Constantes.COLOR_ERROR);
            etiqueta.setText(modelo.getMensajeError());
        } else {
            etiqueta.setForeground(Constantes.COLOR_TEXTO);
            etiqueta.setText(yaElegi ? Constantes.TEXTO_ESPERANDO_RIVAL : Constantes.TEXTO_ELEGIR_ANIMAL);
        }
    }

    public JButton getBoton(String especie) {
        return botones.get(especie);
    }

    public String getTextoEtiqueta() {
        return etiqueta.getText();
    }

    private class AccionEspecie implements ActionListener {
        private final String especie;

        AccionEspecie(String especie) {
            this.especie = especie;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controlador.elegirEspecie(especie);
            modeloActualizado();
        }
    }
}
