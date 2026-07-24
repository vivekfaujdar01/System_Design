public class Main {
    public static void main(String[] args) {
        Pen pen = new PenAdapter();
        Assignment assignment = new Assignment(pen);
        assignment.doAssignment();
    }
}
