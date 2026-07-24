# 01 – Why Do We Need Singleton? (The Problem)

> **Study order:** Start here. Understand the real pain-points before looking at the pattern itself.

---

## 1. The Core Problem: Uncontrolled Multiple Instances

Some objects in a system should exist **exactly once**. When multiple instances of such an object are allowed, the following problems arise:

| Problem | Real Example |
|---------|-------------|
| **Inconsistent state** | Two `ConfigManager` instances each read config at different times → different values in the same run |
| **Resource waste** | Five threads each open their own `DBConnectionPool` → 5× the connections, 5× the memory |
| **Race conditions** | Two `Logger` instances write to the same file simultaneously → garbled log output |
| **Split responsibility** | Two `ThreadPool` instances each schedule tasks independently → impossible to reason about load |

---

## 2. A Concrete Scenario — Configuration Manager

Imagine an enterprise application that reads `application.properties` from disk at startup:

```java
// ❌ Without Singleton — every class creates its own instance
public class PaymentService {
    ConfigManager config = new ConfigManager();   // reads file from disk
    // ...
}

public class OrderService {
    ConfigManager config = new ConfigManager();   // reads file from disk AGAIN
    // ...
}

public class UserService {
    ConfigManager config = new ConfigManager();   // reads file from disk YET AGAIN
    // ...
}
```

**Problems:**
- The config file is read **3 times** — wasteful I/O.
- If `PaymentService` updates a runtime config value, `OrderService` and `UserService` **never see it** because they hold different instances.
- Memory holds **3 identical copies** of the same data.

---

## 3. Another Scenario — Logger

```java
// ❌ Multi-instance Logger causes file corruption
Logger log1 = new Logger("app.log");
Logger log2 = new Logger("app.log");

// Both write to the same file handle independently
log1.log("Payment processed");
log2.log("Order created");
// → File output is interleaved and potentially corrupt
```

A Logger should write through a **single, coordinated** point of access.

---

## 4. The Thread-Safety Dimension

With multiple threads, the problem gets worse:

```
Thread-1 ──► new ConfigManager()  ─┐
Thread-2 ──► new ConfigManager()  ─┤─ both pass the "is it null?" check
Thread-3 ──► new ConfigManager()  ─┘   before any one assigns the field

Result: 3 separate instances created despite trying to guard against it
```

---

## 5. What We Actually Want

> **"Guarantee that a class has exactly one instance, and provide a global point of access to it."**
> — GoF (Gang of Four)

```
Any caller anywhere in the codebase
        │
        ▼
ConfigManager.getInstance()
        │
        └── always returns the SAME object in memory
```

This is precisely what the **Singleton Pattern** delivers.

---

## 6. Real-World Objects That Should Be Singletons

| Object | Why Only One? |
|--------|--------------|
| **DB Connection Pool** | Fixed pool of connections; multiple pools would exhaust DB resources |
| **Logger** | Single write-point prevents interleaved / corrupt output |
| **Configuration Manager** | One source of truth for runtime settings |
| **Thread Pool / Executor** | Central task scheduler; multiple pools cause uncontrolled parallelism |
| **Cache Manager** | Shared in-memory cache; multiple instances invalidate each other |
| **File System** | OS-level resource; only one representation needed |
| **Print Spooler** | Serialises print jobs; multiple spoolers conflict |

---

## 7. Key Takeaway

> Without Singleton, objects that should be unique can be instantiated multiple times — causing **wasted resources**, **inconsistent state**, and **race conditions**.  
> Singleton enforces "create once, reuse always", making the single instance the global, authoritative source of truth.

---

**Next →** [`02_What_is_Singleton_Pattern.md`](./02_What_is_Singleton_Pattern.md)
