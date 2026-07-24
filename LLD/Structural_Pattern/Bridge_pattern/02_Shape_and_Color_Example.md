# Module 2: Shape and Color Rendering Example

The **Shape and Color** problem is one of the classic scenarios used in system design interviews to demonstrate the power of the Bridge Pattern.

---

## 1. Problem Statement

Suppose you are building a graphics rendering engine that supports various **Shapes** (e.g., Circle, Square, Triangle) in various **Colors** (e.g., Red, Blue, Green).

### Pure Inheritance Approach (Cartesian Product Failure)
If you rely solely on class inheritance, every shape variant must be combined with every color variant:

```
                      +-------------------+
                      |       Shape       |
                      +-------------------+
                                ^
         ┌──────────────────────┼──────────────────────┐
         │                      │                      │
  +--------------+       +--------------+       +--------------+
  |    Circle    |       |    Square    |       |   Triangle   |
  +--------------+       +--------------+       +--------------+
     ^   ^   ^              ^   ^   ^              ^   ^   ^
     │   │   │              │   │   │              │   │   │
  Red Blue Green         Red Blue Green         Red Blue Green
Circle Circle Circle   Square Square Square Triangle Triangle Triangle
```

#### Results of Pure Inheritance:
* 3 Shapes $\times$ 3 Colors = **9 Subclasses** (`RedCircle`, `BlueCircle`, `GreenCircle`, `RedSquare`, etc.)
* Adding 1 new Shape (e.g., Rectangle) requires creating **3 new classes** (`RedRectangle`, `BlueRectangle`, `GreenRectangle`).
* Adding 1 new Color (e.g., Yellow) requires creating **4 new classes** (`YellowCircle`, `YellowSquare`, `YellowTriangle`, `YellowRectangle`).

---

## 2. Bridge Pattern Solution

Instead of multiplying subclasses via inheritance, we decouple **Shape** (Abstraction) from **Color** (Implementor) using composition:

```
               +--------------------+                             +---------------------+
               |   Shape (Abstract) | ────── Bridge (has-a) ─────►|  Color (Interface)  |
               +--------------------+                             +---------------------+
               | # color: Color     |                             | + applyColor()      |
               | + draw()           |                             +---------------------+
               +--------------------+                                        ^
                         ^                                                   │
      ┌──────────────────┴──────────────────┐             ┌──────────────────┼──────────────────┐
      │                                     │             │                  │                  │
+-----------+                         +-----------+ +-----------+      +-----------+      +-----------+
|  Circle   |                         |  Square   | | RedColor  |      | BlueColor |      |GreenColor |
+-----------+                         +-----------+ +-----------+      +-----------+      +-----------+
```

Now:
* Shapes: 3 classes (`Shape`, `Circle`, `Square`)
* Colors: 3 classes (`Color`, `RedColor`, `BlueColor`)
* Total: **6 classes** instead of 9!

---

## 3. Complete Step-by-Step Java Implementation

### Step 1: Implementor Interface (`Color.java`)
```java
// Implementor Interface
public interface Color {
    String applyColor();
}
```

### Step 2: Concrete Implementors (`RedColor.java`, `BlueColor.java`, `GreenColor.java`)
```java
// Concrete Implementor 1: Red
public class RedColor implements Color {
    @Override
    public String applyColor() {
        return "RED";
    }
}

// Concrete Implementor 2: Blue
public class BlueColor implements Color {
    @Override
    public String applyColor() {
        return "BLUE";
    }
}

// Concrete Implementor 3: Green
public class GreenColor implements Color {
    @Override
    public String applyColor() {
        return "GREEN";
    }
}
```

### Step 3: Abstraction (`Shape.java`)
```java
// Abstraction holding the Bridge reference to Color Implementor
public abstract class Shape {
    protected final Color color; // The Bridge!

    public Shape(Color color) {
        this.color = color;
    }

    public abstract void draw();
}
```

### Step 4: Refined Abstractions (`Circle.java` & `Square.java`)
```java
// Refined Abstraction 1: Circle
public class Circle extends Shape {
    private final double radius;

    public Circle(double radius, Color color) {
        super(color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color.applyColor() + " Circle with radius " + radius + " units.");
    }
}

// Refined Abstraction 2: Square
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
```

### Step 5: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Create Red Circle
        Shape redCircle = new Circle(5.0, new RedColor());
        redCircle.draw();

        // Create Blue Circle
        Shape blueCircle = new Circle(10.0, new BlueColor());
        blueCircle.draw();

        // Create Green Square
        Shape greenSquare = new Square(4.0, new GreenColor());
        greenSquare.draw();

        // Create Red Square
        Shape redSquare = new Square(8.0, new RedColor());
        redSquare.draw();
    }
}
```

### Execution Output
```text
Drawing a RED Circle with radius 5.0 units.
Drawing a BLUE Circle with radius 10.0 units.
Drawing a GREEN Square with side length 4.0 units.
Drawing a RED Square with side length 8.0 units.
```

---

## 4. Control Flow Analysis

```text
main()
  │
  ├──► redCircle.draw()
  │      └──► Circle.draw() ──► color.applyColor() [RedColor] ──► Returns "RED"
  │             └──► Prints: "Drawing a RED Circle..."
  │
  ├──► greenSquare.draw()
  │      └──► Square.draw() ──► color.applyColor() [GreenColor] ──► Returns "GREEN"
  │             └──► Prints: "Drawing a GREEN Square..."
```

---

## 5. Mathematical Comparison: Inheritance vs. Bridge

| Metrics | Inheritance Approach | Bridge Pattern Approach |
| :--- | :--- | :--- |
| **Number of Shapes ($M$)** | $3$ | $3$ |
| **Number of Colors ($N$)** | $3$ | $3$ |
| **Total Classes Created** | $M \times N = 9$ classes | $M + N = 6$ classes |
| **Adding 5 New Colors** | $+15$ new subclasses! | $+5$ new color classes! |
| **Coupling Level** | Extremely Tight | Loose (Decoupled) |

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/02_Shape_Color_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Bridge_pattern/code/02_Shape_Color_Example).
