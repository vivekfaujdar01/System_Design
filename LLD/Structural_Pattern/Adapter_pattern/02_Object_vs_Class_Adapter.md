# Module 2: Object Adapter vs Class Adapter

The Adapter Pattern can be implemented in two distinct ways depending on the relationship between the **Adapter** and the **Adaptee**:

1. **Object Adapter** (Uses **Composition** — `HAS-A` relationship)
2. **Class Adapter** (Uses **Inheritance** — `IS-A` relationship)

---

## 1. Object Adapter (Composition-Based)

In an **Object Adapter**, the adapter holds an instance reference of the Adaptee class inside it (Composition). It implements the `Target` interface and delegates requests to the encapsulated instance.

### Class Diagram (Object Adapter)
```
+--------------------+            +-------------------+
| <<interface>>      |            |      Adaptee      |
| Target             |            +-------------------+
+--------------------+            | + specificReq()   |
| + request()        |            +-------------------+
+--------------------+                      ^
          ^                                 |
          | implements                      | wraps (has-a)
+--------------------+                      |
|   ObjectAdapter    | ---------------------+
+--------------------+
| - adaptee: Adaptee |
| + request()        |
+--------------------+
```

### Complete Java Implementation (Object Adapter)

```java
// Step 1: Target Interface expected by Client
public interface SocketPlug {
    void provideElectricity();
}

// Step 2: Adaptee (Existing class with incompatible interface plugInTypeD)
public class IndianSocket {
    public void plugInTypeD() {
        System.out.println("Providing electricity via Indian Type-D socket.");
    }
}

// Step 3: Object Adapter (Uses Composition - HAS-A relationship)
public class ObjectSocketAdapter implements SocketPlug {
    private final IndianSocket indianSocket;

    public ObjectSocketAdapter(IndianSocket indianSocket) {
        this.indianSocket = indianSocket;
    }

    @Override
    public void provideElectricity() {
        // Delegates call to wrapped Adaptee instance
        indianSocket.plugInTypeD();
    }
}
```

---

## 2. Class Adapter (Inheritance-Based)

In a **Class Adapter**, the adapter extends the Adaptee class (Inheritance) and implements the `Target` interface. It overrides the target interface methods and calls the inherited parent methods directly.

### Class Diagram (Class Adapter)
```
+--------------------+            +-------------------+
| <<interface>>      |            |      Adaptee      |
| Target             |            +-------------------+
+--------------------+            | + specificReq()   |
| + request()        |            +-------------------+
+--------------------+                      ^
          ^                                 |
          | implements                      | extends (is-a)
          +---------------+-----------------+
                          |
                 +-----------------+
                 |  ClassAdapter   |
                 +-----------------+
                 | + request()     |
                 +-----------------+
```

### Complete Java Implementation (Class Adapter)

```java
// Step 1: Target Interface
public interface SocketPlug {
    void provideElectricity();
}

// Step 2: Adaptee
public class IndianSocket {
    public void plugInTypeD() {
        System.out.println("Providing electricity via Indian Type-D socket.");
    }
}

// Step 3: Class Adapter (Uses Inheritance - IS-A relationship)
public class ClassSocketAdapter extends IndianSocket implements SocketPlug {
    @Override
    public void provideElectricity() {
        // Inherited method call directly from parent IndianSocket class
        plugInTypeD();
    }
}
```

---

## 3. Main Demonstration & Execution Code

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("--- Object Adapter (Composition) ---");
        // Object Adapter holds an instance of IndianSocket internally
        SocketPlug objectAdapter = new ObjectSocketAdapter(new IndianSocket());
        objectAdapter.provideElectricity();

        System.out.println("\n--- Class Adapter (Inheritance) ---");
        // Class Adapter inherits directly from IndianSocket
        SocketPlug classAdapter = new ClassSocketAdapter();
        classAdapter.provideElectricity();
    }
}
```

### Execution Output
```text
--- Object Adapter (Composition) ---
Providing electricity via Indian Type-D socket.

--- Class Adapter (Inheritance) ---
Providing electricity via Indian Type-D socket.
```

---

## 4. Program Flow Analysis

### Object Adapter Control Flow
```
main()
  └──> new ObjectSocketAdapter(new IndianSocket())
  └──> objectAdapter.provideElectricity() [Target Interface]
         └──> ObjectSocketAdapter.provideElectricity() [Adapter]
                └──> indianSocket.plugInTypeD() [Adaptee Instance]
                       └──> Prints Output
```

### Class Adapter Control Flow
```
main()
  └──> new ClassSocketAdapter()
  └──> classAdapter.provideElectricity() [Target Interface]
         └──> ClassSocketAdapter.provideElectricity() [Adapter]
                └──> this.plugInTypeD() [Inherited Parent Method]
                       └──> Prints Output
```

---

## 5. Detailed Comparison: Object Adapter vs Class Adapter

| Feature | Object Adapter | Class Adapter |
| :--- | :--- | :--- |
| **Primary Mechanism** | Composition (**HAS-A**) | Inheritance (**IS-A**) |
| **Language Support** | Supported in all OOP languages (Java, C++, C#, Python) | Requires multiple inheritance or interfaces + single inheritance |
| **Flexibility** | High (can adapt Adaptee and any of its subclasses) | Low (bound to a single specific Adaptee class) |
| **Coupling** | Loose coupling with Adaptee | Tight coupling with Adaptee |
| **Override Behavior** | Cannot override Adaptee methods directly | Can override Adaptee methods if needed |
| **Efficiency** | Negligible indirection cost (one pointer dereference) | Slightly faster (direct method call via inheritance) |

---

## 6. Why Java Primarily Uses the Object Adapter

Java developers almost exclusively use the **Object Adapter Pattern** for several key architectural reasons:

1. **No Multiple Class Inheritance in Java**: Java does not allow a class to extend more than one class (`extends ClassA, ClassB` is illegal). If the `Target` is a class rather than an interface, a Class Adapter cannot be implemented in Java.
2. **Favor Composition Over Inheritance**: This is a fundamental design principle. Composition provides greater flexibility at runtime and prevents fragile base class issues.
3. **Subclass Compatibility**: An Object Adapter can adapt not only the `Adaptee` class, but also any of `Adaptee`'s subclasses seamlessly.
4. **Polymorphic Adaptation**: Object Adapters allow swapping out different adaptee instances dynamically at runtime.

---

> 📂 **Source Code Location**: The standalone Java code files for this module can be found in [code/02_Object_vs_Class_Adapter/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Adapter_pattern/code/02_Object_vs_Class_Adapter).
