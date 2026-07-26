# Module 2: Pizza Customization Engine Example

Customizing food items (e.g., pizzas, burgers, sandwiches) with arbitrary toppings is a classic Low-Level Design (LLD) interview question.

---

## 1. Problem Statement

Suppose a pizzeria POS (Point of Sale) system needs to compute pricing for customized pizzas:
* **Base Pizza**: Thin Crust Plain Pizza (Base Price: ₹250).
* **Available Toppings**:
  * Extra Cheese (+ ₹50)
  * Mushrooms (+ ₹40)
  * Jalapeños (+ ₹35)

Customers can select any combination of toppings in any order. The POS engine must calculate the total price and build the description dynamically.

```
                          [ Jalapeño Decorator ] (+₹35)
                                    │
                             wraps  ▼
                          [ Mushroom Decorator ] (+₹40)
                                    │
                             wraps  ▼
                        [ Extra Cheese Decorator ] (+₹50)
                                    │
                             wraps  ▼
                       [ Thin Crust Base Pizza ] (₹250)
```

---

## 2. Complete Step-by-Step Java Implementation

### Step 1: Component Interface (`Pizza.java`)
```java
// Component Interface
public interface Pizza {
    String getDescription();
    double getCost();
}
```

### Step 2: Concrete Component (`PlainPizza.java`)
```java
// Base Component
public class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Thin Crust Plain Pizza";
    }

    @Override
    public double getCost() {
        return 250.0; // Base Crust Price
    }
}
```

### Step 3: Base Decorator (`PizzaDecorator.java`)
```java
// Abstract Decorator wrapping a Pizza instance
public abstract class PizzaDecorator implements Pizza {
    protected final Pizza decoratedPizza; // The Bridge / Wrapper reference!

    public PizzaDecorator(Pizza pizza) {
        this.decoratedPizza = pizza;
    }

    @Override
    public String getDescription() {
        return decoratedPizza.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedPizza.getCost();
    }
}
```

### Step 4: Concrete Topping Decorators (`ExtraCheeseDecorator.java`, `MushroomDecorator.java`, `JalapenoDecorator.java`)
```java
// Concrete Decorator 1: Extra Cheese
public class ExtraCheeseDecorator extends PizzaDecorator {
    public ExtraCheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Extra Mozzarella Cheese";
    }

    @Override
    public double getCost() {
        return super.getCost() + 50.0;
    }
}

// Concrete Decorator 2: Mushrooms
public class MushroomDecorator extends PizzaDecorator {
    public MushroomDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Fresh Button Mushrooms";
    }

    @Override
    public double getCost() {
        return super.getCost() + 40.0;
    }
}

// Concrete Decorator 3: Jalapeños
public class JalapenoDecorator extends PizzaDecorator {
    public JalapenoDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Spicy Jalapeños";
    }

    @Override
    public double getCost() {
        return super.getCost() + 35.0;
    }
}
```

### Step 5: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("=== PIZZERIA POS ORDER SYSTEM ===");

        // Order A: Plain Pizza
        Pizza orderA = new PlainPizza();
        System.out.println("\nOrder A: " + orderA.getDescription());
        System.out.println("Cost: ₹" + orderA.getCost());

        // Order B: Cheese Burst Pizza (Plain + Extra Cheese + Extra Cheese)
        Pizza orderB = new PlainPizza();
        orderB = new ExtraCheeseDecorator(orderB);
        orderB = new ExtraCheeseDecorator(orderB); // Double Cheese!
        System.out.println("\nOrder B: " + orderB.getDescription());
        System.out.println("Cost: ₹" + orderB.getCost());

        // Order C: Supreme Deluxe (Plain + Cheese + Mushroom + Jalapeno)
        Pizza orderC = new JalapenoDecorator(
                           new MushroomDecorator(
                               new ExtraCheeseDecorator(
                                   new PlainPizza()
                               )
                           )
                       );
        System.out.println("\nOrder C: " + orderC.getDescription());
        System.out.println("Cost: ₹" + orderC.getCost());
    }
}
```

### Execution Output
```text
=== PIZZERIA POS ORDER SYSTEM ===

Order A: Thin Crust Plain Pizza
Cost: ₹250.0

Order B: Thin Crust Plain Pizza + Extra Mozzarella Cheese + Extra Mozzarella Cheese
Cost: ₹350.0

Order C: Thin Crust Plain Pizza + Extra Mozzarella Cheese + Fresh Button Mushrooms + Spicy Jalapeños
Cost: ₹375.0
```

---

## 3. Detailed Cost Breakdown & Execution Sequence

```text
orderC.getCost()
  │
  ├──► JalapenoDecorator.getCost() (adds ₹35.0)
  │      └──► MushroomDecorator.getCost() (adds ₹40.0)
  │             └──► ExtraCheeseDecorator.getCost() (adds ₹50.0)
  │                    └──► PlainPizza.getCost() (base ₹250.0)
  │
  └──► Subtotal sum: 250.0 + 50.0 + 40.0 + 35.0 = ₹375.0
```

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/02_Pizza_Ordering_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Decorator_pattern/code/02_Pizza_Ordering_Example).
