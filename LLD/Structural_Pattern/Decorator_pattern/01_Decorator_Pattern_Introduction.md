# Module 1: Decorator Design Pattern - Introduction

## 1. Real-Life Analogy

### Analogy 1: Coffee Customization in a Cafe
Imagine ordering a coffee at Starbucks:
* You start with a **Simple Black Coffee** (Base Cost: ₹100).
* You want to add **Milk** (+ ₹30).
* You want to add **Sugar** (+ ₹10).
* You want to top it with **Whipped Cream** (+ ₹40).

```
   [ Whipped Cream Decorator ] (₹40)
          │ wraps
          ▼
   [ Sugar Decorator ] (₹10)
          │ wraps
          ▼
   [ Milk Decorator ] (₹30)
          │ wraps
          ▼
   [ Simple Coffee (Base Component) ] (₹100)
```

Instead of creating separate classes for every possible coffee combination (`CoffeeWithMilk`, `CoffeeWithMilkAndSugar`, `CoffeeWithMilkAndWhip`, `CoffeeWithSugarAndWhip`...), you start with a base coffee and dynamically **wrap** it with decorators at runtime!

### Analogy 2: Dressing for Winter Weather
When getting dressed for cold weather:
1. Base Layer: T-Shirt.
2. Second Layer (Decorator): Sweater over T-Shirt.
3. Outer Layer (Decorator): Raincoat over Sweater.

Each layer adds warmth or waterproofing without altering the clothes underneath. You can add or remove layers dynamically depending on the weather.

---

## 2. The Subclass Explosion Problem ($2^N$ Class Combinations)

Suppose you have a base `Coffee` class and 4 optional add-ons: *Milk*, *Sugar*, *Whip*, *Caramel*.

### Without Decorator Pattern (Pure Inheritance)
If you attempt to use static inheritance to support every possible combination of 4 add-ons:
* Number of possible combinations = $2^4 = 16$ subclasses!
* If you have 10 add-ons = $2^{10} = 1024$ subclasses!

```
                               +-------------------+
                               |      Coffee       |
                               +-------------------+
                                         ^
         ┌───────────────────────────────┼───────────────────────────────┐
         │                               │                               │
+------------------+           +-------------------+           +-------------------+
|  CoffeeWithMilk  |           |  CoffeeWithSugar  |           | CoffeeMilkSugar   | ... (16 classes!)
+------------------+           +-------------------+           +-------------------+
```

### With Decorator Pattern (Dynamic Composition)
You create:
* **1 Base Component**: `SimpleCoffee`
* **4 Decorator Classes**: `MilkDecorator`, `SugarDecorator`, `WhipDecorator`, `CaramelDecorator`
* Total classes needed: **5 classes** instead of 16!

> **Core Insight**: Decorator converts exponential $2^N$ class hierarchies into a linear $1 + N$ set of reusable wrapper classes.

---

## 3. Core Definition & Intent

> **Definition**: The **Decorator Design Pattern** attaches additional responsibilities to an object dynamically at runtime. Decorators provide a flexible alternative to subclassing for extending functionality without modifying original class code.

* **Intent**: Add behavior to individual objects dynamically without affecting other objects of the same class.
* **Category**: Structural Design Pattern.
* **GoF Definition**: "Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality."

---

## 4. Components of Decorator Pattern

```
                       +---------------------------+
                       |   Component (Interface)   |
                       +---------------------------+
                       | + getDescription()        |
                       | + getCost()               |
                       +---------------------------+
                                     ^
                                     │
             ┌───────────────────────┴───────────────────────┐
             │                                               │
+---------------------------+                   +---------------------------+
|     ConcreteComponent     |                   |  CoffeeDecorator (Base)   |
+---------------------------+                   +---------------------------+
| + getDescription()        |                   | # decoratedCoffee: Coffee |
| + getCost()               |                   | + getDescription()        |
+---------------------------+                   | + getCost()               |
                                                +---------------------------+
                                                              ^
                                                              │ implements / extends
                                             ┌────────────────┴────────────────┐
                                             │                                 │
                                +-------------------------+       +-------------------------+
                                |     MilkDecorator       |       |     SugarDecorator      |
                                +-------------------------+       +-------------------------+
                                | + getDescription()      |       | + getDescription()      |
                                | + getCost()             |       | + getCost()             |
                                +-------------------------+       +-------------------------+
```

