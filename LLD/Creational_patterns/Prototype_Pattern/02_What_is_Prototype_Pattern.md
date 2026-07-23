# 02 – What is the Prototype Pattern? (Concept & Structure)

> **GoF Category:** Creational  
> **Intent:** Specify the kinds of objects to create using a *prototypical instance*, and create new objects by **copying** (cloning) this prototype.

---

## 1. One-Line Definition

> **Prototype** = an object that knows how to copy itself.

Instead of creating a new object from scratch, you ask an existing object to hand you a ready-made copy.

---

## 2. The Key Insight

Traditional object creation:
```
Client  ──new──►  ConcreteClass  ──►  Object
```

Prototype creation:
```
Client  ──clone()──►  Prototype  ──►  Copy of Object
```

The client **never** calls `new ConcreteClass()`. It calls a generic `clone()` method defined on an interface, keeping the client decoupled from the concrete type.

---

## 3. UML Class Diagram

```
        «interface»
        Prototype
        ──────────
      + clone() : Prototype
            ▲
            │ implements
    ┌───────┴────────┐
    │                │
ConcretePrototype1  ConcretePrototype2
──────────────────  ──────────────────
- field1            - fieldA
- field2            - fieldB
+ clone()           + clone()
    │                    │
    │ returns deep/      │ returns deep/
    │ shallow copy       │ shallow copy
    ▼                    ▼
  Copy1               Copy2
```

---

## 4. Participants

| Participant | Responsibility |
|-------------|----------------|
| **Prototype** (interface) | Declares `clone()` method |
| **ConcretePrototype** | Implements `clone()` — knows how to copy its own state |
| **Client** | Calls `clone()` without knowing the concrete class |
| **Prototype Registry** *(optional)* | Stores named prototypes; client asks registry for a clone |

---

## 5. Java's Built-In Support

Java provides the `Cloneable` marker interface and `Object.clone()` out of the box:

```java
public interface Cloneable { }   // marker — no methods

// Object.clone() performs a shallow copy by default
protected Object clone() throws CloneNotSupportedException
```

### 5.1 Using Java's Cloneable

```java
public class Document implements Cloneable {
    private String title;
    private List<String> pages;

    @Override
    public Document clone() {
        try {
            return (Document) super.clone();  // shallow copy
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

> ⚠️ `super.clone()` gives you a **shallow copy** — nested mutable objects still share the same reference.

### 5.2 Custom clone() for Deep Copy

```java
@Override
public Document clone() {
    try {
        Document copy = (Document) super.clone();
        copy.pages = new ArrayList<>(this.pages);  // deep-copy the list
        return copy;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

---

## 6. Shallow vs Deep Copy — Quick Visual

```
Original Object
│
├── name  = "Alice"       ← primitive / String → copied by value ✅
└── address ──────────────────► Address { city="Bangalore" }
                                         ▲
Shallow Copy                             │ same reference ⚠️
│
├── name  = "Alice"       ← new copy ✅
└── address ──────────────────────────── (points to same Address object!)


Deep Copy
│
├── name  = "Alice"       ← new copy ✅
└── address ──────────────► NEW Address { city="Bangalore" } ✅ (independent)
```

---

## 7. The Prototype Registry (Optional Enhancement)

A **registry** stores a map of named prototypes. Clients request clones by key — they don't even need to hold a reference to the prototype themselves.

```java
public class PrototypeRegistry {
    private final Map<String, Employee> registry = new HashMap<>();

    public void register(String key, Employee prototype) {
        registry.put(key, prototype);
    }

    public Employee get(String key) {
        return registry.get(key).clone();   // always returns a fresh clone
    }
}
```

```java
// Usage
registry.register("senior-engineer", seniorEngineerTemplate);

Employee newHire = registry.get("senior-engineer");
newHire.setName("Bob");   // doesn't affect the template
```

---

## 8. Pattern Summary Card

| Aspect | Detail |
|--------|--------|
| **Intent** | Clone existing objects without coupling to their classes |
| **Also known as** | Copy pattern |
| **Applicability** | Costly construction, unknown concrete type at runtime, need snapshot |
| **Key method** | `clone()` |
| **Java support** | `Cloneable` + `Object.clone()` |
| **Main risk** | Shallow copy — nested mutable objects accidentally shared |

---

**Next →** [`03_Implementing_Prototype_Java.md`](./03_Implementing_Prototype_Java.md)
