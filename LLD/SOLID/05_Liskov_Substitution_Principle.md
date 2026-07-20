# L — Liskov Substitution Principle (LSP)

> **"Objects of a subclass should be replaceable with objects of the superclass without breaking the program."**
> — Barbara Liskov, 1987

---

## 📖 Definition

The **Liskov Substitution Principle** states that if class `B` is a subclass of class `A`, then anywhere you use an object of type `A`, you should be able to use an object of type `B` without the program behaving incorrectly.

In simple terms: **Subclasses must honor the contract of the parent class.**

Formal definition:
> *If S is a subtype of T, then objects of type T may be replaced with objects of type S without altering any desirable property of the program.*

---

## 🧠 Why Does It Matter?

LSP violations lead to:
- Subclasses throwing unexpected exceptions for operations the parent promises to support
- `instanceof` checks scattered through the code to handle "special" subtypes
- Broken polymorphism — you can't trust that substitution will work
- Fragile inheritance hierarchies that are hard to extend

---

## ❌ Bad Example (Violates LSP)

The classic example: **Square extends Rectangle**

```java
class Rectangle {
    protected int width;
    protected int height;
    public void setWidth(int w)  { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    // Square must keep width == height at all times
    @Override
    public void setWidth(int w)  { this.width = w; this.height = w; }  // ❌
    @Override
    public void setHeight(int h) { this.width = h; this.height = h; }  // ❌
}
```

**Why it breaks**:
```java
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(10);
System.out.println(rect.area()); // Expected: 50, Actual: 100 ❌
```

A `Square` cannot be substituted for a `Rectangle` without breaking the program.

---

## ✅ Good Example (Follows LSP)

```java
interface Shape {
    int area();
}

class Rectangle implements Shape {
    private int width, height;
    public Rectangle(int w, int h) { this.width = w; this.height = h; }
    public int area() { return width * height; }
}

class Square implements Shape {
    private int side;
    public Square(int s) { this.side = s; }
    public int area() { return side * side; }
}
```

Now both implement `Shape` and can be substituted freely because neither overrides mutable setters.

---

## 🔍 Rules for LSP (Substitution Rules)

| Rule | Description |
|------|-------------|
| **Preconditions** | Subclass cannot strengthen preconditions (accept less input) |
| **Postconditions** | Subclass cannot weaken postconditions (return less/wrong output) |
| **Invariants** | Subclass must preserve all invariants of the parent |
| **No new exceptions** | Subclass cannot throw exceptions not declared in parent |
| **History constraint** | Subclass cannot modify state in ways that parent doesn't allow |

---

## 🌍 Real-World Analogies

| Scenario | LSP Violation | LSP Applied |
|---------|--------------|-------------|
| Birds | `Penguin extends Bird` → `fly()` throws exception | `FlyingBird` and `NonFlyingBird` interfaces |
| Vehicles | `Bicycle extends Vehicle` → `fillFuel()` doesn't make sense | Separate abstractions for fuel-based and non-fuel vehicles |
| Animals | `Fish extends Animal` → `walk()` throws exception | `WalkingAnimal`, `SwimmingAnimal` interfaces |

---

## 📌 Signs of LSP Violation

- Overriding a method to throw `UnsupportedOperationException`
- `instanceof` checks in client code to handle specific subtypes
- Empty method overrides (doing nothing)
- Subclass return types that are less useful than the parent's
- Comments like *"don't call this method on this subclass"*

---

## 📌 Key Takeaways

- LSP ensures that inheritance is used **correctly and meaningfully**
- Use inheritance only when there is a true **"is-a"** relationship
- Prefer **composition over inheritance** when LSP would be violated
- Design parent classes with substitutability in mind
- LSP violations often surface when subclasses need to **weaken** parent behavior

---

## 🔗 Relationship with Other Principles

- LSP violations often violate **OCP** too — you need `instanceof` checks to handle special subtypes
- LSP encourages correct use of **interfaces** (ISP)
- Proper application of LSP makes systems more robust with **polymorphism**

---

## 📄 See Code Example

➡️ [`06_Liskov_Substitution_Principle.java`](./06_Liskov_Substitution_Principle.java)

---

*Previous: [03_Open_Closed_Principle.md](./03_Open_Closed_Principle.md)*
*Next: [07_Interface_Segregation_Principle.md](./07_Interface_Segregation_Principle.md)*
