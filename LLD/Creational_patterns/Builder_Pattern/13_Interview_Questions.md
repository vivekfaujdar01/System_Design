# Module 13 – Interview Questions

> **Study order:** Read after `12_Real_World_Examples.md`.  
> Covers every commonly asked Builder pattern interview question with clear, detailed answers.

---

## Table of Contents
- [Conceptual Questions](#conceptual-questions)
- [Design & Code Questions](#design--code-questions)
- [Comparison Questions](#comparison-questions)
- [Advanced / Deep-Dive Questions](#advanced--deep-dive-questions)
- [System Design Integration](#system-design-integration)

---

## Conceptual Questions

---

**Q1. What is the Builder Design Pattern?**

> **Answer:**  
> Builder is a **GoF Creational Design Pattern** that separates the construction of a complex object from its representation.  
> Instead of using a constructor with many parameters, you create a nested `Builder` class that:
> 1. Accepts mandatory fields in its constructor
> 2. Provides fluent setter methods for optional fields (each returns `this`)
> 3. Provides a `build()` method that validates and creates the final immutable object
>
> The result is a **readable, validated, immutable** object created through a clean API.

---

**Q2. What problem does Builder solve?**

> **Answer:**  
> Builder solves three problems:
> 1. **Telescoping constructors** — When a class has many optional fields, you'd need dozens of constructor overloads. Builder gives named, optional parameters.
> 2. **JavaBeans anti-pattern** — Using setters leaves objects in invalid intermediate states and makes immutability impossible. Builder is atomic — the object is only created when `build()` succeeds.
> 3. **Readability** — `new Pizza("Large", "Thin", true, false, true, false)` is unreadable. Builder gives you `.size("Large").crust("Thin").cheese(true)`.

---

**Q3. When should you use the Builder Pattern?**

> **Answer:**  
> Use Builder when:
> - The object has **4 or more fields**, especially optional ones
> - You want the created object to be **immutable**
> - You need to **validate combinations** of fields before creation
> - Multiple valid configurations of the same object type exist
> - You want a **readable call site** (self-documenting code)
>
> Don't use Builder for simple objects with 1–3 fields — a plain constructor is cleaner.

---

**Q4. What are the participants of the Builder pattern?**

> **Answer:**
> - **Product**: The complex object being constructed (immutable, private constructor)
> - **Builder**: Interface or abstract class declaring all construction steps
> - **ConcreteBuilder**: Implements the steps; holds intermediate state; provides `getResult()`
> - **Director** *(optional)*: Orchestrates builder steps in a specific order, encapsulates recipes
> - **Client**: Creates the builder, optionally uses the director, retrieves the product

---

**Q5. Is the Director mandatory in the Builder pattern?**

> **Answer:**  
> No. The Director is optional in modern usage.  
> - Use a Director when the same construction sequence must be reused in multiple places, or when you want named presets (`constructLuxury()`, `constructBudget()`).
> - Skip the Director when clients use the Builder's fluent API directly (the common modern Java style — OkHttp, Retrofit, Java 11 HttpRequest all skip the Director).

---

**Q6. What is a Fluent Interface? How does it relate to Builder?**

> **Answer:**  
> A Fluent Interface is an API design style where methods return `this` (the current object), enabling method chaining. In the Builder pattern, each setter method returns `this`, allowing:
> ```java
> Employee emp = new Employee.Builder("Alice", 28)
>     .department("Engineering")
>     .salary(120000)
>     .build();
> ```
> Without `return this`, you'd need to call each setter on a separate line without chaining. Fluent interface makes Builder APIs concise and readable — the code reads like a natural language sentence.

---

## Design & Code Questions

---

**Q7. Write a Builder for a `Person` class with name, age, email (mandatory) and address, phone (optional).**

```java
public final class Person {
    private final String name;
    private final int    age;
    private final String email;
    private final String address;
    private final String phone;

    private Person(Builder b) {
        this.name    = b.name;
        this.age     = b.age;
        this.email   = b.email;
        this.address = b.address;
        this.phone   = b.phone;
    }

    public String getName()    { return name; }
    public int    getAge()     { return age; }
    public String getEmail()   { return email; }
    public String getAddress() { return address; }
    public String getPhone()   { return phone; }

    @Override
    public String toString() {
        return "Person{name=" + name + ", age=" + age + ", email=" + email
             + ", address=" + address + ", phone=" + phone + "}";
    }

    public static class Builder {
        // Mandatory
        private final String name;
        private final int    age;
        private final String email;
        // Optional
        private String address = null;
        private String phone   = null;

        public Builder(String name, int age, String email) {
            if (name == null || name.isBlank())   throw new IllegalArgumentException("Name required.");
            if (age < 0 || age > 150)             throw new IllegalArgumentException("Invalid age.");
            if (email == null || !email.contains("@")) throw new IllegalArgumentException("Valid email required.");
            this.name  = name;
            this.age   = age;
            this.email = email;
        }

        public Builder address(String address) { this.address = address; return this; }
        public Builder phone(String phone)     { this.phone = phone; return this; }

        public Person build() {
            return new Person(this);
        }
    }
}

// Usage
Person p = new Person.Builder("Alice", 28, "alice@example.com")
    .address("123 Main St, Mumbai")
    .phone("+91-9876543210")
    .build();
```

---

**Q8. How does Builder achieve immutability?**

> **Answer:**
> 1. All fields in the Product are declared `private final`
> 2. The Product has a `private` constructor — only the inner `Builder` can call it
> 3. No setters on the Product — only getters
> 4. The class is declared `final` to prevent subclassing (which could add mutability)
> 5. `build()` passes all values to the Product constructor in one atomic step — no intermediate invalid state
> 6. For collections/arrays, defensive copies are made: `List.copyOf(builder.list)`

---

**Q9. Why is the Builder class a static nested class (not a regular inner class)?**

> **Answer:**  
> A **static nested class** can be instantiated without an instance of the outer class:
> ```java
> new Employee.Builder("Alice", 28)  // no Employee instance needed
> ```
> A regular (non-static) **inner class** would require an outer class instance first:
> ```java
> new Employee().new Builder(...)   // ← ugly and wrong!
> ```
> Since we haven't created an `Employee` yet (we're building it), the Builder must be `static` so it can be created first, independently, before the Product exists.

---

**Q10. What happens if `build()` is not called in a Builder?**

> **Answer:**  
> If `build()` is not called, the `Builder` object is created and configured but no `Product` is ever created. The Builder object just gets garbage collected. No object is produced. This is a **misuse** of the pattern — every Builder chain should end with `build()`.  
> Some IDEs and static analysis tools (SpotBugs, SonarQube) warn about this.

---

**Q11. Can a Builder be reused after `build()` is called?**

> **Answer:**  
> Technically yes — the Builder object still holds its state. But it's **not recommended** because:
> 1. You might accidentally share/modify the same builder state
> 2. Some implementations intentionally reset or invalidate the builder after `build()`
>
> The safe practice is: **create a new Builder for each new Product**. If you want slight variations, use Lombok's `toBuilder()` pattern:
> ```java
> Employee updated = existingEmployee.toBuilder().salary(150000).build();
> ```

---

## Comparison Questions

---

**Q12. What is the difference between Builder and Factory patterns?**

> **Answer:**
> | Aspect | Factory | Builder |
> |--------|---------|---------|
> | Core question | Which TYPE to create? | How to CONFIGURE one type? |
> | Returns | Different subtypes | Same type, different configs |
> | Fields | Usually simple | Many optional fields |
> | Steps | One call | Multiple setter calls + build() |
> | Immutability | Not inherent | Built-in by design |
>
> Factory = type selection. Builder = complex configuration.

---

**Q13. How is Builder different from the Abstract Factory pattern?**

> **Answer:**  
> - **Abstract Factory** creates **families of related objects** — multiple products that must work together (e.g., `Button + Checkbox + TextBox` all in Windows style).
> - **Builder** creates **one complex object** step-by-step with many configurable options.
>
> Abstract Factory produces *multiple different objects*. Builder produces *one well-configured object*.

---

**Q14. How is Builder related to Lombok's `@Builder`?**

> **Answer:**  
> Lombok's `@Builder` is an annotation processor that **auto-generates** the exact static nested Builder pattern (Effective Java style) at compile time. It generates:
> - Static `builder()` method
> - Static inner `XxxBuilder` class
> - Fluent setter methods returning `this`
> - `build()` method
>
> It saves you from writing boilerplate but follows the same exact pattern you'd write manually.

---

## Advanced / Deep-Dive Questions

---

**Q15. How do you handle Builder inheritance in Java?**

> **Answer:**  
> Use the **CRTP (Curiously Recurring Template Pattern)** with generics:
> ```java
> // Base
> public abstract static class Builder<T extends Builder<T>> {
>     private String name;
>     @SuppressWarnings("unchecked")
>     public T name(String name) { this.name = name; return (T) this; }
>     public abstract Product build();
> }
>
> // Concrete
> public static class SpecificBuilder extends Builder<SpecificBuilder> {
>     private int extra;
>     public SpecificBuilder extra(int e) { this.extra = e; return this; }
>     public SpecificProduct build() { return new SpecificProduct(this); }
> }
>
> // Chain works correctly — name() returns SpecificBuilder, not Builder
> SpecificProduct p = new SpecificBuilder().name("X").extra(42).build();
> ```

---

**Q16. What is the Step Builder pattern?**

> **Answer:**  
> The Step Builder is a variant where each step is represented by a **separate interface**. The `build()` method is only available after all required steps are completed — enforced at **compile time**.
> ```java
> // Entry point returns first step interface only
> QueryBuilder.newQuery()        // returns TableStep
>     .fromTable("users")        // returns ColumnStep
>     .selectColumns("id","name")// returns ConditionStep
>     .where("age > 18")         // returns BuildStep
>     .build();                  // only now can you call build()
>
> // This won't compile — build() not available on TableStep:
> // QueryBuilder.newQuery().build();  ← compilation error!
> ```

---

**Q17. Is Builder thread-safe?**

> **Answer:**  
> - The **Builder itself is NOT thread-safe** — if multiple threads configure the same Builder, you'll have race conditions. Always use **one Builder per thread**.
> - The **Product produced by Builder IS thread-safe** — because the product is immutable. All fields are `final`, no setters exist, so it can be safely shared across any number of threads.
>
> Rule: Create one Builder per object creation; share the resulting Product freely.

---

**Q18. What is the difference between `build()` throwing `IllegalStateException` vs `IllegalArgumentException`?**

> **Answer:**
> - **`IllegalArgumentException`**: A single field has an invalid value (bad format, null, out of range). Thrown in the Builder constructor or in setter methods immediately when the bad value is provided. *"This one argument is wrong."*
> - **`IllegalStateException`**: A cross-field business rule is violated — individual fields are valid in isolation, but their combination is invalid. Thrown in `build()` after all fields are set. *"These fields together create an invalid state."*
>
> Example:
> ```java
> // IllegalArgumentException — one field is wrong
> builder.age(-5);   // immediately throws
>
> // IllegalStateException — combination is wrong
> builder.accountType("CURRENT").creditLimit(0).build();  // throws in build()
> ```

---

## System Design Integration

---

**Q19. Where would you use Builder in a real system design?**

> **Answer:**
> 1. **Configuration objects**: `DatabaseConfig`, `ServerConfig`, `CacheConfig` — complex, immutable, constructed once at startup
> 2. **HTTP clients / requests**: `OkHttpClient`, `HttpRequest`, `RestTemplate` configuration
> 3. **Test fixtures**: `new UserBuilder().withRole(ADMIN).withEmail(...).build()` — readable test data
> 4. **Database query objects**: `QueryBuilder.from("users").select(...).where(...).build()`
> 5. **Domain objects**: Any entity with many optional fields — `Order`, `Product`, `Employee`
> 6. **DTO construction**: When building response DTOs from multiple service calls

---

**Q20. How would you implement a "copy-and-modify" operation using Builder?**

> **Answer:**  
> This is the `toBuilder()` / `withXxx()` pattern:
> ```java
> // Option 1: toBuilder() (Lombok supports this natively)
> Employee updated = existingEmployee.toBuilder()
>     .salary(150000)
>     .level(Level.SENIOR)
>     .build();
>
> // Option 2: Implement manually in the Product
> public Employee withSalary(double newSalary) {
>     return new Employee.Builder(this.employeeId, this.name, this.email, this.department)
>         .level(this.level)
>         .salary(newSalary)      // ← only this changes
>         .joiningDate(this.joiningDate)
>         // ... copy all other fields
>         .build();
> }
> ```
> This is essential for immutable objects — you "change" them by creating a new one based on the old.

---

**← Prev:** [`12_Real_World_Examples.md`](./12_Real_World_Examples.md)  
**Next →** [`14_Best_Practices.md`](./14_Best_Practices.md)
