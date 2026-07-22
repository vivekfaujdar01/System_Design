# Module 08 – Immutable Objects Using Builder

> **Study order:** Read after `07_Director_Class.md`.  
> This module covers why Builder is the gold standard for creating immutable objects in Java.

---

## Table of Contents
1. [What Is an Immutable Object?](#1-what-is-an-immutable-object)
2. [Why Immutability Matters](#2-why-immutability-matters)
3. [Thread Safety Through Immutability](#3-thread-safety-through-immutability)
4. [Rules for Writing an Immutable Class](#4-rules-for-writing-an-immutable-class)
5. [Builder + Immutability — The Perfect Pair](#5-builder--immutability--the-perfect-pair)
6. [Complete Example – Immutable Configuration Object](#6-complete-example--immutable-configuration-object)
7. [Defensive Copies for Collections and Arrays](#7-defensive-copies-for-collections-and-arrays)
8. [Common Immutability Pitfalls](#8-common-immutability-pitfalls)
9. [Immutability in Java Standard Library](#9-immutability-in-java-standard-library)
10. [Advantages Summary](#10-advantages-summary)

---

## 1. What Is an Immutable Object?

An **immutable object** is an object whose **state cannot be changed after it is created**.

```java
// Mutable (state can change at any time)
public class MutablePoint {
    public int x;
    public int y;
}
MutablePoint p = new MutablePoint();
p.x = 10;   // allowed — state changed
p.x = 99;   // allowed again — changed again

// Immutable (state is fixed at creation)
public final class ImmutablePoint {
    private final int x;
    private final int y;
    public ImmutablePoint(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    // No setters!
}
ImmutablePoint p = new ImmutablePoint(10, 20);
// p.x = 99; ← Compilation error! x is private + final
```

Famous immutable classes in Java: `String`, `Integer`, `LocalDate`, `BigDecimal`, `UUID`

---

## 2. Why Immutability Matters

### A – Simpler Reasoning

```java
// Mutable: you must track every place that could change the object
MutableConfig config = new MutableConfig();
config.setHost("localhost");
someService.configure(config);   // did someService change config?!
anotherService.use(config);      // is config still "localhost"? Not sure!

// Immutable: once created, forever the same
ImmutableConfig config = new ImmutableConfig.Builder()
    .host("localhost")
    .build();
someService.configure(config);   // config is unchanged — guaranteed
anotherService.use(config);      // config is still "localhost" — always
```

### B – Safe Sharing

```java
// Mutable object — UNSAFE to share between threads
// Thread A might read while Thread B is writing → race condition!
MutableConfig shared = new MutableConfig("localhost");

// Immutable object — SAFE to share between any number of threads
// No thread can ever change it, so no race conditions possible
ImmutableConfig shared = new ImmutableConfig.Builder().host("localhost").build();
threadPool.submit(() -> shared.getHost());   // ✅ safe
threadPool.submit(() -> shared.getHost());   // ✅ safe
```

### C – Cache-Friendly

```java
// Immutable objects can be safely cached and reused
// (A mutable object cached could be mutated by one user, affecting all users!)
private static final ImmutableConfig DEFAULTS = new ImmutableConfig.Builder()
    .host("localhost")
    .port(8080)
    .build();

// Safe: DEFAULTS is never mutated
public ImmutableConfig getConfig(String env) {
    if (env.equals("dev")) return DEFAULTS;   // reuse safely
    return new ImmutableConfig.Builder().host("prod.example.com").build();
}
```

---

## 3. Thread Safety Through Immutability

### What Is a Race Condition?

```java
// Mutable Configuration — race condition example
public class MutableServerConfig {
    private String host;
    private int    port;

    public void setHost(String host) { this.host = host; }
    public void setPort(int port)    { this.port = port; }
    public String getConnectionString() { return host + ":" + port; }
}

MutableServerConfig config = new MutableServerConfig();

// Thread 1: Updates config for production
new Thread(() -> {
    config.setHost("prod.example.com");
    config.setPort(443);
}).start();

// Thread 2: Reads config simultaneously
new Thread(() -> {
    // Might read "prod.example.com" but port is STILL 0 (old value!)
    // → inconsistent state! "prod.example.com:0"
    System.out.println(config.getConnectionString());
}).start();
```

### Immutable Solution — No Race Condition Possible

```java
// Immutable Configuration — thread-safe by design
public final class ServerConfig {
    private final String host;
    private final int    port;

    private ServerConfig(Builder b) {
        this.host = b.host;
        this.port = b.port;
    }

    public String getHost() { return host; }
    public int    getPort() { return port; }
    public String getConnectionString() { return host + ":" + port; }

    public static class Builder {
        private String host;
        private int    port = 80;
        public Builder host(String h) { this.host = h; return this; }
        public Builder port(int p)    { this.port = p; return this; }
        public ServerConfig build()   { return new ServerConfig(this); }
    }
}

// Thread 1: Creates NEW config (doesn't modify existing)
ServerConfig prod = new ServerConfig.Builder()
    .host("prod.example.com").port(443).build();

// Thread 2: Reads original config — completely safe
// (No thread can ever change what prod.host or prod.port is)
System.out.println(prod.getConnectionString());   // always "prod.example.com:443"
```

> **Key insight:** Immutable objects are **inherently thread-safe** without any synchronization.  
> This is why `String`, `Integer`, `LocalDate` in Java are all immutable.

---

## 4. Rules for Writing an Immutable Class

```java
// RULE 1: Declare the class as 'final' — prevents subclasses from adding mutability
public final class ImmutableOrder { ... }

// RULE 2: All fields must be 'private' AND 'final'
private final String orderId;
private final double totalAmount;
private final List<String> items;   // ← be careful with collections!

// RULE 3: No setters whatsoever
// (If you need to "change" state, return a NEW object with the new value)
public ImmutableOrder withDiscount(double discount) {
    return new ImmutableOrder.Builder()
        .orderId(this.orderId)
        .totalAmount(this.totalAmount - discount)
        .items(this.items)
        .build();
}

// RULE 4: Initialize all fields via constructor
private ImmutableOrder(Builder builder) {
    this.orderId     = builder.orderId;
    this.totalAmount = builder.totalAmount;
    this.items       = List.copyOf(builder.items);  // ← defensive copy!
}

// RULE 5: Return defensive copies of mutable fields (arrays, collections)
public List<String> getItems() {
    return Collections.unmodifiableList(items);  // ← don't expose the internal list
}
```

---

## 5. Builder + Immutability — The Perfect Pair

```
WITHOUT Builder:                    WITH Builder:
  Immutable class needs many        Immutable class has ONE
  constructor overloads for         private constructor.
  different field combinations.     Builder handles all combinations.
  → Telescoping constructor        → Elegant, readable API
    problem!
```

```java
// The problem: how do you create an immutable object with 8 optional fields?

// ❌ Constructor approach — telescoping nightmare
public ImmutableOrder(String id, double amt) { ... }
public ImmutableOrder(String id, double amt, String coupon) { ... }
public ImmutableOrder(String id, double amt, String coupon, String address) { ... }
// ... 64+ constructors for all combinations

// ✅ Builder approach — one elegant solution
ImmutableOrder order = new ImmutableOrder.Builder("ORD-001", 2500.00)
    .couponCode("SAVE10")
    .shippingAddress("123 Main St")
    .giftWrapped(true)
    .build();
```

---

## 6. Complete Example – Immutable Configuration Object

```java
// AppConfig.java — Production-grade immutable config
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AppConfig {

    // All fields private and final
    private final String              appName;
    private final String              environment;
    private final String              dbHost;
    private final int                 dbPort;
    private final String              dbName;
    private final int                 connectionPoolSize;
    private final int                 requestTimeoutMs;
    private final boolean             cacheEnabled;
    private final Map<String, String> additionalProperties;   // mutable map — needs defensive copy

    // Private constructor
    private AppConfig(Builder builder) {
        this.appName              = builder.appName;
        this.environment          = builder.environment;
        this.dbHost               = builder.dbHost;
        this.dbPort               = builder.dbPort;
        this.dbName               = builder.dbName;
        this.connectionPoolSize   = builder.connectionPoolSize;
        this.requestTimeoutMs     = builder.requestTimeoutMs;
        this.cacheEnabled         = builder.cacheEnabled;
        // Defensive copy of mutable collection — prevents external mutation
        this.additionalProperties = Collections.unmodifiableMap(
            new HashMap<>(builder.additionalProperties)
        );
    }

    // Only getters
    public String  getAppName()            { return appName; }
    public String  getEnvironment()        { return environment; }
    public String  getDbHost()             { return dbHost; }
    public int     getDbPort()             { return dbPort; }
    public String  getDbName()             { return dbName; }
    public int     getConnectionPoolSize() { return connectionPoolSize; }
    public int     getRequestTimeoutMs()   { return requestTimeoutMs; }
    public boolean isCacheEnabled()        { return cacheEnabled; }

    // Returns unmodifiable view — caller cannot mutate internal map
    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

    public String getProperty(String key) {
        return additionalProperties.getOrDefault(key, null);
    }

    @Override
    public String toString() {
        return "AppConfig {" +
               "\n  app         = " + appName +
               "\n  env         = " + environment +
               "\n  db          = " + dbHost + ":" + dbPort + "/" + dbName +
               "\n  pool        = " + connectionPoolSize +
               "\n  timeout     = " + requestTimeoutMs + "ms" +
               "\n  cache       = " + cacheEnabled +
               "\n  extra props = " + additionalProperties +
               "\n}";
    }

    // ══════════════════════════════════════════════════════════════
    // STATIC NESTED BUILDER
    // ══════════════════════════════════════════════════════════════
    public static class Builder {
        // Mandatory
        private final String appName;
        private final String environment;

        // Optional with defaults
        private String              dbHost               = "localhost";
        private int                 dbPort               = 5432;
        private String              dbName               = "app_db";
        private int                 connectionPoolSize   = 10;
        private int                 requestTimeoutMs     = 5000;
        private boolean             cacheEnabled         = false;
        private Map<String, String> additionalProperties = new HashMap<>();

        public Builder(String appName, String environment) {
            if (appName == null || appName.isBlank())
                throw new IllegalArgumentException("App name is required.");
            if (environment == null || environment.isBlank())
                throw new IllegalArgumentException("Environment is required.");
            if (!environment.matches("dev|staging|production"))
                throw new IllegalArgumentException("Environment must be dev, staging, or production.");
            this.appName     = appName;
            this.environment = environment;
        }

        public Builder dbHost(String dbHost)                   { this.dbHost = dbHost; return this; }
        public Builder dbPort(int dbPort)                      { this.dbPort = dbPort; return this; }
        public Builder dbName(String dbName)                   { this.dbName = dbName; return this; }
        public Builder connectionPoolSize(int size)            { this.connectionPoolSize = size; return this; }
        public Builder requestTimeoutMs(int ms)                { this.requestTimeoutMs = ms; return this; }
        public Builder cacheEnabled(boolean enabled)           { this.cacheEnabled = enabled; return this; }
        public Builder property(String key, String value)      { this.additionalProperties.put(key, value); return this; }

        public AppConfig build() {
            if (connectionPoolSize < 1 || connectionPoolSize > 100)
                throw new IllegalStateException("Connection pool size must be 1–100.");
            if (requestTimeoutMs < 100)
                throw new IllegalStateException("Request timeout must be at least 100ms.");
            if (environment.equals("production") && dbHost.equals("localhost"))
                throw new IllegalStateException("Production environment cannot use localhost as DB host.");
            return new AppConfig(this);
        }
    }
}

// ── Client ─────────────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {

        // Dev config
        AppConfig devConfig = new AppConfig.Builder("MyApp", "dev")
            .cacheEnabled(false)
            .property("logLevel", "DEBUG")
            .property("featureFlag", "true")
            .build();
        System.out.println(devConfig);

        // Production config
        AppConfig prodConfig = new AppConfig.Builder("MyApp", "production")
            .dbHost("prod-db.internal.company.com")
            .dbPort(5432)
            .dbName("myapp_prod")
            .connectionPoolSize(50)
            .requestTimeoutMs(3000)
            .cacheEnabled(true)
            .property("logLevel", "WARN")
            .build();
        System.out.println(prodConfig);

        // ❌ Try to modify a property (returns unmodifiable map)
        try {
            prodConfig.getAdditionalProperties().put("hack", "value");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable config: " + e.getClass().getSimpleName());
        }

        // ❌ Production with localhost — validation fails
        try {
            AppConfig bad = new AppConfig.Builder("App", "production")
                .build();   // dbHost defaults to "localhost" — invalid for production!
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

## 7. Defensive Copies for Collections and Arrays

If your immutable class contains a `List`, `Map`, `Set`, or array, you must **defensively copy** them:

```java
// ❌ WRONG — leaks reference to internal list
private final List<String> tags;

private MyClass(Builder b) {
    this.tags = b.tags;   // b.tags is the SAME list as internal tags
    // b.tags.add("hack") would mutate this.tags! Immutability BROKEN!
}

public List<String> getTags() {
    return tags;   // external code can mutate this.tags!
}

// ✅ CORRECT — defensive copy in constructor
private MyClass(Builder b) {
    this.tags = List.copyOf(b.tags);   // new unmodifiable copy
    // b.tags.add("hack") won't affect this.tags ✅
}

public List<String> getTags() {
    return Collections.unmodifiableList(tags);   // safe view
    // OR: return new ArrayList<>(tags);         // safe copy
}
```

---

## 8. Common Immutability Pitfalls

| Pitfall                               | Effect                              | Fix                                       |
|---------------------------------------|-------------------------------------|-------------------------------------------|
| Mutable field `List` not defensively copied | External code mutates internal state | Use `List.copyOf()` in constructor    |
| Getter returns direct reference to mutable field | Caller mutates the field  | Return `Collections.unmodifiableList()`  |
| Class not declared `final`            | Subclass can add setters            | Always declare `final`                   |
| Field is `final` but object is mutable| `final` only prevents reassignment  | Make the field's type immutable too       |
| Null fields allowed in immutable class| Partial state possible              | Validate non-null in constructor          |

---

## 9. Immutability in Java Standard Library

| Class             | Immutable? | Notes                                      |
|-------------------|------------|--------------------------------------------|
| `String`          | ✅ Yes     | Classic example — shared safely everywhere |
| `Integer`         | ✅ Yes     | All wrapper types are immutable            |
| `LocalDate`       | ✅ Yes     | Java 8+ date API — fully immutable        |
| `BigDecimal`      | ✅ Yes     | Operations return new objects              |
| `List.of(...)`    | ✅ Yes     | Java 9+ immutable list factory             |
| `Map.of(...)`     | ✅ Yes     | Java 9+ immutable map factory              |
| `UUID`            | ✅ Yes     | Immutable identifier                       |
| `ArrayList`       | ❌ No      | Mutable collection                         |
| `StringBuilder`   | ❌ No      | Mutable by design (for string building)    |

---

## 10. Advantages Summary

| Advantage              | Why It Matters                                                |
|------------------------|---------------------------------------------------------------|
| **Thread safety**      | No synchronization needed — safe to share across threads      |
| **Simple reasoning**   | Object state never changes — easy to track and debug          |
| **Safe caching**       | Can cache and reuse without risk of stale state               |
| **Free sharing**       | Pass to any method without fear of mutation                   |
| **Fail-fast errors**   | Invalid state caught at creation time, not later at use time  |
| **Hashable**           | `hashCode()` is stable — safe to use as Map keys             |

---

**← Prev:** [`07_Director_Class.md`](./07_Director_Class.md)  
**Next →** [`09_Builder_vs_Factory.md`](./09_Builder_vs_Factory.md)
