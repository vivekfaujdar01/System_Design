# 06 – Interview Questions & Comparison with Other Creational Patterns

---

## Section A — Core Interview Questions

---

### Q1 — What is the Singleton Design Pattern? State its two guarantees.

> **Model Answer:**  
> Singleton is a **Creational GoF design pattern** that:  
> 1. Ensures a class has **only one instance** in the entire JVM.  
> 2. Provides a **global access point** (`getInstance()`) to that instance.  
>
> It achieves this via three mechanisms:
> - `private` constructor → blocks `new` from outside
> - `private static` field → holds the single instance
> - `public static getInstance()` → the only way to obtain the instance

---

### Q2 — Why is the naïve lazy Singleton NOT thread-safe?

> **Model Answer:**  
> Two (or more) threads can simultaneously pass the `if (instance == null)` check before either assigns the field, resulting in multiple instances being created:
>
> ```
> Thread-1: reads instance → null → enters if block
> Thread-2: reads instance → null → enters if block (before T1 assigns)
> Thread-1: instance = new Singleton()
> Thread-2: instance = new Singleton()   ← second object created ❌
> ```
>
> **Fix:** Use `synchronized`, Double-Checked Locking with `volatile`, Bill Pugh, or Enum.

---

### Q3 — Explain Double-Checked Locking. Why is `volatile` mandatory?

> **Model Answer:**
>
> ```java
> private static volatile Singleton instance;
>
> public static Singleton getInstance() {
>     if (instance == null) {                  // check 1 — no lock (fast path)
>         synchronized (Singleton.class) {
>             if (instance == null) {          // check 2 — inside lock
>                 instance = new Singleton();
>             }
>         }
>     }
>     return instance;
> }
> ```
>
> **Why two checks?**  
> Two threads may both pass check-1 before either acquires the lock. The second check (inside the lock) prevents both from creating an instance.
>
> **Why `volatile`?**  
> `instance = new Singleton()` is NOT atomic. Under the hood it is:
> 1. Allocate memory
> 2. Invoke constructor
> 3. Assign reference to `instance`
>
> The JVM may reorder steps 2 and 3. Without `volatile`, another thread could read a non-null `instance` that points to a **partially-constructed object**. `volatile` prevents this reordering by establishing a *happens-before* relationship.

---

### Q4 — What is the Bill Pugh Singleton? Why is it preferred?

> **Model Answer:**
>
> ```java
> public class Singleton {
>     private Singleton() { }
>
>     private static class Holder {
>         static final Singleton INSTANCE = new Singleton();
>     }
>
>     public static Singleton getInstance() {
>         return Holder.INSTANCE;
>     }
> }
> ```
>
> **Why preferred:**
> - **Lazy** — `Holder` is not loaded until `getInstance()` is first called.
> - **Thread-safe** — JVM class initialisation is atomic (guaranteed by JLS §12.4.2).
> - **No `synchronized`** — no lock overhead after initialisation.
> - **No `volatile`** — not needed because the field is `final`.
>
> It gives you lazy + thread-safe Singleton with zero locking overhead and clean code.

---

### Q5 — Why is Enum Singleton considered the best implementation?

> **Model Answer (citing Effective Java, Item 3):**
>
> ```java
> public enum Singleton {
>     INSTANCE;
>     public void doSomething() { ... }
> }
> ```
>
> | Threat | Enum Solution |
> |--------|--------------|
> | **Serialization** | JVM's enum serialization guarantees same instance returned |
> | **Reflection** | `Constructor.newInstance()` throws `IllegalArgumentException` for enums |
> | **Cloning** | Enums cannot be cloned by design |
> | **Thread safety** | JVM initialises enum constants exactly once |
>
> The only downside: enums cannot be lazily initialised (created at class-load time).

---

### Q6 — How can Reflection break a Singleton? How do you prevent it?

> **Model Answer:**
>
> ```java
> // Attack
> Constructor<Singleton> ctor = Singleton.class.getDeclaredConstructor();
> ctor.setAccessible(true);
> Singleton s2 = ctor.newInstance();   // second instance! ❌
> ```
>
> **Defence — guard in the private constructor:**
>
> ```java
> private Singleton() {
>     if (instance != null) {
>         throw new RuntimeException(
>             "Use getInstance() — reflection-based creation is forbidden.");
>     }
> }
> ```
>
> **Best defence:** Use **Enum Singleton** — the JVM itself rejects reflection-based enum instantiation.

---

### Q7 — How does Serialization break a Singleton? How do you fix it?

