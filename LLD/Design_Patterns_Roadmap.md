# 🗺️ Design Patterns Roadmap

> A comprehensive guide to learning and mastering Design Patterns for Low Level Design (LLD).

---

## 📌 What Are Design Patterns?

Design patterns are **reusable solutions to commonly occurring problems** in software design. They are not ready-made code, but rather **templates or blueprints** that can be applied to solve recurring design challenges in object-oriented software.

> Originally documented by the "Gang of Four" (GoF) — Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides — in the book *"Design Patterns: Elements of Reusable Object-Oriented Software"* (1994).

---

## 🧭 Roadmap Overview

```
Design Patterns
│
├── 1. Creational Patterns       → HOW objects are created
│   ├── Singleton
│   ├── Factory Method
│   ├── Abstract Factory
│   ├── Builder
│   └── Prototype
│
├── 2. Structural Patterns       → HOW objects are composed
│   ├── Adapter
│   ├── Bridge
│   ├── Composite
│   ├── Decorator
│   ├── Facade
│   ├── Flyweight
│   └── Proxy
│
└── 3. Behavioral Patterns       → HOW objects communicate
    ├── Chain of Responsibility
    ├── Command
    ├── Iterator
    ├── Mediator
    ├── Memento
    ├── Observer
    ├── State
    ├── Strategy
    ├── Template Method
    ├── Visitor
    └── Interpreter
```

---

## 🔵 Phase 1 — Foundations (Prerequisites)

Before diving into design patterns, make sure you have a solid understanding of:

### ✅ Object-Oriented Programming (OOP)
- [ ] **Encapsulation** — hiding internal state and requiring interaction through methods
- [ ] **Abstraction** — exposing only the relevant details
- [ ] **Inheritance** — deriving behavior from a parent class
- [ ] **Polymorphism** — one interface, many implementations

### ✅ SOLID Principles
| Principle | Full Name | Core Idea |
|-----------|-----------|-----------|
| **S** | Single Responsibility | A class should have only one reason to change |
| **O** | Open/Closed | Open for extension, closed for modification |
| **L** | Liskov Substitution | Subclasses must be substitutable for their base class |
| **I** | Interface Segregation | Clients should not depend on interfaces they don't use |
| **D** | Dependency Inversion | Depend on abstractions, not on concretions |

### ✅ UML Basics
- [ ] Class diagrams
- [ ] Sequence diagrams
- [ ] Relationship types: association, aggregation, composition, dependency, inheritance

---

## 🟢 Phase 2 — Creational Patterns

> **Goal**: Control and simplify the object creation process.

---

### 1. 🔒 Singleton Pattern

**Intent**: Ensure a class has only one instance and provide a global access point to it.

**When to use**:
- Logging systems
- Configuration managers
- Database connection pools
- Thread pools

**Structure**:
```
Singleton
├── private static instance: Singleton
├── private constructor()
└── public static getInstance(): Singleton
```

**Key Concepts**:
- Lazy vs Eager initialization
- Thread safety (double-checked locking)
- Serialization issues and prevention

**Code Sketch (Java)**:
```java
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**Pitfalls**: Global state, hard to test, violates SRP if overused.

---

### 2. 🏭 Factory Method Pattern

**Intent**: Define an interface for creating an object, but let subclasses decide which class to instantiate.

**When to use**:
- When the exact type of object to create isn't known until runtime
- When subclasses should control what gets created
- Frameworks and libraries

**Structure**:
```
Creator (abstract)
└── + createProduct(): Product   ← Factory Method

ConcreteCreatorA extends Creator
└── + createProduct(): ProductA

