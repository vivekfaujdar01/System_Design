# Module 1: Facade Pattern Fundamentals

## 1. What is a Facade?

A **Facade** is a structural design pattern that provides a single simplified interface to a complex subsystem.

Instead of the client interacting directly with many individual classes, the client interacts with one **Facade** class, which internally coordinates everything.

---

## 2. Definition

> **Facade Pattern**: Hides the complexity of a subsystem by exposing a single high-level interface.

---

## 3. Real-Life Examples

### Example 1: Home Theater System ("Movie Mode")
Imagine you want to watch a movie at home.

**Without a facade**, you need to manually:
1. Turn on TV
2. Turn on Sound System
3. Turn on Streaming Device
4. Select HDMI input
5. Connect Wi-Fi
6. Open Netflix
7. Search Movie
8. Press Play

Lots of individual steps.

Now imagine there is a single remote button: **`🎬 Movie Mode`**. You press one button, and everything happens automatically behind the scenes.

That button is the **Facade**.

```text
                                  Client
                                    │
                                    │
                            [ Movie Button ]  <-- Facade
                                    │
         ┌──────────────────┬───────┴───────┬──────────────────┐
         ▼                  ▼               ▼                  ▼
    ┌──────────┐     ┌─────────────┐   ┌─────────┐      ┌─────────────┐
    │    TV    │     │ Sound System│   │  Wi-Fi  │      │   Netflix   │
    └──────────┘     └─────────────┘   └─────────┘      └─────────────┘
```

The client only sees and interacts with one button.

---

### Example 2: Car Ignition System
When starting a car:
* Check fuel levels
* Start battery
* Start engine
* Enable ignition
* Check sensors

The driver does not execute these individually. The driver simply turns the key or presses the **Engine Start** button.

The ignition system acts as a **Facade**.

---

### Example 3: Software Example (Online Shopping Application)
Suppose you are creating an Online Shopping Application. To place an order, many services are involved:
* `InventoryService`
* `PaymentService`
* `NotificationService`
* `ShippingService`
* `InvoiceService`

#### Without Facade:
```text
Client ──► InventoryService ──► PaymentService ──► ShippingService ──► NotificationService ──► InvoiceService
```
The client must know every service, instantiate every object, and manage execution order.

#### With Facade:
```text
                               Client
                                 │
                                 ▼
                            OrderFacade
                                 │
   ┌───────────────┬─────────────┼─────────────┬───────────────┐
   ▼               ▼             ▼             ▼               ▼
Inventory       Payment       Shipping    Notification      Invoice
```
Now the client simply calls:
```java
orderFacade.placeOrder();
```
Everything happens internally.

---

## 4. Problem Without Facade vs. Solution With Facade

### Problem Without Facade
Suppose there are 10 subsystem classes ($A, B, C, \dots, J$).

```text
Client ──► A ──► B ──► C ──► D ──► E ──► F ──► G ──► H ──► I ──► J
```
The client now has to:
* Create every object
* Know the correct execution order
* Remember class dependencies
* Handle failures across all components

This makes the client **tightly coupled** with the subsystem.

### Solution With Facade
Introduce one object.

```text
Client ──► Facade ──► Subsystem (A, B, C, ...)
```
The facade knows:
* Which class to call
* When to call it
* In what order to call it

The client doesn't care about the internal complexity.

---

## 5. Intent of the Pattern

The Facade Pattern aims to:
1. Simplify a complex API.
2. Reduce coupling between client and subsystem.
3. Provide a single entry point.
4. Hide implementation details.

---

## 6. Structure & Components

```text
                Client
                  │
                  ▼
             Facade Class
        ──────────┴──────────
       │          │          │
  SubsystemA  SubsystemB  SubsystemC
```

### Components
1. **Client**: Uses only the facade (e.g., `facade.startMovie()`).
2. **Facade**: Provides simplified high-level methods (e.g., `placeOrder()`, `startMovie()`, `bookTicket()`). It internally calls multiple subsystem methods.
3. **Subsystem Classes**: Perform the actual work (e.g., `PaymentService`, `InventoryService`). They are unaware of the facade and can still be used directly if needed.

---

## 7. When Should You Use Facade?

Use the Facade Pattern when:
* A subsystem has many interacting classes.
* Clients should not depend on internal implementation details.
* You want a simpler API for common operations.
* You want to reduce coupling between clients and complex libraries.

---

## 8. Advantages & Limitations

### Advantages
* ✅ **Simplifies Client Code**: Client calls one method instead of orchestrating many.
* ✅ **Reduces Coupling**: Decouples client from subsystem classes.
* ✅ **Hides Complexity**: Subsystem internal workflows remain encapsulated.
* ✅ **Improves Readability**: Makes the subsystem significantly easier to use.

### Limitations
* ❌ **God Object Risk**: The facade can become a "God Object" if too many responsibilities are added.
* ❌ **Does Not Prevent Direct Access**: It doesn't prevent clients from directly accessing subsystem classes if they choose to.

---

## 9. Key Takeaways

* A Facade provides **one simple interface** to a complex subsystem.
* It does **not add new functionality**; it only simplifies access.
* The subsystem classes remain **independent and reusable**.
* Clients interact with the facade instead of managing many subsystem objects.
