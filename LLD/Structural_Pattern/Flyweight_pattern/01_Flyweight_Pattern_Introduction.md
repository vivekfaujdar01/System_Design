# Module 1: Flyweight Pattern Fundamentals

## 1. What is the Flyweight Pattern?

The **Flyweight Pattern** is a structural design pattern focused on **memory optimization**. It enables an application to support vast numbers of objects efficiently by sharing common parts of state among multiple objects instead of keeping all data in each individual object.

The term **Flyweight** comes from boxing (the lightest weight category). In software architecture, a Flyweight object is a lightweight object that minimizes memory footprint by sharing its invariant data across thousands or millions of instances.

---

## 2. Definition & GoF Classification

> **Flyweight Pattern**: Use sharing to support large numbers of fine-grained objects efficiently.
> 
> — *Gang of Four (GoF)*

* **Category**: Structural Design Pattern
* **Primary Objective**: Reduce RAM consumption when creating massive numbers of similar objects.
* **Core Strategy**: Divide object state into **Intrinsic (Shared)** and **Extrinsic (Contextual)** state.

---

## 3. The Core Concept: Intrinsic vs. Extrinsic State

The cornerstone of the Flyweight Pattern is separating an object's state into two distinct components:

```text
┌────────────────────────────────────────────────────────────────────────┐
│                             OBJECT STATE                               │
├───────────────────────────────────┬────────────────────────────────────┤
│         INTRINSIC STATE           │          EXTRINSIC STATE           │
│         (Context-Independent)     │          (Context-Dependent)       │
├───────────────────────────────────┼────────────────────────────────────┤
│ • Constant & Immutable            │ • Variable & Mutable               │
│ • Stored INSIDE the Flyweight     │ • Passed OUTSIDE into Flyweight    │
│ • Shared across millions of items │ • Unique to each individual context│
│ • Example: Texture, Mesh, Font    │ • Example: X/Y Coordinates, Color  │
└───────────────────────────────────┴────────────────────────────────────┘
```

### Intrinsic State (Shared / Heavy)
* Information that is constant across all instances.
* Does not depend on the object's context or location.
* Stored directly inside the **Flyweight** object and remains **immutable**.
* *Examples*: Tree 3D Mesh Model, Texture Image Data, Character Font Glyph shape.

### Extrinsic State (Contextual / Lightweight)
* Information that is unique to a specific instance and depends on its context.
* Changes based on where, when, or how the object is used.
* Computed on-the-fly or stored in **Client / Context** objects, then passed to Flyweight methods as parameters.
* *Examples*: X/Y/Z screen coordinates, current health, scale factor, character row/column position.

---

## 4. The Problem: Memory Exhaustion

Imagine building a 3D Open-World Video Game containing a massive forest with **1,000,000 trees**.

```text
WITHOUT FLYWEIGHT:
Each Tree Object contains:
 ├── double x (8 bytes)
 ├── double y (8 bytes)
 ├── String name ("Oak") (50 bytes)
 ├── Byte[] textureData (10 MB texture image)
 └── Byte[] meshModelData (5 MB 3D geometry)

Total Memory for 1,000,000 Trees = 1,000,000 × ~15 MB = ~15 TERABYTES of RAM!
Result: java.lang.OutOfMemoryError (Application Crashes)
```

```text
WITH FLYWEIGHT:
1. Create ONLY ONE "Oak TreeType" Flyweight Object containing:
    ├── String name ("Oak")
    ├── Byte[] textureData (10 MB)
    └── Byte[] meshModelData (5 MB)
   Total Shared Memory = 15 MB

2. Create 1,000,000 Lightweight Context Objects containing:
    ├── double x (8 bytes)
    ├── double y (8 bytes)
    └── Reference to shared "Oak TreeType" Flyweight (8 bytes)
   Total Context Memory = 1,000,000 × 24 bytes = 24 MB

Total System Memory = 15 MB + 24 MB = ~39 MB of RAM! (99.999% Memory Reduction!)
```

---

## 5. Real-Life Analogies

### Analogy 1: Word Processor / Text Editor Document
When typing a 500-page book in Microsoft Word containing **1,000,000 characters**:
* Creating 1,000,000 `Character` objects with full font render matrices, SVG glyph paths, and color tables would exhaust memory.
* Instead, Word creates a single **Flyweight Glyph** for letter `'A'` in font `Arial 12pt`.
* The 1,000,000 character instances store only their `(x, y)` position and a reference to the shared `'A'` Flyweight.

