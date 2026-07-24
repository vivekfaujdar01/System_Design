# 03 – Implementing Singleton in Java (All Variants)

> **Source code lives in:** `src/` subdirectories — one per variant.

---

## Variant 1 — Eager Initialisation

> **Location:** `src/basic/EagerSingleton.java`

The instance is created **when the class is loaded** by the JVM, before any thread ever calls `getInstance()`.

```java
// src/basic/EagerSingleton.java
public class EagerSingleton {

    // Instance created at class-load time — JVM guarantees thread safety here
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {
        System.out.println("EagerSingleton: constructor called");
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public void showMessage() {
        System.out.println("Hello from EagerSingleton @ " + System.identityHashCode(this));
    }
}
```

**Pros:**
- ✅ Simple
- ✅ Thread-safe (class-loading is atomic in JVM)

**Cons:**
- ❌ Instance created even if never used → wasteful if creation is expensive

---

## Variant 2 — Basic Lazy (Naïve, ❌ Not Thread-Safe)

> **Location:** `src/basic/LazySingleton.java`

Instance is created **only when first requested** — but this is NOT thread-safe.

```java
// src/basic/LazySingleton.java
public class LazySingleton {

    private static LazySingleton instance;   // null until first call

    private LazySingleton() {
        System.out.println("LazySingleton: constructor called");
    }

    /**
     * ❌ NOT thread-safe.
     * If two threads check `instance == null` simultaneously,
     * both pass the check and each creates a new object.
     */
    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from LazySingleton @ " + System.identityHashCode(this));
    }
}
```

**Use only in:** Single-threaded environments.

---

## Variant 3 — Synchronized Method

> **Location:** `src/thread_safe/SynchronizedSingleton.java`

Adds `synchronized` to `getInstance()` — ensures only one thread executes it at a time.

```java
// src/thread_safe/SynchronizedSingleton.java
public class SynchronizedSingleton {

    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
        System.out.println("SynchronizedSingleton: constructor called");
    }

    /**
     * ✅ Thread-safe.
     * ⚠️ Performance bottleneck — the lock is acquired on EVERY call,
     *    even after the instance is already created.
     */
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from SynchronizedSingleton @ " + System.identityHashCode(this));
    }
}
```

**Pros:** ✅ Simple, thread-safe  
**Cons:** ❌ Locking on every `getInstance()` call → poor performance under high contention

---

## Variant 4 — Double-Checked Locking (DCL)

> **Location:** `src/double_checked/DCLSingleton.java`

Locks **only during creation**. Once the instance exists, subsequent calls skip the lock entirely.

```java
// src/double_checked/DCLSingleton.java
public class DCLSingleton {

    /**
     * `volatile` is CRITICAL here.
     * Without it, the JVM may reorder instructions so that `instance`
     * is assigned BEFORE the constructor finishes (due to CPU/compiler
     * optimisations). Another thread could then receive a half-constructed object.
     */
    private static volatile DCLSingleton instance;

    private DCLSingleton() {
        System.out.println("DCLSingleton: constructor called");
    }

    public static DCLSingleton getInstance() {
        if (instance == null) {                          // 1st check — no lock
            synchronized (DCLSingleton.class) {
                if (instance == null) {                  // 2nd check — inside lock
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from DCLSingleton @ " + System.identityHashCode(this));
    }
}
```

**Why two checks?**

```
Thread-1 passes check-1 (instance == null) ────────────────────┐
Thread-2 passes check-1 (instance == null) ──┐                 │
                                              ▼                 ▼
                                         Only ONE enters synchronized block
                                         at a time (check-2 prevents double-create)
```

**Pros:** ✅ Thread-safe, ✅ Fast after initialisation  
**Cons:** ⚠️ `volatile` is mandatory (Java 5+), slightly more complex to read

---

## Variant 5 — Bill Pugh (Initialization-on-Demand Holder)

> **Location:** `src/bill_pugh/BillPughSingleton.java`

The **cleanest** lazy + thread-safe pattern. Uses a **private static inner class** as a holder.

```java
// src/bill_pugh/BillPughSingleton.java
public class BillPughSingleton {

    private BillPughSingleton() {
        System.out.println("BillPughSingleton: constructor called");
    }

    /**
     * The JVM loads SingletonHolder ONLY when getInstance() is first called.
     * Class loading is atomic — no synchronization needed.
     * The `final` field is written exactly once during class initialization.
     */
    private static class SingletonHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void showMessage() {
        System.out.println("Hello from BillPughSingleton @ " + System.identityHashCode(this));
    }
}
```

