package jungleattack.red;

import jungleattack.modelo.ModeloCliente;

/**
 * Aplica un mensaje al modelo. Se entrega a SwingUtilities.invokeLater para que
 * el modelo solo se toque desde el hilo de Swing y no desde el hilo de red.
 */
public class ActualizadorModelo implements Runnable {

    private final ModeloCliente modelo;
    private final Mensaje mensaje;

    public ActualizadorModelo(ModeloCliente modelo, Mensaje mensaje) {
        this.modelo = modelo;
        this.mensaje = mensaje;
    }

    @Override
    public void run() {
        modelo.procesarMensaje(mensaje);
    }
}
