# 06 – Interview Questions & Comparison with Other Creational Patterns

---

## 1. Core Interview Questions

### Q1 — What is the Prototype Design Pattern?

> **Model Answer:**  
> Prototype is a **creational pattern** where an object creates copies of itself via a `clone()` method. Instead of instantiating new objects with `new`, clients ask an existing (prototype) object to produce a ready-made copy. This is useful when object creation is expensive (DB, network, heavy computation) or when the concrete type is unknown at runtime.

---

### Q2 — What is the difference between Shallow Copy and Deep Copy?

> **Model Answer:**

| | Shallow Copy | Deep Copy |
|---|---|---|
| **What is copied** | Top-level fields only | All nested objects recursively |
| **Nested mutable objects** | Shared reference (same memory) | New independent copies |
| **Mutation effect** | Changing clone's nested object affects original | Fully isolated |
| **Java mechanism** | `super.clone()` | `super.clone()` + clone each mutable field |
| **When safe** | All nested objects are immutable | Always safe, but more code required |

```java
// Shallow — shared reference risk
Employee clone = (Employee) super.clone();
// clone.address == original.address → SAME object ⚠️

// Deep — explicit deep clone of each mutable field
Employee copy = (Employee) super.clone();
copy.address = this.address.clone();   // NEW object ✅
copy.skills  = new ArrayList<>(this.skills);
```

---

### Q3 — Why is Java's `Cloneable` interface considered broken?

> **Model Answer (citing Effective Java, Item 13):**
> 1. `Cloneable` is a marker interface — it has no `clone()` method, yet controls the behaviour of `Object.clone()`.
> 2. `Object.clone()` is `protected`; every class must override it and make it `public`.
> 3. It throws a checked `CloneNotSupportedException` that clutters code.
> 4. `super.clone()` returns `Object`, requiring unchecked casts.
> 5. Subclasses can silently break cloning by adding mutable fields without updating `clone()`.
>
> **Preferred alternative:** Copy constructor or static copy factory:
> ```java
> public Employee(Employee source) {
>     this.name    = source.name;
>     this.address = new Address(source.address);
>     this.skills  = new ArrayList<>(source.skills);
> }
> ```

---

### Q4 — What is a Prototype Registry?

> **Model Answer:**  
> A Prototype Registry (also called a Prototype Cache or Object Pool in some contexts) is a `Map<String, Prototype>` that stores pre-configured prototype instances. Clients request clones by key rather than by class. This further decouples the client from concrete types and centralises prototype management.
>
> ```java
> registry.register("goblin", new Goblin(...));      // once
> GameCharacter enemy = registry.spawn("goblin");    // clone every time
> ```

---

### Q5 — How does Prototype handle circular references?

> **Model Answer:**  
> Naive recursive `clone()` will infinite-loop on circular references. Standard solutions:
> 1. **Track visited objects** during cloning using an `IdentityHashMap<Original, Clone>`.
> 2. **Java Serialization deep copy** — serialise to bytes and deserialise; handles circular refs automatically via `ObjectOutputStream`'s back-reference tracking.
>
> ```java
> public T deepCopy() {
>     ByteArrayOutputStream bos = new ByteArrayOutputStream();
>     new ObjectOutputStream(bos).writeObject(this);
>     return (T) new ObjectInputStream(
>         new ByteArrayInputStream(bos.toByteArray())).readObject();
> }
> ```

---

### Q6 — Where is Prototype used in real frameworks/JDK?

| Framework / JDK | Usage |
|-----------------|-------|
| **Spring IoC** | `scope="prototype"` — each `getBean()` call returns a fresh clone |
| **Java `Object.clone()`** | Core Prototype mechanism |
| **`ArrayList` copy constructor** | `new ArrayList<>(existing)` — shallow prototype |
| **`HashMap.clone()`** | Shallow clone of the map structure |
| **Hibernate** | `session.merge()` / detached entity cloning for new records |
| **`java.awt.BasicStroke`** | Cloned before modification |

---

### Q7 — Can you implement Prototype without `Cloneable`?

