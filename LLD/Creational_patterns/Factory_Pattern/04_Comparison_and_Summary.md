# 04 – Factory Patterns: Complete Comparison & Decision Guide

> **Study order:** Read this LAST — after all three pattern files.  
> This file synthesises everything and helps you decide which pattern to use in any scenario.

---

## 1. The Big Picture in One Diagram

```
Object Creation Problem
         │
         ▼
 Do you need to hide
 concrete class names
 from client code?
         │
        YES
         │
         ▼
 ┌──────────────────────────────────────────────────────────┐
 │              FACTORY PATTERNS                            │
 │                                                          │
 │  Simple Factory ──► Factory Method ──► Abstract Factory  │
 │  (idiom)            (GoF pattern)      (GoF pattern)     │
 └──────────────────────────────────────────────────────────┘
         │                   │                    │
    One static          Abstract class      Interface with
    method with         + subclass          multiple creation
    switch/if           override            methods (family)
```

---

## 2. Side-by-Side Comparison Table

| Criterion                    | Simple Factory            | Factory Method               | Abstract Factory                     |
|------------------------------|---------------------------|------------------------------|--------------------------------------|
| **GoF Pattern?**             | ❌ No (idiom)              | ✅ Yes                        | ✅ Yes                                |
| **Java mechanism**           | `static` method + switch  | `abstract` method in class   | `interface` with multiple methods    |
| **How creation varies**      | `switch/if` in one method | Subclass overrides method    | Different implementing class         |
| **Products created**         | One type                  | One type                     | Multiple related types (family)      |
| **OCP compliance**           | ❌ Fails (edit factory)    | ✅ Extend via subclass         | ✅ Extend by adding factory + products|
| **Complexity**               | 🟢 Low                    | 🟡 Medium                    | 🔴 High                              |
| **Boilerplate**              | Minimal                   | Moderate                     | High (many classes/interfaces)       |
| **Client depends on**        | Factory class (concrete)  | Creator abstraction (abstract)| Factory interface                   |
| **Extension mechanism**      | Edit `switch` block       | Add new subclass             | Add new implementing class           |
| **Family consistency**       | ❌ Not enforced            | ❌ Not enforced                | ✅ Enforced by design                 |
| **Best for**                 | Small, stable products    | Single product, extensible   | Multiple interdependent products     |

---

## 3. Structural Comparison

### Simple Factory
```
Client ──► VehicleFactory.create("car")
                    │
            static switch/if
                    │
               return new Car()
```

### Factory Method
```
Client ──► NotificationService.notify()   [abstract class]
                │
                └──► this.createNotification()   ← abstract method
                             │
                  EmailService.createNotification()   [subclass]
                             │
                     return new EmailNotification()
```

### Abstract Factory
```
Client ──► GUIFactory (interface)
               ├── createButton()   ──► Button
               ├── createCheckbox() ──► Checkbox
               └── createTextBox()  ──► TextBox
                        ↑
             WindowsFactory   or   MacFactory
             (implements GUIFactory)
```

---

## 4. Code Comparison – Same Domain (Vehicle Creation)

### 4.1 Simple Factory
```java
public class VehicleFactory {
    public static Vehicle create(String type) {
        switch (type) {
            case "car":  return new Car();
            case "bike": return new Bike();
            default: throw new IllegalArgumentException(type);
        }
    }
}

// Client
Vehicle v = VehicleFactory.create("car");
```

### 4.2 Factory Method
```java
public abstract class VehicleCreator {
    public abstract Vehicle createVehicle();   // ← factory method

    public void startJourney() {
        Vehicle v = createVehicle();           // polymorphic call
        v.move();
    }
}

public class CarCreator extends VehicleCreator {
    @Override
    public Vehicle createVehicle() {
        return new Car();
    }
}

// Client
VehicleCreator creator = new CarCreator();
creator.startJourney();
```

### 4.3 Abstract Factory
```java
public interface TransportFactory {
    Vehicle createVehicle();
    Driver  createDriver();
    Route   createRoute();
}

public class UrbanTransportFactory implements TransportFactory {
    @Override public Vehicle createVehicle() { return new Car(); }
    @Override public Driver  createDriver()  { return new CityDriver(); }
    @Override public Route   createRoute()   { return new CityRoute(); }
}

// Client
TransportFactory factory = new UrbanTransportFactory();
Vehicle vehicle = factory.createVehicle();
Driver  driver  = factory.createDriver();
Route   route   = factory.createRoute();
// Guaranteed to be a consistent Urban family ✅
```

---

## 5. Decision Flowchart

```
START
  │
  ▼
Do you need to create multiple
RELATED objects as a consistent family?
  │
  ├── YES ──► Use ABSTRACT FACTORY
  │
  └── NO
        │
        ▼
  Do you want subclasses to control
  which object gets created?
        │
        ├── YES ──► Use FACTORY METHOD
        │
        └── NO
              │
              ▼
        Is your product set small &
        unlikely to change often?
              │
              ├── YES ──► Use SIMPLE FACTORY
              │
              └── NO ──── Reconsider your design.
                          Maybe separate concerns first.
```

---

## 6. Java Keyword Summary

