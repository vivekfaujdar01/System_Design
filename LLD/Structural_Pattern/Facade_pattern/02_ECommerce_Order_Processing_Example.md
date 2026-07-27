# Module 2: E-Commerce Order Processing Example

E-commerce microservices and monolithic backends frequently use Facades to orchestrate complex multi-step workflows.

---

## 1. Problem Statement

When a user clicks **"Place Order"** on Amazon or Flipkart, the system must execute a complex sequence across multiple microservices:
1. **Inventory Service**: Verify item stock availability and lock inventory.
2. **Payment Service**: Charge the user's credit card or UPI account.
3. **Shipping Service**: Create a courier shipping label and assign a tracking ID.
4. **Notification Service**: Send email/SMS confirmation to the user.

```
                                  +-----------------------+
                                  |      OrderFacade      |
                                  +-----------------------+
                                  | + placeOrder(...)     |
                                  +-----------------------+
                                              │
         ┌───────────────────┬────────────────┴───────────────────┬───────────────────┐
         ▼                   ▼                                   ▼                   ▼
+------------------+ +------------------+               +------------------+ +---------------------+
| InventoryService | |  PaymentService  |               | ShippingService  | | NotificationService |
+------------------+ +------------------+               +------------------+ +---------------------+
```

Without a Facade, the web UI controller or mobile app would need to manually invoke all 4 services, manage error rollbacks, and handle intermediate states.

Using **`OrderFacade`**, the client simply invokes:
```java
orderFacade.placeOrder("PROD_1009", 2, "CARD_8899", "Bangalore, India");
```

---

## 2. Complete Step-by-Step Java Implementation

### Step 1: Subsystem Services (`InventoryService.java`, `PaymentService.java`, `ShippingService.java`, `NotificationService.java`)

```java
// Subsystem 1: Inventory Service
public class InventoryService {
    public boolean checkStock(String productId, int quantity) {
        System.out.println("  [Inventory Service] Checking stock for Product ID: " + productId);
        return true; // Stock available
    }

    public void reserveStock(String productId, int quantity) {
        System.out.println("  [Inventory Service] Reserved " + quantity + " unit(s) of " + productId);
    }
}

// Subsystem 2: Payment Service
public class PaymentService {
    public boolean processPayment(String accountId, double amount) {
        System.out.println("  [Payment Service] Charging ₹" + amount + " to account: " + accountId);
        System.out.println("  [Payment Service] Payment transaction SUCCESSFUL.");
        return true;
    }
}

// Subsystem 3: Shipping Service
public class ShippingService {
    public String createShippingLabel(String productId, String destinationAddress) {
        String trackingId = "TRK-" + System.currentTimeMillis() % 100000;
        System.out.println("  [Shipping Service] Generated courier tracking ID: " + trackingId + " to " + destinationAddress);
        return trackingId;
    }
}

// Subsystem 4: Notification Service
public class NotificationService {
    public void sendOrderConfirmation(String email, String orderId, String trackingId) {
        System.out.println("  [Notification Service] Sending confirmation email to " + email);
        System.out.println("  [Notification Service] Email Content: Order #" + orderId + " confirmed. Track at: " + trackingId);
    }
}
```

### Step 2: Facade Class (`OrderFacade.java`)

```java
// Order Facade coordinating all 4 microservices
public class OrderFacade {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notification;

    public OrderFacade() {
        this.inventory = new InventoryService();
        this.payment = new PaymentService();
        this.shipping = new ShippingService();
        this.notification = new NotificationService();
    }

    public boolean placeOrder(String productId, int quantity, double pricePerUnit, 
                             String userAccount, String email, String address) {
        System.out.println("=== PROCESSING E-COMMERCE ORDER FOR: " + productId + " ===");

        // Step 1: Check Stock
        if (!inventory.checkStock(productId, quantity)) {
            System.out.println("❌ Order Failed: Product out of stock.");
            return false;
        }

        // Step 2: Reserve Stock
        inventory.reserveStock(productId, quantity);

        // Step 3: Process Payment
        double totalAmount = pricePerUnit * quantity;
        boolean paymentSuccess = payment.processPayment(userAccount, totalAmount);
        if (!paymentSuccess) {
            System.out.println("❌ Order Failed: Payment declined.");
            return false;
        }

        // Step 4: Generate Shipping Tracking Label
        String trackingId = shipping.createShippingLabel(productId, address);

        // Step 5: Send Notification
        String orderId = "ORD-" + ((int) (Math.random() * 90000) + 10000);
        notification.sendOrderConfirmation(email, orderId, trackingId);

        System.out.println("=== SUCCESS: ORDER #" + orderId + " PLACED SUCCESSFULLY! ===\n");
        return true;
    }
}
```

### Step 3: Main Demonstration Execution (`Main.java`)

```java
public class Main {
    public static void main(String[] args) {
        // Instantiate Facade
        OrderFacade orderFacade = new OrderFacade();

        // Client places an order through ONE single simple method call!
        orderFacade.placeOrder(
            "LAPTOP-MACBOOK-M3",
            1,
            149900.0,
            "CARD_4111_2222_3333",
            "customer@example.com",
            "123 Tech Park, Whitefield, Bangalore"
        );
    }
}
```

### Execution Output
```text
=== PROCESSING E-COMMERCE ORDER FOR: LAPTOP-MACBOOK-M3 ===
  [Inventory Service] Checking stock for Product ID: LAPTOP-MACBOOK-M3
  [Inventory Service] Reserved 1 unit(s) of LAPTOP-MACBOOK-M3
  [Payment Service] Charging ₹149900.0 to account: CARD_4111_2222_3333
  [Payment Service] Payment transaction SUCCESSFUL.
  [Shipping Service] Generated courier tracking ID: TRK-54321 to 123 Tech Park, Whitefield, Bangalore
  [Notification Service] Sending confirmation email to customer@example.com
  [Notification Service] Email Content: Order #ORD-48291 confirmed. Track at: TRK-54321
=== SUCCESS: ORDER #ORD-48291 PLACED SUCCESSFULLY! ===
```

---

## 3. Order Processing Control Flow

```text
main()
  │
  └──► orderFacade.placeOrder(...)
         │
         ├──► 1. inventory.checkStock("LAPTOP-MACBOOK-M3", 1) ──► true
         ├──► 2. inventory.reserveStock("LAPTOP-MACBOOK-M3", 1)
         ├──► 3. payment.processPayment("CARD_4111...", 149900.0) ──► true
         ├──► 4. shipping.createShippingLabel(...) ──► returns "TRK-54321"
         └──► 5. notification.sendOrderConfirmation(...) ──► sends Email
```

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/02_ECommerce_Order_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Facade_pattern/code/02_ECommerce_Order_Example).