> **Yes. Copy Constructor Pattern:**
> ```java
> public class Employee {
>     private String       name;
>     private Address      address;
>     private List<String> skills;
>
>     // Copy constructor — explicit and readable
>     public Employee(Employee source) {
>         this.name    = source.name;
>         this.address = new Address(source.address.getCity(), source.address.getCountry());
>         this.skills  = new ArrayList<>(source.skills);
>     }
> }
>
> // Usage — still follows Prototype intent
> Employee template = new Employee("Alice", new Address("BLR","India"), skills);
> Employee copy     = new Employee(template);   // deep clone via copy ctor
> ```
> This is **preferred** in modern Java because it's explicit, type-safe, and avoids `Cloneable`'s pitfalls.

---

## 2. Comparison with Other Creational Patterns

### 2.1 Prototype vs. Singleton

| Aspect | Prototype | Singleton |
|--------|-----------|-----------|
| **Number of instances** | Many clones | Exactly one |
| **Object identity** | Each clone is independent | Global shared instance |
| **Use case** | "I need many similar, independent objects" | "I need one shared state globally" |
| **Mutable state** | ✅ Clones can diverge safely | ⚠️ All code shares the same mutable state |

---

### 2.2 Prototype vs. Factory Method

| Aspect | Prototype | Factory Method |
|--------|-----------|---------------|
| **Creates object via** | `clone()` on existing instance | Subclass overrides a factory method |
| **Concrete class needed** | ❌ No — clone through interface | ✅ Yes — subclass instantiates specific class |
| **Costly construction avoided** | ✅ Yes | ❌ No — still calls `new` |
| **Polymorphism mechanism** | Object polymorphism (clone) | Class polymorphism (subclassing) |
| **When to use** | Copy expensive templates | Let subclasses decide which class to create |

---

### 2.3 Prototype vs. Abstract Factory

| Aspect | Prototype | Abstract Factory |
|--------|-----------|-----------------|
| **Creates** | Copies of one object | Families of related objects |
| **Configuration** | Template configured before cloning | Factory method for each product |
| **Extensibility** | Register new prototypes at runtime | Add new ConcreteFactory subclass |
| **Complexity** | Lower | Higher |
| **When to use** | Dynamic type set, costly creation | Product families with variants |

---

### 2.4 Prototype vs. Builder

| Aspect | Prototype | Builder |
|--------|-----------|---------|
| **Focus** | Copying an existing fully-built object | Constructing a new object step-by-step |
| **Configuration** | Clone then tweak | Provide each field to the builder |
| **Use case** | Template with small variations | Complex objects with many optional parts |
| **State required** | Pre-existing prototype needed | No existing object needed |

---

### 2.5 Overall Creational Pattern Decision Tree

```
Do you need to create objects?
│
├─ Only one instance ever needed?
│    └─► SINGLETON
│
├─ Object construction is expensive / concrete type unknown?
│    └─► PROTOTYPE (clone an existing instance)
│
├─ Subclasses should decide which class to instantiate?
│    └─► FACTORY METHOD
│
├─ Families of related objects that must be used together?
│    └─► ABSTRACT FACTORY
│
└─ Complex objects with many optional steps / configurations?
     └─► BUILDER
```

---

## 3. Quick Revision Flashcards

| Question | Answer |
|----------|--------|
| GoF category of Prototype | Creational |
| Java marker interface for cloning | `Cloneable` |
| Default copy performed by `super.clone()` | Shallow copy |
| Risk of shallow copy | Shared mutable nested objects |
| How to deep-clone a `List<String>` | `new ArrayList<>(original.list)` |
| Alternative to `Cloneable` | Copy constructor / copy factory |
| Spring equivalent | `scope="prototype"` bean |
| Pattern for storing named prototypes | Prototype Registry |
| Pattern often combined with Prototype for undo | Memento |
| Prototype is NOT suitable when | Object creation is cheap / object graph is circular |

---

**← Previous:** [`05_Advantages_Disadvantages_When_to_Use.md`](./05_Advantages_Disadvantages_When_to_Use.md)  
**← Back to start:** [`01_Why_Prototype_Problem.md`](./01_Why_Prototype_Problem.md)