ConcreteCreatorB extends Creator
└── + createProduct(): ProductB
```

**Real-world examples**: `LoggerFactory`, `DocumentFactory` (Word, PDF, HTML)

---

### 3. 🏗️ Abstract Factory Pattern

**Intent**: Provide an interface for creating **families of related objects** without specifying their concrete classes.

**When to use**:
- Cross-platform UI toolkits (Windows Button vs Mac Button)
- When the system must be independent of how its products are created

**Difference from Factory Method**:
| Factory Method | Abstract Factory |
|----------------|-----------------|
| Creates ONE type of product | Creates FAMILIES of products |
| Uses inheritance | Uses object composition |
| Single factory method | Multiple factory methods |

**Real-world examples**: UI component libraries (e.g., `WinFactory`, `MacFactory`)

---

### 4. 🧱 Builder Pattern

**Intent**: Construct a complex object step-by-step, separating the construction process from the representation.

**When to use**:
- Creating objects with many optional parameters
- When the construction process must allow different representations
- Avoiding telescoping constructors

**Structure**:
```
Director → uses → Builder (interface)
                     ↑
              ConcreteBuilder → builds → Product
```

**Real-world examples**: `StringBuilder`, SQL query builders, HTTP request builders

**Code Sketch (Java)**:
```java
public class Pizza {
    private String size;
    private boolean cheese;
    private boolean pepperoni;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
    }

    public static class Builder {
        private String size;
        private boolean cheese;
        private boolean pepperoni;

        public Builder(String size) { this.size = size; }
        public Builder cheese(boolean value) { cheese = value; return this; }
        public Builder pepperoni(boolean value) { pepperoni = value; return this; }
        public Pizza build() { return new Pizza(this); }
    }
}
// Usage: new Pizza.Builder("Large").cheese(true).pepperoni(false).build();
```

---

### 5. 🧬 Prototype Pattern

**Intent**: Create new objects by copying (cloning) an existing object.

**When to use**:
- When object creation is expensive (database read, complex computation)
- When you need many similar objects with minor differences
- Undo mechanisms

**Types**:
- **Shallow Copy**: Copies object references
- **Deep Copy**: Recursively copies all referenced objects

**Real-world examples**: `Object.clone()` in Java, JavaScript's spread operator, game character templates

---

## 🟡 Phase 3 — Structural Patterns

> **Goal**: Compose objects and classes into larger, more flexible structures.

---

### 6. 🔌 Adapter Pattern

**Intent**: Allow incompatible interfaces to work together by wrapping an existing class with a new interface.

**Also known as**: Wrapper

**When to use**:
- Integrating legacy code with new systems
- Using third-party libraries with incompatible interfaces

**Types**:
- **Class Adapter**: Uses multiple inheritance
- **Object Adapter**: Uses composition (preferred)

**Real-world examples**: Power plug adapters, Java's `Arrays.asList()`, `InputStreamReader`

**Structure**:
```
Client → Target (interface)
              ↑
           Adapter → wraps → Adaptee (legacy/incompatible)
```

---

### 7. 🌉 Bridge Pattern

**Intent**: Decouple an abstraction from its implementation so both can vary independently.

**When to use**:
- When you want to avoid a permanent binding between abstraction and implementation
- When both abstraction and implementation should be extensible via subclassing
- GUI frameworks, device drivers

**Structure**:
```
Abstraction ──────── Implementor (interface)
    │                      │
RefinedAbstraction    ConcreteImplementorA
                      ConcreteImplementorB
```

**Example**: Remote controls (abstraction) and devices (implementation — TV, Radio)

---

### 8. 🌲 Composite Pattern

**Intent**: Compose objects into tree structures to represent part-whole hierarchies. Lets clients treat individual objects and compositions uniformly.

**When to use**:
- File system (files and folders)
- UI component trees
- Organization hierarchies

**Structure**:
```
Component (interface)
├── Leaf (no children)
└── Composite (has children → list of Component)
```

**Key insight**: Both `Leaf` and `Composite` implement the same `Component` interface, so clients don't need to know the difference.

---

### 9. 🎨 Decorator Pattern

**Intent**: Attach additional responsibilities to an object dynamically. Provides a flexible alternative to subclassing for extending functionality.

**When to use**:
- Adding features without modifying the base class
- When subclassing leads to an explosion of classes
- Stacking behaviors

**Structure**:
```
Component (interface)
├── ConcreteComponent
└── Decorator (wraps Component)
    ├── ConcreteDecoratorA
    └── ConcreteDecoratorB
