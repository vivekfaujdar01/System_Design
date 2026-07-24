# 04 – Real-World Example: Application Configuration Manager

> **Scenario:** A Spring-style enterprise application has a `ConfigManager` that loads `application.properties` once from disk. Multiple services (PaymentService, NotificationService, OrderService) need to read and occasionally update config values at runtime — all from **the same source of truth**.

---

## 1. Why This Needs Singleton

| Requirement | Why Singleton Solves It |
|-------------|------------------------|
| Properties file read once | Expensive I/O done only at first `getInstance()` |
| All services see the same config | They share the same object in memory |
| Runtime config updates are visible everywhere | One object's state = everyone's view |
| Thread-safe reads/writes | `synchronized` / `volatile` or Bill Pugh variant |

---

## 2. Class Design

```
┌───────────────────────────────────────────────────┐
│               ConfigManager                        │
│               (Bill Pugh Singleton)                │
├───────────────────────────────────────────────────┤
│ - properties : Map<String, String>  «private»      │
│ - configFile : String               «private»      │
├───────────────────────────────────────────────────┤
│ - ConfigManager()          «private»               │
│ + getInstance() : ConfigManager  «static»          │
│ + get(key) : String                                │
│ + set(key, value)                                  │
│ + reload()                                         │
└───────────────────────────────────────────────────┘
         ▲            ▲              ▲
         │            │              │
  PaymentService  OrderService  NotificationService
```

---

## 3. Full Java Implementation

### 3.1 ConfigManager.java (Singleton)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * Application Configuration Manager — Bill Pugh Singleton.
 *
 * Loads application properties once and provides a single,
 * globally-accessible source of truth for runtime configuration.
 */
public class ConfigManager {

    private final Map<String, String> properties = new HashMap<>();

    /** Private constructor — simulates reading a properties file */
    private ConfigManager() {
        System.out.println("[ConfigManager] Loading configuration from disk...");
        // In production: read from application.properties / environment variables
        properties.put("db.url",             "jdbc:mysql://prod-db:3306/app");
        properties.put("db.pool.size",       "10");
        properties.put("payment.gateway.url","https://payments.example.com/api");
        properties.put("notification.email", "noreply@example.com");
        properties.put("feature.dark-mode",  "true");
        System.out.println("[ConfigManager] " + properties.size() + " properties loaded.");
    }

    /** Bill Pugh Holder — loaded only on first getInstance() call */
    private static class ConfigHolder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    /** Global access point */
    public static ConfigManager getInstance() {
        return ConfigHolder.INSTANCE;
    }

    /** Get a config value by key */
    public String get(String key) {
        return properties.getOrDefault(key, "<not-set>");
    }

    /** Update a config value at runtime */
    public synchronized void set(String key, String value) {
        System.out.println("[ConfigManager] Updating '" + key + "' → '" + value + "'");
        properties.put(key, value);
    }

    /** Reload all properties (simulated) */
    public synchronized void reload() {
        System.out.println("[ConfigManager] Reloading configuration...");
        properties.put("db.pool.size", "20");    // simulate changed value
        System.out.println("[ConfigManager] Reload complete.");
    }

    public void printAll() {
        System.out.println("[ConfigManager] Current configuration:");
        properties.forEach((k, v) -> System.out.println("  " + k + " = " + v));
    }
}
```

### 3.2 PaymentService.java

```java
/**
 * Uses ConfigManager to read payment gateway URL.
 * Never creates its own ConfigManager — always calls getInstance().
 */
public class PaymentService {

    private final ConfigManager config = ConfigManager.getInstance();

    public void processPayment(String orderId, double amount) {
        String gateway = config.get("payment.gateway.url");
        System.out.println("[PaymentService] Processing payment for order=" + orderId
                + " amount=$" + amount + " via " + gateway);
    }
}
```

### 3.3 NotificationService.java

```java
/**
 * Uses ConfigManager to read sender email.
 * Shares the SAME ConfigManager instance as PaymentService.
 */
public class NotificationService {

    private final ConfigManager config = ConfigManager.getInstance();

    public void sendConfirmation(String orderId, String userEmail) {
        String sender = config.get("notification.email");
        System.out.println("[NotificationService] Sending confirmation for order=" + orderId
                + " from=" + sender + " to=" + userEmail);
    }
}
```

### 3.4 OrderService.java

```java
/**
 * Uses ConfigManager to check feature flags.
 */
