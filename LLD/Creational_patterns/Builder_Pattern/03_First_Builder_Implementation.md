# Module 03 – First Builder Implementation

> **Study order:** Read after `02_Introduction_to_Builder.md`.  
> This module walks you through writing a Builder from scratch — **every line explained**.

---

## Table of Contents
1. [The Goal](#1-the-goal)
2. [Step 1 – Define the Product](#2-step-1--define-the-product)
3. [Step 2 – Create the Builder Class](#3-step-2--create-the-builder-class)
4. [Step 3 – Add Builder Fields](#4-step-3--add-builder-fields)
5. [Step 4 – Add Setter Methods (return this)](#5-step-4--add-setter-methods-return-this)
6. [Step 5 – Add the build() Method](#6-step-5--add-the-build-method)
7. [Step 6 – Tie Product to Builder](#7-step-6--tie-product-to-builder)
8. [Step 7 – Write the Client](#8-step-7--write-the-client)
9. [Object Creation Flow (Full Trace)](#9-object-creation-flow-full-trace)
10. [Complete Code (All Together)](#10-complete-code-all-together)

---

## 1. The Goal

We will build a `Computer` class step by step.  
A `Computer` has:

| Field       | Mandatory? | Default      |
|-------------|------------|--------------|
| `cpu`       | ✅ Yes     | —            |
| `ram`       | ✅ Yes     | —            |
| `storage`   | ❌ No      | `"256GB SSD"` |
| `gpu`       | ❌ No      | `"Integrated"` |
| `os`        | ❌ No      | `"Linux"`    |
| `bluetooth` | ❌ No      | `false`      |

We will implement a **simple, separate Builder class** (not yet inner class — that comes in Module 4).

---

## 2. Step 1 – Define the Product

```java
// Computer.java

// 'final' — this class cannot be subclassed (immutability guarantee)
public final class Computer {

    // All fields are 'final' — once set in constructor, they NEVER change
    // This makes the object IMMUTABLE
    private final String  cpu;
    private final String  ram;
    private final String  storage;
    private final String  gpu;
    private final String  os;
    private final boolean bluetooth;

    // Constructor is 'package-private' (not public, not private).
    // Only classes in the same package (our Builder) can call it.
    // This prevents anyone outside from creating a Computer directly.
    Computer(ComputerBuilder builder) {
        // Copy all values FROM the builder INTO this final object
        this.cpu       = builder.cpu;       // ← reads from builder
        this.ram       = builder.ram;
        this.storage   = builder.storage;
        this.gpu       = builder.gpu;
        this.os        = builder.os;
        this.bluetooth = builder.bluetooth;
    }

    // ONLY getters — no setters — enforces immutability
    public String  getCpu()       { return cpu; }
    public String  getRam()       { return ram; }
    public String  getStorage()   { return storage; }
    public String  getGpu()       { return gpu; }
    public String  getOs()        { return os; }
    public boolean isBluetooth()  { return bluetooth; }

    @Override
    public String toString() {
        return "Computer {" +
               "\n  cpu       = " + cpu +
               "\n  ram       = " + ram +
               "\n  storage   = " + storage +
               "\n  gpu       = " + gpu +
               "\n  os        = " + os +
               "\n  bluetooth = " + bluetooth +
               "\n}";
    }
}
```

**Line-by-line explanation:**

```
final class Computer          → Class cannot be extended (extra safety)
private final String cpu      → Field is private (hidden) AND final (immutable)
Computer(ComputerBuilder b)   → Package-private constructor; only Builder (same package) can call this
this.cpu = builder.cpu        → Transfer state from Builder to Product
```

---

## 3. Step 2 – Create the Builder Class

```java
// ComputerBuilder.java

public class ComputerBuilder {

    // Builder holds the SAME fields as Computer.
    // These are NOT final — they need to be set one by one.
    // They are package-private so Computer's constructor can read them.
    String  cpu;        // ← will be read by Computer's constructor
    String  ram;
    String  storage;
    String  gpu;
    String  os;
    boolean bluetooth;

    // More code will be added below...
}
```

**Key insight:**
```
Builder fields     → mutable (no 'final') — accumulated during configuration
Product fields     → immutable (all 'final') — set once during construction

The Builder is a temporary "staging area" for the data.
The Product is the permanent, immutable result.
```

---

## 4. Step 3 – Add Builder Fields

Now we add field visibility and defaults:

```java
public class ComputerBuilder {

    // ── Mandatory fields (no defaults) ──────────────────────────
    String cpu;   // MUST be set — no default
    String ram;   // MUST be set — no default

    // ── Optional fields (with sensible defaults) ─────────────────
    String  storage   = "256GB SSD";   // default if not specified
    String  gpu       = "Integrated";  // default if not specified
    String  os        = "Linux";       // default if not specified
    boolean bluetooth = false;         // default if not specified

    // ── Mandatory fields in the constructor ─────────────────────
    // Forces caller to provide cpu and ram BEFORE configuring anything else
    public ComputerBuilder(String cpu, String ram) {
        // Validate mandatory fields immediately
        if (cpu == null || cpu.isBlank())
            throw new IllegalArgumentException("CPU cannot be empty!");
        if (ram == null || ram.isBlank())
            throw new IllegalArgumentException("RAM cannot be empty!");

        this.cpu = cpu;
        this.ram = ram;
    }
}
```

**Why mandatory fields in the Builder constructor?**
```
✅ Client MUST provide cpu and ram to even CREATE the builder.
✅ It's impossible to forget a mandatory field — compiler enforces it.
✅ Optional fields fall back to defaults if not explicitly set.
```

---

## 5. Step 4 – Add Setter Methods (return this)

```java
// Inside ComputerBuilder.java

// Each method:
//   1. Sets one optional field on 'this' builder
//   2. Returns 'this' (the same builder object)
//      → This enables METHOD CHAINING (fluent API)

public ComputerBuilder storage(String storage) {
    this.storage = storage;
    return this;              // ← return this builder (not void!)
}

public ComputerBuilder gpu(String gpu) {
    this.gpu = gpu;
    return this;
}

public ComputerBuilder os(String os) {
    this.os = os;
    return this;
}

public ComputerBuilder bluetooth(boolean bluetooth) {
    this.bluetooth = bluetooth;
    return this;
}
```

**Why `return this`?**
```
Without 'return this':
  ComputerBuilder b = new ComputerBuilder("i9", "32GB");
  b.storage("1TB");    // void return
  b.gpu("RTX 4090");   // void return
  b.os("Windows");     // void return
  Computer c = b.build();

With 'return this':
  Computer c = new ComputerBuilder("i9", "32GB")
      .storage("1TB")        // returns same builder
      .gpu("RTX 4090")       // returns same builder
      .os("Windows")         // returns same builder
      .build();              // returns Computer

Same result — but method chaining is far more READABLE.
```

---

## 6. Step 5 – Add the build() Method

```java
// Inside ComputerBuilder.java

public Computer build() {
    // ── Validation of combinations (invariants) ──────────────────
    // You can add any cross-field validation here BEFORE creating the object

    // Example: Gaming GPU needs at least 16GB RAM
    if (gpu.startsWith("RTX") || gpu.startsWith("RX")) {
        int ramGB = Integer.parseInt(ram.replace("GB", "").trim());
        if (ramGB < 16) {
            throw new IllegalStateException(
                "Gaming GPU '" + gpu + "' requires at least 16GB RAM. Got: " + ram
            );
        }
    }

    // ── Create and return the immutable Product ──────────────────
    return new Computer(this);  // 'this' = the fully-configured builder
}
```

**What happens at `new Computer(this)`?**
```
① Java calls Computer's package-private constructor
② Constructor receives 'this' builder as parameter
③ Computer copies all builder fields into its own final fields
④ Computer object is fully initialised, all final fields set
⑤ Computer is returned — immutable from this point forward
⑥ Builder object is no longer referenced — garbage collector will clean it up
```

---

## 7. Step 6 – Tie Product to Builder

The `Computer` constructor reads from the builder:

```java
// Computer.java (revisited)

Computer(ComputerBuilder builder) {
    // Direct field access — builder fields are package-private
    this.cpu       = builder.cpu;
    this.ram       = builder.ram;
    this.storage   = builder.storage;
    this.gpu       = builder.gpu;
    this.os        = builder.os;
    this.bluetooth = builder.bluetooth;
    // After this line, all fields are set and FINAL — cannot change
}
```

---

## 8. Step 7 – Write the Client

```java
// Main.java

public class Main {
    public static void main(String[] args) {

        // ── Computer 1: Gaming PC (all fields specified) ──────────
        Computer gamingPC = new ComputerBuilder("Intel i9-13900K", "32GB DDR5")
            .storage("2TB NVMe SSD")
            .gpu("RTX 4090")
            .os("Windows 11")
            .bluetooth(true)
            .build();

        System.out.println("=== Gaming PC ===");
        System.out.println(gamingPC);


        // ── Computer 2: Basic Office PC (only mandatory + some optional) ──
        Computer officePC = new ComputerBuilder("Intel i5-12400", "8GB DDR4")
            // storage → uses default "256GB SSD"
            // gpu     → uses default "Integrated"
            .os("Ubuntu 22.04")
            // bluetooth → uses default false
            .build();

        System.out.println("\n=== Office PC ===");
        System.out.println(officePC);


        // ── Computer 3: Validation Error ────────────────────────────
        try {
            Computer invalidPC = new ComputerBuilder("AMD Ryzen 5", "8GB DDR4")
                .gpu("RTX 4080")   // Gaming GPU but only 8GB RAM!
                .build();          // ← throws IllegalStateException
        } catch (IllegalStateException e) {
            System.out.println("\nValidation Error: " + e.getMessage());
        }


        // ── Computer 4: Missing mandatory field ─────────────────────
        try {
            Computer badPC = new ComputerBuilder(null, "16GB");  // ← throws immediately
        } catch (IllegalArgumentException e) {
            System.out.println("Missing Field Error: " + e.getMessage());
        }
    }
}
```

**Output:**
```
=== Gaming PC ===
Computer {
  cpu       = Intel i9-13900K
  ram       = 32GB DDR5
  storage   = 2TB NVMe SSD
  gpu       = RTX 4090
  os        = Windows 11
  bluetooth = true
}

=== Office PC ===
Computer {
  cpu       = Intel i5-12400
  ram       = 8GB DDR4
  storage   = 256GB SSD
  gpu       = Integrated
  os        = Ubuntu 22.04
  bluetooth = false
}

Validation Error: Gaming GPU 'RTX 4080' requires at least 16GB RAM. Got: 8GB
Missing Field Error: CPU cannot be empty!
```

---

## 9. Object Creation Flow (Full Trace)

Let's trace `new ComputerBuilder("i9", "32GB").storage("1TB").gpu("RTX 4090").build()`:

```
Step ①  new ComputerBuilder("i9", "32GB")
         → ComputerBuilder object created on heap
         → builder.cpu = "i9"
         → builder.ram = "32GB"
         → builder.storage = "256GB SSD"  (default)
         → builder.gpu = "Integrated"     (default)
         → builder.os = "Linux"           (default)
         → builder.bluetooth = false      (default)
         → returns: builder reference

Step ②  .storage("1TB")
         → calls builder.storage("1TB")
         → sets builder.storage = "1TB"
         → returns: same builder reference (this)

Step ③  .gpu("RTX 4090")
         → calls builder.gpu("RTX 4090")
         → sets builder.gpu = "RTX 4090"
         → returns: same builder reference (this)

Step ④  .build()
         → validation runs (RTX + 32GB RAM → OK ✅)
         → calls new Computer(builder)
             → Computer.cpu       = "i9"         (from builder)
             → Computer.ram       = "32GB"       (from builder)
             → Computer.storage   = "1TB"        (from builder)
             → Computer.gpu       = "RTX 4090"   (from builder)
             → Computer.os        = "Linux"      (from builder, default)
             → Computer.bluetooth = false        (from builder, default)
         → Computer object is now IMMUTABLE (all fields final)
         → builder object is no longer referenced
         → returns: Computer reference

Step ⑤  Client holds reference to the immutable Computer object
```

---

## 10. Complete Code (All Together)

```java
// ═══════════════════════════════════════════════════════════
// Computer.java — Product
// ═══════════════════════════════════════════════════════════
public final class Computer {
    private final String  cpu;
    private final String  ram;
    private final String  storage;
    private final String  gpu;
    private final String  os;
    private final boolean bluetooth;

    Computer(ComputerBuilder builder) {
        this.cpu       = builder.cpu;
        this.ram       = builder.ram;
        this.storage   = builder.storage;
        this.gpu       = builder.gpu;
        this.os        = builder.os;
        this.bluetooth = builder.bluetooth;
    }

    public String  getCpu()      { return cpu; }
    public String  getRam()      { return ram; }
    public String  getStorage()  { return storage; }
    public String  getGpu()      { return gpu; }
    public String  getOs()       { return os; }
    public boolean isBluetooth() { return bluetooth; }

    @Override
    public String toString() {
        return "Computer {\n  cpu=" + cpu + "\n  ram=" + ram +
               "\n  storage=" + storage + "\n  gpu=" + gpu +
               "\n  os=" + os + "\n  bluetooth=" + bluetooth + "\n}";
    }
}

// ═══════════════════════════════════════════════════════════
// ComputerBuilder.java — Builder
// ═══════════════════════════════════════════════════════════
public class ComputerBuilder {
    String  cpu;
    String  ram;
    String  storage   = "256GB SSD";
    String  gpu       = "Integrated";
    String  os        = "Linux";
    boolean bluetooth = false;

    public ComputerBuilder(String cpu, String ram) {
        if (cpu == null || cpu.isBlank()) throw new IllegalArgumentException("CPU cannot be empty!");
        if (ram == null || ram.isBlank()) throw new IllegalArgumentException("RAM cannot be empty!");
        this.cpu = cpu;
        this.ram = ram;
    }

    public ComputerBuilder storage(String storage)    { this.storage = storage; return this; }
    public ComputerBuilder gpu(String gpu)            { this.gpu = gpu; return this; }
    public ComputerBuilder os(String os)              { this.os = os; return this; }
    public ComputerBuilder bluetooth(boolean bt)      { this.bluetooth = bt; return this; }

    public Computer build() {
        if ((gpu.startsWith("RTX") || gpu.startsWith("RX"))) {
            int ramGB = Integer.parseInt(ram.replace("GB", "").trim());
            if (ramGB < 16) throw new IllegalStateException(
                "Gaming GPU '" + gpu + "' requires at least 16GB RAM. Got: " + ram);
        }
        return new Computer(this);
    }
}

// ═══════════════════════════════════════════════════════════
// Main.java — Client
// ═══════════════════════════════════════════════════════════
public class Main {
    public static void main(String[] args) {
        Computer pc = new ComputerBuilder("Intel i9", "32GB DDR5")
            .storage("2TB SSD")
            .gpu("RTX 4090")
            .os("Windows 11")
            .bluetooth(true)
            .build();
        System.out.println(pc);
    }
}
```

---

**← Prev:** [`02_Introduction_to_Builder.md`](./02_Introduction_to_Builder.md)  
**Next →** [`04_Static_Nested_Builder.md`](./04_Static_Nested_Builder.md)
