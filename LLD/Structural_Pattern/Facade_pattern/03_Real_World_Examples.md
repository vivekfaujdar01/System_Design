# Module 3: Facade vs Adapter vs Decorator vs Bridge

These four structural patterns are frequently confused in interviews because all of them involve wrapping or connecting classes. However, their intentions are completely different.

---

## 1. Facade Pattern

### Purpose
Provide **one simple interface** to a complex subsystem. It hides complexity.

> **Keyword**: **Simplify**

### Example
Instead of calling:
* `Inventory`
* `Payment`
* `Shipping`
* `Invoice`
* `Notification`

You simply call:
```java
orderFacade.placeOrder();
```
The facade internally coordinates everything.

### Structure
```text
        Client
          │
          ▼
        Facade
     ┌────┼────┐
     ▼    ▼    ▼
     A    B    C
```
The client only talks to the facade.

### Real-Life Analogy
**Movie Mode button**: Instead of turning on TV, turning on Sound, switching HDMI, launching Netflix, and searching movie, you press one single button.

---

## 2. Adapter Pattern

### Purpose
Make two **incompatible interfaces** work together. It converts one interface into another.

> **Keyword**: **Convert**

### Example
Suppose your laptop has only a **USB-C** port, but your flash drive is **USB-A**. They can't connect directly. You use an **Adapter**.

```text
Laptop ──► USB-C Adapter ──► USB Flash Drive
```

### Software Example
* Expected interface: `Printer.print()`
* Existing class: `LegacyPrinter.printDocument()`
* **Adapter converts**: `print()` ──► `printDocument()`

No new functionality is added. Only the interface changes.

---

## 3. Decorator Pattern

### Purpose
Add **new behavior dynamically** without modifying the original class.

> **Keyword**: **Enhance**

### Coffee Example
```text
Basic Coffee ──► Milk ──► Sugar ──► Whipped Cream
```
Each decorator adds extra functionality and cost.

```text
Coffee ──► MilkDecorator ──► SugarDecorator ──► CreamDecorator
```
Every wrapper adds something new.

### Software Example
```java
coffee.cost(); 
// wrapped by
milkDecorator.cost(); 
// wrapped by
sugarDecorator.cost();
```
The object gains additional behavior at runtime.

---

## 4. Bridge Pattern

### Purpose
Separate **abstraction from implementation** so both can evolve independently.

> **Keyword**: **Separate**

### Example
Remote Control and TV Brand both vary independently.

```text
Remote (Abstraction) ──► Sony TV / Samsung TV / LG TV (Implementation)
```
You can combine any remote with any TV implementation.

### Structure
```text
Abstraction
     │
     ▼
Implementation
```
Instead of inheritance explosion (e.g., `SonyRemote`, `SamsungRemote`), Bridge uses composition.

---

## 5. Side-by-Side Comparison

| Pattern | Main Goal | Keyword | Primary Mechanism |
| :--- | :--- | :--- | :--- |
| **Facade** | Simplify a subsystem | **Simplify** | Provides one unified high-level interface over multiple classes |
| **Adapter** | Convert one interface to another | **Convert** | Wraps an incompatible object to match expected interface |
| **Decorator** | Add behavior dynamically | **Enhance** | Wraps an object with same interface to add responsibilities |
| **Bridge** | Separate abstraction & implementation | **Separate** | Composes abstraction with implementation interface |

---

## 6. Real-Life Analogy Summary

Imagine you are traveling abroad:

* **Adapter**: Different wall plug shape? You use a **travel adapter**. Problem solved. Nothing else changes.
* **Decorator**: You order pizza, then add **Cheese**, **Olives**, and **Mushrooms**. The pizza becomes richer with more features.
* **Facade**: You book an entire **holiday package**. The travel agency handles flight, hotel, taxi, and food. You make one request.
* **Bridge**: You choose independently:
  * Car Type: `SUV`, `Sedan`
  * Engine Type: `Petrol`, `Diesel`, `Electric`
  
  Instead of creating 6 classes (`PetrolSUV`, `ElectricSUV`, etc.), you compose them: `SUV + ElectricEngine`.

---

## 7. Common Interview Questions

### Q1. Does Facade hide subsystem classes?
**Yes**, from normal client interaction.

Normally, the client interacts only with the facade. However, subsystem classes are **not private or inaccessible**. If needed, another client can still use them directly.

```java
// Simplified access via Facade:
OrderFacade facade = new OrderFacade();
facade.placeOrder("Laptop", 50000);

// Direct access if advanced control is needed:
InventoryService inventory = new InventoryService();
inventory.checkStock("Laptop");
```
So the Facade offers a simpler path but **does not forbid direct access**.

### Q2. Does Facade add new functionality?
**No.**

A facade does not create new business logic.
```java
orderFacade.placeOrder();
```
Internally, it simply calls:
```java
inventory.checkStock();
payment.makePayment();
invoice.generateInvoice();
shipping.shipProduct();
notification.sendNotification();
```
It only organizes and simplifies existing operations.

---

## 8. Easy Memory Trick

| Pattern | Remember This |
| :--- | :--- |
| **Adapter** | I can't connect ──► **Convert** |
| **Decorator** | I need more features ──► **Enhance** |
| **Facade** | Too many classes ──► **Simplify** |
| **Bridge** | Too many combinations ──► **Separate** |

---

## 9. Key Takeaways

* **Facade**: Hides complexity behind one simple interface.
* **Adapter**: Makes incompatible interfaces compatible.
* **Decorator**: Adds responsibilities dynamically without changing original class.
* **Bridge**: Separates abstraction from implementation to avoid class explosion.
