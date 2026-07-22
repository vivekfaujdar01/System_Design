# Module 10 – Builder vs Factory Method vs Abstract Factory

> **Study order:** Read after `09_Builder_vs_Factory.md`.  
> This module gives a three-way comparison — a very common interview topic.

---

## Table of Contents
1. [Quick Recap of Each Pattern](#1-quick-recap-of-each-pattern)
2. [The Three Core Questions](#2-the-three-core-questions)
3. [Structural Comparison (UML Summary)](#3-structural-comparison-uml-summary)
4. [Side-by-Side Code – Same Domain](#4-side-by-side-code--same-domain)
5. [Comparison Table](#5-comparison-table)
6. [SOLID Compliance](#6-solid-compliance)
7. [Pattern Evolution Path](#7-pattern-evolution-path)
8. [Decision Flowchart](#8-decision-flowchart)
9. [Interview Summary](#9-interview-summary)

---

## 1. Quick Recap of Each Pattern

### Simple Factory (Idiom — Not GoF)
> "One class with a static method that creates different objects based on a parameter."

```java
VehicleFactory.create("car")   // → Car
VehicleFactory.create("truck") // → Truck
```

### Factory Method (GoF)
> "Define an interface for creating an object, but let subclasses decide which class to instantiate."

```java
// Abstract creator with abstract factory method
abstract class NotificationService {
    abstract Notification createNotification();   // ← factory method
    void notify(String msg) { createNotification().send(msg); }
}
class EmailService extends NotificationService {
    Notification createNotification() { return new EmailNotification(); }
}
```

### Abstract Factory (GoF)
> "Provide an interface for creating FAMILIES of related objects."

```java
// One factory for an entire product family
interface GUIFactory {
    Button   createButton();
    Checkbox createCheckbox();
    TextBox  createTextBox();
}
class WindowsFactory implements GUIFactory { ... }
class MacFactory     implements GUIFactory { ... }
```

### Builder (GoF)
> "Separate the construction of a complex object from its representation."

```java
// One type, many configurations
Pizza pizza = new Pizza.Builder("Large")
    .cheese(true).pepperoni(true).extraSauce(false)
    .build();
```

---

## 2. The Three Core Questions

| Question                                 | Simple Factory | Factory Method | Abstract Factory | Builder  |
|------------------------------------------|:--------------:|:--------------:|:----------------:|:--------:|
| Creates different **types**?             | ✅             | ✅             | ✅               | ❌       |
| Creates **families** of related objects? | ❌             | ❌             | ✅               | ❌       |
| Configures a **single complex object**?  | ❌             | ❌             | ❌               | ✅       |

---

## 3. Structural Comparison (UML Summary)

### Simple Factory
```
Client ──► StaticFactory.create("type") ──► ConcreteProduct
                   (switch / if-else)
```

### Factory Method
```
Client ──► AbstractCreator.operation()
                │
                └──► this.factoryMethod()   ← overridden
                              │
                     ConcreteProduct
```

### Abstract Factory
```
Client ──► AbstractFactory (interface)
                ├── createProductA() ──► AbstractProductA
                └── createProductB() ──► AbstractProductB
                         ↑
               ConcreteFactoryX or ConcreteFactoryY
```

### Builder
```
Client ──► Product.Builder(mandatory)
                .optionalField1(val)
                .optionalField2(val)
                .build()
                    │
            validates + returns
                    │
             Immutable Product
```

---

## 4. Side-by-Side Code – Same Domain (Document Creation)

We'll create a `Document` with a title, body, author, and footer.

### Simple Factory
```java
// Factory decides which type of document to create
public class DocumentFactory {
    public static Document create(String type) {
        switch (type) {
            case "invoice":  return new InvoiceDocument();
            case "report":   return new ReportDocument();
            case "contract": return new ContractDocument();
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }
}

// Client gets DIFFERENT DOCUMENT TYPES
Document doc = DocumentFactory.create("invoice");
```

### Factory Method
```java
// Abstract creator with factory method — subclass decides product type
public abstract class DocumentProcessor {
    public abstract Document createDocument();   // ← factory method

    public void process() {
        Document doc = createDocument();
        doc.generate();
        doc.print();
    }
}

public class InvoiceProcessor extends DocumentProcessor {
    public Document createDocument() { return new InvoiceDocument(); }
}

// Client works with DocumentProcessor abstraction
DocumentProcessor processor = new InvoiceProcessor();
processor.process();   // creates and processes an InvoiceDocument
```

### Abstract Factory
```java
// Family of related document components
public interface DocumentFactory {
    Header  createHeader();
    Body    createBody();
    Footer  createFooter();
}

public class FormalDocumentFactory implements DocumentFactory {
    public Header createHeader() { return new FormalHeader(); }
    public Body   createBody()   { return new FormalBody(); }
    public Footer createFooter() { return new FormalFooter(); }
}

public class CasualDocumentFactory implements DocumentFactory {
    public Header createHeader() { return new CasualHeader(); }
    public Body   createBody()   { return new CasualBody(); }
    public Footer createFooter() { return new CasualFooter(); }
}

// Client works with whole family
DocumentFactory factory = new FormalDocumentFactory();
Header  h = factory.createHeader();   // all components match "Formal" family
Body    b = factory.createBody();
Footer  f = factory.createFooter();
```

### Builder
```java
// ONE document type, configured differently each time
public final class Document {
    private final String title;
    private final String body;
    private final String author;
    private final String footer;
    private final boolean isConfidential;

    private Document(Builder b) {
        this.title          = b.title;
        this.body           = b.body;
        this.author         = b.author;
        this.footer         = b.footer;
        this.isConfidential = b.isConfidential;
    }

    public static class Builder {
        private final String title;
        private String  body           = "";
        private String  author         = "Anonymous";
        private String  footer         = "";
        private boolean isConfidential = false;

        public Builder(String title)           { this.title = title; }
        public Builder body(String b)          { this.body = b; return this; }
        public Builder author(String a)        { this.author = a; return this; }
        public Builder footer(String f)        { this.footer = f; return this; }
        public Builder confidential(boolean c) { this.isConfidential = c; return this; }
        public Document build()                { return new Document(this); }
    }
}

// Client configures the SAME Document type differently each time
Document invoice = new Document.Builder("Invoice #1001")
    .author("Billing Team")
    .body("Total: ₹50,000")
    .confidential(false)
    .build();

Document contract = new Document.Builder("Service Agreement")
    .author("Legal Dept")
    .body("Terms and conditions...")
    .footer("Signed and sealed")
    .confidential(true)
    .build();
```

---

## 5. Comparison Table

| Criterion                    | Simple Factory     | Factory Method     | Abstract Factory   | Builder              |
|------------------------------|--------------------|--------------------|--------------------|----------------------|
| **GoF Pattern?**             | ❌ No (idiom)       | ✅ Yes              | ✅ Yes              | ✅ Yes                |
| **Creates different types?** | ✅ Yes              | ✅ Yes              | ✅ Yes (families)   | ❌ Same type          |
| **Optional fields support**  | ❌ Poor             | ❌ Poor             | ❌ Poor             | ✅ Excellent          |
| **Produces immutable obj?**  | 🟡 Depends         | 🟡 Depends         | 🟡 Depends         | ✅ Yes (by design)    |
| **Validation in one place?** | 🟡 In factory      | 🟡 In subclass     | 🟡 In factory      | ✅ In build()         |
| **OCP compliance**           | ❌ Fails            | ✅ Yes              | ✅ Yes              | ✅ Yes                |
| **Complexity**               | 🟢 Low             | 🟡 Medium          | 🔴 High            | 🟡 Medium             |
| **Boilerplate**              | Minimal            | Moderate           | High               | Moderate              |
| **Java keyword**             | `static` method    | `abstract` method  | `interface`        | static nested class   |
| **Family consistency**       | ❌                  | ❌                  | ✅ Enforced         | ❌                    |
| **Multi-step construction**  | ❌                  | ❌                  | ❌                  | ✅                    |
| **Best for**                 | Simple type switch | Subclass-controlled| Related products   | Complex single object |

---

## 6. SOLID Compliance

| SOLID Principle       | Simple Factory | Factory Method | Abstract Factory | Builder  |
|-----------------------|:--------------:|:--------------:|:----------------:|:--------:|
| **S** – Single Resp.  | 🟡 Partial     | ✅             | ✅               | ✅       |
| **O** – Open/Closed   | ❌ Violates    | ✅             | ✅               | ✅       |
| **L** – Liskov Subst. | ✅             | ✅             | ✅               | ✅       |
| **I** – ISP           | ✅             | ✅             | ⚠️ Large iface   | ✅       |
| **D** – Dep. Inversion| ❌ Partial     | ✅             | ✅               | ✅       |

---

## 7. Pattern Evolution Path

Systems naturally evolve through these patterns as complexity grows:

```
1. Direct instantiation (new Product())
        │ Problem: scattered 'new', hard to change
        ▼
2. Simple Factory (centralized creation)
        │ Problem: OCP violation — edit factory for each new type
        ▼
3. Factory Method (inheritance-based)
        │ Problem: only creates ONE type per factory
        ▼
4. Abstract Factory (creates FAMILIES)
        │ Problem: doesn't handle complex single-object configuration
        ▼
5. Builder (configures one complex object, step-by-step)
```

In practice, a mature system uses **all of these** together:
```
Abstract Factory creates product families.
Factory Method lets subclasses decide one product type.
Builder assembles one complex product.
Simple Factory handles lookup/registry of subtypes.
```

---

## 8. Decision Flowchart

```
START: I need to create an object
            │
            ▼
  Do I need multiple DIFFERENT TYPES
  of objects?
            │
    YES─────┴─────NO
     │                  │
     ▼                  ▼
  Do I need a         Does the object have
  FAMILY of           MANY optional fields
  related types?      OR need step-by-step
     │                  construction?
  YES──NO            YES─────────NO
   │     │            │           │
   ▼     ▼            ▼           ▼
Abstract Factory    BUILDER    Use a simple
                              constructor or
           │                  static factory
  Do subclasses control
  which type is created?
           │
         YES──NO
          │     │
          ▼     ▼
     Factory  Simple
     Method   Factory
```

---

## 9. Interview Summary

**Q: Explain the difference between Simple Factory, Factory Method, Abstract Factory, and Builder.**

> - **Simple Factory** is not a GoF pattern. It's a helper class with a `static` method that uses `switch/if` to choose which concrete class to instantiate. Simple but violates OCP.
>
> - **Factory Method** is a GoF pattern using inheritance. An abstract creator class defines a `factoryMethod()` that subclasses override to return a specific product. Extensible without modifying existing code.
>
> - **Abstract Factory** is a GoF pattern for creating **families of related objects**. A factory interface declares methods for each product type in the family. Different concrete factories implement the interface to produce consistent product families (e.g., Windows vs Mac UI components).
>
> - **Builder** is a GoF pattern for constructing **one complex object** step-by-step. Client calls optional setter methods on a `Builder` inner class, then calls `build()` to get a validated, often immutable `Product`.

**Q: What's the key difference between Abstract Factory and Builder?**

> Abstract Factory creates **multiple related product types** (families). Builder creates **one product type** with many optional configurations. Abstract Factory answers "which family?". Builder answers "how configured?".

**Q: When would you choose Builder over Factory Method?**

> Choose Builder when the object has many optional fields and you want a readable, named-parameter style API. Choose Factory Method when you want subclasses to control which type of object is created, and the object itself is not complex to configure.

---

**← Prev:** [`09_Builder_vs_Factory.md`](./09_Builder_vs_Factory.md)  
**Next →** [`11_Builder_in_Java_Libraries.md`](./11_Builder_in_Java_Libraries.md)
