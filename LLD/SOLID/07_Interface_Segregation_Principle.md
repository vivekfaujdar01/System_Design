# I — Interface Segregation Principle (ISP)

> **"Clients should not be forced to depend on interfaces they do not use."**
> — Robert C. Martin

---

## 📖 Definition

The **Interface Segregation Principle** states that instead of having one large, fat interface, you should break it into **smaller, more specific interfaces** so that clients only need to know about the methods that are relevant to them.

In simple terms: **Many small, focused interfaces > One big, bloated interface.**

---

## 🧠 Why Does It Matter?

When an interface is too large:
- Classes that implement it are forced to implement methods they don't need
- Often results in empty or exception-throwing implementations
- Changes to unrelated methods force unnecessary recompilation/changes
- Creates tight coupling between unrelated functionality

---

## ❌ Bad Example (Violates ISP)

```java
// Fat interface — forces all implementors to implement EVERYTHING
interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeCode();
    void manageTeam();
}

// RobotWorker doesn't eat or sleep — forced to implement them anyway ❌
class RobotWorker implements Worker {
    public void work() { System.out.println("Robot is working..."); }
    public void eat()  { throw new UnsupportedOperationException("Robots don't eat!"); } // ❌
    public void sleep(){ throw new UnsupportedOperationException("Robots don't sleep!"); } // ❌
    // ... other forced, meaningless implementations
}
```

**Problem**: `RobotWorker` is forced to implement `eat()` and `sleep()` even though robots have no concept of these.

---

## ✅ Good Example (Follows ISP)

```java
// Segregated interfaces — each has a single focused purpose
interface Workable    { void work(); }
interface Eatable     { void eat(); }
interface Sleepable   { void sleep(); }
interface Codeable    { void writeCode(); }
interface Manageable  { void manageTeam(); }

// Human employee uses what it needs
class HumanEmployee implements Workable, Eatable, Sleepable, Codeable {
    public void work()      { ... }
    public void eat()       { ... }
    public void sleep()     { ... }
    public void writeCode() { ... }
}

// Robot only uses what's relevant — no forced implementations ✅
class RobotWorker implements Workable, Codeable {
    public void work()      { ... }
    public void writeCode() { ... }
}
```

---

## 🔍 How to Identify ISP Violations

Look for:
- Classes with **empty method bodies** or `throw new UnsupportedOperationException()`
- Interface methods that are **irrelevant** to some implementors
- A large interface where different clients use **different subsets** of methods
- Comments like *"this class doesn't support this method"*

---

## 🌍 Real-World Analogies

| Analogy | ISP Violation | ISP Applied |
|---------|--------------|-------------|
| Printers | One interface with `print`, `scan`, `fax`, `copy` — basic printers forced to implement all | Separate `Printable`, `Scannable`, `Faxable` interfaces |
| Restaurant menu | One giant menu for all customers (dine-in, delivery, takeaway) | Separate menus for each service type |
| Smartphone | All apps must implement camera, GPS, NFC, Bluetooth APIs | Each feature has its own permission/API |

---

## 📐 ISP in Java Standard Library

Java itself follows ISP with its collection interfaces:

```
Iterable
  └── Collection
        ├── List (adds index-based access)
        ├── Set  (adds uniqueness)
        └── Queue (adds queue operations)
```

`Set` does not have index access — because it's not relevant. This is ISP in action.

---

## 🔧 How to Apply ISP

1. **Role interfaces**: Create small interfaces representing roles/capabilities
2. **Analyze clients**: What methods does each client actually use?
3. **Split fat interfaces**: Use interface inheritance to compose when needed
4. **Default methods (Java 8+)**: Use for optional extensions without forcing implementation

---

## 📌 Key Takeaways

- ISP is about **client needs**, not about the class implementing the interface
- Small, focused interfaces are **easier to implement, test, and mock**
- ISP reduces the **impact of change** — changing one small interface doesn't affect unrelated clients
- ISP works hand-in-hand with **SRP** — both promote cohesion
- In Java, multiple interface implementation makes ISP natural to apply

---

## 🔗 Relationship with Other Principles

- ISP is closely related to **SRP** — both promote focused, cohesive design
- ISP enables **LSP** — smaller interfaces are easier to implement correctly
- ISP combined with **DIP** enables clean dependency injection

---

## 📄 See Code Example

➡️ [`08_Interface_Segregation_Principle.java`](./08_Interface_Segregation_Principle.java)

---

*Previous: [05_Liskov_Substitution_Principle.md](./05_Liskov_Substitution_Principle.md)*
*Next: [09_Dependency_Inversion_Principle.md](./09_Dependency_Inversion_Principle.md)*
