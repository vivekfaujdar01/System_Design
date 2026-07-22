# Module 06 – Fluent Builder

> **Study order:** Read after `05_Builder_with_Mandatory_Fields.md`.  
> This module deep-dives into method chaining, `return this`, and how to design readable APIs.

---

## Table of Contents
1. [What Is a Fluent Interface?](#1-what-is-a-fluent-interface)
2. [The return this Mechanism](#2-the-return-this-mechanism)
3. [Fluent vs Non-Fluent – Side by Side](#3-fluent-vs-non-fluent--side-by-side)
4. [Designing a Readable Fluent API](#4-designing-a-readable-fluent-api)
5. [Complete Example – SQL Query Builder](#5-complete-example--sql-query-builder)
6. [Returning a Subtype (Covariant Return)](#6-returning-a-subtype-covariant-return)
7. [Fluent Builder with Enums](#7-fluent-builder-with-enums)
8. [Common Fluent Builder Mistakes](#8-common-fluent-builder-mistakes)

---

## 1. What Is a Fluent Interface?

A **Fluent Interface** is an API design style where:
- Methods return **the current object** (`this`)
- Multiple operations can be **chained** in a single expression
- The resulting code reads **like a natural language sentence**

```java
// Without Fluent Interface
QueryBuilder qb = new QueryBuilder();
qb.select("name", "email");
qb.from("users");
qb.where("age > 18");
qb.orderBy("name");
qb.limit(10);
String sql = qb.build();

// With Fluent Interface
String sql = new QueryBuilder()
    .select("name", "email")
    .from("users")
    .where("age > 18")
    .orderBy("name")
    .limit(10)
    .build();
```

The fluent version:
- Uses **less code** (no repeated variable name)
- **Reads like a sentence**: select name, email from users where age > 18...
- Is **harder to accidentally misorder** (each method is clearly labelled)

---

## 2. The `return this` Mechanism

This is the single technical ingredient that enables fluent chaining:

```java
public class CarBuilder {

    private String brand;
    private String color;
    private int    year;

    // ❌ Without 'return this' — cannot chain
    public void brand(String brand) {
        this.brand = brand;
        // returns nothing (void)
    }

    // ✅ With 'return this' — can chain
    public CarBuilder brand(String brand) {
        this.brand = brand;
        return this;   // ← returns the SAME CarBuilder object
    }

    public CarBuilder color(String color) {
        this.color = color;
        return this;   // ← same object, same reference
    }

    public CarBuilder year(int year) {
        this.year = year;
        return this;
    }
}
```

**How the chain works — step by step:**

```java
CarBuilder b = new CarBuilder()   // ← creates CarBuilder, assigns to 'b'
    .brand("Toyota")              // ← b.brand("Toyota") → sets brand, returns b
    .color("Red")                 // ← b.color("Red")   → sets color, returns b
    .year(2024);                  // ← b.year(2024)     → sets year,  returns b
```

```
Memory:
  CarBuilder object at memory address 0xABCD12
    brand = null
    color = null
    year  = 0

After .brand("Toyota"):
    brand = "Toyota"    ← address still 0xABCD12 (same object!)

After .color("Red"):
    brand = "Toyota"
    color = "Red"       ← address still 0xABCD12

After .year(2024):
    brand = "Toyota"
    color = "Red"
    year  = 2024        ← address still 0xABCD12
```

---

## 3. Fluent vs Non-Fluent – Side by Side

```java
// ══════════════════════════════════════════════
// Non-Fluent (verbose, repetitive)
// ══════════════════════════════════════════════
HttpRequest.Builder builder = new HttpRequest.Builder();
builder.setMethod("POST");
builder.setUrl("https://api.example.com/data");
builder.setBody("{\"key\":\"value\"}");
builder.setHeader("Content-Type", "application/json");
builder.setHeader("Authorization", "Bearer token123");
builder.setTimeout(5000);
HttpRequest req = builder.build();

// ══════════════════════════════════════════════
// Fluent (concise, readable)
// ══════════════════════════════════════════════
HttpRequest req = new HttpRequest.Builder()
    .method("POST")
    .url("https://api.example.com/data")
    .body("{\"key\":\"value\"}")
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer token123")
    .timeout(5000)
    .build();
```

Both create identical objects. The fluent version:
- Has **no repeated `builder.`** — less visual noise
- Is **one expression** — can be used inline in assignments or method calls
- **Groups related configuration** — all HTTP config is together

---

## 4. Designing a Readable Fluent API

Good fluent API naming reads like a sentence:

```java
// ✅ Reads naturally (verb/noun style)
Email email = new Email.Builder()
    .from("alice@example.com")
    .to("bob@example.com")
    .subject("Meeting Tomorrow")
    .body("Hi Bob, let's meet at 10am.")
    .withAttachment("agenda.pdf")
    .send();

// ❌ Poor naming — doesn't read naturally
Email email = new Email.Builder()
    .setSender("alice@example.com")
    .setReceiver("bob@example.com")
    .setTitle("Meeting Tomorrow")
    .setContent("Hi Bob, let's meet at 10am.")
    .addAttachmentFile("agenda.pdf")
    .sendNow();
```

**Naming conventions for fluent builders:**

| Instead of            | Prefer                 | Reason                          |
|-----------------------|------------------------|---------------------------------|
| `setName("Alice")`    | `name("Alice")`        | Shorter, reads as property      |
| `setSalary(50000)`    | `salary(50000)`        | Fluent, no redundant "set"      |
| `setIsActive(true)`   | `active(true)`         | Boolean doesn't need "is"       |
| `addTag("java")`      | `tag("java")`          | Simpler                         |
| `withDiscount(10)`    | `discount(10)` or `withDiscount(10)` | `with` prefix is also fine for clarity |

---

## 5. Complete Example – SQL Query Builder

```java
// SqlQuery.java
public final class SqlQuery {

    private final String   table;
    private final String[] columns;
    private final String   condition;
    private final String   orderByColumn;
    private final String   orderDirection;
    private final int      limitCount;
    private final int      offsetCount;

    private SqlQuery(Builder builder) {
        this.table          = builder.table;
        this.columns        = builder.columns;
        this.condition      = builder.condition;
        this.orderByColumn  = builder.orderByColumn;
        this.orderDirection = builder.orderDirection;
        this.limitCount     = builder.limitCount;
        this.offsetCount    = builder.offsetCount;
    }

    public String toSql() {
        StringBuilder sql = new StringBuilder("SELECT ");

        // Columns
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }

        // FROM
        sql.append(" FROM ").append(table);

        // WHERE
        if (condition != null && !condition.isBlank()) {
            sql.append(" WHERE ").append(condition);
        }

        // ORDER BY
        if (orderByColumn != null && !orderByColumn.isBlank()) {
            sql.append(" ORDER BY ").append(orderByColumn)
               .append(" ").append(orderDirection);
        }

        // LIMIT
        if (limitCount > 0) {
            sql.append(" LIMIT ").append(limitCount);
        }

        // OFFSET
        if (offsetCount > 0) {
            sql.append(" OFFSET ").append(offsetCount);
        }

        return sql.toString();
    }

    // ════════════════════════════════════════════════
    // FLUENT BUILDER
    // ════════════════════════════════════════════════
    public static class Builder {

        // Mandatory
        private final String table;

        // Optional
        private String[] columns        = null;
        private String   condition      = null;
        private String   orderByColumn  = null;
        private String   orderDirection = "ASC";
        private int      limitCount     = 0;
        private int      offsetCount    = 0;

        public Builder(String table) {
            if (table == null || table.isBlank())
                throw new IllegalArgumentException("Table name is required.");
            this.table = table;
        }

        // Fluent setter — varargs allows: .select("id", "name", "email")
        public Builder select(String... columns) {
            this.columns = columns;
            return this;
        }

        public Builder where(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder orderBy(String column) {
            this.orderByColumn  = column;
            this.orderDirection = "ASC";
            return this;
        }

        public Builder orderByDesc(String column) {
            this.orderByColumn  = column;
            this.orderDirection = "DESC";
            return this;
        }

        public Builder limit(int count) {
            if (count < 1) throw new IllegalArgumentException("Limit must be at least 1.");
            this.limitCount = count;
            return this;
        }

        public Builder offset(int count) {
            if (count < 0) throw new IllegalArgumentException("Offset cannot be negative.");
            this.offsetCount = count;
            return this;
        }

        public SqlQuery build() {
            if (offsetCount > 0 && limitCount == 0)
                throw new IllegalStateException("OFFSET requires LIMIT to be set.");
            return new SqlQuery(this);
        }
    }
}

// Main.java — Client
public class Main {
    public static void main(String[] args) {

        // Query 1: Select all users
        SqlQuery q1 = new SqlQuery.Builder("users")
            .build();
        System.out.println(q1.toSql());
        // → SELECT * FROM users

        // Query 2: Select specific columns with condition
        SqlQuery q2 = new SqlQuery.Builder("employees")
            .select("id", "name", "salary")
            .where("department = 'Engineering'")
            .orderBy("name")
            .limit(20)
            .build();
        System.out.println(q2.toSql());
        // → SELECT id, name, salary FROM employees WHERE department = 'Engineering' ORDER BY name ASC LIMIT 20

        // Query 3: Paginated query
        SqlQuery q3 = new SqlQuery.Builder("products")
            .select("id", "name", "price")
            .where("price < 1000")
            .orderByDesc("price")
            .limit(10)
            .offset(20)
            .build();
        System.out.println(q3.toSql());
        // → SELECT id, name, price FROM products WHERE price < 1000 ORDER BY price DESC LIMIT 10 OFFSET 20
    }
}
```

**Output:**
```
SELECT * FROM users
SELECT id, name, salary FROM employees WHERE department = 'Engineering' ORDER BY name ASC LIMIT 20
SELECT id, name, price FROM products WHERE price < 1000 ORDER BY price DESC LIMIT 10 OFFSET 20
```

---

## 6. Returning a Subtype (Covariant Return)

When you extend a Builder, you must ensure chained methods return the **subtype**, not the base type.  
Use **generics (CRTP)** for this:

```java
// Base Builder
public abstract static class BaseBuilder<T extends BaseBuilder<T>> {
    private String name;

    @SuppressWarnings("unchecked")
    public T name(String name) {
        this.name = name;
        return (T) this;   // ← cast to concrete subtype
    }

    public abstract Object build();
}

// Concrete Builder — extends base, adds its own fields
public static class PersonBuilder extends BaseBuilder<PersonBuilder> {
    private int age;

    public PersonBuilder age(int age) {
        this.age = age;
        return this;
    }

    @Override
    public Person build() { return new Person(this); }
}

// ✅ Chain works correctly — name() returns PersonBuilder (not BaseBuilder)
Person p = new PersonBuilder()
    .name("Alice")   // returns PersonBuilder (thanks to CRTP)
    .age(25)         // PersonBuilder method — would fail if name() returned BaseBuilder
    .build();
```

---

## 7. Fluent Builder with Enums

Using enums instead of raw Strings for constrained choices:

```java
public enum AccountType { SAVINGS, CURRENT, FIXED_DEPOSIT }
public enum SortOrder   { ASC, DESC }

public static class Builder {
    private AccountType accountType = AccountType.SAVINGS;
    private SortOrder   sortOrder   = SortOrder.ASC;

    // ✅ Compile-time type safety — can't pass "SAVINGGS" (typo) 
    public Builder accountType(AccountType type) {
        this.accountType = type;
        return this;
    }

    public Builder sortOrder(SortOrder order) {
        this.sortOrder = order;
        return this;
    }
}

// Usage
BankAccount acc = new BankAccount.Builder("ACC-001", "Alice", "SBIN0001234")
    .accountType(AccountType.CURRENT)   // ← compiler validates this
    .build();
```

---

## 8. Common Fluent Builder Mistakes

| Mistake                                      | Problem                                | Fix                                    |
|----------------------------------------------|----------------------------------------|----------------------------------------|
| Forgetting `return this` in a setter         | Cannot chain; returns `void`           | Always `return this` in every setter  |
| Reusing a Builder after `build()`            | Builder still holds old state          | Create a new Builder each time         |
| Mutating the Product after `build()`         | Breaks immutability                    | Never expose setters on Product        |
| Naming setters `setXxx()` in a fluent API   | Inconsistent style                     | Use `xxx()` form (without `set`)       |
| Not returning `this` in the final setter     | Breaks the chain at the last step      | Every single setter must `return this` |
| Sharing a Builder across threads             | Race condition on Builder's fields     | Create one Builder per thread          |

---

**← Prev:** [`05_Builder_with_Mandatory_Fields.md`](./05_Builder_with_Mandatory_Fields.md)  
**Next →** [`07_Director_Class.md`](./07_Director_Class.md)