**How it works:**
```
First call to getInstance()
        │
        ▼
JVM loads BillPughSingleton.SingletonHolder
        │
        ▼
Static field INSTANCE = new BillPughSingleton()   ← happens atomically
        │
        ▼
INSTANCE returned — subsequent calls reuse it without any locking
```

**Pros:** ✅ Lazy, ✅ Thread-safe, ✅ No `volatile`, ✅ No `synchronized` overhead  
**Cons:** ❌ Still vulnerable to Reflection attack

---

## Variant 6 — Enum Singleton (Most Robust)

> **Location:** `src/enum_singleton/DatabaseConnection.java`

Joshua Bloch (*Effective Java*, Item 3) recommends this as the **best Singleton implementation** in Java.

```java
// src/enum_singleton/DatabaseConnection.java
public enum DatabaseConnection {

    INSTANCE;   // The single instance — JVM guarantees only one exists

    private String url;
    private String user;

    // Enum constructors are implicitly private
    DatabaseConnection() {
        this.url  = "jdbc:mysql://localhost:3306/mydb";
        this.user = "admin";
        System.out.println("DatabaseConnection: initialized");
    }

    public String getUrl()  { return url; }
    public String getUser() { return user; }

    public void query(String sql) {
        System.out.println("Executing [" + sql + "] on " + url);
    }

    public void showInfo() {
        System.out.println("DB @ " + System.identityHashCode(this) + " | url=" + url);
    }
}
```

**Usage:**
```java
DatabaseConnection db = DatabaseConnection.INSTANCE;
db.query("SELECT * FROM orders");
```

**Why Enum is Superior:**

| Threat | Enum Handles It? |
|--------|-----------------|
| **Multi-threading** | ✅ JVM guarantees one INSTANCE per enum constant |
| **Serialization** | ✅ `readResolve()` auto-handled — deserialization returns same instance |
| **Reflection** | ✅ `Constructor.newInstance()` throws `IllegalArgumentException` for enums |
| **Cloning** | ✅ Enums cannot be cloned |

---

## Main Runner — All Variants Compared

```java
// src/MainRunner.java
public class MainRunner {
    public static void main(String[] args) {

        System.out.println("===== Eager =====");
        EagerSingleton e1 = EagerSingleton.getInstance();
        EagerSingleton e2 = EagerSingleton.getInstance();
        System.out.println("Same instance? " + (e1 == e2));   // true

        System.out.println("\n===== Lazy (single-threaded) =====");
        LazySingleton l1 = LazySingleton.getInstance();
        LazySingleton l2 = LazySingleton.getInstance();
        System.out.println("Same instance? " + (l1 == l2));   // true

        System.out.println("\n===== Synchronized =====");
        SynchronizedSingleton s1 = SynchronizedSingleton.getInstance();
        SynchronizedSingleton s2 = SynchronizedSingleton.getInstance();
        System.out.println("Same instance? " + (s1 == s2));   // true

        System.out.println("\n===== Double-Checked Locking =====");
        DCLSingleton d1 = DCLSingleton.getInstance();
        DCLSingleton d2 = DCLSingleton.getInstance();
        System.out.println("Same instance? " + (d1 == d2));   // true

        System.out.println("\n===== Bill Pugh =====");
        BillPughSingleton b1 = BillPughSingleton.getInstance();
        BillPughSingleton b2 = BillPughSingleton.getInstance();
        System.out.println("Same instance? " + (b1 == b2));   // true

        System.out.println("\n===== Enum =====");
        DatabaseConnection db1 = DatabaseConnection.INSTANCE;
        DatabaseConnection db2 = DatabaseConnection.INSTANCE;
        System.out.println("Same instance? " + (db1 == db2)); // true
        db1.showInfo();
    }
}
```

---

## Quick Variant Comparison Table

| Variant | Lazy? | Thread-Safe? | Reflection-Safe? | Serialization-Safe? | Notes |
|---------|-------|-------------|-----------------|--------------------|-|
| Eager | ❌ | ✅ | ❌ | ❌ | Simplest |
| Lazy (naïve) | ✅ | ❌ | ❌ | ❌ | Single-thread only |
| Synchronized | ✅ | ✅ | ❌ | ❌ | Slow |
| DCL + volatile | ✅ | ✅ | ❌ | ❌ | Best non-enum option |
| Bill Pugh | ✅ | ✅ | ❌ | ❌ | Cleanest code |
| **Enum** | ❌ | ✅ | ✅ | ✅ | **Recommended** |

---

**Next →** [`04_Real_World_Example.md`](./04_Real_World_Example.md)
