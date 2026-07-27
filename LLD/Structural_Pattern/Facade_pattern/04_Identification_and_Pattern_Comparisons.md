# Module 4: How to Identify Facade Pattern & Structural Comparisons

Recognizing when to use the Facade Pattern vs. other structural/behavioral patterns is a frequent topic in Low-Level Design (LLD) interviews.

---

## 1. Five Rules to Identify the Facade Pattern

1. **Complex Multi-Class Subsystem**: You have a subsystem with many interacting classes, complex configuration requirements, or strict execution ordering.
2. **Simplified Interface Needed**: Most clients need to perform common high-level workflows without touching low-level subsystem details.
3. **Decoupling Subsystem from Clients**: You want to reduce tight dependencies between client code and internal subsystem classes.
4. **Layered System Architecture**: You need an entry point for each layer of your application (e.g., API Gateway in microservices, Service Layer in enterprise Java).
5. **Wrapping Legacy or Library Code**: Simplifying interaction with a complex third-party library or legacy C++/Java codebase.

---

## 2. Decision Tree

```text
Do you have a complex subsystem with multiple interacting classes?
                 │
                 ├──► NO  ---> No Facade Pattern needed.
                 │
                 └──► YES ---> Do clients need a simple unified interface for common tasks?
                                 │
                                 ├──► NO  ---> Allow clients to interact with subsystem classes directly.
                                 │
                                 └──► YES ---> USE FACADE PATTERN!
```

---

## 3. Structural & Behavioral Patterns Comparison

The Facade Pattern shares structural similarities with Adapter, Mediator, Proxy, and Decorator because all involve delegation or wrapping. However, their **intents and scopes** differ fundamentally.

### Comparison Matrix

| Pattern | Category | Primary Intent | Subsystem Scope | Modifies Interface? |
| :--- | :--- | :--- | :--- | :--- |
| **Facade** | Structural | Provides a simplified unified interface to a **complex subsystem** | **Multiple Subsystem Classes** | **YES** (Simplifies interface) |
| **Adapter** | Structural | Makes an **incompatible interface** compatible with a target interface | **Single Adaptee Class** | **YES** (Translates interface) |
| **Mediator** | Behavioral | Centralizes complex communication between **peer objects** | **Multiple Peer Objects** | **NO** (Manages peer interactions) |
| **Proxy** | Structural | Controls access, security, or caching for a **target object** | **Single Target Object** | **NO** (Preserves exact interface) |
| **Decorator** | Structural | Dynamically adds new behavior to an object at runtime | **Single Component Object** | **NO** (Preserves exact interface) |

---

## 4. Key Architectural Distinctions

### A. Facade vs. Adapter
* **Facade**: Defines a **new simplified interface** over **multiple subsystem classes**. Its goal is simplicity.
* **Adapter**: Converts an **existing incompatible interface** of **one class** to match a target interface. Its goal is compatibility.

```text
Facade:  [Client] ──► [HomeTheaterFacade] ──► [DVD, Projector, Amp, Lights, Screen]  (Simplifies 5 classes)
Adapter: [Client] ──► [PaymentAdapter]   ──► [IncompatibleRazorpay]                   (Translates 1 class)
```

### B. Facade vs. Mediator
* **Facade**: Unidirectional delegation—clients talk to Facade, Facade delegates to subsystems. Subsystems don't know about Facade.
* **Mediator**: Multidirectional communication—peer objects talk to each other *through* the Mediator to prevent direct peer-to-peer coupling.

### C. Facade vs. Proxy
* **Facade**: Wraps **multiple** classes to simplify usage.
* **Proxy**: Wraps **one** class to control access (security, lazy loading, logging) while keeping the exact same interface.

---

## 5. Code Comparison: Facade vs. Adapter vs. Proxy

### A. Facade Pattern Code Structure (Simplifies Multiple Subsystem Classes)
```java
class ComputerFacade {
    private CPU cpu = new CPU();
    private Memory memory = new Memory();
    private HardDrive hardDrive = new HardDrive();

    public void startComputer() {
        cpu.freeze();
        memory.load(BOOT_ADDRESS, hardDrive.read(BOOT_SECTOR, SECTOR_SIZE));
        cpu.execute();
    }
}
```

### B. Adapter Pattern Code Structure (Translates Single Incompatible Class)
```java
class PrinterAdapter implements ModernPrinter {
    private LegacyPrinter legacyPrinter;
    public PrinterAdapter(LegacyPrinter lp) { this.legacyPrinter = lp; }
    public void print(String text) { legacyPrinter.printOldDocument(text); }
}
```

### C. Proxy Pattern Code Structure (Controls Access to Single Object, Same Interface)
```java
class ProtectedDatabaseProxy implements Database {
    private RealDatabase realDb = new RealDatabase();
    public void executeQuery(String sql) {
        if (UserContext.isAdmin()) { realDb.executeQuery(sql); }
        else { throw new SecurityException("Unauthorized SQL Query!"); }
    }
}
```

---

## 6. Common Pitfalls & Best Practices

1. **Avoid the "God Object" Anti-Pattern**: Do not put every possible feature of an entire application into one massive Facade class. If a Facade gets too large, split it into domain-specific facades (e.g., `OrderFacade`, `UserFacade`, `InventoryFacade`).
2. **Keep Facades Stateless**: Facades should primarily coordinate methods rather than holding persistent business state.
3. **Do Not Hide Subsystems Completely**: Facades should make common cases easy, but advanced clients should still be allowed to access underlying subsystem classes if fine-grained control is necessary.

---

## 7. Mental Mnemonics & Memory Tricks

```text
+-----------+-------------------------------------------------------------+
| Pattern   | Mental Mnemonic                                             |
+-----------+-------------------------------------------------------------+
| Facade    | 🏢 Hotel Reception Desk (One contact for room/food/taxi)    |
| Adapter   | 🔌 Power Plug Converter (US plug to EU wall socket)         |
| Mediator  | 🛩️ Air Traffic Control Tower managing aircraft communications|
| Proxy     | 🛡️ Security Guard / Bank ATM controlling cash access        |
+-----------+-------------------------------------------------------------+
```

---

## 8. Quick Summary Checklist

* **Intent**: Provide a simplified, unified interface to a set of interfaces in a complex subsystem.
* **Key Components**: Facade, Subsystem Classes, Client.
* **GoF Category**: Structural Pattern.
* **Core Rule**: Facade simplifies complex multi-class subsystem interactions into single high-level method calls.

---

> 📂 **All Runnable Code Demos**: Find organized Java projects in the [code/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Facade_pattern/code) directory.
