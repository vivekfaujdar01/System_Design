# Module 4: How to Identify Composite Pattern & Structural Comparisons

Recognizing when to apply the Composite Pattern is a critical skill in Low-Level Design (LLD) interviews and software architecture planning.

---

## 1. Five Rules to Identify the Composite Pattern

1. **Tree-Like Part-Whole Hierarchy**: Your domain model naturally forms a tree structure (e.g., Files/Folders, Departments/Employees, UI Panels/Buttons, HTML DOM nodes).
2. **Uniform Treatment Requirement**: Client code should treat individual elements (`Leaf`) and containers (`Composite`) identically without checking their specific types (`instanceof`).
3. **Recursive Aggregation**: Operations performed on containers must aggregate results recursively from their child elements (e.g., total folder size, total department salary, total order weight).
4. **Nested Container Support**: Containers can hold both primitive leaf items and other containers at arbitrary depth levels.
5. **Eliminating Conditional Type Checks**: You notice client code cluttered with nested `if-else` or `switch` statements attempting to differentiate primitive objects from group objects.

---

## 2. Decision Tree

```text
Do you need to represent a part-whole tree structure?
                 │
                 ├──► NO  ---> No Composite Pattern needed.
                 │
                 └──► YES ---> Should clients treat leaves and containers identically?
                                 │
                                 ├──► NO  ---> Use separate classes with custom interfaces.
                                 │
                                 └──► YES ---> USE COMPOSITE PATTERN!
```

---

## 3. Design Trade-off: Transparency vs. Safety

One of the most frequently discussed topics in Composite Pattern interviews is where to declare child-management methods (`add`, `remove`, `getChild`):

```text
                  +-----------------------------------+
                  |         Design Trade-Off          |
                  +-----------------------------------+
                   /                                 \
                  /                                   \
   +------------------------------+     +------------------------------+
   |   Transparent Interface      |     |       Safe Interface         |
   | (Methods in Component Base)  |     | (Methods ONLY in Composite)  |
   +------------------------------+     +------------------------------+
```

### Option A: Transparent Design (Uniformity Priority)
Child management methods (`add()`, `remove()`) are declared in the base `Component` interface.
* **Pro**: Maximum uniformity—client can call `add()` on any `Component` reference.
* **Con**: Violates Type Safety—calling `add()` on a `Leaf` node makes no sense and must either throw an `UnsupportedOperationException` or do nothing.

### Option B: Safe Design (Type Safety Priority)
Child management methods are declared **ONLY** inside the `Composite` class.
* **Pro**: Type-safe—leaves don't inherit invalid methods (`Leaf` has no `add()` method).
* **Con**: Reduces uniformity—clients must cast a `Component` to a `Composite` before adding children.

> **Best Practice**: In Java, **Safe Design** is generally preferred unless strict polymophic transparency is mandated.

---

## 4. Structural & Behavioral Patterns Comparison

The Composite Pattern shares similarities with Decorator, Chain of Responsibility, Iterator, and Flyweight because all involve object composition or tree traversals.

### Comparison Matrix

| Pattern | Category | Primary Intent | Relationship to Children | Modifies Behavior? |
| :--- | :--- | :--- | :--- | :--- |
| **Composite** | Structural | Treats primitive leaves and composite containers uniformly | Holds **multiple** children (`List<Component>`) | **NO** (Aggregates recursively) |
| **Decorator** | Structural | Dynamically adds new behavior to an object at runtime | Holds **single** wrapped target (`Component`) | **YES** (Wraps & enhances) |
| **Chain of Resp.**| Behavioral | Passes requests along a chain of candidate handlers | Holds **single** next handler reference | **YES** (Consumes or passes) |
| **Iterator** | Behavioral | Sequentially traverses collection elements without exposing structure | Traverses collection elements | **NO** (Traverses only) |
| **Flyweight** | Structural | Shares fine-grained leaf instances to save memory | Shared immutable instances | **NO** (Memory optimization) |

---

## 5. Code Comparison: Composite vs. Decorator vs. Chain of Responsibility

### A. Composite Pattern (Tree Container with Multiple Children)
```java
class Folder implements FileComponent {
    private List<FileComponent> children = new ArrayList<>(); // Multiple children!
    public void show() {
        for (FileComponent child : children) { child.show(); } // Recursive delegation
    }
}
```

### B. Decorator Pattern (Single Target Wrapper)
```java
class CompressionDecorator implements DataSource {
    private DataSource wrappee; // Single wrapped object!
    public void writeData(String data) {
        String compressed = compress(data);
        wrappee.writeData(compressed); // Enhanced behavior!
    }
}
```

### C. Chain of Responsibility (Single Next Pointer)
```java
class LoggerHandler {
    private LoggerHandler nextHandler; // Single next link!
    public void handle(String msg) {
        if (canHandle(msg)) { log(msg); }
        else if (nextHandler != null) { nextHandler.handle(msg); }
    }
}
```

---

## 6. Mental Mnemonics & Memory Tricks

```text
+-----------+---------------------------------------------------------------+
| Pattern   | Mental Mnemonic                                               |
+-----------+---------------------------------------------------------------+
| Composite | 📁 File System Directory containing files and sub-folders     |
| Decorator | 🎁 Layering Gift Wrapping around a single coffee cup          |
| Chain     | 🔗 Customer Support Escalation (Agent ──► Manager ──► VP)     |
| Flyweight | 🔤 Reusing font characters in a text editor to save RAM       |
+-----------+---------------------------------------------------------------+
```

---

## 7. Quick Summary Checklist

* **Intent**: Compose objects into part-whole tree hierarchies and treat leaves and containers uniformly.
* **Key Components**: Component, Leaf, Composite, Client.
* **GoF Category**: Structural Pattern.
* **Core Rule**: Leverage recursion so container operations naturally delegate down the tree to individual leaf nodes.

---

> 📂 **All Runnable Code Demos**: Find organized Java projects in the [code/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Composite_pattern/code) directory.
