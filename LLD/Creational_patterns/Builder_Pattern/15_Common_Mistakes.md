# Module 15 – Common Mistakes

> **Study order:** Read after `14_Best_Practices.md`.  
> The final module. Learn what NOT to do — these mistakes appear frequently in code reviews and interviews.

---

## Table of Contents
1. [Mistake 1 – Forgetting `return this`](#mistake-1--forgetting-return-this)
2. [Mistake 2 – Making the Product Constructor Public](#mistake-2--making-the-product-constructor-public)
3. [Mistake 3 – Mutable Product Fields (not final)](#mistake-3--mutable-product-fields-not-final)
4. [Mistake 4 – Mandatory Fields as Setters](#mistake-4--mandatory-fields-as-setters)
5. [Mistake 5 – No Validation in build()](#mistake-5--no-validation-in-build)
6. [Mistake 6 – Exposing Mutable Collections Without Defensive Copy](#mistake-6--exposing-mutable-collections-without-defensive-copy)
7. [Mistake 7 – Reusing the Same Builder Instance](#mistake-7--reusing-the-same-builder-instance)
8. [Mistake 8 – Non-Static Inner Builder](#mistake-8--non-static-inner-builder)
9. [Mistake 9 – Product Has Setters](#mistake-9--product-has-setters)
10. [Mistake 10 – Not Calling build()](#mistake-10--not-calling-build)
11. [Mistake 11 – Overusing Builder for Simple Classes](#mistake-11--overusing-builder-for-simple-classes)
12. [Mistake 12 – Sharing Builder Across Threads](#mistake-12--sharing-builder-across-threads)
13. [Mistake 13 – Breaking CRTP in Inherited Builders](#mistake-13--breaking-crtp-in-inherited-builders)
14. [Common Mistakes Quick Reference Table](#common-mistakes-quick-reference-table)

---

## Mistake 1 – Forgetting `return this`

**Symptom:** Can't chain methods. IDE shows compile error on the next method in the chain.

```java
// ❌ WRONG — returns void, breaks chaining
public class PersonBuilder {
    private String name;
    private int    age;

    public void name(String name) {     // ← 'void' breaks the chain!
        this.name = name;
    }
    public void age(int age) {
        this.age = age;
    }
}

// ❌ Cannot chain — must use separate statements
PersonBuilder b = new PersonBuilder();
b.name("Alice");   // works
b.age(25);         // works, but NOT chainable
// new PersonBuilder().name("Alice").age(25)  ← Compilation error!
```

```java
// ✅ CORRECT — return this enables chaining
public PersonBuilder name(String name) {
    this.name = name;
    return this;    // ← always return this!
}
public PersonBuilder age(int age) {
    this.age = age;
    return this;
}

// ✅ Now chain works perfectly
Person p = new PersonBuilder().name("Alice").age(25).build();
```

**Rule:** Every setter in a Builder must return `this`. No exceptions.

---

## Mistake 2 – Making the Product Constructor Public

**Symptom:** Anyone can bypass the Builder and create a partially-configured or invalid Product.

```java
// ❌ WRONG — public constructor defeats the entire purpose of Builder!
public final class Employee {
    private final String name;
    private final String email;

    public Employee(String name, String email) {   // ← public! Anyone can call this
        this.name  = name;
        this.email = email;
    }

    public static class Builder { ... }
}

// Client bypasses Builder entirely — no validation, no defaults!
Employee emp = new Employee("Alice", null);   // ← compiles! But email is null — invalid!
```

```java
// ✅ CORRECT — private constructor forces use of Builder
public final class Employee {
    private final String name;
    private final String email;

    private Employee(Builder builder) {   // ← private! Only Builder (inner class) can call it
        this.name  = builder.name;
        this.email = builder.email;
    }

    public static class Builder {
        ...
        public Employee build() {
            return new Employee(this);   // only way to create an Employee
        }
    }
}

// ❌ Now this won't compile:
// Employee emp = new Employee("Alice", null);   // Compilation error!

// ✅ Must use Builder
Employee emp = new Employee.Builder("Alice", "alice@example.com").build();
```

---

## Mistake 3 – Mutable Product Fields (not final)

**Symptom:** The "immutable" object can be changed after creation — all thread-safety and reliability guarantees are lost.

```java
// ❌ WRONG — fields are NOT final → object is mutable after creation!
public class Product {
    private String name;    // no 'final' — can be reassigned!
    private int    price;

    Product(Builder b) {
        this.name  = b.name;
        this.price = b.price;
    }
}

// Someone could do this:
Product p = new Product.Builder("Laptop", 85000).build();
// Then via reflection or package access, p.name = "Changed";   // possible!
```

```java
// ✅ CORRECT — all product fields are private AND final
public final class Product {
    private final String name;    // 'final' — set once in constructor, never again
    private final int    price;

    private Product(Builder b) {
        this.name  = b.name;
        this.price = b.price;
        // After this constructor returns, name and price are LOCKED FOREVER
    }
}
```

---

## Mistake 4 – Mandatory Fields as Setters

**Symptom:** Mandatory fields can be forgotten silently. Error only appears at `build()` time (or worse — at runtime when the null value causes a NullPointerException somewhere else).

```java
// ❌ WRONG — all fields as optional setters, nothing mandatory
public static class Builder {
    private String employeeId;   // mandatory — but treated as optional!
    private String name;         // mandatory — but treated as optional!

    public Builder employeeId(String id) { this.employeeId = id; return this; }
    public Builder name(String name)     { this.name = name; return this; }

    public Employee build() {
        if (employeeId == null) throw new IllegalStateException("ID required");
        return new Employee(this);
    }
}

// ❌ Nothing prevents this from compiling:
Employee emp = new Employee.Builder()
    .name("Alice")
    // forgot employeeId — only found at build() time
    .build();   // ← throws, but too late and hard to trace
```

```java
// ✅ CORRECT — mandatory fields in Builder constructor
public static class Builder {
    private final String employeeId;   // mandatory → final → in constructor
    private final String name;         // mandatory → final → in constructor

    public Builder(String employeeId, String name) {
        // Validated IMMEDIATELY at construction — fail fast!
        if (employeeId == null || employeeId.isBlank())
            throw new IllegalArgumentException("Employee ID required");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name required");
        this.employeeId = employeeId;
        this.name       = name;
    }
}

// ✅ Compiler enforces: you MUST provide employeeId and name
new Employee.Builder("EMP-001", "Alice")   // mandatory fields enforced here
    .salary(100000)
    .build();

// ❌ This won't compile — Builder() no-arg constructor doesn't exist:
// new Employee.Builder().name("Alice").build();   // Compilation error!
```

---

## Mistake 5 – No Validation in build()

**Symptom:** Invalid objects are created and errors appear much later in the application — hard to debug.

```java
// ❌ WRONG — build() creates object without any checks
public Employee build() {
    return new Employee(this);   // no validation — bad combinations slip through!
}

// This silently creates an invalid Employee:
Employee e = new Employee.Builder("EMP-001", "Alice", "alice@co.com", Department.HR)
    .level(Level.INTERN)
    .salary(5_000_000)   // ₹50L for an intern — clearly wrong!
    .build();            // no error! Invalid object created.
```

```java
// ✅ CORRECT — build() is a validation gate
public Employee build() {
    // Cross-field rules — things you can only check after ALL fields are set
    if (level == Level.INTERN && salary > 100_000)
        throw new IllegalStateException(
            "Intern salary cannot exceed ₹1L. Got: ₹" + salary);

    if (level == Level.MANAGER && reportingManager == null)
        throw new IllegalStateException(
            "Manager must have a reporting manager assigned.");

    if (isRemote && department == Department.OPERATIONS)
        throw new IllegalStateException(
            "Operations cannot be fully remote.");

    return new Employee(this);
}
```

---

## Mistake 6 – Exposing Mutable Collections Without Defensive Copy

**Symptom:** External code mutates the internal list of an "immutable" object — immutability silently broken.

```java
// ❌ WRONG — shallow copy; external list still modifies internal state
private final List<String> skills;

private Employee(Builder b) {
    this.skills = b.skills;   // same reference! External mutation affects internal state!
}

// Attack:
List<String> mySkills = new ArrayList<>(List.of("Java"));
Employee e = new Employee.Builder(...)
    .skills(mySkills)   // mySkills and e.skills point to SAME list
    .build();

mySkills.add("Hacking");   // ← mutates e's internal list! Immutability BROKEN!
System.out.println(e.getSkills());   // [Java, Hacking] — unexpected!
```

```java
// ✅ CORRECT — defensive copy breaks the external reference
private Employee(Builder b) {
    // List.copyOf creates a NEW, unmodifiable list
    this.skills = List.copyOf(b.skills);   // independent copy
}

// ✅ Getter returns the unmodifiable copy
public List<String> getSkills() {
    return skills;   // already unmodifiable (List.copyOf result)
}

// Now the attack fails:
mySkills.add("Hacking");   // only modifies mySkills — employee's list unchanged ✅
```

---

## Mistake 7 – Reusing the Same Builder Instance

**Symptom:** Two objects share state because they were built from the same configured Builder.

```java
// ❌ WRONG — reusing Builder creates objects that share Builder's ArrayList reference
Product.Builder builder = new Product.Builder("Laptop");
builder.tag("electronics");

Product product1 = builder.build();    // product1 tags = ["electronics"]

builder.tag("premium");
Product product2 = builder.build();    // product2 tags = ["electronics", "premium"]

// Problem: product1 and product2 share the SAME ArrayList from builder.tags
// If that list isn't defensively copied in the constructor, both products
// will show ["electronics", "premium"]!
```

```java
// ✅ CORRECT — create a fresh Builder for each product
Product product1 = new Product.Builder("Laptop")
    .tag("electronics")
    .build();

Product product2 = new Product.Builder("Laptop")
    .tag("electronics")
    .tag("premium")
    .build();
// Both products have independent, correct tag lists
```

---

## Mistake 8 – Non-Static Inner Builder

**Symptom:** Can't create a Builder without first creating a Product — defeats the purpose.

```java
// ❌ WRONG — non-static inner class requires an outer class instance
public class Employee {
    public class Builder {   // non-static!
        ...
    }
}

// Usage is bizarre — need an Employee to create a Builder?!
Employee temp = new Employee(null, null);  // creates broken Employee just to get Builder
Employee.Builder builder = temp.new Builder();   // ← ugly and wrong!
```

```java
// ✅ CORRECT — static nested class can be instantiated independently
public class Employee {
    public static class Builder {   // static!
        ...
    }
}

// Clean usage — no Employee needed first
Employee emp = new Employee.Builder("Alice", 28).build();
```

---

## Mistake 9 – Product Has Setters

**Symptom:** The product is "built" by the Builder but can be mutated afterwards — immutability guarantee is gone.

```java
// ❌ WRONG — Product exposes setters, breaking immutability
public final class Config {
    private String host;    // no 'final' — can be changed!
    private int    port;

    Config(Builder b) { this.host = b.host; this.port = b.port; }

    public void setHost(String host) { this.host = host; }   // ← breaks immutability!
    public void setPort(int port)    { this.port = port; }   // ← breaks immutability!
}

Config config = new Config.Builder("localhost", 8080).build();
config.setHost("malicious.server.com");   // ← anyone can change it!
```

```java
// ✅ CORRECT — getters ONLY on the Product
public final class Config {
    private final String host;   // 'final' — cannot be reassigned
    private final int    port;

    private Config(Builder b) { this.host = b.host; this.port = b.port; }

    public String getHost() { return host; }   // ✅ getter only
    public int    getPort() { return port; }   // ✅ getter only
    // No setters!
}
```

---

## Mistake 10 – Not Calling build()

**Symptom:** Code compiles but no Product is created. The Builder object is created and discarded.

```java
// ❌ WRONG — forgot to call build()!
Employee emp = (Employee) new Employee.Builder("Alice", 28)
    .department("Engineering")
    .salary(100000);
// ↑ This is a COMPILATION ERROR (Builder ≠ Employee) — at least you find out!

// But sometimes it's more subtle:
void createEmployee() {
    new Employee.Builder("Alice", 28)    // Builder created
        .department("Engineering")
        .salary(100000)
        .build();                         // ← build() called but result DISCARDED!
    // No variable assigned to the built Employee — it's immediately garbage collected!
}
```

```java
// ✅ CORRECT — always assign the result of build()
Employee emp = new Employee.Builder("Alice", 28)
    .department("Engineering")
    .salary(100000)
    .build();          // ← result is stored in 'emp'

// Or pass directly to a method
repository.save(
    new Employee.Builder("Alice", 28)
        .department("Engineering")
        .build()
);
```

> **Tip:** Static analysis tools like SpotBugs (`RV_RETURN_VALUE_IGNORED`) and SonarQube will warn about discarded `build()` results.

---

## Mistake 11 – Overusing Builder for Simple Classes

**Symptom:** Unnecessary boilerplate for a class that only has 1–2 fields.

```java
// ❌ OVERKILL — Builder for a class with one field!
public final class Color {
    private final String hex;
    private Color(Builder b) { this.hex = b.hex; }
    public static class Builder {
        private String hex;
        public Builder hex(String hex) { this.hex = hex; return this; }
        public Color build() { return new Color(this); }
    }
}
// Usage: new Color.Builder().hex("#FF5733").build()   ← absurd!

// ✅ SIMPLE — just use a constructor or static factory
public final class Color {
    private final String hex;
    public Color(String hex) { this.hex = hex; }
    // Or:
    public static Color of(String hex) { return new Color(hex); }
}
// Usage: new Color("#FF5733")   ← clean and obvious
```

**Rule of thumb:** Use Builder only when you have **4+ fields**, especially optional ones.

---

## Mistake 12 – Sharing Builder Across Threads

**Symptom:** Race condition — two threads write to the same Builder simultaneously → unpredictable Product state.

```java
// ❌ WRONG — shared mutable Builder in multi-threaded context
Employee.Builder sharedBuilder = new Employee.Builder("EMP-001", "Alice");

// Thread 1                          // Thread 2
sharedBuilder.salary(100000);  ←→  sharedBuilder.salary(200000);   // race!
Employee emp1 = sharedBuilder.build();   // salary = 100000? 200000? Unknown!
```

```java
// ✅ CORRECT — each thread creates its own Builder
// Thread 1
Employee emp1 = new Employee.Builder("EMP-001", "Alice")
    .salary(100000).build();

// Thread 2 (simultaneously — safe because different Builder instances)
Employee emp2 = new Employee.Builder("EMP-002", "Bob")
    .salary(200000).build();
```

---

## Mistake 13 – Breaking CRTP in Inherited Builders

**Symptom:** Method chaining breaks after calling an inherited method — returns base type, not the concrete type.

```java
// ❌ WRONG — base builder methods return base type; concrete methods inaccessible after
public abstract static class AnimalBuilder {
    protected String name;
    public AnimalBuilder name(String name) { this.name = name; return this; }  // returns AnimalBuilder!
}
public static class DogBuilder extends AnimalBuilder {
    private String breed;
    public DogBuilder breed(String breed) { this.breed = breed; return this; }
}

// Broken chain:
Dog dog = new DogBuilder()
    .name("Max")         // returns AnimalBuilder — not DogBuilder!
    .breed("Labrador")   // ← Compilation error! AnimalBuilder has no .breed()!
    .build();
```

```java
// ✅ CORRECT — use CRTP (T extends Builder<T>) so each method returns the concrete type
public abstract static class AnimalBuilder<T extends AnimalBuilder<T>> {
    protected String name;

    @SuppressWarnings("unchecked")
    public T name(String name) { this.name = name; return (T) this; }  // returns T (concrete type)!
}

public static class DogBuilder extends AnimalBuilder<DogBuilder> {
    private String breed;
    public DogBuilder breed(String breed) { this.breed = breed; return this; }
    public Dog build() { return new Dog(this); }
}

// ✅ Chain works:
Dog dog = new DogBuilder()
    .name("Max")          // returns DogBuilder (thanks to T = DogBuilder)
    .breed("Labrador")    // ✅ DogBuilder method available!
    .build();
```

---

## Common Mistakes Quick Reference Table

| # | Mistake                                      | Symptom                              | Fix                                             |
|---|----------------------------------------------|--------------------------------------|-------------------------------------------------|
| 1 | Forgetting `return this`                     | Can't chain methods                  | Always `return this` in every setter            |
| 2 | Public Product constructor                   | Builder bypassed                     | Make constructor `private`                      |
| 3 | Product fields not `final`                   | Object is mutable after build        | Add `final` to all Product fields               |
| 4 | Mandatory fields as optional setters         | Silent omission of required data     | Put mandatory fields in Builder constructor     |
| 5 | No validation in `build()`                   | Invalid objects created silently     | Add cross-field validation in `build()`         |
| 6 | No defensive copy of collections             | External mutation breaks immutability| Use `List.copyOf()` in Product constructor      |
| 7 | Reusing same Builder instance                | Objects share state                  | Create fresh Builder per object                 |
| 8 | Non-static inner Builder                     | Can't create Builder without Product | Always use `public static class Builder`        |
| 9 | Product has setters                          | Immutability destroyed               | Remove all setters from Product                 |
| 10| Not calling `build()`                        | No object created, or result lost    | Always assign result of `build()` to a variable |
| 11| Builder for simple 1–2 field class           | Unnecessary boilerplate              | Use a simple constructor instead               |
| 12| Sharing Builder across threads               | Race condition on Builder state      | One Builder per thread                          |
| 13| CRTP not used in inherited Builders          | Chain breaks after base method       | Use `T extends Builder<T>` generics            |

---

## Final Reminder: The Builder Mental Model

```
Builder = a controlled staging area
  ↓
  Configure it step by step (mutable, temporary)
  ↓
Call build() = atomic gate
  ↓
  Validate everything
  ↓
Product = immutable, fully-formed, valid object
  ↓
  Builder is discarded
  Product lives forever (immutably)
```

---

**← Prev:** [`14_Best_Practices.md`](./14_Best_Practices.md)  
**← Back to Start:** [`01_Why_Builder_Pattern.md`](./01_Why_Builder_Pattern.md)

---

> 🎯 **You have completed the full Builder Design Pattern curriculum!**  
> You now understand: the problem, the GoF pattern, 4 implementation variants, real-world usage,  
> library examples, 5 complete examples, 20 interview Q&As, best practices, and 13 common mistakes.
