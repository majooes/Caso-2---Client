package jungleattack.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import jungleattack.controlador.ControladorJuego;
import jungleattack.controlador.ControladorMouse;
import jungleattack.modelo.AnimalVista;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.ObjetoVista;
import jungleattack.modelo.ObservadorModelo;
import jungleattack.modelo.Punto;
import jungleattack.util.Constantes;

/**
 * Dibuja la zona que está viendo el jugador. Solo dibuja: no tiene lógica de juego.
 * Un Timer lo redibuja 30 veces por segundo y mueve a los animales suavemente
 * entre los dos últimos snapshots del servidor.
 */
public class PanelJuego extends JPanel implements ObservadorModelo, ActionListener {

    private final ModeloCliente modelo;
    private final ControladorJuego controlador;
    private final ControladorMouse mouse;
    private final Timer temporizador;
    private long tiempoUltimoSnapshot = System.currentTimeMillis();
    private List<Integer> zonasMostradas = new ArrayList<>();
    private int zonaMostrada = -1;

    public PanelJuego(ModeloCliente modelo, ControladorJuego controlador, ControladorMouse mouse) {
        this.modelo = modelo;
        this.controlador = controlador;
        this.mouse = mouse;
        setPreferredSize(new Dimension(Constantes.ANCHO_ZONA, Constantes.ALTO_ZONA));
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        temporizador = new Timer(Constantes.MS_POR_SEGUNDO / Constantes.FPS, this);
        temporizador.start();
    }

