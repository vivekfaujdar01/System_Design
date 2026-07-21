# 01 – Simple Factory

> **Study order:** Start here. This is NOT a GoF design pattern — it's a programming idiom / helper class.  
> It lays the conceptual groundwork so that Factory Method and Abstract Factory make sense.

---

## 1. What Problem Does It Solve?

Without a factory, client code that needs to create objects ends up littered with `if/else` or `switch` blocks:

```java
// ❌ Client is tightly coupled to every concrete class
Vehicle vehicle;
if (vehicleType.equals("Car")) {
    vehicle = new Car();
} else if (vehicleType.equals("Bike")) {
    vehicle = new Bike();
} else if (vehicleType.equals("Truck")) {
    vehicle = new Truck();
}
```

Every time a new vehicle type is added, **every caller** must be updated. This violates the **Open/Closed Principle**.

---

## 2. Core Idea

Centralise object creation in **one place** — a *factory class* — so clients only ask for an object by type/key and never instantiate a concrete class themselves.

```
Client  ──asks──►  VehicleFactory  ──creates──►  ConcreteProduct
                        │
                  knows all concrete
                     classes
```

---

## 3. Structure

| Participant         | Role                                                       |
|---------------------|------------------------------------------------------------|
| **Product**         | Common interface for created objects                       |
| **ConcreteProduct** | Specific implementations (`Car`, `Bike`, `Truck` …)       |
| **SimpleFactory**   | Static method that decides which concrete class to instantiate |
| **Client**          | Calls the factory; never touches concrete classes directly |

---

## 4. Code Example (Java)

### 4.1 Product Interface

```java
// Vehicle.java
public interface Vehicle {
    String getInfo();
}
```

### 4.2 Concrete Products

```java
// Car.java
public class Car implements Vehicle {
    @Override
    public String getInfo() {
        return "🚗  Car: 4 wheels, enclosed cabin";
    }
}

// Bike.java
public class Bike implements Vehicle {
    @Override
    public String getInfo() {
        return "🚴  Bike: 2 wheels, open ride";
    }
}

// Truck.java
public class Truck implements Vehicle {
    @Override
    public String getInfo() {
        return "🚚  Truck: 6 wheels, heavy load";
    }
}
```

### 4.3 Simple Factory

```java
// VehicleFactory.java
public class VehicleFactory {

    /**
     * Centralises object creation.
     * Client code only imports VehicleFactory, never Car / Bike / Truck.
     */
    public static Vehicle create(String vehicleType) {
        switch (vehicleType.toLowerCase()) {
            case "car":   return new Car();
            case "bike":  return new Bike();
            case "truck": return new Truck();
            default:
                throw new IllegalArgumentException(
                    "Unknown vehicle type: '" + vehicleType + "'"
                );
        }
    }
}
```

### 4.4 Client Code

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        String[] types = {"car", "bike", "truck"};

        for (String type : types) {
            Vehicle vehicle = VehicleFactory.create(type);
            System.out.println(vehicle.getInfo());
        }
    }
}
```

**Output:**
```
🚗  Car: 4 wheels, enclosed cabin
🚴  Bike: 2 wheels, open ride
🚚  Truck: 6 wheels, heavy load
```

---

## 5. File Structure for This Example

```
simple_factory/
├── Vehicle.java         ← Product interface
├── Car.java             ← Concrete product
├── Bike.java            ← Concrete product
├── Truck.java           ← Concrete product
├── VehicleFactory.java  ← Simple Factory (static method)
└── Main.java            ← Client
```

---

## 6. Real-World Analogies

| Analogy               | Factory                         | Products                              |
|-----------------------|---------------------------------|---------------------------------------|
| Pizza shop            | `PizzaFactory`                  | `Margherita`, `Pepperoni`, `BBQ`      |
| Java `Calendar`       | `Calendar.getInstance()`        | `GregorianCalendar`, `BuddhistCalendar` |
| Java `NumberFormat`   | `NumberFormat.getCurrencyInstance()` | `DecimalFormat` variants          |
| JDBC                  | `DriverManager.getConnection()` | MySQL / PostgreSQL `Connection` objects |

---

## 7. Pros and Cons

| ✅ Pros                                       | ❌ Cons                                                              |
|-----------------------------------------------|----------------------------------------------------------------------|
| Single place to change creation logic         | Factory class itself violates OCP — adding a product = editing it    |
| Clients decoupled from concrete classes       | All concrete classes must be imported into one file (coupling at top)|
| Easy to add a lookup/registry pattern         | Not easily extensible via inheritance                               |
| Simple to understand and implement            | Not a true GoF pattern — lacks formal structure                     |

---

## 8. When to Use

- ✅ Small set of products that rarely changes
- ✅ You just want to hide `new` calls from client code
- ✅ Quick prototyping / internal utility

## 9. When NOT to Use

- ❌ Products change or grow frequently → use **Factory Method** (next file)
- ❌ You have *families* of related products → use **Abstract Factory** (file 03)

---

## 10. Key Takeaway

> Simple Factory = **one centralised factory that knows all products**.  
> It solves coupling but introduces a different coupling: the factory class itself.  
> The GoF patterns (Factory Method, Abstract Factory) solve *that* problem.

---

**Next →** [`02_Factory_Method.md`](./02_Factory_Method.md)
