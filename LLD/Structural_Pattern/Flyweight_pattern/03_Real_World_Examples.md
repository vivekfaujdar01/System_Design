# Module 3: Real-World Framework Examples & Pattern Comparisons

The Flyweight Pattern is widely used in language runtimes, GUI toolkits, game engines, and database connection pools.

---

## 1. Real-World Enterprise Scenarios

| System / Framework | Flyweight Object | Intrinsic (Shared) State | Extrinsic (Contextual) State | Benefit |
| :--- | :--- | :--- | :--- | :--- |
| **Java String Pool** | `java.lang.String` | Character array (`char[]` / `byte[]`) in String Constant Pool | Reference pointers in stack frame / local variables | Prevents duplicating identical string literals in heap |
| **Java Integer Cache** | `java.lang.Integer` | `Integer` instances for numbers `-128` to `127` | Variable assignment context in methods | Avoids millions of small `Integer` object allocations |
| **Game Engine Particle System** | `ParticleType` | Texture asset, color gradient, physics mass | 3D Position $(X,Y,Z)$, Velocity vector, Lifetime remaining | Renders 100,000 smoke/fire particles in real time |
| **Browser Document Object Model (DOM)** | CSS Class Rule (`.btn-primary`) | Background color, padding, border radius, font-family | DOM Node position in HTML document tree | Millions of DOM elements share unified CSS styling rules |

---

## 2. Detailed JDK Example 1: Java String Constant Pool

Java's `java.lang.String` is one of the most famous implementations of the Flyweight Pattern.

### How it Works
When you declare string literals:
```java
String s1 = "Hello";
String s2 = "Hello";
String s3 = new String("Hello").intern();
```

* Java stores only **ONE copy** of the character sequence `"Hello"` in a special JVM heap region called the **String Constant Pool**.
* `s1`, `s2`, and `s3` all point to the **exact same memory address** in the pool!

```text
Stack Frame                               String Constant Pool (JVM Heap)
┌────────────┐                           ┌──────────────────────────────┐
│  s1        ├──────────────────────────►│                              │
├────────────┤                           │   "Hello" (Flyweight)        │
│  s2        ├──────────────────────────►│   char[] = {'H','e','l','l','o'} │
├────────────┤                           │                              │
│  s3        ├──────────────────────────►└──────────────────────────────┘
└────────────┘
```

```java
System.out.println(s1 == s2); // Output: true (Same object reference!)
```

---

## 3. Detailed JDK Example 2: `Integer.valueOf()` Caching

In Java, wrapper classes cache low-value immutable objects using a Flyweight pool.

```java
Integer a = Integer.valueOf(100);
Integer b = Integer.valueOf(100);
System.out.println(a == b); // Output: true (Shared Flyweight instance!)

Integer x = Integer.valueOf(500);
Integer y = Integer.valueOf(500);
System.out.println(x == y); // Output: false (Out of default cache range -128 to 127)
```

### JDK Source Code Implementation (`Integer.java`)
```java
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```
Here, `IntegerCache.cache` acts as the **FlyweightFactory** map holding pre-allocated `Integer` flyweight instances for numbers between `-128` and `127`.

---

## 4. Pattern Comparisons

Understanding how Flyweight differs from other structural and creational patterns is a frequent LLD interview topic.

---

### A. Flyweight vs. Singleton

```text
SINGLETON PATTERN                                FLYWEIGHT PATTERN
┌─────────────────────────┐                      ┌─────────────────────────┐
│     Singleton Class     │                      │    Flyweight Pool       │
├─────────────────────────┤                      ├─────────────────────────┤
│ ONLY ONE Instance exists│                      │ MULTIPLE Instances      │
│ for the ENTIRE system.  │                      │ (Key-based shared pool) │
└─────────────────────────┘                      └─────────────────────────┘
```

* **Singleton**: Ensures a class has **only one instance** across the entire application (e.g., `DatabaseConnectionPool`, `Logger`).
* **Flyweight**: Manages **multiple instances** in a shared factory pool. Each instance represents a distinct *intrinsic state* (e.g., Oak TreeType, Pine TreeType, Cherry TreeType).

---

### B. Flyweight vs. Prototype

* **Prototype**: Used when creating new objects by **cloning an existing prototype** object. Every cloned object gets its own separate memory copy.
* **Flyweight**: Used to **share existing immutable objects** across contexts without creating new copies.

---

### C. Flyweight vs. Composite

* **Composite**: Builds hierarchical tree structures of objects (Leaves and Composites).
* **Flyweight**: Often combined with Composite! Leaf nodes in a massive Composite tree can be implemented as shared Flyweights to save memory.

---

### D. Summary Comparison Matrix

| Pattern | Category | Primary Purpose | Object Creation Behavior | Memory Impact |
| :--- | :--- | :--- | :--- | :--- |
| **Flyweight** | Structural | Reduce memory usage by sharing intrinsic state | Shares existing objects from pool via key lookup | 🟢 **Massive Memory Reduction** |
| **Singleton** | Creational | Guarantee a single global instance for a class | Instantiates exactly 1 instance system-wide | 🟡 Neutral |
| **Prototype** | Creational | Duplicate/clone existing objects | Copies prototype instance to create new objects | 🔴 Increases memory allocation |
| **Proxy** | Structural | Control access to a target object | Wraps target object to intercept calls | 🟡 Neutral |
