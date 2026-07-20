// ============================================================
//  SOLID Principle #3 — Liskov Substitution Principle (LSP)
//  "Subclasses must be substitutable for their base classes."
// ============================================================

// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Violates LSP
//  Classic Rectangle-Square problem.
//  Square "is-a" Rectangle mathematically, but NOT behaviorally.
// ─────────────────────────────────────────────

class BadRectangle {
    protected int width;
    protected int height;

    public void setWidth(int width)   { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public int area() { return width * height; }

    @Override
    public String toString() {
        return "Rectangle(" + width + "x" + height + ") area=" + area();
    }
}

// Square breaks the contract of Rectangle!
// Setting width independently should not affect height (but it does here)
class BadSquare extends BadRectangle {

    @Override
    public void setWidth(int width) {
        // Square must keep width == height — violates Rectangle's contract ❌
        this.width = width;
        this.height = width;
    }

    @Override
    public void setHeight(int height) {
        // Square must keep width == height — violates Rectangle's contract ❌
        this.width = height;
        this.height = height;
    }
}


// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Birds
//  Penguin extends Bird but cannot fly → throws exception ❌
// ─────────────────────────────────────────────

class BadBird {
    public void fly() {
        System.out.println("Bird is flying...");
    }
}

class BadEagle extends BadBird {
    @Override
    public void fly() {
        System.out.println("Eagle soars high in the sky!");
    }
}

class BadPenguin extends BadBird {
    @Override
    public void fly() {
        // Penguins cannot fly — forced to violate parent's promise ❌
        throw new UnsupportedOperationException("Penguins cannot fly!");
    }
}


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Follows LSP
//  Use abstraction properly — separate shape hierarchy.
// ─────────────────────────────────────────────

interface Shape {
    int area();
    String describe();
}

// Rectangle and Square are SEPARATE — neither extends the other
class Rectangle implements Shape {
    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWidth(int width)   { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    @Override
    public int area() { return width * height; }

    @Override
    public String describe() {
        return "Rectangle(" + width + "x" + height + ") → area=" + area();
    }
}

class Square implements Shape {
    private int side;

    public Square(int side) {
        this.side = side;
    }

    public void setSide(int side) { this.side = side; }

    @Override
    public int area() { return side * side; }

    @Override
    public String describe() {
        return "Square(side=" + side + ") → area=" + area();
    }
}


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Birds with proper hierarchy
// ─────────────────────────────────────────────

interface Bird {
    void eat();
    String name();
}

interface FlyingBird extends Bird {
    void fly();
}

// Eagle can fly — implements FlyingBird ✅
class Eagle implements FlyingBird {
    @Override
    public void fly()  { System.out.println(name() + " soars high in the sky!"); }
    @Override
    public void eat()  { System.out.println(name() + " catches fish."); }
    @Override
    public String name() { return "Eagle"; }
}

// Penguin cannot fly — only implements Bird ✅
class Penguin implements Bird {
    @Override
    public void eat()  { System.out.println(name() + " swims and catches fish."); }
    @Override
    public String name() { return "Penguin"; }
}

// Parrot can fly too ✅
class Parrot implements FlyingBird {
    @Override
    public void fly()  { System.out.println(name() + " flutters between branches!"); }
    @Override
    public void eat()  { System.out.println(name() + " eats seeds."); }
    @Override
    public String name() { return "Parrot"; }
}


// ─────────────────────────────────────────────
//  Utility — demonstrates substitution is safe
// ─────────────────────────────────────────────

class ShapeAreaPrinter {
    // Works correctly with ANY Shape (Rectangle, Square, or new shapes) ✅
    public static void printArea(Shape shape) {
        System.out.println(shape.describe());
    }
}

class BirdHandler {
    // Works correctly with ANY Bird ✅
    public static void feedBird(Bird bird) {
        bird.eat();
    }

    // Works correctly with ANY FlyingBird ✅
    public static void letFly(FlyingBird bird) {
        bird.fly();
    }
}


// ─────────────────────────────────────────────
//  Main — Demo
// ─────────────────────────────────────────────

class Liskov_Substitution_Principle {

    public static void main(String[] args) {

        // ─── BAD: Rectangle-Square ───
        System.out.println("=== ❌ BAD DESIGN (Rectangle-Square) ===\n");

        BadRectangle r = new BadRectangle();
        r.setWidth(5);
        r.setHeight(10);
        System.out.println("Expected area: 50, Got: " + r.area());  // ✅ 50

        BadRectangle s = new BadSquare();  // Substituting Square for Rectangle
        s.setWidth(5);
        s.setHeight(10);
        System.out.println("Expected area: 50, Got: " + s.area() + " ❌ (LSP broken!)");  // 100!

        // ─── BAD: Birds ───
        System.out.println("\n=== ❌ BAD DESIGN (Birds) ===\n");

        BadBird eagle = new BadEagle();
        eagle.fly();  // Works fine

        BadBird penguin = new BadPenguin();
        try {
            penguin.fly();  // Throws exception — LSP violated ❌
        } catch (UnsupportedOperationException e) {
            System.out.println("Penguin.fly() threw: " + e.getMessage() + " ❌");
        }

        // ─── GOOD: Shapes ───
        System.out.println("\n=== ✅ GOOD DESIGN (Shapes) ===\n");

        Shape[] shapes = {
            new Rectangle(5, 10),
            new Square(7),
            new Rectangle(3, 8)
        };

        for (Shape shape : shapes) {
            ShapeAreaPrinter.printArea(shape);  // Safe substitution ✅
        }

        // ─── GOOD: Birds ───
        System.out.println("\n=== ✅ GOOD DESIGN (Birds) ===\n");

        FlyingBird[] flyingBirds = { new Eagle(), new Parrot() };
        Bird[] allBirds = { new Eagle(), new Parrot(), new Penguin() };

        System.out.println("All birds can eat:");
        for (Bird bird : allBirds) {
            BirdHandler.feedBird(bird);  // Penguin included — no exception ✅
        }

        System.out.println("\nFlying birds can fly:");
        for (FlyingBird bird : flyingBirds) {
            BirdHandler.letFly(bird);  // Only birds that can fly ✅
        }
    }
}

/*
    EXPECTED OUTPUT:
    =================
    === ❌ BAD DESIGN (Rectangle-Square) ===

    Expected area: 50, Got: 50
    Expected area: 50, Got: 100 ❌ (LSP broken!)

    === ❌ BAD DESIGN (Birds) ===

    Eagle soars high in the sky!
    Penguin.fly() threw: Penguins cannot fly! ❌

    === ✅ GOOD DESIGN (Shapes) ===

    Rectangle(5x10) → area=50
    Square(side=7) → area=49
    Rectangle(3x8) → area=24

    === ✅ GOOD DESIGN (Birds) ===

    All birds can eat:
    Eagle catches fish.
    Parrot eats seeds.
    Penguin swims and catches fish.

    Flying birds can fly:
    Eagle soars high in the sky!
    Parrot flutters between branches!
*/
