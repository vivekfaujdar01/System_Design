# 05 – Advantages, Disadvantages & When to Use Prototype

---

## 1. Advantages ✅

### 1.1 Avoids Expensive Re-Initialisation

If creating an object from scratch involves file I/O, DB queries, network calls, or heavy computation, cloning skips all of that:

```
new Object() → DB query → file parse → network call → 500 ms
clone()      → memcopy of fields                   →   1 ms ✅
```

### 1.2 Decouples Client from Concrete Classes

The client works only with the `Prototype` interface and calls `clone()`. It never imports or instantiates a concrete class:

```java
// Client only knows the interface
Prototype p = registry.get("employee-template");
Prototype copy = p.clone();   // no "new ConcreteEmployee()" anywhere in client code
```

### 1.3 Produces Objects at Runtime

Unlike Factory patterns that hard-code the list of types, Prototype can produce any object whose type was registered at runtime — ideal for plugin systems and configuration-driven architectures.

### 1.4 Enables Undo / Snapshot / Memento

Prototype is naturally combined with Memento: capture a clone before a mutating operation; restore on undo.

```java
Employee snapshot = current.clone();   // before edit
// ... user edits ...
if (undo) current = snapshot;          // restore
```

### 1.5 Simplifies Object Hierarchies

Instead of creating a parallel class hierarchy of factories for every product, each product simply knows how to copy itself.

---

## 2. Disadvantages ❌

### 2.1 Deep Copy Can Be Tricky

Correctly implementing a deep clone for objects with circular references or deeply nested graphs is complex and error-prone:

```java
// Circular reference — naive clone loops forever
class Node {
    Node next;
    Node prev;  // circular!
}
```

You need to track visited objects or use serialization.

### 2.2 Java's Cloneable Has Design Problems

The `Cloneable` interface is widely considered a poor design in Java:
- It's a marker interface with no methods.
- `Object.clone()` is `protected` — you must override it to make it public.
- It throws `CloneNotSupportedException` that must be caught or declared.
- Josh Bloch (*Effective Java*, Item 13): *"The Cloneable interface is broken."*

**Alternative:** Use a copy constructor or copy factory method:

```java
// Preferred by many Java practitioners
public Employee(Employee source) {
    this.name    = source.name;
    this.address = new Address(source.address);
    this.skills  = new ArrayList<>(source.skills);
}

// Or a static factory
public static Employee copyOf(Employee source) { ... }
```

### 2.3 Hidden Coupling in clone()

If a subclass adds a new mutable field but forgets to update `clone()`, the field is silently shallow-copied — a subtle bug that may only surface in production.

### 2.4 Not Always the Right Tool

If object construction is cheap, adding the Prototype infrastructure (interface, registry, clone logic) is over-engineering.

---

## 3. When to Use ✅

| Situation | Reason |
|-----------|--------|
| Object construction is **expensive** (DB, network, file) | Clone avoids re-running the setup |
| You need **many similar objects** with minor variations | Clone a template, tweak the variation |
| The **concrete type is unknown at compile time** | `clone()` on the interface works polymorphically |
| You need **snapshots / undo** | Clone = instant snapshot |
| **Plugin / runtime-configurable** systems | Register prototypes at runtime |

---

## 4. When NOT to Use ❌

| Situation | Better Alternative |
|-----------|--------------------|
| Object construction is cheap | Just use `new` |
| All objects are identical | Use a singleton or flyweight |
| Deep object graph with circular references | Use Builder or a dedicated serialiser |
| You control all concrete types | Copy constructor is simpler and clearer |

---

## 5. Prototype vs. Other Creation Mechanisms

| Aspect | `new` | Factory Method | Prototype |
|--------|-------|---------------|-----------|
| Knows concrete type | ✅ Yes | ✅ Yes (internally) | ❌ No — type-agnostic |
| Avoids re-initialisation | ❌ No | ❌ No | ✅ Yes |
| Runtime-registered types | ❌ No | ❌ No | ✅ Yes |
| Complexity | Low | Medium | Medium-High |
| Deep copy responsibility | N/A | N/A | ⚠️ On each clone() |

---

## 6. Summary Card

```
┌──────────────────────────────────────────────────────┐
│                  PROTOTYPE — CHEAT SHEET             │
├────────────────────┬─────────────────────────────────┤
│ GoF Category       │ Creational                       │
│ Problem Solved     │ Costly/complex object creation   │
│ Mechanism          │ Self-cloning via clone()         │
│ Java Built-in      │ Cloneable + Object.clone()       │
│ Main Risk          │ Shallow copy bug                 │
│ Preferred Alt.     │ Copy constructor / copy factory  │
│ Spring Parallel    │ scope="prototype" in Spring IoC  │
└────────────────────┴─────────────────────────────────┘
```

---

**Next →** [`06_Interview_Questions_and_Comparison.md`](./06_Interview_Questions_and_Comparison.md)
