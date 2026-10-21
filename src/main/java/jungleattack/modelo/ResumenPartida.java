package jungleattack.modelo;

/** Datos que se muestran al terminar la partida. A = jugador 1, B = jugador 2 (igual que el servidor). */
public class ResumenPartida {

    private final int botsEliminados;
    private final int animalesPerdidosA;
    private final int animalesPerdidosB;
    private final int tazasA;
    private final int tazasB;
    private final long duracionMs;
    private final String apodoA;
    private final String apodoB;
    private final String ganador;

    public ResumenPartida(int botsEliminados, int animalesPerdidosA, int animalesPerdidosB,
                          int tazasA, int tazasB, long duracionMs,
                          String apodoA, String apodoB, String ganador) {
        this.botsEliminados = botsEliminados;
        this.animalesPerdidosA = animalesPerdidosA;
        this.animalesPerdidosB = animalesPerdidosB;
        this.tazasA = tazasA;
        this.tazasB = tazasB;
        this.duracionMs = duracionMs;
        this.apodoA = apodoA;
        this.apodoB = apodoB;
        this.ganador = ganador;
    }

    public int getBotsEliminados() { return botsEliminados; }
    public int getAnimalesPerdidosA() { return animalesPerdidosA; }
    public int getAnimalesPerdidosB() { return animalesPerdidosB; }
    public int getTazasA() { return tazasA; }
    public int getTazasB() { return tazasB; }
    public long getDuracionMs() { return duracionMs; }
    public String getApodoA() { return apodoA; }
    public String getApodoB() { return apodoB; }
    public String getGanador() { return ganador; }
}
