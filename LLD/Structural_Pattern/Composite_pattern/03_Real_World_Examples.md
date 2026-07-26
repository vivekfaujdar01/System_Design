# Module 3: Real-World Examples of Composite Pattern

The Composite Pattern is widely used across operating systems, graphic rendering frameworks, compiler design, and e-commerce logistics platforms.

---

## 1. Enterprise Practical Scenarios

| Domain | Component | Leaf Objects | Composite Objects | Real-World Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **Logistics & E-Commerce** | `OrderComponent` | `ProductItem` | `BoxContainer` | Calculates total price and weight of nested shipping boxes recursively |
| **GUI Frameworks / DOM** | `UIComponent` | `Button`, `Label`, `TextField` | `Panel`, `Window`, `Form` | Renders and paints complex nested UI layouts uniformly |
| **Expression Evaluator** | `ExpressionNode` | `NumberNode` | `AddExpression`, `MultiplyExpression` | Evaluates complex arithmetic trees like `(3 + 5) * (10 - 2)` |
| **Graphic Drawing Software** | `GraphicShape` | `Circle`, `Rectangle`, `Line` | `CompoundGraphic` (Grouped Shapes) | Moves, rotates, or scales grouped drawings as a single unit |

---

## 2. Complete Java Example: E-Commerce Order Packaging & Pricing

### Problem Scenario
An e-commerce platform packs orders using nested boxes:
* Individual items (e.g., iPhone, Charger, Phone Case) have individual prices.
* Items can be packed into small gift boxes, which are then packed into larger shipping boxes.
* The system must compute the total price of any box or individual item uniformly.

```
Shipping Container (Box / Composite)
 ├── Small Box (Box / Composite)
 │    ├── iPhone 15 (Product / Leaf) ───────► ₹79,900
 │    └── Screen Guard (Product / Leaf) ────► ₹999
 └── Fast Charger (Product / Leaf) ──────────► ₹2,499
```

### Step-by-Step Implementation

#### Step 1: Component Interface (`OrderComponent.java`)
```java
// Component Interface
public interface OrderComponent {
    String getName();
    double getPrice();
    void printPackingList(String indent);
}
```

#### Step 2: Leaf Implementation (`ProductItem.java`)
```java
// Leaf Component representing a standalone product
public class ProductItem implements OrderComponent {
    private final String name;
    private final double price;

    public ProductItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override public String getName() { return name; }
    @Override public double getPrice() { return price; }

    @Override
    public void printPackingList(String indent) {
        System.out.println(indent + "└─ Item: " + name + " | Price: ₹" + price);
    }
}
```

#### Step 3: Composite Implementation (`BoxContainer.java`)
```java
import java.util.ArrayList;
import java.util.List;

// Composite Component representing a container box
public class BoxContainer implements OrderComponent {
    private final String boxName;
    private final double packagingCost;
    private final List<OrderComponent> contents = new ArrayList<>();

    public BoxContainer(String boxName, double packagingCost) {
        this.boxName = boxName;
        this.packagingCost = packagingCost;
    }

    public void add(OrderComponent item) {
        contents.add(item);
    }

    public void remove(OrderComponent item) {
        contents.remove(item);
    }

    @Override public String getName() { return boxName; }

    @Override
    public double getPrice() {
        double total = packagingCost; // Includes box packaging fee
        for (OrderComponent item : contents) {
            total += item.getPrice(); // Recursive delegation!
        }
        return total;
    }

    @Override
    public void printPackingList(String indent) {
        System.out.println(indent + "📦 Box: " + boxName + " (Packaging Fee: ₹" + packagingCost + ")");
        for (OrderComponent item : contents) {
            item.printPackingList(indent + "    "); // Recursive printing!
        }
    }
}
```

#### Step 4: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Create Individual Products
        OrderComponent phone = new ProductItem("iPhone 15 Pro", 134900.0);
        OrderComponent screenGuard = new ProductItem("Tempered Glass Screen Guard", 999.0);
        OrderComponent charger = new ProductItem("20W USB-C Power Adapter", 1900.0);
        OrderComponent magSafeWallet = new ProductItem("Leather MagSafe Wallet", 5900.0);

        // Pack Mobile Accessories into Small Gift Box
        BoxContainer giftBox = new BoxContainer("Premium Accessory Gift Box", 150.0);
        giftBox.add(screenGuard);
        giftBox.add(magSafeWallet);

        // Pack Everything into Main Shipping Box
        BoxContainer mainShippingCrate = new BoxContainer("Outer Shipping Container", 300.0);
        mainShippingCrate.add(phone);
        mainShippingCrate.add(charger);
        mainShippingCrate.add(giftBox); // Nesting Composite inside Composite!

        // Print Packing Manifest
        System.out.println("=== SHIPMENT PACKING MANIFEST ===");
        mainShippingCrate.printPackingList("");

        // Compute Total Prices Uniformly
        System.out.println("\n=== COST BREAKDOWN ===");
        System.out.println("Gift Box Subtotal (with packaging): ₹" + giftBox.getPrice());
        System.out.println("Total Shipment Order Price: ₹" + mainShippingCrate.getPrice());
    }
}
```

### Execution Output
```text
=== SHIPMENT PACKING MANIFEST ===
📦 Box: Outer Shipping Container (Packaging Fee: ₹300.0)
    └─ Item: iPhone 15 Pro | Price: ₹134900.0
    └─ Item: 20W USB-C Power Adapter | Price: ₹1900.0
    📦 Box: Premium Accessory Gift Box (Packaging Fee: ₹150.0)
        └─ Item: Tempered Glass Screen Guard | Price: ₹999.0
        └─ Item: Leather MagSafe Wallet | Price: ₹5900.0

=== COST BREAKDOWN ===
Gift Box Subtotal (with packaging): ₹7049.0
Total Shipment Order Price: ₹144149.0
```

---

## 3. Composite Pattern in Graphic & UI Frameworks (Swing / React / DOM)

Java Swing's UI component hierarchy is a textbook implementation of the Composite Pattern:

```text
java.awt.Component (Component Interface)
 ├── java.awt.Button (Leaf)
 ├── java.awt.TextField (Leaf)
 └── java.awt.Container (Composite)
      ├── javax.swing.JPanel (Composite)
      └── javax.swing.JFrame (Composite)
```

Calling `repaint()` or `revalidate()` on a top-level `JFrame` recursively paints every child button, panel, and text field automatically!

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/03_Order_Packaging_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Composite_pattern/code/03_Order_Packaging_Example).