    // ---------- Dibujo ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        dibujarMapa(g2);
        dibujarObjetos(g2);
        dibujarAnimales(g2);
        dibujarSeleccion(g2);
        dibujarHud(g2);
    }

    /** Fondo del bosque y los caminos hacia las zonas vecinas. */
    private void dibujarMapa(Graphics2D g) {
        BufferedImage fondo = CacheImagenes.obtener(Constantes.IMAGEN_FONDO);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, Constantes.ANCHO_ZONA, Constantes.ALTO_ZONA, null);
        }
        int zona = modelo.getZonaActual();
        int columna = zona % Constantes.COLUMNAS_MAPA;
        int fila = zona / Constantes.COLUMNAS_MAPA;
        int cx = Constantes.ANCHO_ZONA / 2;
        int cy = Constantes.ALTO_ZONA / 2;

        List<int[]> tramos = new ArrayList<>();
        if (columna > 0) {
            tramos.add(new int[] {cx, cy, 0, cy});
        }
        if (columna < Constantes.COLUMNAS_MAPA - 1) {
            tramos.add(new int[] {cx, cy, Constantes.ANCHO_ZONA, cy});
        }
        if (fila > 0) {
            tramos.add(new int[] {cx, cy, cx, 0});
        }
        if (fila < Constantes.FILAS_MAPA - 1) {
            tramos.add(new int[] {cx, cy, cx, Constantes.ALTO_ZONA});
        }
        // Primero el borde oscuro de todos los tramos y encima el color del camino
        g.setStroke(new BasicStroke(Constantes.ANCHO_CAMINO + 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Constantes.COLOR_BORDE_CAMINO);
        for (int[] t : tramos) {
            g.drawLine(t[0], t[1], t[2], t[3]);
        }
        g.setStroke(new BasicStroke(Constantes.ANCHO_CAMINO, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Constantes.COLOR_CAMINO);
        for (int[] t : tramos) {
            g.drawLine(t[0], t[1], t[2], t[3]);
        }
        g.setStroke(new BasicStroke(1f));
    }

    private void dibujarObjetos(Graphics2D g) {
        for (ObjetoVista o : modelo.getObjetos()) {
            if (Constantes.TIPO_TAZA.equals(o.getTipo())) {
                dibujarImagen(g, Constantes.IMAGEN_TAZA, o.getPosicion(), Constantes.TAMANO_TAZA);
            } else if (Constantes.TIPO_BANDERA.equals(o.getTipo())) {
                dibujarImagen(g, Constantes.IMAGEN_BANDERA, o.getPosicion(), Constantes.TAMANO_BANDERA);
            }
        }
    }

    private void dibujarAnimales(Graphics2D g) {
        double t = calcularFraccion(System.currentTimeMillis(), tiempoUltimoSnapshot);
        for (AnimalVista a : modelo.getAnimales()) {
            Punto p = a.posicionInterpolada(t);
            int medio = Constantes.TAMANO_ANIMAL / 2;

            g.setColor(colorDe(a));
            g.setStroke(new BasicStroke(3f));
            g.drawOval((int) p.getX() - medio - 2, (int) p.getY() - medio - 2,
                       Constantes.TAMANO_ANIMAL + 4, Constantes.TAMANO_ANIMAL + 4);
            dibujarImagen(g, a.getEspecie().toLowerCase(), p, Constantes.TAMANO_ANIMAL);
            if (a.isSeleccionado()) {
                g.setColor(Constantes.COLOR_SELECCION);
                g.setStroke(new BasicStroke(3f));
                g.drawOval((int) p.getX() - medio - 6, (int) p.getY() - medio - 6,
                           Constantes.TAMANO_ANIMAL + 12, Constantes.TAMANO_ANIMAL + 12);
            }
            g.setStroke(new BasicStroke(1f));
            dibujarBarraEnergia(g, a, p);
        }
    }

    /** Barra verde sobre el animal; se pone roja cuando le queda poca energía. */
    private void dibujarBarraEnergia(Graphics2D g, AnimalVista a, Punto p) {
        double proporcion = Math.max(0.0, Math.min(1.0, a.getEnergia() / (double) Constantes.MAX_ENERGIA));
        int x = (int) p.getX() - Constantes.ANCHO_BARRA_ENERGIA / 2;
        int y = (int) p.getY() - Constantes.TAMANO_ANIMAL / 2 - 12;
        g.setColor(Constantes.COLOR_BARRA_FONDO);
        g.fillRect(x, y, Constantes.ANCHO_BARRA_ENERGIA, Constantes.ALTO_BARRA_ENERGIA);
        g.setColor(proporcion < Constantes.ENERGIA_BAJA
                ? Constantes.COLOR_ENERGIA_BAJA : Constantes.COLOR_ENERGIA_ALTA);
        g.fillRect(x, y, (int) (Constantes.ANCHO_BARRA_ENERGIA * proporcion), Constantes.ALTO_BARRA_ENERGIA);
    }

    /** Rectángulo amarillo mientras el jugador arrastra el mouse para seleccionar. */
    private void dibujarSeleccion(Graphics2D g) {
        Punto inicio = mouse.getPuntoInicio();
        Punto actual = mouse.getPuntoActual();
        if (inicio == null || actual == null) {
            return;
        }
        int x = (int) Math.min(inicio.getX(), actual.getX());
        int y = (int) Math.min(inicio.getY(), actual.getY());
        int ancho = (int) Math.abs(inicio.getX() - actual.getX());
        int alto = (int) Math.abs(inicio.getY() - actual.getY());
        g.setColor(Constantes.COLOR_RECTANGULO);
        g.fillRect(x, y, ancho, alto);
        g.setColor(Constantes.COLOR_SELECCION);
        g.drawRect(x, y, ancho, alto);
    }

    /** Daño, tazas, bots eliminados y zona actual. */
    private void dibujarHud(Graphics2D g) {
        String texto = String.format(Constantes.TEXTO_HUD, modelo.getCapacidadDanio(),
                modelo.getTazasConsumidas(), modelo.getBotsEliminados(), modelo.getZonaActual() + 1);
        int alto = g.getFontMetrics().getHeight();
        int ancho = g.getFontMetrics().stringWidth(texto);
        int y = Constantes.ALTO_ZONA - 10;
        g.setColor(Constantes.COLOR_HUD_FONDO);
        g.fillRect(6, y - alto + 4, ancho + 12, alto + 4);
        g.setColor(Constantes.COLOR_HUD_TEXTO);
        g.drawString(texto, 12, y);
    }

    /** Dibuja una imagen del caché centrada en el punto. Si no existe, no dibuja nada. */
    private void dibujarImagen(Graphics2D g, String nombre, Punto centro, int tamano) {
        BufferedImage imagen = CacheImagenes.obtener(nombre);
        if (imagen != null) {
            g.drawImage(imagen, (int) centro.getX() - tamano / 2, (int) centro.getY() - tamano / 2,
                        tamano, tamano, null);
        }
    }

    private Color colorDe(AnimalVista a) {
        if (a.isEsMio()) {
            return Constantes.COLOR_MIO;
        }
        return a.isEsBot() ? Constantes.COLOR_BOT : Constantes.COLOR_RIVAL;
    }

    /**
     * Cuánto del camino entre los dos últimos snapshots ya se recorrió (0 = recién llegó el
     * último, 1 = ya debería llegar el siguiente).
     */
    static double calcularFraccion(long ahoraMs, long ultimoSnapshotMs) {
        return (ahoraMs - ultimoSnapshotMs) / (double) Constantes.INTERVALO_SNAPSHOT_MS;
    }

    // ---------- Botones de zona ----------

    /** Un botón por cada zona donde tengo animales; sirve para cambiar de zona. */
    private void actualizarBotonesZona() {
        List<Integer> zonas = modelo.getZonasConAnimales();
        if (zonas.equals(zonasMostradas) && modelo.getZonaActual() == zonaMostrada) {
            return;
        }
        zonasMostradas = new ArrayList<>(zonas);
        zonaMostrada = modelo.getZonaActual();
        removeAll();
        for (Integer id : zonasMostradas) {
            JButton boton = new JButton(String.format(Constantes.TEXTO_ZONA, id + 1));
            boton.setEnabled(id != zonaMostrada);
            boton.addActionListener(new AccionZona(id));
            add(boton);
        }
        revalidate();
        repaint();
    }

    public int contarBotonesZona() {
        int total = 0;
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                total++;
            }
        }
        return total;
    }

    // ---------- Eventos ----------

    @Override
    public void modeloActualizado() {
        tiempoUltimoSnapshot = System.currentTimeMillis();
        actualizarBotonesZona();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private class AccionZona implements ActionListener {
        private final int idZona;

        AccionZona(int idZona) {
            this.idZona = idZona;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controlador.cambiarZona(idZona);
        }
    }
}
