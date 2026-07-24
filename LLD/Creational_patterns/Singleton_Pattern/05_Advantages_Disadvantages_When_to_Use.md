# 05 – Advantages, Disadvantages & When to Use Singleton

---

## 1. Advantages ✅

### 1.1 Guarantees a Single Instance

The most obvious advantage — the pattern mathematically guarantees that only one object of the class ever exists in the JVM:

```java
ConfigManager c1 = ConfigManager.getInstance();
ConfigManager c2 = ConfigManager.getInstance();
System.out.println(c1 == c2);  // true — same object, always
```

No matter how many threads, services, or modules call `getInstance()`, they all share the same state.

---

### 1.2 Controlled Access to Shared Resource

All access passes through a single, well-defined entry point:

```
Thread-1 ──► getInstance() ──► shared Logger
Thread-2 ──► getInstance() ──┘ (same object)
Thread-3 ──► getInstance() ──┘
```

This makes it easy to add auditing, logging, or rate-limiting at the single access point without modifying callers.

---

### 1.3 Lazy Initialisation (Saves Resources)

With the Bill Pugh or DCL variant, the expensive object is created **only when first needed**:

```java
// App starts
// ConfigManager is NOT created yet

// First request arrives:
ConfigManager.getInstance();   // ← created here, reads disk once

// All subsequent calls return immediately — no I/O
```

---

### 1.4 Global State Without Global Variables

Provides a structured alternative to `public static` global variables. The state lives inside a properly-encapsulated object rather than a raw static field.

---

### 1.5 Reduces Object Creation Overhead

Creating complex objects (DB connection pools, thread pools, XML parsers) is expensive. Singleton creates them once and amortises the cost across the lifetime of the application.

---

## 2. Disadvantages ❌

### 2.1 Violates the Single Responsibility Principle (SRP)

A Singleton class takes on two responsibilities:
1. Its own business logic
2. Managing its own instantiation lifecycle

```java
public class ConfigManager {
    // Business logic
    public String get(String key) { ... }

    // Lifecycle management (should be separate concern)
    public static ConfigManager getInstance() { ... }
}
```

---

### 2.2 Introduces Global State (Hidden Coupling)

Any class can reach the Singleton from anywhere — creating invisible dependencies that don't appear in constructors or method signatures:

```java
// OrderService looks independent but secretly couples to ConfigManager
public class OrderService {
    public void placeOrder() {
        ConfigManager.getInstance().get("db.url");  // hidden dependency ⚠️
    }
}
```

This makes the codebase harder to understand and reason about.

---

### 2.3 Difficult to Unit Test

Because the Singleton controls its own creation, you cannot easily inject a mock or stub:

```java
// ❌ Cannot pass a mock ConfigManager to OrderService
OrderService service = new OrderService();
// service always uses the real ConfigManager.getInstance()
```

**Workaround:** Dependency Injection (DI) — inject the instance via constructor, not via `getInstance()` inside the class.

```java
// ✅ Testable
public class OrderService {
    private final ConfigManager config;
    public OrderService(ConfigManager config) { this.config = config; }
    // Now you can inject a mock in tests
}
```

---

### 2.4 Concurrency Bugs in Naïve Implementations

The basic lazy Singleton is NOT thread-safe. Developers often copy the pattern without adding `volatile` or proper synchronisation, leading to subtle multi-threading bugs that are hard to reproduce.

---

### 2.5 Breaks Object-Oriented Principles

- **Cannot be subclassed easily** — private constructor blocks inheritance.
- **Cannot be passed as an interface** unless explicitly programmed to implement one.
- **Hard to swap implementations** — e.g., switching from file-based config to DB-based config requires changing all callers.

---

### 2.6 Singleton Anti-Pattern in Distributed Systems

In a microservice or distributed environment:
- Different JVM instances each have their **own Singleton** — they are NOT the same object.
- Singleton provides no guarantee of uniqueness across processes or machines.
- Use a **distributed cache** (Redis, Hazelcast) or **shared DB** instead.

---

## 3. When to Use ✅

| Situation | Reason |
|-----------|--------|
| Resource that must be shared (DB pool, thread pool) | Prevents duplicate resource allocation |
| Expensive one-time initialisation (config, parsers) | Amortises the cost across the app lifetime |
| Coordinating a shared resource (logger, print spooler) | Prevents race conditions through a single point |
| Central registry / cache / counter | One authoritative state |
| When Spring is NOT used (manual DI) | Spring manages Singletons through IoC; raw Singleton is a fallback |

---

## 4. When NOT to Use ❌

| Situation | Better Alternative |
|-----------|--------------------|
| Multiple independent configurations needed | Regular class + DI |
| Unit testing is important | DI framework (Spring, Guice) |
| Distributed / multi-JVM environment | Distributed cache (Redis) |
| Object is cheap to create | Just use `new` |
| Object has no shared state | Static utility class or stateless service |
| You need to swap implementations | DI + interface |

---

## 5. Singleton in Modern Frameworks

> In most modern Java applications, you should **NOT implement Singleton yourself**.  
> Use a DI framework — Spring, Quarkus, Micronaut — which manages Singletons for you via IoC containers.

```java
// Spring — default bean scope IS singleton
@Service
public class ConfigService {
    // Spring guarantees one instance per ApplicationContext
    // No getInstance(), no private constructor, fully testable
}

// Inject it
@Autowired
private ConfigService configService;
```

Spring's approach gives you Singleton behaviour without any of the testability drawbacks.

---

## 6. Summary Card

```
┌────────────────────────────────────────────────────────────┐
│                SINGLETON — CHEAT SHEET                      │
├──────────────────────┬─────────────────────────────────────┤
│ GoF Category         │ Creational                           │
│ Problem Solved       │ Uncontrolled multiple instances      │
│ Mechanism            │ private ctor + static getInstance()  │
│ Best Java Impl.      │ Enum or Bill Pugh                    │
│ Biggest Risk         │ Global state, untestable code        │
│ Modern Alternative   │ DI framework (Spring @Service)       │
│ Distributed?         │ ❌ Not applicable — use Redis etc.   │
└──────────────────────┴─────────────────────────────────────┘
```

---

**Next →** [`06_Interview_Questions.md`](./06_Interview_Questions.md)
