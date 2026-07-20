# D — Dependency Inversion Principle (DIP)

> **"High-level modules should not depend on low-level modules. Both should depend on abstractions."**
> **"Abstractions should not depend on details. Details should depend on abstractions."**
> — Robert C. Martin

---

## 📖 Definition

The **Dependency Inversion Principle** has two key rules:

1. **High-level modules** (business logic, policies) should NOT depend on **low-level modules** (database, file system, HTTP).
2. Both should depend on **abstractions** (interfaces or abstract classes).
3. **Abstractions** should NOT depend on **details** (concrete implementations).
4. **Details** (concrete classes) should depend on abstractions.

In simple terms: **Program to interfaces, not to implementations.**

---

## 🧠 Why Does It Matter?

Without DIP:
- Business logic is tightly coupled to infrastructure (databases, APIs, file systems)
- Changing the database requires changing business logic code
- Unit testing is hard — you can't mock concrete dependencies
- Swapping implementations (e.g., MySQL → MongoDB) requires large refactoring

With DIP:
- High-level code is **stable** and **independent** of low-level details
- Low-level modules can be **swapped freely** (different DB, mock for tests)
- **Unit testing becomes trivial** — inject a mock implementation
- The system is **pluggable and extensible**

---

## ❌ Bad Example (Violates DIP)

```java
class MySQLDatabase {
    public void save(String data) {
        System.out.println("Saving to MySQL: " + data);
    }
}

// HIGH-LEVEL class depends directly on LOW-LEVEL concrete class ❌
class OrderService {
    private MySQLDatabase database = new MySQLDatabase(); // Hard dependency ❌

    public void placeOrder(String order) {
        // business logic...
        database.save(order);
    }
}
```

**Problems**:
- `OrderService` is coupled to `MySQLDatabase` — can't switch to PostgreSQL or MongoDB
- Cannot unit test `OrderService` without a real MySQL database
- Adding a mock for testing is impossible without modifying `OrderService`

---

## ✅ Good Example (Follows DIP)

```java
// Abstraction (interface)
interface Database {
    void save(String data);
}

// Low-level module 1 — depends on abstraction ✅
class MySQLDatabase implements Database {
    public void save(String data) { System.out.println("MySQL: " + data); }
}

// Low-level module 2 — depends on abstraction ✅
class MongoDatabase implements Database {
    public void save(String data) { System.out.println("MongoDB: " + data); }
}

// High-level module — depends on abstraction, NOT on concrete class ✅
class OrderService {
    private Database database; // depends on interface ✅

    public OrderService(Database database) {  // Dependency Injection ✅
        this.database = database;
    }

    public void placeOrder(String order) {
        // business logic...
        database.save(order);
    }
}

// Usage — inject the implementation you want
Database db = new MySQLDatabase();     // or new MongoDatabase() — easily swappable
OrderService service = new OrderService(db);
```

---

## 🔍 Dependency Injection (DI) — The DIP Enabler

DIP is usually achieved through **Dependency Injection**. There are three types:

| Type | How Dependency is Injected |
|------|---------------------------|
| **Constructor Injection** | Passed via constructor (most common, preferred) |
| **Setter Injection** | Set via a setter method |
| **Interface Injection** | The interface itself defines the injection method |

**Constructor injection is the most recommended** — it makes dependencies explicit and the class immutable after construction.

---

## 🌍 Real-World Analogies

| Analogy | Without DIP | With DIP |
|---------|------------|---------|
| Power sockets | Each device has its own fixed wiring | Standard socket (abstraction) any device can plug into |
| Streaming apps | App hard-coded to one CDN | CDN abstraction — swap providers freely |
| Payment systems | Checkout hard-coded to Stripe | `PaymentGateway` interface — Stripe, PayPal, Razorpay all implement it |
| Logging | Class uses `System.out.println` directly | `Logger` interface — console, file, cloud logging implementations |

---

## 📌 Key Takeaways

- DIP is the **"D"** in SOLID and is perhaps the most powerful principle for testability
- DIP does NOT mean "don't use concrete classes" — it means **don't directly depend on them in high-level code**
- DIP enables **mocking** in tests, **hot-swappable** implementations, and clean architecture
- Frameworks like **Spring** are built entirely around DIP (IoC Container = DI framework)
- DIP inverts the traditional layered architecture dependency direction

---

## 🏗️ Dependency Inversion in Architecture

```
Traditional (without DIP):
  UI → Business Logic → Database

With DIP:
  UI → Business Logic ← Database
              ↕
         (Interface)
```

High-level layers define the interface. Low-level layers implement it. Dependencies point **upward** (toward abstraction), not downward.

---

## 🔗 Relationship with Other Principles

- DIP **enables OCP** — by depending on abstractions, you can extend without modifying
- DIP makes **LSP** matter more — when you inject implementations, they must be substitutable
- DIP combined with **ISP** — inject small, focused interfaces rather than fat ones
- DIP is the foundation of **clean architecture**, **hexagonal architecture**, and **microservices**

---

## 📄 See Code Example

➡️ [`10_Dependency_Inversion_Principle.java`](./10_Dependency_Inversion_Principle.java)

---

*Previous: [07_Interface_Segregation_Principle.md](./07_Interface_Segregation_Principle.md)*
