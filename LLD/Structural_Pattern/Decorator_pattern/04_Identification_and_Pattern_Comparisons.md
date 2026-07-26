# Module 4: How to Identify Decorator Pattern & Structural Comparisons

Recognizing when to use the Decorator Pattern vs. other structural patterns is a mandatory evaluation topic in Low-Level Design (LLD) interviews.

---

## 1. Five Rules to Identify the Decorator Pattern

1. **Dynamic Runtime Behavior Extension**: You need to add or remove responsibilities from individual objects dynamically at runtime without affecting other instances.
2. **Subclass Explosion Prevention**: Using static inheritance leads to an exponential $2^N$ number of subclasses for feature combinations.
3. **Transparent Interface Requirement**: The enhanced wrapper object must implement the **exact same interface** as the original wrapped object so clients cannot distinguish between decorated and undecorated instances.
4. **Recursive Composition (Layering)**: Wrappers can be nested inside other wrappers at arbitrary depth levels (e.g., `Whip(Sugar(Milk(Coffee)))`).
5. **Single Responsibility Feature Splitting**: Complex monolithic features can be broken down into small, single-purpose decorator classes.

---

## 2. Decision Tree

```text
Do you need to add responsibilities to an existing object?
                 │
                 ├──► NO  ---> No Decorator Pattern needed.
                 │
                 └──► YES ---> Must this be done dynamically at runtime without modifying original class?
                                 │
                                 ├──► NO  ---> Use standard class inheritance.
                                 │
                                 └──► YES ---> Must the wrapper preserve the exact same interface?
                                                 │
                                                 ├──► YES ---> USE DECORATOR PATTERN!
                                                 │
                                                 └──► NO  ---> Consider Adapter Pattern.
```

---

## 3. Structural Design Patterns Comparison

The Decorator Pattern shares structural similarities with Adapter, Proxy, Composite, and Strategy because all rely on object composition (`HAS-A`). However, their **intents** differ completely.

### Comparison Matrix

| Pattern | Category | Primary Intent | Modifies Interface? | Adds New Behavior? | Target Count |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Decorator** | Structural | Dynamically adds behavior at runtime | **NO** (Preserves Interface) | **YES** | Single Component |
| **Adapter** | Structural | Converts incompatible interfaces | **YES** (Translates Interface) | **NO** | Single Adaptee |
| **Proxy** | Structural | Controls access, security, or caching | **NO** (Preserves Interface) | **NO** (Adds Access Control) | Single RealSubject |
| **Composite** | Structural | Represents part-whole tree hierarchies | **NO** (Preserves Interface) | **NO** (Recursively Aggregates) | Multiple Children |
| **Strategy** | Behavioral | Swaps algorithms at runtime | **NO** (Swaps internal algorithm) | **YES** (Alternative Algorithm) | N/A |

---

## 4. Key Architectural Distinctions

### A. Decorator vs. Adapter
* **Decorator**: Preserves the existing interface, wrapping an object to **add new behavior**.
* **Adapter**: Changes the existing interface, wrapping an object to **make it compatible** with an expected client target interface.

```text
Decorator: [Client] ──► [MilkDecorator (Coffee)] ──► [SimpleCoffee (Coffee)]  (Same Interface!)
Adapter:   [Client] ──► [PaymentAdapter (Target)] ──► [Razorpay (Adaptee)]      (Interface Translated!)
```

### B. Decorator vs. Proxy
* **Decorator**: The client creates and chains wrappers dynamically to **enhance functionality**.
* **Proxy**: The proxy controls access (e.g., lazy loading, authentication, caching) and manages the lifecycle of the underlying object. The client is often unaware of the proxy.

### C. Decorator vs. Composite
* **Decorator**: Can be viewed as a degenerate Composite with **only one child**. A decorator adds behavior; a composite aggregates child results.

---

## 5. Code Comparison: Decorator vs. Adapter vs. Proxy

### A. Decorator Pattern Code Structure (Same Interface + Added Behavior)
```java
class MilkDecorator implements Coffee {
    private Coffee coffee;
    public MilkDecorator(Coffee c) { this.coffee = c; }
    public double getCost() { return coffee.getCost() + 30.0; } // Enhanced cost calculation!
}
```

### B. Adapter Pattern Code Structure (Interface Translation)
```java
class PaymentAdapter implements TargetPayment {
    private IncompatibleRazorpay razorpay;
    public PaymentAdapter(IncompatibleRazorpay r) { this.razorpay = r; }
    public void pay(double amount) { razorpay.makePayment(amount); } // Interface translation!
}
```

### C. Proxy Pattern Code Structure (Same Interface + Access Control)
```java
class ProtectedDocumentProxy implements Document {
    private RealDocument realDoc;
    private String userRole;
    public void display() {
        if ("ADMIN".equals(userRole)) { realDoc.display(); } // Access control!
        else { throw new SecurityException("Access Denied!"); }
    }
}
```

---

## 6. Mental Mnemonics & Memory Tricks

```text
+-----------+-------------------------------------------------------------+
| Pattern   | Mental Mnemonic                                             |
+-----------+-------------------------------------------------------------+
| Decorator | 🎁 Gift Wrap (Layering ribbon, card, box around a present)   |
| Adapter   | 🔌 Power Plug Converter (US plug to EU wall socket)         |
| Proxy     | 🛡️ Security Guard / Bank ATM controlling cash access        |
| Composite | 📁 File System Folder holding files & sub-folders           |
+-----------+-------------------------------------------------------------+
```

---

## 7. Quick Summary Checklist

* **Intent**: Dynamically attach additional responsibilities to an object at runtime.
* **Key Components**: Component, ConcreteComponent, BaseDecorator, ConcreteDecorator.
* **GoF Category**: Structural Pattern.
* **Core Rule**: Decorators must implement the `Component` interface and wrap a `Component` reference (`IS-A` and `HAS-A` simultaneously).

---

> 📂 **All Runnable Code Demos**: Find organized Java projects in the [code/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Decorator_pattern/code) directory.