1. **Component**: Interface defining methods for objects that can have responsibilities added to them dynamically.
2. **Concrete Component**: The basic object being decorated (e.g., `SimpleCoffee`).
3. **Decorator (Base Decorator)**: Abstract class implementing `Component` and holding a reference (`HAS-A`) to a `Component` instance.
4. **Concrete Decorators**: Classes extending the base decorator to add specific features or behavior before/after delegating to the wrapped component.

---

## 5. Complete Java Code Example: Coffee Customization

### Step 1: Component Interface (`Coffee.java`)
```java
// Target Component Interface
public interface Coffee {
    String getDescription();
    double getCost();
}
```

### Step 2: Concrete Component (`SimpleCoffee.java`)
```java
// Concrete Base Component
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Plain Black Coffee";
    }

    @Override
    public double getCost() {
        return 100.0; // Base Price
    }
}
```

### Step 3: Base Decorator (`CoffeeDecorator.java`)
```java
// Base Decorator implementing Coffee interface and wrapping another Coffee object
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee; // The wrapped component!

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}
```

### Step 4: Concrete Decorators (`MilkDecorator.java`, `SugarDecorator.java`, `WhipDecorator.java`)
```java
// Concrete Decorator 1: Milk
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Steamed Milk";
    }

    @Override
    public double getCost() {
        return super.getCost() + 30.0; // Adds 30.0 for Milk
    }
}

// Concrete Decorator 2: Sugar
public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Organic Sugar";
    }

    @Override
    public double getCost() {
        return super.getCost() + 10.0; // Adds 10.0 for Sugar
    }
}

// Concrete Decorator 3: Whipped Cream
public class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Whipped Cream";
    }

    @Override
    public double getCost() {
        return super.getCost() + 40.0; // Adds 40.0 for Whipped Cream
    }
}
```

### Step 5: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Order 1: Plain Coffee
        Coffee coffee1 = new SimpleCoffee();
        System.out.println("Order 1: " + coffee1.getDescription());
        System.out.println("Total Cost: ₹" + coffee1.getCost());

        // Order 2: Coffee + Milk + Sugar
        System.out.println("\n--- Preparing Order 2 ---");
        Coffee coffee2 = new SimpleCoffee();
        coffee2 = new MilkDecorator(coffee2);
        coffee2 = new SugarDecorator(coffee2);
        System.out.println("Order 2: " + coffee2.getDescription());
        System.out.println("Total Cost: ₹" + coffee2.getCost());

        // Order 3: Fully Loaded Coffee (Simple + Milk + Sugar + Whip)
        System.out.println("\n--- Preparing Order 3 ---");
        Coffee coffee3 = new WhipDecorator(
                            new SugarDecorator(
                                new MilkDecorator(
                                    new SimpleCoffee()
                                )
                            )
                        );
        System.out.println("Order 3: " + coffee3.getDescription());
        System.out.println("Total Cost: ₹" + coffee3.getCost());
    }
}
```

### Output
```text
Order 1: Plain Black Coffee
Total Cost: ₹100.0

--- Preparing Order 2 ---
Order 2: Plain Black Coffee + Steamed Milk + Organic Sugar
Total Cost: ₹140.0

--- Preparing Order 3 ---
Order 3: Plain Black Coffee + Steamed Milk + Organic Sugar + Whipped Cream
Total Cost: ₹180.0
```

---

## 6. Program Control & Layering Flow

```text
coffee3.getCost()
  │
  ├──► WhipDecorator.getCost() (₹40.0 + inner)
  │      └──► SugarDecorator.getCost() (₹10.0 + inner)
  │             └──► MilkDecorator.getCost() (₹30.0 + inner)
  │                    └──► SimpleCoffee.getCost() (₹100.0)
  │
  └──► Result Computation: 100.0 + 30.0 + 10.0 + 40.0 = ₹180.0
```

---

## 7. Key Advantages & Disadvantages

### Advantages
1. **Dynamic Extension**: Adds responsibilities to objects at runtime without re-compiling existing classes.
2. **Single Responsibility Principle (SRP)**: Each decorator class focuses on adding a single specific feature.
3. **Open/Closed Principle (OCP)**: You can add new decorators without altering existing components or decorators.
4. **Prevents Class Explosion**: Replaces $2^N$ subclass combinations with $N$ modular decorator classes.

### Disadvantages
1. **Lots of Small Objects**: Creates many small wrapper instances in memory.
2. **Order Sensitivity**: Wrapping order can sometimes matter if decorators depend on execution sequence (e.g., Encrypt before Compress vs. Compress before Encrypt).

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/01_Coffee_Shop_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Decorator_pattern/code/01_Coffee_Shop_Example).