public class OrderService {

    private final ConfigManager config = ConfigManager.getInstance();

    public void placeOrder(String userId) {
        boolean darkMode = Boolean.parseBoolean(config.get("feature.dark-mode"));
        int poolSize     = Integer.parseInt(config.get("db.pool.size"));
        System.out.println("[OrderService] Order placed for user=" + userId
                + " | darkMode=" + darkMode + " | dbPoolSize=" + poolSize);
    }
}
```

### 3.5 Main.java — Wiring It All Together

```java
public class Main {
    public static void main(String[] args) {

        // ── All three services share ONE ConfigManager ─────────────────────────
        PaymentService      payment      = new PaymentService();
        NotificationService notification = new NotificationService();
        OrderService        order        = new OrderService();

        System.out.println("\n=== Initial State ===");
        ConfigManager.getInstance().printAll();

        System.out.println("\n=== Processing Requests ===");
        order.placeOrder("user-42");
        payment.processPayment("ORD-1001", 299.99);
        notification.sendConfirmation("ORD-1001", "alice@example.com");

        System.out.println("\n=== Runtime Config Update ===");
        // Admin increases DB pool size — ALL services immediately see the new value
        ConfigManager.getInstance().set("db.pool.size", "25");
        order.placeOrder("user-43");   // will show poolSize=25 ✅

        System.out.println("\n=== Verify Single Instance ===");
        ConfigManager c1 = ConfigManager.getInstance();
        ConfigManager c2 = ConfigManager.getInstance();
        System.out.println("c1 == c2 ? " + (c1 == c2));     // true ✅
        System.out.println("hashCode c1: " + System.identityHashCode(c1));
        System.out.println("hashCode c2: " + System.identityHashCode(c2));
    }
}
```

**Expected Output:**
```
[ConfigManager] Loading configuration from disk...
[ConfigManager] 5 properties loaded.

=== Initial State ===
[ConfigManager] Current configuration:
  db.url = jdbc:mysql://prod-db:3306/app
  db.pool.size = 10
  payment.gateway.url = https://payments.example.com/api
  notification.email = noreply@example.com
  feature.dark-mode = true

=== Processing Requests ===
[OrderService]  Order placed for user=user-42 | darkMode=true | dbPoolSize=10
[PaymentService] Processing payment for order=ORD-1001 amount=$299.99 via https://payments.example.com/api
[NotificationService] Sending confirmation for order=ORD-1001 from=noreply@example.com to=alice@example.com

=== Runtime Config Update ===
[ConfigManager] Updating 'db.pool.size' → '25'
[OrderService]  Order placed for user=user-43 | darkMode=true | dbPoolSize=25

=== Verify Single Instance ===
c1 == c2 ? true
hashCode c1: 1829164700
hashCode c2: 1829164700
```

---

## 4. Sequence Diagram

```
  Main          PaymentService    OrderService     ConfigManager
    │                │                 │                 │
    │── new ────────►│                 │                 │
    │                │── getInstance() ────────────────►│
    │                │                 │   (1st call: loads config from disk)
    │                │◄─────────────── INSTANCE ────────│
    │── new ──────────────────────────►│                 │
    │                │                 │── getInstance()►│
    │                │                 │◄─ INSTANCE ─────│  (same object, no I/O)
    │── placeOrder ───────────────────►│                 │
    │                │                 │── get("db.pool.size") ──────────────►│
    │                │                 │◄─ "10" ─────────────────────────────│
    │── processPayment ──────────────►│                 │
    │                │── get("payment.gateway.url") ───►│
    │                │◄─ "https://..." ─────────────────│
```

---

## 5. Other Real-World Singleton Usages

| Framework / JDK | Singleton Usage |
|-----------------|----------------|
| **`java.lang.Runtime`** | `Runtime.getRuntime()` — one JVM runtime |
| **Spring IoC** | Default bean scope is singleton per ApplicationContext |
| **`java.util.logging.Logger`** | `Logger.getLogger(name)` — shared per-name logger |
| **Hibernate `SessionFactory`** | One factory per database — expensive to create |
| **`java.awt.Desktop`** | `Desktop.getDesktop()` — one desktop instance |
| **Android `Application`** | `getApplication()` — single app instance |

---

**Next →** [`05_Advantages_Disadvantages_When_to_Use.md`](./05_Advantages_Disadvantages_When_to_Use.md)