```

**Real-world examples**: Java I/O streams (`BufferedReader`, `FileInputStream`), HTTP middleware chains, coffee shop pricing

**Code Sketch**:
```java
// Base
interface Coffee { double getCost(); String getDescription(); }
class SimpleCoffee implements Coffee { ... }

// Decorator
class MilkDecorator implements Coffee {
    private Coffee coffee;
    public MilkDecorator(Coffee c) { this.coffee = c; }
    public double getCost() { return coffee.getCost() + 0.5; }
    public String getDescription() { return coffee.getDescription() + ", Milk"; }
}
```

---

### 10. 🏛️ Facade Pattern

**Intent**: Provide a simplified interface to a complex subsystem.

**When to use**:
- Simplifying complex library/framework interactions
- Layered architecture (each layer is a facade for the layer below)
- Legacy system wrapping

**Structure**:
```
Client → Facade → Subsystem A
                → Subsystem B
                → Subsystem C
```

**Real-world examples**: Home theater system controller, bank `AccountService` facade, `slf4j` logging facade

---

### 11. 🪶 Flyweight Pattern

**Intent**: Use sharing to efficiently support a large number of fine-grained objects.

**When to use**:
- When a large number of similar objects consume too much memory
- Game development (particles, trees, bullets)
- Text editors (character rendering)

**Key Concept**:
- **Intrinsic state**: Shared, stored in flyweight (e.g., character shape)
- **Extrinsic state**: Context-dependent, passed by client (e.g., character position)

**Real-world examples**: Java's `String` pool, `Integer.valueOf()` caching (-128 to 127)

---

### 12. 🛡️ Proxy Pattern

**Intent**: Provide a surrogate or placeholder for another object to control access to it.

**Types**:
| Proxy Type | Purpose |
|------------|---------|
| **Virtual Proxy** | Lazy initialization (load heavy object on demand) |
| **Protection Proxy** | Access control |
| **Remote Proxy** | Represents object in a different address space |
| **Caching Proxy** | Cache results of expensive operations |
| **Logging Proxy** | Log requests before passing to real object |

**Real-world examples**: Spring AOP, Java RMI, lazy-loaded ORM entities, CDN (content caching proxy)

---

## 🔴 Phase 4 — Behavioral Patterns

> **Goal**: Define how objects interact and distribute responsibility among them.

---

### 13. ⛓️ Chain of Responsibility

**Intent**: Pass a request along a chain of handlers; each handler either processes it or passes it to the next one.

**When to use**:
- Middleware pipelines
- Event handling systems
- Approval workflows (Manager → Director → VP)

**Structure**:
```
Handler (abstract)
├── + setNext(Handler)
├── + handle(Request)
└── ConcreteHandlerA → ConcreteHandlerB → ConcreteHandlerC
```

**Real-world examples**: Java servlet filters, Express.js middleware, logging level handlers

---

### 14. 🎮 Command Pattern

**Intent**: Encapsulate a request as an object, thereby allowing parameterization, queuing, logging, and undo/redo operations.

**When to use**:
- Undo/redo functionality
- Task queues and job schedulers
- Macro recording
- Transactional operations

**Structure**:
```
Invoker → Command (interface)
               ↑
          ConcreteCommand → Receiver
