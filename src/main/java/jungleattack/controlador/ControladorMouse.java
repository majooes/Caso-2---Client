package jungleattack.controlador;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import jungleattack.modelo.AnimalVista;
import jungleattack.modelo.ModeloCliente;
import jungleattack.modelo.Punto;
import jungleattack.util.Constantes;

/**
 * Traduce el mouse en acciones:
 *  - arrastrar: selecciona mis animales dentro del rectángulo
 *  - clic izquierdo sobre un animal mío: lo selecciona solo a él
 *  - clic izquierdo sobre un enemigo o bot: los seleccionados lo atacan
 *  - clic izquierdo en terreno libre: los seleccionados caminan hacia ahí
 *  - clic derecho: quita la selección
 */
public class ControladorMouse extends MouseAdapter {

    private final ControladorJuego controlador;
    private final ModeloCliente modelo;
    private Punto puntoInicio;
    private Punto puntoActual;

    public ControladorMouse(ControladorJuego controlador, ModeloCliente modelo) {
        this.controlador = controlador;
        this.modelo = modelo;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        puntoInicio = new Punto(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (puntoInicio != null) {
            puntoActual = new Punto(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (puntoInicio == null) {
            return;
        }
        Punto fin = new Punto(e.getX(), e.getY());
        if (SwingUtilities.isRightMouseButton(e)) {
            modelo.limpiarSeleccion();
        } else if (puntoInicio.distanciaA(fin) > Constantes.UMBRAL_ARRASTRE) {
            modelo.seleccionarAnimales(puntoInicio, fin);
        } else {
            manejarClic(fin);
        }
        puntoInicio = null;
        puntoActual = null;
    }

    /** Esquina donde empezó el arrastre, o null si no hay arrastre en curso. */
    public Punto getPuntoInicio() {
        return puntoInicio;
    }

    /** Posición actual del mouse durante el arrastre, o null si no hay arrastre. */
    public Punto getPuntoActual() {
        return puntoActual;
    }

    private void manejarClic(Punto punto) {
        AnimalVista animal = modelo.buscarAnimalEn(punto.getX(), punto.getY());
        if (animal == null) {
            controlador.ordenarMover(punto);
        } else if (animal.isEsMio()) {
            modelo.limpiarSeleccion();
            animal.setSeleccionado(true);
        } else {
            controlador.ordenarAtacar(animal.getId());
        }
    }
}
