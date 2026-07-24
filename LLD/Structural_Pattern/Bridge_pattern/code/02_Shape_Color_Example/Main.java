public class Main {
    public static void main(String[] args) {
        Shape redCircle = new Circle(5.0, new RedColor());
        redCircle.draw();

        Shape blueCircle = new Circle(10.0, new BlueColor());
        blueCircle.draw();

        Shape greenSquare = new Square(4.0, new GreenColor());
        greenSquare.draw();

        Shape redSquare = new Square(8.0, new RedColor());
        redSquare.draw();
    }
}
