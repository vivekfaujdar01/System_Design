# Module 2: Facade Pattern in Java (Online Shopping System)

Let's implement the Facade Pattern using a simple Online Shopping System.

---

## 1. Problem Statement

When a customer places an order, multiple services need to work together:
1. **Check Inventory** (`InventoryService`)
2. **Process Payment** (`PaymentService`)
3. **Generate Invoice** (`InvoiceService`)
4. **Arrange Shipping** (`ShippingService`)
5. **Send Notification** (`NotificationService`)

Without a facade, the client must instantiate and call each service individually.

---

## 2. Code Implementation Without Facade

### Subsystem Classes

```java
// Inventory Service
class InventoryService {
    public boolean checkStock(String product) {
        System.out.println("Checking stock for " + product);
        return true;
    }
}

// Payment Service
class PaymentService {
    public void makePayment(double amount) {
        System.out.println("Payment of ₹" + amount + " successful.");
    }
}

// Invoice Service
class InvoiceService {
    public void generateInvoice() {
        System.out.println("Invoice generated.");
    }
}

// Shipping Service
class ShippingService {
    public void shipProduct() {
        System.out.println("Product shipped.");
    }
}

// Notification Service
class NotificationService {
    public void sendNotification() {
        System.out.println("Notification sent.");
    }
}
```

### Client Code (Without Facade)

```java
public class MainWithoutFacade {
    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();
        PaymentService payment = new PaymentService();
        InvoiceService invoice = new InvoiceService();
        ShippingService shipping = new ShippingService();
        NotificationService notification = new NotificationService();

        if (inventory.checkStock("Laptop")) {
            payment.makePayment(50000);
            invoice.generateInvoice();
            shipping.shipProduct();
            notification.sendNotification();
        }
    }
}
```

### Output
```text
Checking stock for Laptop
Payment of ₹50000 successful.
Invoice generated.
Product shipped.
Notification sent.
```

### Problems Here
The client:
* Knows every service class.
* Creates every subsystem object.
* Knows the exact execution order.
* Must update its code whenever the workflow or service order changes.

This violates the idea of keeping the client simple and loosely coupled.

---

## 3. Applying the Facade Pattern

We introduce a single class: **`OrderFacade`**.

The client will only call:
```java
orderFacade.placeOrder("Laptop", 50000);
```
Everything else happens internally.

---

## 4. Code Implementation With Facade

### OrderFacade Class

```java
class OrderFacade {
    private InventoryService inventory;
    private PaymentService payment;
    private InvoiceService invoice;
    private ShippingService shipping;
    private NotificationService notification;

    public OrderFacade() {
        this.inventory = new InventoryService();
        this.payment = new PaymentService();
        this.invoice = new InvoiceService();
        this.shipping = new ShippingService();
        this.notification = new NotificationService();
    }

    public void placeOrder(String product, double amount) {
        if (inventory.checkStock(product)) {
            payment.makePayment(amount);
            invoice.generateInvoice();
            shipping.shipProduct();
            notification.sendNotification();
        } else {
            System.out.println("Product is out of stock.");
        }
    }
}
```

### Client Code (With Facade)

```java
public class Main {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();

        orderFacade.placeOrder("Laptop", 50000);
    }
}
```

Notice how the client no longer interacts with the five subsystem classes directly.

### Output
```text
Checking stock for Laptop
Payment of ₹50000 successful.
Invoice generated.
Product shipped.
Notification sent.
```

The behavior is identical, but the client code is much cleaner.

---

## 5. Execution Flow

### Without Facade
```text
Client
  │
  ├──► InventoryService
  ├──► PaymentService
  ├──► InvoiceService
  ├──► ShippingService
  └──► NotificationService
```
The client is responsible for coordinating everything.

### With Facade
```text
Client
  │
  ▼
OrderFacade
  │
  ├──► InventoryService
  ├──► PaymentService
  ├──► InvoiceService
  ├──► ShippingService
  └──► NotificationService
```
The coordination logic moves entirely into the `OrderFacade`.

---

## 6. What Changed?

### Before (Client Orchestrates Workflow)
```java
inventory.checkStock();
payment.makePayment();
invoice.generateInvoice();
shipping.shipProduct();
notification.sendNotification();
```

### After (Facade Orchestrates Workflow)
```java
orderFacade.placeOrder();
```

---

## 7. Responsibilities

* **Client**: Requests a high-level operation. Doesn't know how the subsystem works.
* **Facade**: Creates or holds subsystem objects. Coordinates the sequence of operations. Exposes a simple API.
* **Subsystem Classes**: Perform their individual tasks. Don't know that a facade exists. Can still be used directly if needed.

---

## 8. Key Observation

The Facade **does not merge** the subsystem classes into one. Each service still has a single responsibility:
* `InventoryService` → Stock checking
* `PaymentService` → Payment processing
* `InvoiceService` → Billing
* `ShippingService` → Delivery logistics
* `NotificationService` → Customer updates

The Facade simply provides a convenient way to use them together.

---

## 9. Key Takeaways

* Without a facade, the client controls the workflow.
* With a facade, the facade controls the workflow.
* The subsystem remains unchanged; only a simplified entry point is added.
* This reduces coupling and makes the client code easier to read and maintain.
