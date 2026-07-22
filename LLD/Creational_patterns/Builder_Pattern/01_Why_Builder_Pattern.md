# Module 01 – Why Builder Pattern? (The Problem)

> **Study order:** Start here. Before learning the pattern, understand the PAIN it cures.  
> If you skip this module, the Builder pattern will feel like unnecessary boilerplate.

---

## Table of Contents
1. [What Problem Does It Solve?](#1-what-problem-does-it-solve)
2. [Constructor Explosion](#2-constructor-explosion)
3. [Telescoping Constructors](#3-telescoping-constructors)
4. [Optional vs Mandatory Fields](#4-optional-vs-mandatory-fields)
5. [Why Setters Are NOT a Good Solution](#5-why-setters-are-not-a-good-solution)
6. [The Root Cause](#6-the-root-cause)
7. [Summary of Pain Points](#7-summary-of-pain-points)

---

## 1. What Problem Does It Solve?

Imagine you're building a `Pizza` class. A pizza has many properties:

| Field        | Mandatory? | Type    |
|--------------|------------|---------|
| size         | ✅ Yes     | String  |
| crustType    | ❌ No      | String  |
| cheese       | ❌ No      | boolean |
| pepperoni    | ❌ No      | boolean |
| mushrooms    | ❌ No      | boolean |
| extraSauce   | ❌ No      | boolean |
| onions       | ❌ No      | boolean |

How do you design this class so that:
- Mandatory fields are always provided?
- Optional fields can be skipped?
- The object is easy to read at the call site?
- The object cannot be partially constructed?

You have two naive options — **constructors** and **setters** — and both fail badly.

---

## 2. Constructor Explosion

### What Happens

When you try to handle different combinations of optional fields using multiple constructors, you end up writing an exponentially growing number of them:

```java
// Pizza.java — using multiple constructors
public class Pizza {

    private String  size;         // mandatory
    private String  crustType;    // optional
    private boolean cheese;       // optional
    private boolean pepperoni;    // optional
    private boolean mushrooms;    // optional
    private boolean extraSauce;   // optional
    private boolean onions;       // optional

    // Constructor 1 — only size
    public Pizza(String size) {
        this.size = size;
    }

    // Constructor 2 — size + crust
    public Pizza(String size, String crustType) {
        this.size      = size;
        this.crustType = crustType;
    }

    // Constructor 3 — size + crust + cheese
    public Pizza(String size, String crustType, boolean cheese) {
        this.size      = size;
        this.crustType = crustType;
        this.cheese    = cheese;
    }

    // Constructor 4 — size + crust + cheese + pepperoni
    public Pizza(String size, String crustType, boolean cheese, boolean pepperoni) {
        this.size      = size;
        this.crustType = crustType;
        this.cheese    = cheese;
        this.pepperoni = pepperoni;
    }

    // Constructor 5 — size + crust + cheese + pepperoni + mushrooms
    public Pizza(String size, String crustType, boolean cheese, boolean pepperoni, boolean mushrooms) {
        this.size      = size;
        this.crustType = crustType;
        this.cheese    = cheese;
        this.pepperoni = pepperoni;
        this.mushrooms = mushrooms;
    }

    // ... and this keeps growing! 🚨
}
```

### Problems

```
❌  6 optional fields → 2^6 = 64 possible combinations → 64 constructors?! No.
❌  Maintenance nightmare: every new field = update ALL constructors.
❌  Impossible to have a constructor that skips middle fields (e.g., size + mushrooms but NO crust/cheese).
```

---

## 3. Telescoping Constructors

### What Is It?

Telescoping is the common "fix" to avoid duplicate constructor code — each constructor calls the next larger one with a default value:

```java
// Pizza.java — Telescoping constructor anti-pattern
public class Pizza {

    private String  size;
    private String  crustType;
    private boolean cheese;
    private boolean pepperoni;
    private boolean mushrooms;

    public Pizza(String size) {
        this(size, "Thin");         // calls next constructor with default
    }

    public Pizza(String size, String crustType) {
        this(size, crustType, false);
    }

    public Pizza(String size, String crustType, boolean cheese) {
        this(size, crustType, cheese, false);
    }

    public Pizza(String size, String crustType, boolean cheese, boolean pepperoni) {
        this(size, crustType, cheese, pepperoni, false);
    }

    public Pizza(String size, String crustType, boolean cheese, boolean pepperoni, boolean mushrooms) {
        this.size      = size;
        this.crustType = crustType;
        this.cheese    = cheese;
        this.pepperoni = pepperoni;
        this.mushrooms = mushrooms;
    }
}
```

### Call Site — Unreadable Disaster

```java
// ❌ What do these booleans mean? No one can read this!
Pizza p1 = new Pizza("Large", "Thin", true, false, true);
Pizza p2 = new Pizza("Medium", "Thick", false, true, false);
Pizza p3 = new Pizza("Small");

// Reading this code 6 months later: was that 'true' for pepperoni or mushrooms?!
```

### Problems

```
❌  Call site is completely unreadable — positional arguments are anonymous.
❌  Wrong order of booleans = silent bug (Java won't warn you).
❌  You cannot skip a middle parameter — you MUST pass all preceding ones.
❌  Adding a new field in the middle shifts all following parameters — breaks ALL call sites.
```

---

## 4. Optional vs Mandatory Fields

### The Core Tension

```
Mandatory fields  → MUST be in the constructor (otherwise object is invalid)
Optional fields   → Should NOT be in the constructor (makes it ugly and unreadable)
```

### What We Want

```java
// ✅ Ideal world (pseudo-code — doesn't work in Java directly)
Pizza p = new Pizza(
    size:      "Large",   // mandatory — clearly labeled
    cheese:    true,      // optional  — clearly labeled
    mushrooms: true       // optional  — skip others without issue
);
```

Java doesn't support named parameters. That's exactly the gap Builder fills.

### What Happens Without Builder

```java
// ❌ Forced to pass null or false for every optional field you don't want
Pizza p = new Pizza("Large", null, false, false, true, false, false);
//                             ↑      ↑      ↑      ↑      ↑      ↑
//                           crust  cheese pepper mushroom sauce onions
```

---

## 5. Why Setters Are NOT a Good Solution

You might think: *"Just use a no-arg constructor + setters!"*

```java
// Pizza.java — JavaBeans style
public class Pizza {
    private String  size;
    private boolean cheese;
    private boolean pepperoni;

    public Pizza() {}   // no-arg constructor

    public void setSize(String size)          { this.size = size; }
    public void setCheese(boolean cheese)     { this.cheese = cheese; }
    public void setPepperoni(boolean pepperoni) { this.pepperoni = pepperoni; }
}

// Client
Pizza p = new Pizza();
p.setSize("Large");
p.setCheese(true);
p.setPepperoni(false);
```

This looks cleaner at first. But it has **serious problems**:

### Problem 1: Inconsistent (Partially Constructed) Object

```java
Pizza p = new Pizza();
p.setSize("Large");

// ⚠️ RIGHT HERE — object exists but size is set, cheese is NOT set.
// If another thread calls p.getCheese() now, it gets false (default).
// If you accidentally call some method here, the pizza is INVALID.

p.setCheese(true);
```

> The object is in an **invalid intermediate state** between the first and last setter call.  
> In multi-threaded environments this is a **race condition waiting to happen**.

### Problem 2: Cannot Make the Object Immutable

```java
// ❌ Once you expose setters, ANYONE can mutate the object at any time.
// You can NEVER make this class immutable (final fields + no setters).
Pizza p = new Pizza();
p.setSize("Large");
// ... later in totally different code ...
p.setSize("Small");  // who changed my pizza?! 😱
```

### Problem 3: No Validation of Field Combinations

```java
// With setters, you can't enforce rules like:
// "If size is Small, pepperoni count must be ≤ 2"
// You'd have to validate on EVERY setter — messy and incomplete.

p.setSize("Small");
p.setPepperoniCount(10);   // invalid, but no one stopped you!
```

### Problem 4: Not Thread-Safe

```java
// Thread A                        Thread B
p.setSize("Large");    ←→          p.setCheese(true);
p.setCheese(true);     ←→          p.setSize("Small");
// Result: unpredictable! Both threads write to shared mutable object.
```

---

## 6. The Root Cause

The fundamental problem is:

```
Java constructors force ALL fields to be positional and ALL values to be passed at once.
Java setters allow flexible field-by-field setting but destroy immutability and safety.

We need something that:
  ✅ Lets you set fields by name (not position)
  ✅ Allows some fields to be optional
  ✅ Enforces mandatory fields
  ✅ Validates field combinations
  ✅ Produces a fully-formed, IMMUTABLE object in one atomic step
```

**That "something" is the Builder Pattern.**

---

## 7. Summary of Pain Points

| Problem                       | Constructor | Setters | Builder |
|-------------------------------|:-----------:|:-------:|:-------:|
| Readable call site            | ❌          | 🟡      | ✅      |
| Optional fields without clutter | ❌        | ✅      | ✅      |
| Mandatory field enforcement   | 🟡          | ❌      | ✅      |
| Immutable object              | ✅          | ❌      | ✅      |
| No intermediate invalid state | ✅          | ❌      | ✅      |
| Thread-safe by design         | ✅          | ❌      | ✅      |
| Centralised validation        | ✅          | ❌      | ✅      |
| Scales with many fields       | ❌          | 🟡      | ✅      |

---

**Next →** [`02_Introduction_to_Builder.md`](./02_Introduction_to_Builder.md)
