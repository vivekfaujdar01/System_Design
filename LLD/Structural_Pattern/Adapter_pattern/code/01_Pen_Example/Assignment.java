public class Assignment {
    private final Pen pen;

    public Assignment(Pen pen) {
        this.pen = pen;
    }

    public void doAssignment() {
        pen.write();
    }
}
