# Module 1: Adapter Design Pattern - Introduction

## 1. Real-Life Analogy

### Analogy 1: Indian Charger in Europe
Imagine you have:
* **Your Indian Phone Charger**: Type D plug.
* **A European Socket**: Type C / F socket.

Both the charger and socket work perfectly in their respective environments, but they cannot connect directly due to incompatible interfaces.

```
[Indian Charger]  --->  [Adapter]  --->  [European Socket]
```

The adapter:
* Does **not** modify the charger.
* Does **not** modify the socket.
* Simply **converts** one interface into another so they can work together seamlessly.

### Analogy 2: Laptop and Pendrive
* **Laptop**: USB-C Port
* **Pendrive**: USB-A Port
* **Solution**: USB-C to USB-A Adapter. Neither device is altered; the adapter bridges the interface gap.

---

## 2. The Software Problem

Suppose your application expects a method:
```java
pay(double amount)
```

However, a newly integrated third-party payment library provides:
```java
makePayment(double value)
```

Different method names, different interfaces. Your application cannot directly use it without modifying existing code. Rather than altering third-party code or breaking existing caller logic, we insert an **Adapter**.

```
+-------------------+       +-----------------------+       +---------------------+
|    Application    | ----> |    Payment Adapter    | ----> | Third Party Library |
|  (expects pay())  |       | (implements Payment)  |       | (has makePayment()) |
+-------------------+       +-----------------------+       +---------------------+
```

---

## 3. Core Definition & Intent

> **Definition**: The **Adapter Design Pattern** converts the interface of a class into another interface that clients expect. It enables classes with incompatible interfaces to work together without modifying their source code.

* **Intent**: Compatibility, interoperability, and loose coupling.
* **Category**: Structural Design Pattern.

---

## 4. Components of Adapter Pattern

```
  +------------------+
  |      Client      |
  +------------------+
           |
           v
  +------------------+
  | Target Interface |
  +------------------+
           ^
           | implements
  +------------------+       uses / wraps      +------------------+
  |     Adapter      | ----------------------> |     Adaptee      |
  +------------------+                         +------------------+
```

1. **Client**: The code that needs functionality and programs against the `Target` interface.
2. **Target**: The interface expected by the client.
3. **Adaptee**: The existing class with useful functionality but an incompatible interface.
4. **Adapter**: The wrapper class that implements the `Target` interface and delegates requests to the `Adaptee`.

---

## 5. Complete Java Code Example: Pen & Assignment

Here is the step-by-step code flow for adapting a `PilotPen` (which has a `mark()` method) to meet the `Pen` interface expected by `Assignment` (which expects a `write()` method).

### Step 1: Target Interface (`Pen.java`)
```java
// Target interface expected by the Client
public interface Pen {
    void write();
}
```

### Step 2: Adaptee (`PilotPen.java`)
```java
// Existing class with an incompatible interface (mark instead of write)
public class PilotPen {
    public void mark() {
        System.out.println("Writing using Pilot Pen...");
    }
}
```

### Step 3: Adapter Class (`PenAdapter.java`)
```java
// Bridges Target (Pen) and Adaptee (PilotPen)
public class PenAdapter implements Pen {
    private final PilotPen pilotPen;

    public PenAdapter() {
        this.pilotPen = new PilotPen();
    }

    public PenAdapter(PilotPen pilotPen) {
        this.pilotPen = pilotPen;
    }

    @Override
    public void write() {
        // Delegates call to Adaptee's method
        pilotPen.mark();
    }
}
```

### Step 4: Client Class (`Assignment.java`)
```java
// Client class that depends ONLY on Target interface (Pen)
public class Assignment {
    private final Pen pen;

    public Assignment(Pen pen) {
        this.pen = pen;
    }

    public void doAssignment() {
        pen.write();
    }
}
```

### Step 5: Main Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Create adapter that wraps Adaptee
        Pen pen = new PenAdapter();

        // Pass Target interface to Client
        Assignment assignment = new Assignment(pen);

        // Client calls write(), which delegates to PilotPen.mark()
        assignment.doAssignment();
    }
}
```

### Output
```text
Writing using Pilot Pen...
```

---

## 6. Flow of Execution

```
main() 
  │
  ├──> Creates PenAdapter (holding PilotPen instance)
  ├──> Instantiates Assignment with PenAdapter
  └──> Assignment.doAssignment()
         │
         └──> Pen.write() [Target]
                │
                └──> PenAdapter.write() [Adapter]
                       │
                       └──> PilotPen.mark() [Adaptee]
                              │
                              └──> Output Printed: "Writing using Pilot Pen..."
```

---

## 7. Key Advantages & Disadvantages

### Advantages
1. **Reusability**: Allows legacy or third-party classes to be reused without modifying their source code.
2. **Single Responsibility Principle (SRP)**: Separates interface/data conversion logic from primary business logic.
3. **Open/Closed Principle (OCP)**: Introduces new adapters into the system without breaking existing client code.
4. **Loose Coupling**: The client remains decoupled from specific adaptee implementations.

### Disadvantages
1. **Increased Complexity**: Adds extra classes and interfaces (indirection layer) to the codebase.
2. **Indirection Cost**: Slight runtime overhead due to extra method delegation calls.

---

> 📂 **Source Code Location**: The individual standalone Java code files for this module can be found in [code/01_Pen_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Adapter_pattern/code/01_Pen_Example).