```text
                              ┌──────────────────────┐
                              │  GlyphFlyweight ('A')│  <-- Intrinsic State
                              │  - Font: Arial 12pt  │      (Shared Once)
                              │  - Vector Paths      │
                              └──────────▲───────────┘
                                         │
                 ┌───────────────────────┼───────────────────────┐
                 │                       │                       │
      ┌──────────┴───────────┐┌──────────┴───────────┐┌──────────┴───────────┐
      │ Document Position 1  ││ Document Position 2  ││ Document Position 3  │ <-- Extrinsic State
      │ - Line: 4, Col: 12   ││ - Line: 10, Col: 2   ││ - Line: 45, Col: 80  │     (Unique Context)
      └──────────────────────┘└──────────────────────┘└──────────────────────┘
```

### Analogy 2: Public Library / E-Reader
* **Without Flyweight**: Buying 10,000 physical copies of a textbook for 10,000 students.
* **With Flyweight**: The library keeps **one digital master copy** (Intrinsic content). Each student has a lightweight bookmark pointer (Extrinsic reading progress page number).

---

## 6. Structure & Components

```text
                            ┌─────────────────────┐
                            │  FlyweightFactory   │
                            ├─────────────────────┤
                            │ - flyweightPool     │
                            ├─────────────────────┤
                            │ + getFlyweight(key) │
                            └──────────┬──────────┘
                                       │ returns / creates
                                       ▼
 ┌──────────────┐            ┌────────────────────┐
 │    Client    │───────────►│    Flyweight       │ (Interface / Abstract Class)
 └──────┬───────┘            ├────────────────────┤
        │                    │ + operation(ext)   │
        │ holds              └─────────▲──────────┘
        ▼                              │ implements
 ┌──────────────┐            ┌─────────┴──────────┐
 │   Context    │───────────►│ ConcreteFlyweight  │
 ├──────────────┤ references ├────────────────────┤
 │ - extrinsic  │            │ - intrinsicState   │
 └──────────────┘            └────────────────────┘
```

### Components Description
1. **`Flyweight` Interface**: Defines the contract for flyweight objects and declares methods accepting extrinsic state (e.g., `render(int x, int y)`).
2. **`ConcreteFlyweight`**: Implements the `Flyweight` interface and stores the **Intrinsic State**. Must be **immutable** and shareable across threads.
3. **`FlyweightFactory`**: Creates and manages flyweight objects. Ensures flyweights are shared properly. When client requests a flyweight, the factory returns an existing instance from its pool or creates a new one if it doesn't exist.
4. **`Context` (Client/Manager)**: Contains the **Extrinsic State** and holds a reference to the shared `ConcreteFlyweight`. Computes or passes extrinsic values to flyweight operations at runtime.

---

## 7. When to Use the Flyweight Pattern

Use the Flyweight Pattern **ONLY** when **ALL** of the following conditions are met:

1. An application needs to spawn a **very large number of objects** (e.g., $10^5$ to $10^7$ instances).
2. Memory storage costs are high or causing `OutOfMemoryError` runtime crashes.
3. Most of the object state can be extracted into **shared intrinsic data**.
4. The remaining state (**extrinsic data**) can be calculated on-the-fly or stored externally in lightweight context containers.
5. The application does not rely on object identity (`obj1 == obj2` tests), as shared flyweight objects will have identical memory addresses.

---

## 8. Advantages & Disadvantages

### Advantages
* ✅ **Dramatically Reduces RAM Usage**: Cuts memory overhead by up to 90%–99% in object-heavy applications.
* ✅ **Improves CPU Cache Line Performance**: Fewer unique objects mean better CPU cache utilization and lower Garbage Collection (GC) pressure.
* ✅ **Centralized Asset Management**: Shared immutable state prevents duplicate memory allocations.

### Disadvantages
* ❌ **Increased Code Complexity**: Separating state into intrinsic and extrinsic layers makes architecture harder to read.
* ❌ **Minor CPU Trade-off**: Computing extrinsic state on the fly or looking up objects in a factory map consumes extra CPU cycles.
* ❌ **Object Identity Confusion**: Multiple logical entities share the exact same object reference (`==` checks fail).

---

## 9. Summary Key Takeaways

* **Flyweight = Memory Optimization Pattern**.
* **Key Concept**: Split state into **Intrinsic (Shared/Immutable)** vs. **Extrinsic (Contextual/Passed as param)**.
* **Factory Pattern**: Crucial component to manage flyweight object caching (`HashMap` lookup).
* **Immutability**: Concrete flyweight objects MUST be immutable to prevent side effects across shared references.
