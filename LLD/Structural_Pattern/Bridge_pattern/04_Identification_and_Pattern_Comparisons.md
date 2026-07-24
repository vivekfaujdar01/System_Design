# Module 4: How to Identify Bridge Pattern & Structural Comparisons

Recognizing when to use the Bridge Pattern is essential during Low-Level Design (LLD) interviews and architecture planning.

---

## 1. Five Rules to Identify the Bridge Pattern

1. **Multiple Independent Dimensions of Variation**: Your system varies along two orthogonal dimensions (e.g., *Shape* and *Color*, *Remote Type* and *Device Type*, *Message Type* and *Delivery Channel*).
2. **Preventing Subclass Explosion**: Standard inheritance leads to $M \times N$ subclasses, causing code bloat and maintenance nightmares.
3. **Runtime Implementation Swapping**: You need to switch implementation backends dynamically at runtime (e.g., switching rendering engine from OpenGL to DirectX).
4. **Hiding Platform Details from Clients**: Client code should only interact with high-level features (`Abstraction`), remaining unaware of underlying OS/Hardware implementations.
5. **Decoupling Interface & Implementation Lifecycle**: Both the abstraction layer and the implementation layer need to be extended independently by separate engineering teams without breaking each other.

---

## 2. Decision Tree

```text
Do you have a class with multiple dimensions of variation?
                 │
                 ├──► NO  ---> No Bridge Pattern needed.
                 │
                 └──► YES ---> Does inheritance create M x N subclasses?
                                 │
                                 ├──► NO  ---> Standard inheritance is sufficient.
                                 │
                                 └──► YES ---> Should abstraction and implementation vary independently?
                                                 │
                                                 ├──► YES ---> USE BRIDGE PATTERN!
                                                 │
                                                 └──► NO  ---> Redesign interfaces.
```

---

## 3. Structural & Behavioral Design Patterns Comparison

The Bridge Pattern shares structural similarities with Adapter, Strategy, State, and Decorator because all rely on object composition (`HAS-A`). However, their **timing, intent, and architectural goals** differ significantly.

### Comparison Matrix

| Pattern | Category | Primary Intent | Designed When? | Changes Interface? |
| :--- | :--- | :--- | :--- | :--- |
| **Bridge** | Structural | Decouples abstraction from implementation so both vary independently | **Upfront Design Phase** | **NO** (Decouples 2 hierarchies) |
| **Adapter** | Structural | Makes incompatible existing interfaces work together | **Refactoring / Post-Design** | **YES** (Translates interface A to B) |
| **Strategy** | Behavioral | Swaps algorithms at runtime within a single class | **Runtime Behavior Phase** | **NO** (Swaps internal algorithms) |
| **State** | Behavioral | Alters class behavior when internal state changes | **Runtime State Machine** | **NO** (State transitions) |
| **Decorator** | Structural | Dynamically attaches additional responsibilities | **Runtime Enhancement** | **NO** (Adds feature wrappers) |

---

## 4. Key Architectural Distinctions

### A. Bridge vs. Adapter
* **Bridge**: Designed **upfront** before writing implementation code to let abstractions and implementations evolve independently.
* **Adapter**: Applied **retrospectively** to make legacy or third-party classes compatible with an existing system interface without changing their source code.

```text
Bridge:   [Abstraction] ────── Bridge (has-a) ─────► [Implementor]  (Designed upfront)
Adapter:  [Client] ──────► [Adapter] ──────► [Adaptee]             (Added retroactively)
```

### B. Bridge vs. Strategy
* **Bridge**: Structural pattern focused on **organizing classes** into two independent hierarchies ($M + N$).
* **Strategy**: Behavioral pattern focused on **interchangeable algorithms** inside a single class.

---

## 5. Code Comparison: Bridge vs. Adapter vs. Strategy

### A. Bridge Pattern (Two Independent Hierarchies)
```java
// Abstraction Hierarchy
abstract class RemoteControl {
    protected Device device; // Bridge to Implementor!
    public RemoteControl(Device d) { this.device = d; }
    abstract void power();
}

// Implementor Hierarchy
interface Device { void on(); void off(); }
class TV implements Device { public void on() {} public void off() {} }
```

### B. Adapter Pattern (Retroactive Wrapper)
```java
// Target Interface
interface Target { void request(); }

// Legacy Adaptee
class LegacyAdaptee { void specificRequest() {} }

// Adapter Class
class Adapter implements Target {
    private LegacyAdaptee adaptee;
    public Adapter(LegacyAdaptee a) { this.adaptee = a; }
    public void request() { adaptee.specificRequest(); }
}
```

### C. Strategy Pattern (Interchangeable Algorithm)
```java
interface PaymentStrategy { void pay(double amount); }
class CreditCardStrategy implements PaymentStrategy { public void pay(double a) {} }

class ShoppingCart {
    private PaymentStrategy strategy;
    public void setStrategy(PaymentStrategy s) { this.strategy = s; }
    public void checkout(double amount) { strategy.pay(amount); }
}
```

---

## 6. Mental Mnemonics & Memory Tricks

```text
+-----------+-------------------------------------------------------------+
| Pattern   | Mental Mnemonic                                             |
+-----------+-------------------------------------------------------------+
| Bridge    | 🌉 Bridge connecting TV Remote Control to TV Device Hardware|
| Adapter   | 🔌 Wall Socket Converter for foreign travel                 |
| Strategy  | 🎯 Swapping Navigation Route (Fastest vs Shortest vs Tolls) |
| Decorator | 🎁 Layering Gift Wrapping & Ribbons over a Coffee Cup       |
+-----------+-------------------------------------------------------------+
```

---

## 7. Quick Summary Checklist

* **Intent**: Decouple Abstraction from Implementation ($M + N$).
* **Key Components**: Abstraction, Refined Abstraction, Implementor, Concrete Implementor.
* **GoF Category**: Structural Pattern.
* **Core Rule**: Avoid $M \times N$ subclass explosion by replacing deep inheritance trees with object composition.

---

> 📂 **All Runnable Code Demos**: Find organized Java projects in the [code/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Bridge_pattern/code) directory.
