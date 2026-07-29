# Module 4: Advanced Concepts & Interview Questions

---

## 1. Thread Safety in Flyweight Pattern

Because Flyweight objects are shared across multiple threads simultaneously (e.g., rendering trees in parallel graphics pipelines or handling requests in web servers), thread safety is critical.

### Strategy 1: Flyweight Immutability
All fields in a `ConcreteFlyweight` class MUST be declared `private final`. Once instantiated by the factory, a Flyweight object's intrinsic state can never be modified.

```java
// Thread-Safe Immutable Concrete Flyweight
public final class ImmutableTreeType {
    private final String name;
    private final String color;

    public ImmutableTreeType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
}
```

### Strategy 2: Thread-Safe Flyweight Factory (`ConcurrentHashMap`)
Use `ConcurrentHashMap.computeIfAbsent()` to guarantee thread-safe atomic creation and caching without blocking reads.

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeTreeFactory {
    private static final Map<String, TreeType> cache = new ConcurrentHashMap<>();

    public static TreeType getTreeType(String name, String color, String texture, String mesh) {
        String key = name + "_" + color;
        return cache.computeIfAbsent(key, k -> new TreeType(name, color, texture, mesh));
    }
}
```

---

## 2. Flyweight & SOLID Principles

### Single Responsibility Principle (SRP)
* **Flyweight Class**: Responsible solely for managing and rendering **Intrinsic State**.
* **Context Class**: Responsible solely for maintaining instance-specific **Extrinsic State** (coordinates, scale).
* **Factory Class**: Responsible solely for **pooling and caching** instances.

### Open/Closed Principle (OCP)
New flyweight types (e.g., `PalmTreeType`, `BonsaiTreeType`) can be added without modifying existing client code or factory logic.

---

## 3. Five Rules to Identify Flyweight Pattern

1. **High Object Count**: Application instantiates tens of thousands or millions of fine-grained objects.
2. **High Memory Overhead**: Heap memory consumption is high or approaching RAM limits (`OutOfMemoryError`).
3. **State Separability**: Object state can be cleanly divided into **Intrinsic** (shareable constant) and **Extrinsic** (contextual variable).
4. **Context Extensibility**: Extrinsic state can be passed as parameters to Flyweight operations rather than stored inside each object.
5. **No Identity Dependency**: The application does not rely on unique object memory identity (`==` checks).

---

## 4. Decision Tree

```text
Do you need to create a massive number of fine-grained objects (10,000+)?
                 │
                 ├──► NO  ──► DO NOT USE FLYWEIGHT (Standard Allocation)
                 │
                 └──► YES ──► Can object state be split into Intrinsic & Extrinsic components?
                                 │
                                 ├──► NO  ──► DO NOT USE FLYWEIGHT
                                 │
                                 └──► YES ──► USE FLYWEIGHT PATTERN!
                                              (Create FlyweightFactory + Immutable Pool)
```

---

## 5. Frequently Asked LLD Interview Questions

### Q1. What happens if a client attempts to modify intrinsic state in a Flyweight?
**Answer**: Intrinsic state MUST be immutable (`final` fields with no setters). If intrinsic state were mutable, changing it for one instance would silently alter the state across all thousands of shared objects across the application.

---

### Q2. How is extrinsic state passed to a Flyweight?
**Answer**: Extrinsic state is stored in lightweight Context objects or computed dynamically on the fly, then passed as method parameters directly into Flyweight operation methods (e.g., `flyweight.draw(x, y)`).

---

### Q3. How does Flyweight relate to the Factory Pattern?
**Answer**: Flyweight relies fundamentally on a **Factory Pattern** (`FlyweightFactory`) to manage its object pool. Clients request flyweights from the factory, which checks its internal lookup map (`HashMap`) to return an existing instance or create a new one.

---

### Q4. What is the main trade-off of the Flyweight Pattern?
**Answer**: **CPU cycles vs. RAM space**. Flyweight dramatically reduces RAM consumption, but requires slightly more CPU cycles to perform factory map lookups and compute/pass extrinsic state at runtime.

---

### Q5. Can a Flyweight object be garbage collected?
**Answer**: As long as the `FlyweightFactory` holds a reference to a Flyweight object in its pool map, the JVM Garbage Collector will NOT collect it. If memory pressure is severe, factories can use `WeakHashMap` or soft references (`SoftReference`) so unused flyweights are eligible for GC.

---

## 6. Flyweight Pattern Summary Matrix

```text
Pattern Name        : Flyweight Design Pattern
GoF Category        : Structural Design Pattern
Primary Objective   : Reduce RAM footprint via sharing intrinsic state
Key Mechanism       : Intrinsic (Shared) vs Extrinsic (Contextual) separation
Factory Reliance    : Requires FlyweightFactory (HashMap lookup pool)
Thread Safety       : High (Requires immutable Flyweight objects)
```

> **One-Line Interview Definition**:
> *"The Flyweight Pattern minimizes memory consumption by sharing invariant intrinsic state across large numbers of fine-grained objects while passing variant extrinsic state dynamically at runtime."*
