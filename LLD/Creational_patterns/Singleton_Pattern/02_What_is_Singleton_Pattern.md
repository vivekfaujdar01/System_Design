# 02 – What is the Singleton Pattern? (Concept & Structure)

> **GoF Category:** Creational  
> **Intent:** Ensure a class has **only one instance** and provide a **global point of access** to it.

---

## 1. One-Line Definition

> **Singleton** = a class that controls its own instantiation, guaranteeing only one object ever exists.

---

## 2. The Two Guarantees

| Guarantee | How It Is Enforced |
|-----------|-------------------|
| **Only one instance** | Constructor is `private` — no outsider can call `new` |
| **Global point of access** | A `public static getInstance()` method returns the single instance |

---

## 3. Core Skeleton

```java
public class Singleton {

    // 1. Hold the one-and-only instance
    private static Singleton instance;

    // 2. Private constructor — blocks external instantiation
    private Singleton() { }

    // 3. Public factory method — the only way to get the instance
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();   // created only on first call
        }
        return instance;
    }
}
```

---

## 4. UML Class Diagram

```
┌──────────────────────────────────────────────┐
│                  Singleton                    │
├──────────────────────────────────────────────┤
│  - instance : Singleton          «static»     │
├──────────────────────────────────────────────┤
│  - Singleton()                   «private»    │  ← blocks new
│  + getInstance() : Singleton     «static»     │  ← only entry point
│  + someBusinessMethod()                       │
└──────────────────────────────────────────────┘
```

---

## 5. Five Implementation Variants (Overview)

Java offers several ways to implement Singleton, each with different trade-offs for thread safety, laziness, and simplicity. See `03_Implementing_Singleton_Java.md` for full code.

| Variant | Lazy? | Thread-Safe? | Recommended? |
|---------|-------|-------------|-------------|
| **1. Eager Initialisation** | ❌ No | ✅ Yes (JVM guarantee) | ✅ When instance is always needed |
| **2. Basic Lazy (naïve)** | ✅ Yes | ❌ No | ❌ Single-threaded only |
| **3. Synchronized Method** | ✅ Yes | ✅ Yes | ⚠️ Simple but slow under contention |
| **4. Double-Checked Locking** | ✅ Yes | ✅ Yes (`volatile`) | ✅ Most common in production |
| **5. Bill Pugh (Inner Class)** | ✅ Yes | ✅ Yes (JVM guarantee) | ✅ Cleanest lazy + thread-safe |
| **6. Enum Singleton** | ❌ No | ✅ Yes | ✅ Safest — handles serialization & reflection |

---

## 6. Key Mechanisms Explained

### 6.1 Private Constructor

```java
private Singleton() {
    // No outside code can call `new Singleton()`
}
```

### 6.2 Static Field

```java
private static Singleton instance;   // lives at class level, not object level
```

Because it's `static`, there is exactly one `instance` field shared by the entire JVM, regardless of how many threads access it.

### 6.3 Static Factory Method

```java
public static Singleton getInstance() {
    // Only create on first call (lazy) OR return the pre-created instance (eager)
    return instance;
}
```

### 6.4 `volatile` Keyword (for Double-Checked Locking)

```java
private static volatile Singleton instance;
```

`volatile` prevents the JVM from reordering the write to `instance` before the constructor finishes — a subtle multi-threading bug without it.

---

## 7. Singleton vs. Static Class

A common question: *"Why not just use a class with all static methods?"*

| Aspect | Singleton | Static Class |
|--------|-----------|-------------|
| **Can implement interface** | ✅ Yes | ❌ No |
| **Can extend a class** | ✅ Yes | ❌ No |
| **Lazy initialisation** | ✅ Yes | ❌ No |
| **Testable / mockable** | ✅ With care | ❌ Hard |
| **Polymorphism** | ✅ Yes | ❌ No |
| **State (instance fields)** | ✅ Yes | ❌ Only static fields |

---

## 8. Threats to Singleton Integrity

Java has three mechanisms that can break a Singleton:

| Threat | Problem | Solution |
|--------|---------|---------|
| **Reflection** | Caller forcibly invokes `private` constructor | Guard in constructor: `if (instance != null) throw new RuntimeException()` |
| **Serialization** | Deserializing creates a new object | Override `readResolve()` to return `instance` |
| **Cloning** | `clone()` creates a second object | Override `clone()` to throw `CloneNotSupportedException` |
| **Multiple ClassLoaders** | Each ClassLoader has its own class → its own static field | Specify a canonical ClassLoader |

> 💡 **Enum Singleton automatically handles reflection and serialization** — one reason it is considered the best implementation.

---

## 9. Pattern Summary Card

| Aspect | Detail |
|--------|--------|
| **Intent** | One instance, global access |
| **GoF Category** | Creational |
| **Key mechanisms** | `private` constructor + `static` field + `static` factory |
| **Thread-safety tool** | `synchronized` / `volatile` / inner static class / enum |
| **Best implementation** | Bill Pugh or Enum |
| **Main criticism** | Global state, hard to unit-test |

---

**Next →** [`03_Implementing_Singleton_Java.md`](./03_Implementing_Singleton_Java.md)
