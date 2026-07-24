# Module 5: How to Identify Adapter Pattern & Structural Comparisons

In low-level design (LLD) interviews, recognizing *when* to apply the Adapter pattern is often more critical than implementing it.

---

## 1. Five Rules to Identify the Adapter Pattern

1. **Existing Code Cannot Be Modified**: The Adaptee class comes from a third-party JAR, legacy code, or shared framework that cannot or should not be edited.
2. **Interface Mismatch**: Two modules need to collaborate, but their method names, parameter signatures, or return types differ.
3. **Target System Standardization**: The client application enforces a uniform interface (e.g., `PaymentProcessor`), but external plugins/vendors do not follow it.
4. **Data Translation Required**: One subsystem produces output in format A (e.g., XML/CSV), while the receiving client requires format B (e.g., JSON/Objects).
5. **Legacy System Integration**: Integrating 15-year-old COBOL/Java 1.4 backend systems into a modern Spring Boot microservice.

---

## 2. Decision Tree

```text
Do you need two objects/classes to work together?
                 │
                 ├──► NO  ---> No design pattern required.
                 │
                 └──► YES ---> Can you modify the existing class source code?
                                 │
                                 ├──► YES ---> Redesign the class or refactor interface directly.
                                 │
                                 └──► NO  ---> Does its interface match what the client expects?
                                                 │
                                                 ├──► YES ---> Use the class directly.
                                                 │
                                                 └──► NO  ---> USE ADAPTER PATTERN.
```

---

## 3. Structural Design Patterns Comparison

Structural design patterns (Adapter, Decorator, Facade, Proxy, Bridge) often look syntactically similar because they all involve object composition. However, their **intents** are completely different.

### Comparison Matrix

| Pattern | Main Intent | Modifies Interface? | Adds New Behavior? | Key Focus |
| :--- | :--- | :--- | :--- | :--- |
| **Adapter** | Converts incompatible interfaces | **YES** | **NO** | Interface Compatibility |
| **Decorator** | Dynamically adds behavior at runtime | **NO** | **YES** | Extension / Enhancement |
| **Facade** | Provides a unified simplified interface | **YES** (Simplifies) | **NO** | Subsystem Simplification |
| **Proxy** | Controls access to an underlying object | **NO** | **NO** (Adds Control) | Security, Caching, Lazy Loading |
| **Bridge** | Decouples abstraction from implementation | **NO** | **NO** | Independent Variation |

---

## 4. Code Comparison: Adapter vs. Decorator vs. Facade vs. Proxy

### A. Adapter Pattern Code Flow (Changes Interface)
```java
// Target Interface expected by client
interface Target {
    void write();
}

// Incompatible Adaptee
class Adaptee {
    void mark() { System.out.println("Marking..."); }
}

// Adapter translates write() -> mark()
class Adapter implements Target {
    private Adaptee adaptee;
    public Adapter(Adaptee a) { this.adaptee = a; }
    public void write() { adaptee.mark(); } // Interface translation
}
```

### B. Decorator Pattern Code Flow (Adds Behavior, Interface Unchanged)
```java
interface Coffee { double getCost(); }
class SimpleCoffee implements Coffee { public double getCost() { return 50.0; } }

// Decorator wraps Coffee and ADDS new price calculation behavior
class MilkDecorator implements Coffee {
    private Coffee coffee;
    public MilkDecorator(Coffee c) { this.coffee = c; }
    public double getCost() { return coffee.getCost() + 15.0; } // Added behavior!
}
```

### C. Facade Pattern Code Flow (Simplifies Multiple Complex Classes)
```java
class CPU { void start() {} }
class Memory { void load() {} }
class HardDrive { void read() {} }

// Facade provides ONE simple method to client
class ComputerFacade {
    private CPU cpu = new CPU();
    private Memory memory = new Memory();
    private HardDrive hardDrive = new HardDrive();

    public void startComputer() {
        cpu.start();
        memory.load();
        hardDrive.read();
    }
}
```

### D. Proxy Pattern Code Flow (Controls Access, Same Interface)
```java
interface Image { void display(); }
class RealImage implements Image { public void display() { System.out.println("Displaying HD Image..."); } }

// Proxy controls access with lazy loading
class ProxyImage implements Image {
    private RealImage realImage;
    public void display() {
        if (realImage == null) { realImage = new RealImage(); } // Lazy initialization control
        realImage.display();
    }
}
```

---

## 5. Mental Mnemonics & Real-Life Analogies

```text
+-----------+-----------------------------------------------------------+
| Pattern   | Mental Picture                                            |
+-----------+-----------------------------------------------------------+
| Adapter   | 🔌 Power Plug Converter (US plug to EU socket)           |
| Decorator | 🎁 Gift Wrap (Layering ribbon, card, box around an item)  |
| Facade    | 🏢 Hotel Reception Desk (One contact for room/food/taxi)  |
| Proxy     | 🛡️ Security Guard / Bank ATM (Controls access to cash)     |
| Bridge    | 🌉 Bridge connecting TV Remote Control to TV Hardware     |
+-----------+-----------------------------------------------------------+
```

---

## 6. Common Interview Questions & Answers

### Q1: Does the Adapter Pattern add new functionality?
> **Answer**: **No.** The Adapter Pattern's sole responsibility is interface translation and compatibility. Adding new business logic inside an adapter violates the Single Responsibility Principle. If new functionality is required, consider the **Decorator Pattern**.

### Q2: Why not just modify the existing adaptee class directly?
> **Answer**:
> 1. The adaptee might be compiled third-party code (e.g., inside a JAR library).
> 2. Direct modification violates the **Open/Closed Principle**.
> 3. Modifying a shared legacy class might break other applications depending on it.

### Q3: Can one Adapter adapt multiple Adaptees simultaneously?
> **Answer**: **Yes.** While rare, an adapter can hold references to multiple adaptee objects if a single target interface method requires combining responses from multiple legacy classes.

---

## 7. Quick Summary Checklist

* **Intent**: Make incompatible interfaces work together.
* **Key Components**: Client, Target Interface, Adapter, Adaptee.
* **Preferred Implementation**: Object Adapter (Composition using `HAS-A`).
* **GoF Classification**: Structural Pattern.
* **Key Design Principles**: Open/Closed Principle (OCP), Single Responsibility Principle (SRP), Favor Composition over Inheritance.

---

> 📂 **All Runnable Code Demos**: Find organized Java projects in the [code/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Adapter_pattern/code) directory.
