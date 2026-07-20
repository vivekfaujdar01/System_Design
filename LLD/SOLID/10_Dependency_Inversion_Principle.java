// ============================================================
//  SOLID Principle #5 — Dependency Inversion Principle (DIP)
//  "High-level modules should not depend on low-level modules.
//   Both should depend on abstractions."
// ============================================================

// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Violates DIP
//  OrderService (high-level) directly depends on
//  MySQLDatabase (low-level concrete class).
// ─────────────────────────────────────────────

// Low-level: concrete database implementation
class BadMySQLDatabase {
    public void save(String data) {
        System.out.println("[MySQL] Saving order: " + data);
    }
    public String find(String id) {
        return "[MySQL] Found order: " + id;
    }
}

// Low-level: concrete email sender
class BadEmailSender {
    public void sendEmail(String to, String message) {
        System.out.println("[Email] Sending to " + to + ": " + message);
    }
}

// High-level — tightly coupled to concrete low-level classes ❌
class BadOrderService {
    // Hard dependencies on concrete classes — cannot swap or mock ❌
    private BadMySQLDatabase database = new BadMySQLDatabase();
    private BadEmailSender emailSender = new BadEmailSender();

    public void placeOrder(String orderId, String customerEmail) {
        // Business logic
        database.save(orderId);
        emailSender.sendEmail(customerEmail, "Order " + orderId + " placed!");
        System.out.println("Order placed: " + orderId);
    }
}
// Problem: To test BadOrderService, you need a real MySQL DB and real email server ❌
// Problem: Switching to MongoDB or SMS requires MODIFYING BadOrderService ❌


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Follows DIP
//  Both high-level and low-level modules depend on abstractions.
//  Dependency injection makes implementations swappable.
// ─────────────────────────────────────────────

// ── Abstractions (Interfaces) ──

interface OrderRepository {
    void save(String orderId);
    String findById(String orderId);
}

interface NotificationService {
    void notify(String recipient, String message);
}

interface PaymentGateway {
    boolean processPayment(String orderId, double amount);
}


// ── Low-Level: Concrete Implementations (depend on abstractions) ──

// MySQL implementation ✅
class MySQLOrderRepository implements OrderRepository {
    @Override
    public void save(String orderId) {
        System.out.println("[MySQL] Saved order: " + orderId);
    }
    @Override
    public String findById(String orderId) {
        return "[MySQL] Order: " + orderId;
    }
}

// MongoDB implementation ✅ — can be swapped in without touching OrderService
class MongoOrderRepository implements OrderRepository {
    @Override
    public void save(String orderId) {
        System.out.println("[MongoDB] Saved order: " + orderId);
    }
    @Override
    public String findById(String orderId) {
        return "[MongoDB] Order: " + orderId;
    }
}

// In-Memory implementation — perfect for unit tests ✅
class InMemoryOrderRepository implements OrderRepository {
    private java.util.Map<String, String> store = new java.util.HashMap<>();

    @Override
    public void save(String orderId) {
        store.put(orderId, orderId);
        System.out.println("[InMemory] Saved order: " + orderId);
    }
    @Override
    public String findById(String orderId) {
        return "[InMemory] Order: " + store.getOrDefault(orderId, "NOT FOUND");
    }
}


// Email notification ✅
class EmailNotificationService implements NotificationService {
    @Override
    public void notify(String recipient, String message) {
        System.out.println("[Email] To " + recipient + ": " + message);
    }
}

// SMS notification ✅ — can be swapped without touching OrderService
class SMSNotificationService implements NotificationService {
    @Override
    public void notify(String recipient, String message) {
        System.out.println("[SMS] To " + recipient + ": " + message);
    }
}

// Mock notification — for testing (doesn't actually send) ✅
class MockNotificationService implements NotificationService {
    @Override
    public void notify(String recipient, String message) {
        System.out.println("[MOCK] Notification suppressed for " + recipient);
    }
}


// Stripe payment ✅
class StripePaymentGateway implements PaymentGateway {
    @Override
    public boolean processPayment(String orderId, double amount) {
        System.out.println("[Stripe] Processing ₹" + amount + " for order: " + orderId);
        return true; // assume success
    }
}

// Razorpay payment ✅ — swappable
class RazorpayPaymentGateway implements PaymentGateway {
    @Override
    public boolean processPayment(String orderId, double amount) {
        System.out.println("[Razorpay] Processing ₹" + amount + " for order: " + orderId);
        return true;
    }
}


// ── High-Level Module (depends ONLY on abstractions) ──

class OrderService {
    private final OrderRepository repository;         // interface, not concrete ✅
    private final NotificationService notifications;  // interface, not concrete ✅
    private final PaymentGateway paymentGateway;      // interface, not concrete ✅

