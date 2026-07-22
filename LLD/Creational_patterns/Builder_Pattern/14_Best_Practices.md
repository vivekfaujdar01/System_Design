# Module 14 – Best Practices

> **Study order:** Read after `13_Interview_Questions.md`.  
> Follow these guidelines to write Builder code that is clean, safe, and production-ready.

---

## Table of Contents
1. [Naming Conventions](#1-naming-conventions)
2. [Mandatory Field Discipline](#2-mandatory-field-discipline)
3. [Validation Strategy](#3-validation-strategy)
4. [Immutability Rules](#4-immutability-rules)
5. [Collection Handling](#5-collection-handling)
6. [Builder API Design](#6-builder-api-design)
7. [Documentation](#7-documentation)
8. [When to Use Lombok vs Manual Builder](#8-when-to-use-lombok-vs-manual-builder)
9. [Thread Safety](#9-thread-safety)
10. [Quick Reference Checklist](#10-quick-reference-checklist)

---

## 1. Naming Conventions

### Class Names

```java
// ✅ Builder as static nested class
public class Employee {
    public static class Builder { ... }   // just "Builder" — context makes it clear
}

// ✅ Builder named with product (when it's a top-level class)
public class HttpRequestBuilder { ... }

// ❌ Avoid redundant suffixes
public class EmployeeBuilderClass { ... }   // "Class" is redundant
```

### Method Names

```java
// ✅ Concise, property-like naming (preferred in fluent APIs)
public Builder name(String name)        { ... }
public Builder salary(double salary)    { ... }
public Builder active(boolean active)   { ... }

// ✅ 'with' prefix — also acceptable, especially for clarity
public Builder withName(String name)    { ... }
public Builder withSalary(double s)     { ... }

// ❌ 'set' prefix — breaks fluent style, sounds like a JavaBean
public Builder setName(String name)     { ... }   // avoid in Builders

// ✅ For collections — 'add' prefix makes intent clear
public Builder addSkill(String skill)   { skills.add(skill); return this; }
public Builder addTag(String tag)       { tags.add(tag); return this; }
```

### Entry Points

```java
// ✅ Style 1: Construct Builder directly (most common)
new Employee.Builder("Alice", 28)

// ✅ Style 2: Static factory on the outer class (Lombok style)
Employee.builder()

// ✅ Style 3: Static factory with a meaningful name
HttpRequest.newBuilder()    // used by Java 11 HttpRequest
```

---

## 2. Mandatory Field Discipline

### Rule: Mandatory fields ALWAYS go in the Builder constructor

```java
// ✅ CORRECT — name and email are mandatory, so they're in the constructor
public static class Builder {
    private final String name;    // mandatory — final in Builder too
    private final String email;   // mandatory — final in Builder too
    private String phone = null;  // optional — setter only

    public Builder(String name, String email) {
        // Validate immediately — fail fast
        this.name  = Objects.requireNonNull(name,  "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
    }
}

// ✅ Compiler enforces: you cannot create Builder without name and email
new User.Builder("Alice", "alice@example.com")
    .phone("1234567890")
    .build();

// ❌ WRONG — mandatory fields as optional setters (can be forgotten!)
public Builder name(String name) { this.name = name; return this; }  // silent omission possible!
new User.Builder().phone("1234").build();   // forgot name — only caught at build()!
```

---

## 3. Validation Strategy

### Layer 1: Constructor (Mandatory Field Checks)

```java
public Builder(String name, int age) {
    // ✅ Use Objects.requireNonNull for clean null checks
    this.name = Objects.requireNonNull(name, "Name must not be null");

    // ✅ Business rule on mandatory field — check immediately
    if (age < 0 || age > 150)
        throw new IllegalArgumentException("Age must be 0–150. Got: " + age);

    this.age = age;
}
```

### Layer 2: Setters (Per-Field Checks)

```java
public Builder salary(double salary) {
    // ✅ Validate each optional field in its setter — fail fast
    if (salary < 0)
        throw new IllegalArgumentException("Salary cannot be negative. Got: " + salary);
    this.salary = salary;
    return this;
}

public Builder email(String email) {
    // ✅ Format validation in setter
    if (email != null && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
        throw new IllegalArgumentException("Invalid email format: " + email);
    this.email = email;
    return this;
}
```

### Layer 3: build() (Cross-Field / Business Rules)

```java
public Employee build() {
    // ✅ Cross-field rules — things you can only check after ALL fields are set
    if (level == Level.MANAGER && reportingManager == null)
        throw new IllegalStateException("Manager must have a reporting manager set.");

    if (salary > 1_000_000 && level == Level.INTERN)
        throw new IllegalStateException("Intern salary cannot exceed ₹10L.");

    if (isRemote && department == Department.OPERATIONS)
        throw new IllegalStateException("Operations dept cannot be fully remote.");

    // ✅ All good — create the product
    return new Employee(this);
}
```

### Golden Rule: Validate as Early as Possible

```
Constructor validation  →  fails when Builder is created (earliest)
Setter validation       →  fails when bad value is set (early)
build() validation      →  fails at build() time (cross-field only)
```

---

## 4. Immutability Rules

```java
// ✅ Rule 1: Declare class final
public final class Product { ... }

// ✅ Rule 2: All fields private + final
private final String name;
private final int    quantity;

// ✅ Rule 3: Private constructor — only Builder can call it
private Product(Builder builder) { ... }

// ✅ Rule 4: No setters on the Product
// public void setName(String name) { ... }  ← never add this!

// ✅ Rule 5: Defensive copies for mutable types
private Product(Builder builder) {
    this.tags = List.copyOf(builder.tags);          // immutable copy of list
    this.meta = Map.copyOf(builder.meta);           // immutable copy of map
    this.data = Arrays.copyOf(builder.data, builder.data.length); // copy of array
}

// ✅ Rule 6: Return unmodifiable views from getters
public List<String> getTags() {
    return Collections.unmodifiableList(tags);
}
```

---

## 5. Collection Handling

```java
public static class Builder {

    // ✅ Use mutable collection in Builder (you'll add to it step-by-step)
    private List<String> tags = new ArrayList<>();

    // ✅ Add one at a time — fluent
    public Builder tag(String tag) {
        if (tag != null && !tag.isBlank()) tags.add(tag);
        return this;
    }

    // ✅ Add many at once — convenience method
    public Builder tags(List<String> moreTags) {
        this.tags.addAll(moreTags);
        return this;
    }
}

// In Product constructor:
private final List<String> tags;
private Product(Builder b) {
    // ✅ Defensive copy — prevents external mutation of internal list
    this.tags = List.copyOf(b.tags);    // Java 10+
    // OR: Collections.unmodifiableList(new ArrayList<>(b.tags));
}

// In getter:
public List<String> getTags() {
    return tags;   // already unmodifiable (List.copyOf returns unmodifiable list)
}
```

---

## 6. Builder API Design

### Design for Readability at the Call Site

```java
// ✅ The call site should read like a natural sentence
Order order = new Order.Builder("ORD-001", customer)
    .product("Laptop", 1, 85000)
    .product("Mouse", 2, 1200)
    .shippingAddress("123 Main St")
    .expressDelivery()
    .giftWrapped()
    .coupon("SAVE10")
    .build();

// Ask yourself: "Can a new developer understand this without reading the Builder code?"
// If YES → good API design
// If NO  → rename methods or restructure
```

### Use Enums Instead of Raw Strings for Constrained Choices

```java
// ❌ Raw String — typos cause silent bugs
public Builder status(String status) { ... }
new Order.Builder(...).status("PENDING");   // "PENDNG" would compile silently!

// ✅ Enum — compile-time type safety
public enum OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
public Builder status(OrderStatus status) { ... }
new Order.Builder(...).status(OrderStatus.PENDING);   // typo = compilation error
```

### Avoid Primitive Booleans for Binary Choices

```java
// ❌ Hard to read — what does true mean here?
.expressDelivery(true)

// ✅ Method name encodes the meaning
.expressDelivery()       // call this to enable express delivery
.standardDelivery()      // call this to use standard (default)

// OR: use an enum
public enum DeliveryType { STANDARD, EXPRESS, SAME_DAY }
.deliveryType(DeliveryType.EXPRESS)
```

---

## 7. Documentation

```java
/**
 * Immutable representation of an Employee.
 * Use {@link Employee.Builder} to create instances.
 *
 * <pre>{@code
 * Employee emp = new Employee.Builder("EMP-001", "Alice", "alice@co.com", Department.ENGINEERING)
 *     .level(Level.SENIOR)
 *     .salary(120000)
 *     .isRemote(true)
 *     .build();
 * }</pre>
 */
public final class Employee { ... }

public static class Builder {
    /**
     * Creates a new Builder for an Employee.
     *
     * @param employeeId Unique employee identifier (e.g. "EMP-2024-001"). Cannot be blank.
     * @param name       Full name of the employee. Cannot be blank.
     * @param email      Work email address. Must be valid email format.
     * @param department Department assignment. Cannot be null.
     * @throws IllegalArgumentException if any mandatory parameter is invalid.
     */
    public Builder(String employeeId, String name, String email, Department department) { ... }

    /**
     * Sets the employee's salary in INR (Indian Rupee).
     * @param salary Annual salary. Must be non-negative.
     * @throws IllegalArgumentException if salary is negative.
     */
    public Builder salary(double salary) { ... }

    /**
     * Validates all field combinations and creates the immutable {@link Employee}.
     * @return A fully-constructed, immutable Employee instance.
     * @throws IllegalStateException if field combination violates a business rule.
     */
    public Employee build() { ... }
}
```

---

## 8. When to Use Lombok vs Manual Builder

| Situation                                      | Use Lombok `@Builder` | Use Manual Builder    |
|------------------------------------------------|-----------------------|-----------------------|
| Simple POJO with default-to-null optionals     | ✅ Yes                | Overkill              |
| Need custom validation in `build()`            | ❌ Need `@Builder` + manual override | ✅ Yes     |
| Need mandatory field enforcement via constructor | `@NonNull` + `@Builder` | ✅ Clearer          |
| Library/framework code (no Lombok dependency)  | ❌ Avoid              | ✅ Yes                |
| Interview / whiteboard                         | ❌ Not available      | ✅ Write manually     |
| Team knows Lombok well                         | ✅ Yes                | Either                |
| Complex cross-field validation rules           | 🟡 Possible but messy | ✅ Cleaner            |
| Inherited Builder (CRTP)                       | ❌ Lombok can't       | ✅ Manual required    |
| Step Builder (enforced order)                  | ❌ Lombok can't       | ✅ Manual required    |

---

## 9. Thread Safety

```java
// ✅ Builder: one per thread — NOT shared
// Each thread creates its own builder chain
ExecutorService pool = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100; i++) {
    final int id = i;
    pool.submit(() -> {
        // ✅ New Builder per task — no sharing, no race condition
        Product p = new Product.Builder("item-" + id)
            .quantity(id * 2)
            .build();
        process(p);
    });
}

// ❌ WRONG: Sharing a Builder across threads
Product.Builder sharedBuilder = new Product.Builder("base");
pool.submit(() -> sharedBuilder.quantity(10).build());   // race condition!
pool.submit(() -> sharedBuilder.quantity(20).build());   // quantity unpredictable!

// ✅ Product: freely shareable — immutable
Product config = new Product.Builder("config").quantity(100).build();
// Multiple threads can read config simultaneously — safe!
pool.submit(() -> System.out.println(config.getQuantity()));
pool.submit(() -> System.out.println(config.getQuantity()));
```

---

## 10. Quick Reference Checklist

Use this checklist every time you write a Builder:

```
PRODUCT CLASS:
  [ ] Declared as 'final'
  [ ] All fields are 'private final'
  [ ] Constructor is 'private' (only Builder can call it)
  [ ] No setters — only getters
  [ ] toString() implemented
  [ ] equals() and hashCode() implemented (if used as Map key or in Set)
  [ ] Defensive copies for List, Map, Set, arrays in constructor
  [ ] Getters return unmodifiable views for collections

BUILDER CLASS:
  [ ] Declared as 'public static class Builder'
  [ ] Mandatory fields are 'private final' in Builder
  [ ] Mandatory fields taken in Builder's constructor
  [ ] Constructor validates mandatory fields immediately
  [ ] Optional fields have sensible default values
  [ ] Each setter returns 'this' (for fluent chaining)
  [ ] Each setter validates its own field
  [ ] build() validates cross-field business rules
  [ ] build() returns new Product(this)

API DESIGN:
  [ ] Call site reads naturally (self-documenting)
  [ ] Constrained choices use enums, not raw Strings
  [ ] Boolean choices use named methods, not .feature(true/false)
  [ ] Javadoc on Builder constructor and build()
```

---

**← Prev:** [`13_Interview_Questions.md`](./13_Interview_Questions.md)  
**Next →** [`15_Common_Mistakes.md`](./15_Common_Mistakes.md)