> **Model Answer:**  
> Java's `ObjectInputStream.readObject()` creates a **new object** when deserialising, bypassing the private constructor. The newly deserialized object is a second instance.
>
> **Fix — `readResolve()`:**
>
> ```java
> public class Singleton implements Serializable {
>     private static final long serialVersionUID = 1L;
>     private static final Singleton INSTANCE = new Singleton();
>     private Singleton() { }
>     public static Singleton getInstance() { return INSTANCE; }
>
>     // Called by ObjectInputStream after deserialization
>     protected Object readResolve() {
>         return INSTANCE;   // discard the new object, return the real one
>     }
> }
> ```
>
> Again, **Enum Singleton** handles this automatically.

---

### Q8 — What is the difference between Singleton and Static class?

| Aspect | Singleton | Static Class |
|--------|-----------|-------------|
| Instantiation | One object | No objects |
| Interface implementation | ✅ Yes | ❌ No |
| Inheritance | ✅ (limited) | ❌ No |
| Lazy initialisation | ✅ Yes | ❌ No |
| Polymorphism | ✅ Yes | ❌ No |
| State | Instance fields | Only static fields |
| Testable / mockable | ✅ With DI | ❌ Very hard |
| Serializable | ✅ Yes | ❌ No |

---

### Q9 — Is Singleton thread-safe in Spring? How does Spring handle it?

> **Model Answer:**  
> In Spring, the default bean scope is **singleton per ApplicationContext**. Spring manages the lifecycle and guarantees one bean instance per container — NOT per JVM. Multiple ApplicationContexts would each have their own "singleton" bean.
>
> Spring's singleton beans are thread-safe **only if they are stateless** (no mutable instance fields). If a Spring singleton has mutable state shared across threads, you must synchronize it manually or use `ThreadLocal`.

---

### Q10 — When would you NOT use Singleton?

> **Model Answer:**  
> - When you need **multiple independent configurations** (e.g., multi-tenant apps).
> - When **unit testing** is critical — global state makes tests brittle; use DI instead.
> - In **distributed systems** — a Singleton is local to one JVM; use Redis/Hazelcast for shared state.
> - When the object is **cheap to create** — overhead of the pattern isn't justified.
> - When you need to **swap implementations** — use DI + interface instead.

---

## Section B — Comparison with Other Creational Patterns

### Singleton vs. Prototype

| Aspect | Singleton | Prototype |
|--------|-----------|-----------|
| **Instances** | Exactly one | Many independent clones |
| **State** | Shared by all | Each clone has its own |
| **Use case** | Shared resource manager | Template object cloning |
| **Thread concern** | Must synchronise shared state | Clones are independent |

---

### Singleton vs. Factory Method

| Aspect | Singleton | Factory Method |
|--------|-----------|---------------|
| **Purpose** | Control instance count | Delegate object creation to subclasses |
| **Output** | Same object every time | New object every call |
| **Polymorphism** | Limited | Core mechanism |
| **Combined?** | ✅ Factory can return a Singleton | — |

---

### Singleton vs. Builder

| Aspect | Singleton | Builder |
|--------|-----------|---------|
| **Purpose** | One global instance | Construct complex objects step-by-step |
| **State** | Persistent / shared | Fresh per build |
| **Output** | Always same object | New configured object |

---

### Creational Pattern Decision Tree

```
Do you need to create objects?
│
├─ Exactly ONE instance, shared everywhere?
│    └─► SINGLETON
│
├─ Copy an existing expensive object?
│    └─► PROTOTYPE
│
├─ Subclass decides which class to instantiate?
│    └─► FACTORY METHOD
│
├─ Families of related objects together?
│    └─► ABSTRACT FACTORY
│
└─ Complex object assembled step-by-step?
     └─► BUILDER
```

---

## Section C — Quick Revision Flashcards

| Question | Answer |
|----------|--------|
| GoF category of Singleton | Creational |
| Two guarantees of Singleton | One instance + global access |
| Java mechanism | `private` ctor + `static` field + `static getInstance()` |
| Is basic lazy Singleton thread-safe? | ❌ No |
| Why is `volatile` needed in DCL? | Prevents instruction reordering (half-constructed object) |
| Best lazy + thread-safe pattern | Bill Pugh (inner static class) |
| Best overall Singleton in Java | Enum Singleton |
| Does Enum handle serialization? | ✅ Yes — automatically |
| Does Enum handle reflection? | ✅ Yes — throws IllegalArgumentException |
| Spring equivalent | `@Service` / `@Component` (default scope = singleton) |
| Singleton in distributed systems? | ❌ Not applicable — use Redis/distributed cache |
| How to make Singleton testable? | Use DI — inject via constructor, not via `getInstance()` inside class |

---

**← Previous:** [`05_Advantages_Disadvantages_When_to_Use.md`](./05_Advantages_Disadvantages_When_to_Use.md)  
**← Back to start:** [`01_Why_Singleton_Problem.md`](./01_Why_Singleton_Problem.md)
