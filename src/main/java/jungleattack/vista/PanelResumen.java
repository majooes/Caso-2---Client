package jungleattack.vista;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import jungleattack.modelo.ResumenPartida;
import jungleattack.util.Constantes;

/** Pantalla final: quién ganó y el resumen de la partida. */
public class PanelResumen extends JPanel {

    private final JTextArea texto = new JTextArea();

    public PanelResumen() {
        setLayout(new BorderLayout(Constantes.ESPACIO_RESUMEN_X, Constantes.ESPACIO_RESUMEN_Y));
        setBorder(BorderFactory.createEmptyBorder(Constantes.MARGEN_RESUMEN_VERTICAL,
                Constantes.MARGEN_RESUMEN_HORIZONTAL, Constantes.MARGEN_RESUMEN_VERTICAL,
                Constantes.MARGEN_RESUMEN_HORIZONTAL));
        JLabel titulo = new JLabel(Constantes.TEXTO_RESUMEN_TITULO, SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Constantes.FUENTE_RESUMEN_TITULO));
        add(titulo, BorderLayout.NORTH);
        texto.setEditable(false);
        texto.setFont(texto.getFont().deriveFont(Constantes.FUENTE_RESUMEN_TEXTO));
        texto.setOpaque(false);
        add(texto, BorderLayout.CENTER);
    }

    public void mostrarResumen(ResumenPartida r) {
        if (r == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Ganador: ").append(r.getGanador()).append("\n\n");
        sb.append("Bots eliminados: ").append(r.getBotsEliminados()).append("\n");
        sb.append("Animales eliminados de ").append(r.getApodoA()).append(": ")
          .append(r.getAnimalesPerdidosA()).append("\n");
        sb.append("Animales eliminados de ").append(r.getApodoB()).append(": ")
          .append(r.getAnimalesPerdidosB()).append("\n");
        sb.append("Tazas de café de ").append(r.getApodoA()).append(": ")
          .append(r.getTazasA()).append("\n");
        sb.append("Tazas de café de ").append(r.getApodoB()).append(": ")
          .append(r.getTazasB()).append("\n");
        sb.append("Duración: ").append(formatearDuracion(r.getDuracionMs()));
        texto.setText(sb.toString());
    }

    /** Convierte milisegundos a minutos:segundos, por ejemplo 90000 -> "1:30". */
    public static String formatearDuracion(long ms) {
        long segundosTotales = ms / Constantes.MS_POR_SEGUNDO;
        long minutos = segundosTotales / Constantes.SEGUNDOS_POR_MINUTO;
        long segundos = segundosTotales % Constantes.SEGUNDOS_POR_MINUTO;
        return String.format("%d:%02d", minutos, segundos);
    }

    String getTexto() {
        return texto.getText();
    }
}