```

**Real-world examples**: GUI buttons, database transactions, `java.lang.Runnable`

---

### 15. 🔁 Iterator Pattern

**Intent**: Provide a way to sequentially access elements of a collection without exposing its internal representation.

**When to use**:
- Traversing different types of collections uniformly
- Multiple simultaneous traversals

**Real-world examples**: Java's `Iterator`, Python's `__iter__`, `for-each` loops

---

### 16. 🤝 Mediator Pattern

**Intent**: Define an object that encapsulates how a set of objects interact. Promotes loose coupling by preventing direct references between objects.

**When to use**:
- Chat rooms / messaging systems
- Air traffic control systems
- UI form interactions

**Structure**:
```
Mediator (interface)
    ↑
ConcreteMediator ←→ ColleagueA
                ←→ ColleagueB
```

**Difference from Observer**: Mediator is centralized two-way communication; Observer is one-to-many notification.

---

### 17. 💾 Memento Pattern

**Intent**: Capture and externalize an object's internal state so it can be restored later, without violating encapsulation.

**When to use**:
- Undo/redo systems
- Snapshots and checkpoints
- Transaction rollback

**Participants**:
- **Originator**: The object whose state is saved
- **Memento**: Stores the snapshot
- **Caretaker**: Keeps track of mementos (doesn't inspect them)

**Real-world examples**: Text editors, game save states, version control snapshots

---

### 18. 👁️ Observer Pattern

**Intent**: Define a one-to-many dependency so that when one object changes state, all its dependents are notified and updated automatically.

**Also known as**: Publish-Subscribe, Event Listener

**When to use**:
- Event handling systems
- MVC architecture (Model notifies View)
- Real-time data feeds (stocks, weather)
- Notification systems

**Structure**:
```
Subject (Observable)
├── + attach(Observer)
├── + detach(Observer)
└── + notify()

Observer (interface)
└── + update()
```

**Code Sketch (Java)**:
```java
interface Observer { void update(String event); }

class EventManager {
    Map<String, List<Observer>> listeners = new HashMap<>();

