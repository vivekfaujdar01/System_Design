# Module 5: Pattern Comparisons, Trade-offs & LLD Interview Guide

## 1. How to Identify When to Use the Proxy Pattern

In LLD interviews and production system architecture, apply the Proxy Pattern whenever you hear the following requirements:

* *"We need to lazy-load a heavy component only when requested."* -> **Virtual Proxy**
* *"We must restrict access to sensitive operations based on user roles."* -> **Protection Proxy**
* *"We want to store query results locally so repeated requests don't hit the database."* -> **Caching Proxy**
* *"We need to communicate with a remote microservice/server as if it were a local object."* -> **Remote Proxy**
* *"We need to log runtime metrics, execution duration, or reference counts transparently."* -> **Smart Reference / Audit Proxy**

---

## 2. Comparison Matrix: Proxy vs. Other Structural Patterns

Structural patterns can look surprisingly similar in UML diagrams because many of them use wrapping or composition. However, their **intents and relationships with target objects are fundamentally distinct**.

```text
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                  STRUCTURAL PATTERNS COMPARISON                                  │
├──────────────┬────────────────────────┬─────────────────────────┬────────────────────────────────┤
│ Pattern      │ Interface Relationship │ Target Object           │ Primary Intent                 │
├──────────────┼────────────────────────┼─────────────────────────┼────────────────────────────────┤
│ Proxy        │ EXACT SAME interface   │ Pre-determined / Same   │ Control & manage access to an  │
│              │ as Target              │ interface target        │ object without changing API.   │
├──────────────┼────────────────────────┼─────────────────────────┼────────────────────────────────┤
│ Decorator    │ EXACT SAME interface   │ Wrapped target passed   │ Dynamically add NEW behaviors/ │
│              │ as Target              │ dynamically at runtime  │ responsibilities to an object. │
├──────────────┼────────────────────────┼─────────────────────────┼────────────────────────────────┤
│ Adapter      │ DIFFERENT interface    │ Existing legacy class   │ Convert one interface into     │
│              │ from Target            │                         │ another interface expected.    │
├──────────────┼────────────────────────┼─────────────────────────┼────────────────────────────────┤
│ Facade       │ NEW simplified         │ Entire subsystem of     │ Provide a simple high-level    │
│              │ unified interface      │ multiple objects        │ interface to a complex system. │
├──────────────┼────────────────────────┼─────────────────────────┼────────────────────────────────┤
│ Flyweight    │ Shared interface       │ Shared immutable state  │ Minimize RAM usage by sharing  │
│              │ across objects         │ flyweight objects       │ intrinsic state across items.  │
└──────────────┴────────────────────────┴─────────────────────────┴────────────────────────────────┘
```

---

## 3. Deep Dive: Proxy vs. Decorator vs. Adapter

### Proxy vs. Decorator
* **Similarity**: Both wrap an object and implement the same interface.
* **Key Difference**:
  * **Decorator**: Focuses on **adding responsibilities**. The client composes decorators dynamically (e.g., `new CompressionDecorator(new EncryptionDecorator(new FileStream()))`).
  * **Proxy**: Focuses on **controlling access**. The proxy usually manages the lifecycle of its `RealSubject` internally (instantiating or referencing it directly).

### Proxy vs. Adapter
* **Similarity**: Both act as intermediaries between a client and a target object.
* **Key Difference**:
  * **Adapter**: Changes the interface of an existing object to match what the client expects (e.g., converting 3-pin plug to 2-pin plug).
  * **Proxy**: Preserves the exact same interface without modifying method signatures.

---

## 4. Advantages & Disadvantages of Proxy Pattern

### Advantages (Pros)
* **Single Responsibility Principle (SRP)**: You can introduce security, logging, caching, or lazy loading without cluttering the target object's domain code.
* **Open/Closed Principle (OCP)**: You can introduce new proxies without modifying the `RealSubject` or the `Client`.
* **Security & Governance**: Prevents unauthorized access or malicious input execution before it reaches sensitive business logic.
* **Performance Optimization**: Virtual proxies eliminate upfront resource allocation; caching proxies reduce network bandwidth.
* **Client Transparency**: Clients operate seamlessly through the interface without needing to know a proxy is intercepting calls.

### Disadvantages (Cons)
* **Increased Code Complexity**: Introduces additional classes and interfaces to the codebase.
* **Potential Latency Overhead**: Introduces a minor delegation overhead for every method call.
* **Threading Pitfalls**: Virtual proxies require careful thread synchronization (e.g., double-checked locking) to prevent double initialization in multi-threaded contexts.

---

## 5. LLD Interview Decision Tree

```text
Do you need to mediate access between a Client and an Object?
  │
  ├── YES: Do you need to change the interface method signatures?
  │     ├── YES ──► Use ADAPTER PATTERN
  │     └── NO  ──► Are you wrapping a complex subsystem of many classes into 1 call?
  │           ├── YES ──► Use FACADE PATTERN
  │           └── NO  ──► Are you adding new features/behaviors dynamically?
  │                 ├── YES ──► Use DECORATOR PATTERN
  │                 └── NO  ──► Are you controlling/managing access (lazy init, security, cache)?
  │                       └── YES ──► Use PROXY PATTERN!
  │
  └── NO: Review Creational / Behavioral Patterns.
```

---

## 6. Summary Checklist for Proxy Pattern Mastery

1. [x] **Understand the core intent**: Control access to an object via a placeholder sharing the same interface.
2. [x] **Identify the 5 core variants**: Virtual, Protection, Caching, Remote, Smart Reference.
3. [x] **Master Real-world examples**: Spring AOP, Hibernate Lazy Loading, HikariCP Connection Pooling, NGINX Reverse Proxy, OpenFeign.
4. [x] **Know when NOT to use**: Don't wrap simple lightweight objects in proxies if no lazy loading, caching, or security is required—it adds unnecessary complexity.