    // Constructor Injection — dependencies provided from outside ✅
    public OrderService(
            OrderRepository repository,
            NotificationService notifications,
            PaymentGateway paymentGateway) {
        this.repository     = repository;
        this.notifications  = notifications;
        this.paymentGateway = paymentGateway;
    }

    public void placeOrder(String orderId, String customerContact, double amount) {
        System.out.println("\n--- Placing Order: " + orderId + " ---");

        // Step 1: Process payment
        boolean paymentSuccess = paymentGateway.processPayment(orderId, amount);

        if (!paymentSuccess) {
            System.out.println("Payment failed for order: " + orderId);
            return;
        }

        // Step 2: Save order
        repository.save(orderId);

        // Step 3: Notify customer
        notifications.notify(customerContact, "Your order " + orderId + " of ₹" + amount + " is confirmed!");

        System.out.println("Order " + orderId + " placed successfully!");
    }

    public void getOrder(String orderId) {
        System.out.println(repository.findById(orderId));
    }
}


// ─────────────────────────────────────────────
//  Main — Demo
// ─────────────────────────────────────────────

class Dependency_Inversion_Principle {

    public static void main(String[] args) {

        // ─── BAD DESIGN ───
        System.out.println("=== ❌ BAD DESIGN (Violates DIP) ===\n");
        BadOrderService badService = new BadOrderService();
        badService.placeOrder("ORD-001", "alice@example.com");


        // ─── GOOD DESIGN ───
        System.out.println("\n=== ✅ GOOD DESIGN (Follows DIP) ===\n");

        // Configuration 1: MySQL + Email + Stripe (Production setup)
        System.out.println("--- Production Config (MySQL + Email + Stripe) ---");
        OrderService productionService = new OrderService(
            new MySQLOrderRepository(),
            new EmailNotificationService(),
            new StripePaymentGateway()
        );
        productionService.placeOrder("ORD-101", "bob@example.com", 1500.00);
        productionService.getOrder("ORD-101");


        // Configuration 2: MongoDB + SMS + Razorpay (Alternative setup — zero code change in OrderService) ✅
        System.out.println("\n--- Alternative Config (MongoDB + SMS + Razorpay) ---");
        OrderService altService = new OrderService(
            new MongoOrderRepository(),
            new SMSNotificationService(),
            new RazorpayPaymentGateway()
        );
        altService.placeOrder("ORD-202", "+91-9876543210", 3200.00);


        // Configuration 3: InMemory + Mock (Unit Test setup — no external dependencies) ✅
        System.out.println("\n--- Test Config (InMemory + Mock Notification) ---");
        OrderService testService = new OrderService(
            new InMemoryOrderRepository(),
            new MockNotificationService(),
            new StripePaymentGateway()
        );
        testService.placeOrder("ORD-TEST-001", "test@example.com", 500.00);
        testService.getOrder("ORD-TEST-001");

        System.out.println("\n✅ OrderService code NEVER changed — only injected different implementations!");
    }
}

/*
    EXPECTED OUTPUT:
    =================
    === ❌ BAD DESIGN (Violates DIP) ===

    [MySQL] Saving order: ORD-001
    [Email] Sending to alice@example.com: Order ORD-001 placed!
    Order placed: ORD-001

    === ✅ GOOD DESIGN (Follows DIP) ===

    --- Production Config (MySQL + Email + Stripe) ---

    --- Placing Order: ORD-101 ---
    [Stripe] Processing ₹1500.0 for order: ORD-101
    [MySQL] Saved order: ORD-101
    [Email] To bob@example.com: Your order ORD-101 of ₹1500.0 is confirmed!
    Order ORD-101 placed successfully!
    [MySQL] Order: ORD-101

    --- Alternative Config (MongoDB + SMS + Razorpay) ---

    --- Placing Order: ORD-202 ---
    [Razorpay] Processing ₹3200.0 for order: ORD-202
    [MongoDB] Saved order: ORD-202
    [SMS] To +91-9876543210: Your order ORD-202 of ₹3200.0 is confirmed!
    Order ORD-202 placed successfully!

    --- Test Config (InMemory + Mock Notification) ---

    --- Placing Order: ORD-TEST-001 ---
    [Stripe] Processing ₹500.0 for order: ORD-TEST-001
    [InMemory] Saved order: ORD-TEST-001
    [MOCK] Notification suppressed for test@example.com
    Order ORD-TEST-001 placed successfully!
    [InMemory] Order: ORD-TEST-001

    ✅ OrderService code NEVER changed — only injected different implementations!
*/
