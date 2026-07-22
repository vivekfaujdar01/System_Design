# Module 09 – Builder vs Factory

> **Study order:** Read after `08_Immutable_Objects.md`.  
> This is a very common interview topic. Understand the differences deeply.

---

## Table of Contents
1. [Similarities](#1-similarities)
2. [Core Philosophical Difference](#2-core-philosophical-difference)
3. [Structural Difference](#3-structural-difference)
4. [Side-by-Side Code Comparison](#4-side-by-side-code-comparison)
5. [Differences Table](#5-differences-table)
6. [Decision Guide: Which to Use?](#6-decision-guide-which-to-use)
7. [Can They Work Together?](#7-can-they-work-together)
8. [Interview Comparison Summary](#8-interview-comparison-summary)

---

## 1. Similarities

Both Builder and Factory are **Creational patterns** — they both deal with **object creation**:

| Similarity                        | Builder | Factory |
|-----------------------------------|:-------:|:-------:|
| Hides concrete class from client  | ✅      | ✅      |
| Returns an object to the client   | ✅      | ✅      |
| Decouples creation from usage     | ✅      | ✅      |
| GoF Creational pattern            | ✅      | ✅      |
| Client depends on abstraction     | ✅      | ✅      |

---

## 2. Core Philosophical Difference

```
Factory answers:  "WHICH object do I create?" (type/class selection)
Builder answers:  "HOW do I construct this one object?" (step-by-step configuration)
```

```
Factory → Polymorphism problem (which subtype?)
Builder → Configuration problem (how many fields, in what combination?)
```

```
Factory = "What kind of pizza?" → Margherita / Pepperoni / BBQ Chicken
Builder = "How do you want your pizza?"
              → size: Large
              → crust: Thin
              → cheese: Extra
              → toppings: Mushrooms, Olives
              → sauce: Spicy
```

---

## 3. Structural Difference

### Factory (Simple Factory)
```
Client ──────────────────────► VehicleFactory.create("car")
                                        │
                              static switch statement
                                        │
                              ┌─────────┴─────────┐
                              │                   │
                           new Car()           new Bike()
                              │                   │
                           [Product A]         [Product B]

Client gets DIFFERENT TYPES back.
Factory decides which type — client just names what it wants.
```

### Builder
```
Client ──► new Pizza.Builder("Large")   ← configures step by step
               .cheese(true)
               .pepperoni(false)
               .mushrooms(true)
               .build()
                    │
              new Pizza(builder)
                    │
               [Single Product — but CONFIGURED]

Client gets THE SAME TYPE back — just configured differently.
Client decides the configuration — Builder assembles it.
```

---

## 4. Side-by-Side Code Comparison

### Same Domain: Vehicle Creation

#### Factory Approach
```java
// Factory decides WHICH class to instantiate
public class VehicleFactory {
    public static Vehicle create(String type) {
        switch (type.toLowerCase()) {
            case "car":   return new Car();     // different type
            case "truck": return new Truck();   // different type
            case "bike":  return new Bike();    // different type
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }
}

// Client
Vehicle v1 = VehicleFactory.create("car");    // returns Car
Vehicle v2 = VehicleFactory.create("truck");  // returns Truck
// v1 and v2 are DIFFERENT TYPES
```

#### Builder Approach
```java
// Builder assembles ONE type with different configurations
public final class Car {
    private final String brand;
    private final String color;
    private final String engine;
    private final int    doors;
    private final boolean sunroof;

    private Car(Builder b) {
        this.brand   = b.brand;
        this.color   = b.color;
        this.engine  = b.engine;
        this.doors   = b.doors;
        this.sunroof = b.sunroof;
    }

    public static class Builder {
        private final String brand;
        private String color   = "White";
        private String engine  = "1.5L Petrol";
        private int    doors   = 4;
        private boolean sunroof = false;

        public Builder(String brand)           { this.brand = brand; }
        public Builder color(String color)     { this.color = color; return this; }
        public Builder engine(String engine)   { this.engine = engine; return this; }
        public Builder doors(int doors)        { this.doors = doors; return this; }
        public Builder sunroof(boolean s)      { this.sunroof = s; return this; }
        public Car build()                     { return new Car(this); }
    }
}

// Client
Car sportsCar = new Car.Builder("Toyota")
    .color("Red").engine("2.0L Turbo").doors(2).sunroof(true).build();

Car familyCar = new Car.Builder("Honda")
    .color("Silver").engine("1.5L Petrol").doors(4).build();

// Both are Cars — SAME TYPE — but differently configured
```

---

## 5. Differences Table

| Aspect                        | Factory (Simple/Method)                   | Builder                                       |
|-------------------------------|-------------------------------------------|-----------------------------------------------|
| **Primary question**          | Which class/type to create?               | How to configure one class?                   |
| **Return type**               | Different subtypes (polymorphism)         | Same type, different configurations           |
| **Object complexity**         | Usually simple objects                    | Complex objects with many fields              |
| **Optional fields**           | All-or-nothing (via constructor)          | Elegant optional field handling               |
| **Number of steps**           | One call to factory method                | Multiple step-by-step setter calls            |
| **Client control**            | Client names the type                     | Client configures every field                 |
| **Immutability support**      | Not inherently immutable                  | Naturally produces immutable objects          |
| **Validation location**       | Inside factory method                     | Inside build() + constructor                  |
| **When objects differ**       | By TYPE (Car vs Truck)                    | By CONFIGURATION (Red Car vs Blue Car)        |
| **GoF intent**                | "Create families / let subclass decide"   | "Separate construction from representation"   |
| **Java mechanism**            | `switch` / `if-else` or abstract method   | Step-by-step setters + `build()`              |
| **Number of classes**         | Factory + Product hierarchy               | Product + nested Builder                      |

---

## 6. Decision Guide: Which to Use?

```
Q1: Do you need to create objects of DIFFERENT TYPES?
    YES → Use Factory (the type is what varies)
    NO  → continue to Q2

Q2: Does your object have MANY optional/configurable fields?
    YES → Use Builder
    NO  → continue to Q3

Q3: Is the object construction MULTI-STEP?
    YES → Use Builder
    NO  → A simple constructor is probably fine

Q4: Do you need the object to be IMMUTABLE?
    YES → Use Builder (naturally supports immutability)
    NO  → Either will work
```

### Quick Examples

| Scenario                                                              | Pattern       |
|-----------------------------------------------------------------------|---------------|
| Payment gateway: choose Card, UPI, or Wallet based on user choice     | Factory       |
| Build an HTTP request with method, headers, body, timeout, auth       | Builder       |
| Notification: choose Email, SMS, or Push based on preference          | Factory       |
| Build a SQL query: select columns, where, order, limit, offset        | Builder       |
| Database connection: choose MySQL, PostgreSQL, or SQLite              | Factory       |
| Build a pizza with toppings, size, crust, extra cheese, sauce         | Builder       |
| Create a Shape: Circle, Rectangle, or Triangle                        | Factory       |
| Build an employee profile with optional fields                        | Builder       |

---

## 7. Can They Work Together?

**Yes! Factory can use Builder internally, and Builder can use Factory for sub-objects.**

```java
// Pattern: Factory selects the right Builder, Builder constructs the product

public class ReportFactory {

    // Factory decides WHICH builder to use (type selection)
    public static Report create(String format, String title, String data) {
        switch (format.toLowerCase()) {
            case "pdf":
                return new PdfReport.Builder(title)   // Builder configures it
                    .data(data)
                    .watermark("CONFIDENTIAL")
                    .build();

            case "html":
                return new HtmlReport.Builder(title)  // Different Builder
                    .data(data)
                    .theme("dark")
                    .build();

            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }
}

// Client uses Factory interface — doesn't know about Builders at all
Report pdfReport  = ReportFactory.create("pdf",  "Sales Report", "data...");
Report htmlReport = ReportFactory.create("html", "Sales Report", "data...");
```

---

## 8. Interview Comparison Summary

**Q: What is the difference between Builder and Factory?**

> **Factory** solves the **"which type?"** problem — it selects and returns different subtypes based on input (e.g., `Car`, `Truck`, `Bike`). The client doesn't know or care which concrete class is returned.
>
> **Builder** solves the **"how to configure?"** problem — it assembles one complex object step-by-step with many optional fields. The client always gets the same type back, just configured differently.
>
> **Key distinction:** Factory varies the *type*. Builder varies the *configuration*.

**Q: When would you use Builder over Factory?**

> Use Builder when:
> 1. The object has many fields (especially optional ones)
> 2. You need readable, named-parameter style construction
> 3. You want the object to be immutable after creation
> 4. You need to validate combinations of fields before creating the object

**Q: Can Builder and Factory be used together?**

> Yes. A Factory can internally use a Builder to construct complex products. For example, a `DatabaseFactory.create("mysql")` might internally use a `MySQLConnectionBuilder` to assemble the connection object with all its configuration.

---

**← Prev:** [`08_Immutable_Objects.md`](./08_Immutable_Objects.md)  
**Next →** [`10_Builder_vs_All_Factories.md`](./10_Builder_vs_All_Factories.md)