    public void subscribe(String type, Observer listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public void notify(String type, String data) {
        List<Observer> users = listeners.getOrDefault(type, Collections.emptyList());
        for (Observer listener : users) listener.update(data);
    }
}
```

**Real-world examples**: Java's `EventListener`, Android `LiveData`, RxJava, GUI event systems

---

### 19. 🔄 State Pattern

**Intent**: Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

**When to use**:
- Objects that behave differently based on current state
- Eliminating large `if-else` or `switch` chains
- Vending machines, traffic lights, order management

**Structure**:
```
Context → State (interface)
               ↑
        ConcreteStateA
        ConcreteStateB
        ConcreteStateC
```

**Difference from Strategy**: State allows states to be aware of each other and trigger transitions; Strategy patterns are independent.

---

### 20. 🎯 Strategy Pattern

**Intent**: Define a family of algorithms, encapsulate each one, and make them interchangeable. Let the algorithm vary independently from clients that use it.

**When to use**:
- Multiple sorting algorithms
- Payment processing (Credit Card, PayPal, UPI)
- Compression algorithms (zip, rar, 7z)
- Navigation routing (fastest, shortest, avoid tolls)

**Structure**:
```
Context → Strategy (interface)
               ↑
        ConcreteStrategyA
        ConcreteStrategyB
```

**Code Sketch**:
```java
interface SortStrategy { void sort(int[] data); }

class BubbleSort implements SortStrategy { ... }
class QuickSort implements SortStrategy { ... }

class Sorter {
    private SortStrategy strategy;
    public Sorter(SortStrategy s) { this.strategy = s; }
    public void sort(int[] data) { strategy.sort(data); }
}
```

---

### 21. 📋 Template Method Pattern

**Intent**: Define the skeleton of an algorithm in a base class, deferring some steps to subclasses.

**When to use**:
- When steps of an algorithm are fixed but implementation varies
- Avoiding code duplication across subclasses
- Data parsing pipelines (open → parse → process → close)

**Structure**:
```
AbstractClass
├── + templateMethod()   ← calls primitive operations in order
├── + primitiveOp1()     ← abstract, implemented by subclass
└── + primitiveOp2()     ← abstract, implemented by subclass

ConcreteClass extends AbstractClass
├── + primitiveOp1()
└── + primitiveOp2()
```

**Difference from Strategy**: Template Method uses inheritance; Strategy uses composition.

---

### 22. 🚶 Visitor Pattern

**Intent**: Let you add further operations to objects without modifying them. Separates an algorithm from the object structure it operates on.

**When to use**:
- Adding operations to a stable object structure (AST nodes, DOM elements)
- When you need many distinct, unrelated operations on an object hierarchy
- Compiler design (type checking, code generation on AST)

**Structure**:
```
Visitor (interface)
├── + visitConcreteElementA(ConcreteElementA)
└── + visitConcreteElementB(ConcreteElementB)

Element (interface)
└── + accept(Visitor)
```

**Key insight**: Double dispatch — the operation depends on both the Visitor type and the Element type.

---

### 23. 🗣️ Interpreter Pattern

**Intent**: Define a grammatical representation for a language and provide an interpreter to deal with that grammar.

**When to use**:
- Building scripting engines or mini-languages
- SQL parsing
- Regular expressions
- Mathematical expression evaluators

**Structure**:
```
AbstractExpression
├── TerminalExpression     (leaf nodes)
└── NonTerminalExpression  (composite nodes)
```

---

## 📊 Pattern Comparison Table

| Pattern | Category | Problem Solved |
|--------|----------|----------------|
| Singleton | Creational | One instance only |
| Factory Method | Creational | Defer instantiation to subclass |
| Abstract Factory | Creational | Create families of objects |
| Builder | Creational | Step-by-step complex object creation |
| Prototype | Creational | Clone existing objects |
| Adapter | Structural | Convert incompatible interfaces |
| Bridge | Structural | Separate abstraction from implementation |
| Composite | Structural | Tree structures (part-whole hierarchy) |
| Decorator | Structural | Add behavior dynamically |
| Facade | Structural | Simplify complex system interface |
| Flyweight | Structural | Share fine-grained objects efficiently |
| Proxy | Structural | Control access to an object |
| Chain of Responsibility | Behavioral | Pass request along handler chain |
| Command | Behavioral | Encapsulate request as object |
| Iterator | Behavioral | Traverse collections uniformly |
| Mediator | Behavioral | Centralize complex communications |
| Memento | Behavioral | Capture and restore object state |
| Observer | Behavioral | Notify dependents of state change |
| State | Behavioral | Change behavior based on state |
| Strategy | Behavioral | Swap algorithms at runtime |
| Template Method | Behavioral | Define algorithm skeleton in base class |
| Visitor | Behavioral | Add operations without modifying classes |
| Interpreter | Behavioral | Evaluate sentences in a language |

---

## 🛠️ Phase 5 — Practical Application

### LLD Problems and Patterns Used

| LLD Problem | Primary Patterns |
|-------------|-----------------|
| Parking Lot | Strategy, Factory, Singleton |
| Elevator System | State, Strategy |
| Library Management | Factory, Observer |
| ATM Machine | State, Command |
| Chess Game | Factory, Observer, Command |
| Hotel Booking | Strategy, Observer, Builder |
| Food Delivery App | Observer, Strategy, Facade |
| Amazon Shopping Cart | Strategy, Decorator, Builder |
| Notification System | Observer, Strategy, Factory |
| Logger System | Singleton, Chain of Responsibility |
| Cache (LRU/LFU) | Strategy, Proxy |
| Ride Sharing (Uber/Lyft) | Strategy, Observer, Factory |
| Online Exam Platform | State, Observer, Command |
| Task Manager (Jira) | State, Observer, Command |
| Social Media Feed | Observer, Decorator, Iterator |

---

## 📚 Phase 6 — Anti-Patterns to Avoid

| Anti-Pattern | Description | Better Alternative |
|-------------|-------------|-------------------|
| **God Object** | One class does everything | Single Responsibility Principle |
| **Spaghetti Code** | Tangled, unstructured code | Proper design patterns |
| **Golden Hammer** | Using one pattern for everything | Choose the right tool |
| **Premature Optimization** | Optimizing before profiling | Profile first, optimize later |
| **Copy-Paste Programming** | Duplicating code instead of reusing | Template Method, Strategy |
| **Magic Numbers** | Hardcoded values everywhere | Constants, configuration |
| **Singleton Abuse** | Overusing Singleton | Dependency Injection |
| **Anemic Domain Model** | Classes with no behavior | Rich domain model |

---

## 🎯 Learning Roadmap (Timeline)

```
Week 1-2: Foundations
  ├── OOP Concepts
  ├── SOLID Principles
  └── UML Basics

Week 3-4: Creational Patterns
  ├── Singleton
  ├── Factory Method
  ├── Abstract Factory
  ├── Builder
  └── Prototype

Week 5-6: Structural Patterns
  ├── Adapter
  ├── Decorator
  ├── Facade
  ├── Proxy
  └── Composite

Week 7-8: Behavioral Patterns (Part 1)
  ├── Observer
  ├── Strategy
  ├── Command
  └── State

Week 9-10: Behavioral Patterns (Part 2)
  ├── Chain of Responsibility
  ├── Template Method
  ├── Iterator
  ├── Mediator
  └── Visitor

Week 11-12: Applied Practice
  ├── LLD Problem Solving
  ├── System Design Interviews
  └── Code Reviews + Refactoring
```

---

## 📖 Resources

### Books
- 📘 *Design Patterns: Elements of Reusable Object-Oriented Software* — Gang of Four (GoF)
- 📗 *Head First Design Patterns* — Freeman & Robson (beginner-friendly)
- 📙 *Clean Code* — Robert C. Martin
- 📕 *Refactoring* — Martin Fowler

### Online Resources
- 🌐 [Refactoring.Guru](https://refactoring.guru/design-patterns) — Best visual explanations
- 🌐 [SourceMaking](https://sourcemaking.com/design_patterns) — Detailed examples
- 🌐 [GeeksForGeeks — Design Patterns](https://www.geeksforgeeks.org/software-design-patterns/)

### Practice Platforms
- LeetCode (OOP Design problems)
- GitHub — Open source codebases to study patterns in production
- InterviewBit / Educative.io (System Design / LLD tracks)

---

## ✅ Checklist — Design Pattern Mastery

For each pattern, track your progress:

### Creational
- [ ] Singleton — understand + implement + pitfalls
- [ ] Factory Method — understand + implement + pitfalls
- [ ] Abstract Factory — understand + implement + pitfalls
- [ ] Builder — understand + implement + pitfalls
- [ ] Prototype — understand + implement + pitfalls

### Structural
- [ ] Adapter — understand + implement + pitfalls
- [ ] Bridge — understand + implement + pitfalls
- [ ] Composite — understand + implement + pitfalls
- [ ] Decorator — understand + implement + pitfalls
- [ ] Facade — understand + implement + pitfalls
- [ ] Flyweight — understand + implement + pitfalls
- [ ] Proxy — understand + implement + pitfalls

### Behavioral
- [ ] Chain of Responsibility — understand + implement + pitfalls
- [ ] Command — understand + implement + pitfalls
- [ ] Iterator — understand + implement + pitfalls
- [ ] Mediator — understand + implement + pitfalls
- [ ] Memento — understand + implement + pitfalls
- [ ] Observer — understand + implement + pitfalls
- [ ] State — understand + implement + pitfalls
- [ ] Strategy — understand + implement + pitfalls
- [ ] Template Method — understand + implement + pitfalls
- [ ] Visitor — understand + implement + pitfalls
- [ ] Interpreter — understand + implement + pitfalls

---

*Last Updated: July 2026 | Author: System Design Repository*
