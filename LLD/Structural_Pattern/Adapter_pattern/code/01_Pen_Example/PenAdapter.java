public class PenAdapter implements Pen {
    private final PilotPen pilotPen;

    public PenAdapter() {
        this.pilotPen = new PilotPen();
    }

    public PenAdapter(PilotPen pilotPen) {
        this.pilotPen = pilotPen;
    }

    @Override
    public void write() {
        pilotPen.mark();
    }
}
