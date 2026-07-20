// ============================================================
//  SOLID Principle #2 — Open/Closed Principle (OCP)
//  "Open for extension, closed for modification."
// ============================================================

// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Violates OCP
//  Adding a new shape forces modification of AreaCalculator.
//  Every new shape = modifying existing tested code.
// ─────────────────────────────────────────────

class BadAreaCalculator {

    // Every time a new shape is added, this method MUST be modified ❌
    public double calculateArea(Object shape) {
        if (shape instanceof BadCircle) {
            BadCircle c = (BadCircle) shape;
            return Math.PI * c.radius * c.radius;
        } else if (shape instanceof BadRectangle) {
            BadRectangle r = (BadRectangle) shape;
            return r.length * r.width;
        } else if (shape instanceof BadTriangle) {
            BadTriangle t = (BadTriangle) shape;
            return 0.5 * t.base * t.height;
        }
        // Adding a new shape? Modify this class ❌
        return 0;
    }
}

class BadCircle    { double radius; BadCircle(double r) { radius = r; } }
class BadRectangle { double length, width; BadRectangle(double l, double w) { length = l; width = w; } }
class BadTriangle  { double base, height; BadTriangle(double b, double h) { base = b; height = h; } }


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Follows OCP
//  New shapes are ADDED (not modifying AreaCalculator).
//  AreaCalculator is closed for modification ✅
// ─────────────────────────────────────────────

// Step 1: Define an abstraction (contract)
interface Shape {
    double area();
}

// Step 2: Implement concrete shapes — each knows its own area formula
class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public String toString() {
        return "Circle(radius=" + radius + ")";
    }
}

class Rectangle implements Shape {
    private double length;
    private double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double area() {
        return length * width;
    }

    @Override
    public String toString() {
        return "Rectangle(" + length + "x" + width + ")";
    }
}

class Triangle implements Shape {
    private double base;
    private double height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double area() {
        return 0.5 * base * height;
    }

    @Override
    public String toString() {
        return "Triangle(base=" + base + ", height=" + height + ")";
    }
}

// NEW SHAPE — Just add a new class, zero modification to AreaCalculator ✅
class Pentagon implements Shape {
    private double side;

    public Pentagon(double side) {
        this.side = side;
    }

    @Override
    public double area() {
        return (Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) / 4) * side * side;
    }

    @Override
    public String toString() {
        return "Pentagon(side=" + side + ")";
    }
}

// Step 3: AreaCalculator is CLOSED for modification — works with any Shape ✅
class AreaCalculator {

    // This method NEVER changes regardless of new shapes added
    public double totalArea(Shape[] shapes) {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.area();
        }
        return total;
    }

    public void printAreas(Shape[] shapes) {
        for (Shape shape : shapes) {
            System.out.printf("%-35s → Area: %.2f%n", shape.toString(), shape.area());
        }
    }
}


// ─────────────────────────────────────────────
//  Main — Demo
// ─────────────────────────────────────────────

class Open_Closed_Principle {

    public static void main(String[] args) {

        System.out.println("=== ❌ BAD DESIGN (Violates OCP) ===\n");

        BadAreaCalculator badCalc = new BadAreaCalculator();
        System.out.println("Circle Area   : " + String.format("%.2f", badCalc.calculateArea(new BadCircle(5))));
        System.out.println("Rectangle Area: " + String.format("%.2f", badCalc.calculateArea(new BadRectangle(4, 6))));
        System.out.println("Triangle Area : " + String.format("%.2f", badCalc.calculateArea(new BadTriangle(3, 8))));

        System.out.println("\n=== ✅ GOOD DESIGN (Follows OCP) ===\n");

        Shape[] shapes = {
            new Circle(5),
            new Rectangle(4, 6),
            new Triangle(3, 8),
            new Pentagon(4)   // New shape — AreaCalculator NOT modified ✅
        };

        AreaCalculator calculator = new AreaCalculator();
        calculator.printAreas(shapes);

        System.out.println("\nTotal Area: " + String.format("%.2f", calculator.totalArea(shapes)));
    }
}

/*
    EXPECTED OUTPUT:
    =================
    === ❌ BAD DESIGN (Violates OCP) ===

    Circle Area   : 78.54
    Rectangle Area: 24.00
    Triangle Area : 12.00

    === ✅ GOOD DESIGN (Follows OCP) ===

    Circle(radius=5.0)                  → Area: 78.54
    Rectangle(4.0x6.0)                  → Area: 24.00
    Triangle(base=3.0, height=8.0)      → Area: 12.00
    Pentagon(side=4.0)                  → Area: 27.53

    Total Area: 142.07
*/