| Pattern          | Key Java Keyword(s)              | Example Signature                                |
|------------------|----------------------------------|--------------------------------------------------|
| Simple Factory   | `static`                         | `public static Vehicle create(String type)`      |
| Factory Method   | `abstract` (in class)            | `public abstract Notification createNotification()` |
| Abstract Factory | `interface` (multiple methods)   | `Button createButton(); Checkbox createCheckbox();` |

---

## 7. SOLID Principle Compliance

| SOLID Principle           | Simple Factory | Factory Method | Abstract Factory |
|---------------------------|:--------------:|:--------------:|:----------------:|
| **S** – Single Responsibility | 🟡 Partial  | ✅ Yes          | ✅ Yes            |
| **O** – Open/Closed           | ❌ No        | ✅ Yes          | ✅ Yes            |
| **L** – Liskov Substitution   | ✅ Yes       | ✅ Yes          | ✅ Yes            |
| **I** – Interface Segregation | 🟡 N/A      | ✅ Yes          | ⚠️ Watch out (large interfaces) |
| **D** – Dependency Inversion  | ❌ Partial   | ✅ Yes          | ✅ Yes            |

---

## 8. Real-World Pattern Recognition Cheat Sheet

| Situation You Encounter                                         | Pattern to Use          |
|-----------------------------------------------------------------|-------------------------|
| `if (type.equals("A")) return new A()` scattered in code       | Simple Factory (centralize) |
| Abstract Java class with one abstract `createX()` method        | Factory Method          |
| `new ProductA()` coupled in a class — hard to unit test         | Factory Method          |
| UI components: button + dialog + icon must all match a theme    | Abstract Factory        |
| JDBC: connection + statement + result set must match one RDBMS  | Abstract Factory        |
| Plugin system where each plugin provides multiple services      | Abstract Factory        |
| Quick prototyping, 3–4 products, internal utility               | Simple Factory          |

---

## 9. Common Mistakes to Avoid

| Mistake                                                         | Why It's Wrong                                       |
|-----------------------------------------------------------------|------------------------------------------------------|
| Using Abstract Factory when you only have 1 product             | Overkill; use Factory Method instead                 |
| Putting all logic in Simple Factory for 15+ product types       | Hard to maintain; switch to Factory Method           |
| Making factory methods `static` in Factory Method pattern       | Defeats polymorphism; subclasses cannot override     |
| Adding a new product type to Abstract Factory carelessly         | Requires changing ALL implementing factory classes   |
| Client code calling `new ConcreteClass()` alongside factory use | Defeats the purpose; be consistent                  |

---

## 10. Evolution Path

As your system grows, patterns naturally evolve:

```
Simple Factory
     │
     │  Products proliferate / OCP violated
     ▼
Factory Method
     │
     │  Multiple related products needed together
     ▼
Abstract Factory
     │
     │  Too many products per factory / need runtime flexibility
     ▼
Registry + Prototype / Builder patterns
```

---

## 11. Quick Memory Tricks

| Pattern            | Memory Hook                                                    |
|--------------------|----------------------------------------------------------------|
| **Simple Factory** | "One manager who knows how to hire everyone"                   |
| **Factory Method** | "Hire specialists — each one hires one type of employee"       |
| **Abstract Factory**| "Hire a staffing agency — they hire an entire department team" |

---

## 12. Interview Questions & Answers

**Q: What's the difference between Factory Method and Abstract Factory?**  
> Factory Method uses **inheritance** — one abstract method overridden in subclasses, one product type.  
> Abstract Factory uses **composition** — one factory *interface* with multiple creation methods covering a *family* of products.

**Q: Is Simple Factory a design pattern?**  
> No. It's a programming idiom. It does not appear in the GoF book. However, it's widely taught as a precursor to the real patterns.

**Q: When would you NOT use a factory pattern?**  
> When object creation is trivial, happens in one place only, and the product set is fixed and tiny. Over-engineering with factories adds unnecessary boilerplate and complexity.

**Q: How does Abstract Factory differ from a collection of Factory Methods?**  
> They look structurally similar. The key difference is *intent*: Abstract Factory is specifically designed to ensure **product family consistency** across multiple product types. Factory Method is about letting subclasses control creation of **one** product type.

**Q: In Java, how do I choose between an abstract class and interface for Factory Method?**  
> Use an **abstract class** when the Creator also contains shared implementation (e.g., the `notify()` method that calls `createNotification()`).  
> Use an **interface** when all creation is delegated — this gives more flexibility for the implementer.

---

## 13. File Reference Index

| File                            | Pattern          | Key Concept                                |
|---------------------------------|------------------|--------------------------------------------|
| `01_Simple_Factory.md`          | Simple Factory   | Centralised static creation, `switch/if`   |
| `02_Factory_Method.md`          | Factory Method   | `abstract` method, subclass decides product|
| `03_Abstract_Factory.md`        | Abstract Factory | `interface`, product family consistency    |
| `04_Comparison_and_Summary.md`  | All three        | Decision guide, comparison, interview Q&A  |

---

> 🎯 **Final takeaway:**  
> Start with **Simple Factory** to understand the problem.  
> Graduate to **Factory Method** for extensibility.  
> Reach for **Abstract Factory** only when families of related products matter.
