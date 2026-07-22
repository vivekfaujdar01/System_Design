# Module 04 – Static Nested Builder (Most Common Java Style)

> **Study order:** Read after `03_First_Builder_Implementation.md`.  
> This is the version used in **interviews and production code**.  
> It solves the package-private coupling problem from Module 3 and produces a truly immutable class.

---

## Table of Contents
1. [Why Move the Builder Inside the Class?](#1-why-move-the-builder-inside-the-class)
2. [Key Concepts](#2-key-concepts)
3. [Complete Implementation – Employee Example](#3-complete-implementation--employee-example)
4. [Anatomy of Each Part](#4-anatomy-of-each-part)
5. [Fluent Interface Explained](#5-fluent-interface-explained)
6. [The Private Constructor Rule](#6-the-private-constructor-rule)
7. [The build() Method Gate](#7-the-build-method-gate)
8. [Calling Pattern at Usage Sites](#8-calling-pattern-at-usage-sites)
9. [File Structure](#9-file-structure)
10. [Why This Is the Interview Standard](#10-why-this-is-the-interview-standard)

---

## 1. Why Move the Builder Inside the Class?

In Module 3 we had two separate files: `Computer.java` and `ComputerBuilder.java`.  
The builder needed **package-private** access to the Product's fields — a weak form of encapsulation.

The **Static Nested Builder** solves this:

```
┌─────────────────────────────────────────────────────────┐
│                  Employee.java                          │
│                                                         │
│  public final class Employee {                          │
│      private final String name;    ← private (truly)   │
│      private final int    age;                          │
│                                                         │
│      private Employee(Builder b) { ... }  ← private    │
│                                                         │
│      ┌─────────────────────────────────────────────┐   │
│      │  public static class Builder {              │   │
│      │      // Builder lives INSIDE Employee        │   │
│      │      // Can access private constructor ✅    │   │
│      │      // Can access private fields ✅         │   │
│      │  }                                          │   │
│      └─────────────────────────────────────────────┘   │
│  }                                                      │
└─────────────────────────────────────────────────────────┘
```

**Benefits of static nested approach:**
```
✅ Product constructor is truly PRIVATE — only Builder can call it
✅ Everything in one file — easier to maintain
✅ No package-private field access needed — Builder has full inner-class access
✅ Client usage is clean: new Employee.Builder("Alice", 25).department("Engineering").build()
✅ This is the Joshua Bloch "Effective Java" recommended style
```

---

## 2. Key Concepts

| Concept                | What It Means                                                  |
|------------------------|----------------------------------------------------------------|
| **Static nested class**| A class inside another class but NOT tied to an instance      |
| **Private constructor**| Only `Employee.Builder` (being inside) can call `new Employee()`|
| **Final fields**       | All Product fields are `final` — assigned once, never changed |
| **Method chaining**    | Each setter returns `this` — enabling `.field1().field2()`    |
| **build()**            | The single atomic step that validates + constructs the Product|
| **Fluent API**         | Code reads like natural English: `.name("Alice").age(25).build()`|

---

## 3. Complete Implementation – Employee Example

```java
// Employee.java  (ONE file — everything inside)

public final class Employee {

    // ══════════════════════════════════════════════════════════
    // PRODUCT FIELDS — all private, all final (immutable)
    // ══════════════════════════════════════════════════════════

    private final String name;          // mandatory
    private final int    age;           // mandatory
    private final String department;    // optional
    private final String designation;   // optional
    private final double salary;        // optional
    private final String email;         // optional
    private final String phoneNumber;   // optional
    private final boolean isRemote;     // optional

    // ══════════════════════════════════════════════════════════
    // PRIVATE CONSTRUCTOR — only Builder (inside this class) can call
    // ══════════════════════════════════════════════════════════

    private Employee(Builder builder) {
        this.name        = builder.name;
        this.age         = builder.age;
        this.department  = builder.department;
        this.designation = builder.designation;
        this.salary      = builder.salary;
        this.email       = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.isRemote    = builder.isRemote;
    }

    // ══════════════════════════════════════════════════════════
    // GETTERS ONLY — no setters (immutable object)
    // ══════════════════════════════════════════════════════════

    public String  getName()        { return name; }
    public int     getAge()         { return age; }
    public String  getDepartment()  { return department; }
    public String  getDesignation() { return designation; }
    public double  getSalary()      { return salary; }
    public String  getEmail()       { return email; }
    public String  getPhoneNumber() { return phoneNumber; }
    public boolean isRemote()       { return isRemote; }

    @Override
    public String toString() {
        return "Employee {" +
               "\n  name        = " + name +
               "\n  age         = " + age +
               "\n  department  = " + department +
               "\n  designation = " + designation +
               "\n  salary      = ₹" + salary +
               "\n  email       = " + email +
               "\n  phone       = " + phoneNumber +
               "\n  remote      = " + isRemote +
               "\n}";
    }

    // ══════════════════════════════════════════════════════════
    // STATIC NESTED BUILDER CLASS
    // ══════════════════════════════════════════════════════════

    public static class Builder {

        // ── Mandatory fields (no defaults) ────────────────────
        private final String name;   // final in Builder too — cannot change after constructor
        private final int    age;

        // ── Optional fields (with defaults) ───────────────────
        private String  department  = "General";
        private String  designation = "Associate";
        private double  salary      = 30000.0;
        private String  email       = "";
        private String  phoneNumber = "";
        private boolean isRemote    = false;

        // ── Builder Constructor — takes ONLY mandatory fields ──
        public Builder(String name, int age) {
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Employee name cannot be blank.");
            if (age < 18 || age > 65)
                throw new IllegalArgumentException("Age must be between 18 and 65. Got: " + age);
            this.name = name;
            this.age  = age;
        }

        // ── Optional field setters (each returns 'this') ───────

        public Builder department(String department) {
            this.department = department;
            return this;   // ← enables chaining
        }

        public Builder designation(String designation) {
            this.designation = designation;
            return this;
        }

        public Builder salary(double salary) {
            if (salary < 0)
                throw new IllegalArgumentException("Salary cannot be negative.");
            this.salary = salary;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder isRemote(boolean isRemote) {
            this.isRemote = isRemote;
            return this;
        }

        // ── build() — validate combinations + create Product ──

        public Employee build() {
            // Cross-field validation
            if (salary > 200000 && designation.equals("Associate")) {
                throw new IllegalStateException(
                    "An 'Associate' cannot have a salary above ₹200,000. Got: ₹" + salary);
            }
            if (email.isBlank() && phoneNumber.isBlank()) {
                throw new IllegalStateException(
                    "Employee must have at least one contact: email or phone.");
            }

            // Calls PRIVATE constructor of the outer Employee class
            return new Employee(this);
        }
    }
}
```

---

## 4. Anatomy of Each Part

### A – `public final class Employee`
```java
public final class Employee { ... }
// ↑ 'final' prevents subclassing
// Subclassing could break immutability by overriding methods
```

### B – `private final String name`
```java
private final String name;
// ↑ 'private' — no one outside can access directly
// ↑ 'final' — once assigned in constructor, NEVER changes
```

### C – `private Employee(Builder builder)`
```java
private Employee(Builder builder) { ... }
// ↑ 'private' constructor — NO ONE outside can do: new Employee(...)
// ↑ The only exception: the static nested 'Builder' class
//   (inner classes have access to private members of the outer class)
```

### D – `public static class Builder`
```java
public static class Builder { ... }
// ↑ 'static' — can be instantiated without an Employee instance
//   Usage: new Employee.Builder("Alice", 25)  ← no Employee needed first
// ↑ 'public' — accessible from anywhere
```

### E – `private final String name` inside Builder
```java
private final String name;  // inside Builder
// ↑ Mandatory fields in Builder are also 'final' — set in Builder constructor, never changed
```

### F – `return this` in setters
```java
public Builder department(String department) {
    this.department = department;
    return this;   // ← returns the SAME Builder object
}
// Enables: builder.department("Eng").designation("SDE").salary(80000)
```

### G – `return new Employee(this)` in build()
```java
public Employee build() {
    return new Employee(this);
    // 'this' = the Builder object itself
    // Passes the Builder to Employee's private constructor
    // Employee's constructor reads all fields from 'this' Builder
}
```

---

## 5. Fluent Interface Explained

**Fluent Interface** = a style of API where method calls return the current object (`this`), enabling **method chaining** — a sequence of calls on a single line.

```java
// Without fluent interface (verbose):
Employee.Builder builder = new Employee.Builder("Alice", 28);
builder.department("Engineering");
builder.designation("Senior SDE");
builder.salary(120000);
builder.email("alice@company.com");
builder.isRemote(true);
Employee emp = builder.build();

// With fluent interface (concise and readable):
Employee emp = new Employee.Builder("Alice", 28)
    .department("Engineering")
    .designation("Senior SDE")
    .salary(120000)
    .email("alice@company.com")
    .isRemote(true)
    .build();

// Both produce IDENTICAL results.
// The fluent version reads almost like a sentence.
```

**How chaining works internally:**
```
new Employee.Builder("Alice", 28)   → returns Builder object B
.department("Engineering")          → B.department = "Engineering", returns B
.designation("Senior SDE")          → B.designation = "Senior SDE", returns B
.salary(120000)                     → B.salary = 120000, returns B
.email("alice@company.com")         → B.email = "alice@...", returns B
.isRemote(true)                     → B.isRemote = true, returns B
.build()                            → validates, returns new Employee(B)
```

---

## 6. The Private Constructor Rule

```java
// ❌ This will NOT compile — constructor is private
Employee e = new Employee(...);   // Compilation Error!

// ✅ This is the ONLY valid way to create an Employee
Employee e = new Employee.Builder("Bob", 30)
                 .email("bob@co.com")
                 .build();
```

**Why is this important?**
```
Private constructor = the Builder is the SINGLE ENTRY POINT for object creation.
No one can bypass validation. No one can create a partially-configured Employee.
Every Employee that ever exists has been through build() and is valid and complete.
```

---

## 7. The build() Method Gate

Think of `build()` as a **security checkpoint**:

```
Builder fields configured → build() is called
                                │
                         ┌──── ▼ ────────────────────────────┐
                         │   VALIDATION GATE                  │
                         │   ① Is salary positive?           │
                         │   ② Is at least one contact set?  │
                         │   ③ Is designation consistent?    │
                         │   ④ ... any rule you define ...   │
                         └──────────────┬─────────────────────┘
                                        │
                           ┌────────────┴──────────────┐
                           │                           │
                    PASS ✅                        FAIL ❌
                           │                           │
                  new Employee(this)          throw IllegalStateException
                           │
                  Immutable Employee returned
```

---

## 8. Calling Pattern at Usage Sites

```java
// Main.java

public class Main {
    public static void main(String[] args) {

        // Full Employee
        Employee seniorDev = new Employee.Builder("Riya Sharma", 29)
            .department("Backend Engineering")
            .designation("Senior Software Engineer")
            .salary(150000)
            .email("riya@tech.com")
            .phoneNumber("+91-9876543210")
            .isRemote(true)
            .build();
        System.out.println(seniorDev);

        // Minimal Employee (uses all defaults)
        Employee intern = new Employee.Builder("Arjun Nair", 22)
            .email("arjun@tech.com")   // only email provided (required by validation)
            .build();
        System.out.println(intern);

        // Validation failure
        try {
            Employee invalid = new Employee.Builder("Test User", 30)
                .designation("Associate")
                .salary(500000)         // ← fails validation
                .email("test@co.com")
                .build();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

**Output:**
```
Employee {
  name        = Riya Sharma
  age         = 29
  department  = Backend Engineering
  designation = Senior Software Engineer
  salary      = ₹150000.0
  email       = riya@tech.com
  phone       = +91-9876543210
  remote      = true
}
Employee {
  name        = Arjun Nair
  age         = 22
  department  = General
  designation = Associate
  salary      = ₹30000.0
  email       = arjun@tech.com
  phone       =
  remote      = false
}
Error: An 'Associate' cannot have a salary above ₹200,000. Got: ₹500000.0
```

---

## 9. File Structure

```
// Everything in ONE file:

Employee.java
├── public final class Employee            ← Product (outer class)
│       private final String name
│       private final int age
│       ... all fields
│       private Employee(Builder b)        ← private constructor
│       public String getName()            ← getters only
│       public String toString()
│
└── public static class Builder            ← Builder (static nested class)
        private final String name
        private final int age
        ... all builder fields
        public Builder(String name, int age)
        public Builder department(String d)
        ... all setters returning 'this'
        public Employee build()            ← creates and returns Employee
```

---

## 10. Why This Is the Interview Standard

| Criteria                          | Module 3 Style          | Static Nested (This Module)   |
|-----------------------------------|-------------------------|-------------------------------|
| Files needed                      | 2 (Product + Builder)   | 1 (everything inside)         |
| Product constructor visibility    | Package-private         | **Private** (truly encapsulated)|
| Access to Product fields          | Package-private hack    | **Natural** (inner class access)|
| Immutability                      | Partial                 | **Full** (all fields final)    |
| Production readiness              | ❌ Not ideal            | ✅ Production-grade            |
| Used in: Effective Java (Bloch)   | ❌                      | ✅                             |
| Used in: Lombok @Builder          | ❌                      | ✅ (generates this exact style) |
| Used in: OkHttp, Retrofit, etc.   | ❌                      | ✅                             |

---

**← Prev:** [`03_First_Builder_Implementation.md`](./03_First_Builder_Implementation.md)  
**Next →** [`05_Builder_with_Mandatory_Fields.md`](./05_Builder_with_Mandatory_Fields.md)
