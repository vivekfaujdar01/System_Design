public class Square extends Shape {
    private final double side;

    public Square(double side, Color color) {
        super(color);
        this.side = side;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color.applyColor() + " Square with side length " + side + " units.");
    }
}
