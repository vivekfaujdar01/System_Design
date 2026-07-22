# Module 05 – Builder with Mandatory Fields

> **Study order:** Read after `04_Static_Nested_Builder.md`.  
> This module covers how to distinguish mandatory vs optional fields and enforce validation rigorously.

---

## Table of Contents
1. [The Challenge](#1-the-challenge)
2. [Strategy 1 – Mandatory Fields in Builder Constructor](#2-strategy-1--mandatory-fields-in-builder-constructor)
3. [Strategy 2 – Required + Optional Clearly Separated](#3-strategy-2--required--optional-clearly-separated)
4. [Validation Inside Builder](#4-validation-inside-builder)
5. [Complete Example – Bank Account](#5-complete-example--bank-account)
6. [Types of Validation in build()](#6-types-of-validation-in-build)
7. [Null Safety Patterns](#7-null-safety-patterns)
8. [Summary Table](#8-summary-table)

---

## 1. The Challenge

When a class has both mandatory and optional fields, you must:

```
Mandatory fields → MUST be provided. Object is INVALID without them.
Optional fields  → CAN be omitted. Sensible defaults or null is acceptable.
```

The challenge:
```
❌ A pure fluent builder allows SKIPPING any field (including mandatory ones)
   until build() is called — so the mandatory guarantee must be enforced EXPLICITLY.

❌ If you put all fields in the Builder's constructor, you're back to the
   telescoping constructor problem.

✅ Solution: Mandatory fields in Builder constructor + Optional fields as setter methods
```

---

## 2. Strategy 1 – Mandatory Fields in Builder Constructor

**Rule:** Put ONLY mandatory fields in `Builder(...)`. Everything else is a setter.

```java
// Mandatory: accountNumber, holderName, ifscCode
// Optional: branch, accountType, creditLimit, nomineeName

public static class Builder {

    // Mandatory — in constructor
    private final String accountNumber;
    private final String holderName;
    private final String ifscCode;

    // Optional — as setters, with defaults
    private String accountType  = "SAVINGS";
    private String branch       = "Main Branch";
    private double creditLimit  = 0.0;
    private String nomineeName  = null;

    // Constructor accepts ONLY mandatory fields
    public Builder(String accountNumber, String holderName, String ifscCode) {
        this.accountNumber = accountNumber;
        this.holderName    = holderName;
        this.ifscCode      = ifscCode;
    }

    // Optional setters ...
}
```

**Effect at call site:**
```java
// ✅ Compiler FORCES you to provide accountNumber, holderName, ifscCode
BankAccount acc = new BankAccount.Builder("ACC-001", "Alice", "SBIN0001234")
    .accountType("CURRENT")
    .creditLimit(50000)
    .build();

// ❌ This won't compile — Builder() with no args doesn't exist
BankAccount bad = new BankAccount.Builder()
    .holderName("Alice")
    .build();   // Compilation error!
```

---

## 3. Strategy 2 – Required + Optional Clearly Separated

A pattern for **documenting** intent clearly:

```java
public static class Builder {

    // ══════════════════════════════════════
    // REQUIRED FIELDS (no defaults)
    // Must be provided via constructor
    // ══════════════════════════════════════
    private final String accountNumber;   // required
    private final String holderName;      // required
    private final String ifscCode;        // required

    // ══════════════════════════════════════
    // OPTIONAL FIELDS (with defaults)
    // Caller may override using setter methods
    // ══════════════════════════════════════
    private String accountType  = "SAVINGS";      // optional
    private String branch       = "Main Branch";  // optional
    private double creditLimit  = 0.0;            // optional
    private String nomineeName  = null;           // optional (null = no nominee)
    private boolean isActive    = true;           // optional

    // ...
}
```

This separation makes the code **self-documenting**. A new developer can instantly tell which fields are required.

---

## 4. Validation Inside Builder

Validation has **two stages**:

### Stage 1 — Constructor Validation (Fail-Fast on Mandatory Fields)

```java
public Builder(String accountNumber, String holderName, String ifscCode) {

    // ── Null checks ────────────────────────────────────────────────────
    if (accountNumber == null || accountNumber.isBlank())
        throw new IllegalArgumentException("Account number is required.");

    if (holderName == null || holderName.isBlank())
        throw new IllegalArgumentException("Holder name is required.");

    if (ifscCode == null || ifscCode.isBlank())
        throw new IllegalArgumentException("IFSC code is required.");

    // ── Format validation ──────────────────────────────────────────────
    if (!ifscCode.matches("[A-Z]{4}0[A-Z0-9]{6}"))
        throw new IllegalArgumentException("Invalid IFSC format: " + ifscCode);

    if (!accountNumber.matches("ACC-\\d{3,10}"))
        throw new IllegalArgumentException("Account number must be 'ACC-' followed by digits.");

    this.accountNumber = accountNumber;
    this.holderName    = holderName;
    this.ifscCode      = ifscCode;
}
```

> **Fail-fast**: Throw immediately when a bad value is provided — don't wait for `build()`.

### Stage 2 — build() Validation (Cross-Field Rules)

```java
public BankAccount build() {

    // ── Cross-field / business rules ───────────────────────────────────

    // Rule 1: CURRENT accounts must have a credit limit
    if (accountType.equalsIgnoreCase("CURRENT") && creditLimit <= 0) {
        throw new IllegalStateException(
            "CURRENT accounts must have a positive credit limit.");
    }

    // Rule 2: Credit limit cannot exceed ₹10 lakhs for SAVINGS
    if (accountType.equalsIgnoreCase("SAVINGS") && creditLimit > 1_000_000) {
        throw new IllegalStateException(
            "SAVINGS accounts cannot have credit limit above ₹10,00,000.");
    }

    // Rule 3: Nominee name must be different from holder name
    if (nomineeName != null && nomineeName.equalsIgnoreCase(holderName)) {
        throw new IllegalStateException(
            "Nominee name cannot be the same as the account holder name.");
    }

    return new BankAccount(this);
}
```

### Stage 3 — Setter Validation (Per-Field Rules)

```java
public Builder creditLimit(double creditLimit) {
    if (creditLimit < 0)
        throw new IllegalArgumentException("Credit limit cannot be negative. Got: " + creditLimit);
    this.creditLimit = creditLimit;
    return this;
}

public Builder accountType(String accountType) {
    if (!accountType.equalsIgnoreCase("SAVINGS") && !accountType.equalsIgnoreCase("CURRENT"))
        throw new IllegalArgumentException("Account type must be SAVINGS or CURRENT.");
    this.accountType = accountType.toUpperCase();
    return this;
}
```

---

## 5. Complete Example – Bank Account

```java
// BankAccount.java

public final class BankAccount {

    // ── Product fields (all final, all private) ────────────────────────
    private final String  accountNumber;
    private final String  holderName;
    private final String  ifscCode;
    private final String  accountType;
    private final String  branch;
    private final double  creditLimit;
    private final String  nomineeName;
    private final boolean isActive;

    // ── Private constructor ────────────────────────────────────────────
    private BankAccount(Builder builder) {
        this.accountNumber = builder.accountNumber;
        this.holderName    = builder.holderName;
        this.ifscCode      = builder.ifscCode;
        this.accountType   = builder.accountType;
        this.branch        = builder.branch;
        this.creditLimit   = builder.creditLimit;
        this.nomineeName   = builder.nomineeName;
        this.isActive      = builder.isActive;
    }

    // ── Getters ────────────────────────────────────────────────────────
    public String  getAccountNumber() { return accountNumber; }
    public String  getHolderName()    { return holderName; }
    public String  getIfscCode()      { return ifscCode; }
    public String  getAccountType()   { return accountType; }
    public String  getBranch()        { return branch; }
    public double  getCreditLimit()   { return creditLimit; }
    public String  getNomineeName()   { return nomineeName; }
    public boolean isActive()         { return isActive; }

    @Override
    public String toString() {
        return "BankAccount {" +
               "\n  accountNumber = " + accountNumber +
               "\n  holderName    = " + holderName +
               "\n  ifscCode      = " + ifscCode +
               "\n  accountType   = " + accountType +
               "\n  branch        = " + branch +
               "\n  creditLimit   = ₹" + creditLimit +
               "\n  nominee       = " + (nomineeName != null ? nomineeName : "None") +
               "\n  active        = " + isActive +
               "\n}";
    }

    // ══════════════════════════════════════════════════════════════════════
    // STATIC NESTED BUILDER
    // ══════════════════════════════════════════════════════════════════════
    public static class Builder {

        // REQUIRED FIELDS
        private final String accountNumber;
        private final String holderName;
        private final String ifscCode;

        // OPTIONAL FIELDS
        private String  accountType = "SAVINGS";
        private String  branch      = "Main Branch";
        private double  creditLimit = 0.0;
        private String  nomineeName = null;
        private boolean isActive    = true;

        // ── Constructor (mandatory fields) ─────────────────────────────
        public Builder(String accountNumber, String holderName, String ifscCode) {
            if (accountNumber == null || accountNumber.isBlank())
                throw new IllegalArgumentException("Account number is required.");
            if (holderName == null || holderName.isBlank())
                throw new IllegalArgumentException("Holder name is required.");
            if (ifscCode == null || ifscCode.isBlank())
                throw new IllegalArgumentException("IFSC code is required.");
            if (!ifscCode.matches("[A-Z]{4}0[A-Z0-9]{6}"))
                throw new IllegalArgumentException("Invalid IFSC format: " + ifscCode);

            this.accountNumber = accountNumber;
            this.holderName    = holderName;
            this.ifscCode      = ifscCode;
        }

        // ── Optional setters ──────────────────────────────────────────
        public Builder accountType(String accountType) {
            if (!accountType.equalsIgnoreCase("SAVINGS") && !accountType.equalsIgnoreCase("CURRENT"))
                throw new IllegalArgumentException("Account type must be SAVINGS or CURRENT.");
            this.accountType = accountType.toUpperCase();
            return this;
        }

        public Builder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder creditLimit(double creditLimit) {
            if (creditLimit < 0)
                throw new IllegalArgumentException("Credit limit cannot be negative.");
            this.creditLimit = creditLimit;
            return this;
        }

        public Builder nomineeName(String nomineeName) {
            this.nomineeName = nomineeName;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        // ── build() — cross-field validation + create Product ─────────
        public BankAccount build() {
            if (accountType.equals("CURRENT") && creditLimit <= 0)
                throw new IllegalStateException("CURRENT accounts must have a positive credit limit.");
            if (accountType.equals("SAVINGS") && creditLimit > 1_000_000)
                throw new IllegalStateException("SAVINGS credit limit cannot exceed ₹10,00,000.");
            if (nomineeName != null && nomineeName.equalsIgnoreCase(holderName))
                throw new IllegalStateException("Nominee cannot be the same as account holder.");

            return new BankAccount(this);
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Main.java — Client
// ══════════════════════════════════════════════════════════════════════════
public class Main {
    public static void main(String[] args) {

        // ✅ Valid Savings Account
        BankAccount savings = new BankAccount.Builder("ACC-1001", "Priya Kapoor", "SBIN0001234")
            .branch("South Delhi Branch")
            .nomineeName("Rahul Kapoor")
            .isActive(true)
            .build();
        System.out.println(savings);

        // ✅ Valid Current Account (with credit limit)
        BankAccount current = new BankAccount.Builder("ACC-2002", "Rajesh Sharma", "HDFC0002345")
            .accountType("CURRENT")
            .branch("Connaught Place Branch")
            .creditLimit(200000)
            .isActive(true)
            .build();
        System.out.println(current);

        // ❌ Missing CURRENT credit limit
        try {
            BankAccount bad = new BankAccount.Builder("ACC-3003", "Test User", "ICIC0003456")
                .accountType("CURRENT")
                // forgot to set creditLimit!
                .build();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ❌ Invalid IFSC format
        try {
            BankAccount bad2 = new BankAccount.Builder("ACC-4004", "Test", "INVALID");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

**Output:**
```
BankAccount {
  accountNumber = ACC-1001
  holderName    = Priya Kapoor
  ifscCode      = SBIN0001234
  accountType   = SAVINGS
  branch        = South Delhi Branch
  creditLimit   = ₹0.0
  nominee       = Rahul Kapoor
  active        = true
}
BankAccount {
  accountNumber = ACC-2002
  holderName    = Rajesh Sharma
  ifscCode      = HDFC0002345
  accountType   = CURRENT
  branch        = Connaught Place Branch
  creditLimit   = ₹200000.0
  nominee       = None
  active        = true
}
Error: CURRENT accounts must have a positive credit limit.
Error: Invalid IFSC format: INVALID
```

---

## 6. Types of Validation in build()

| Validation Type     | Where to Put It          | Example                                    |
|---------------------|--------------------------|---------------------------------------------|
| Null / blank check  | Builder constructor       | `if (name == null) throw ...`               |
| Format check        | Builder constructor       | `if (!ifsc.matches(regex)) throw ...`       |
| Range check         | Setter method             | `if (salary < 0) throw ...`                |
| Enum/type check     | Setter method             | `if (!type.equals("SAVINGS") ...) throw ...`|
| Cross-field rule    | `build()` method         | `if (CURRENT && creditLimit == 0) throw ...`|
| Business invariant  | `build()` method         | `if (nominee.equals(holder)) throw ...`    |

---

## 7. Null Safety Patterns

```java
// Option 1: Throw exception for null required field
if (name == null) throw new IllegalArgumentException("Name is required.");

// Option 2: Default value for optional field
private String nickname = "N/A";   // never null

// Option 3: Use Optional<T> for truly optional fields
private Optional<String> middleName = Optional.empty();

// Option 4: Objects.requireNonNull (Java utility)
import java.util.Objects;
this.name = Objects.requireNonNull(name, "Name must not be null");
```

---

## 8. Summary Table

| Field Type      | Builder Location          | Validated In          | Default   |
|-----------------|---------------------------|-----------------------|-----------|
| Mandatory       | Builder constructor       | Constructor           | None      |
| Optional        | Setter method             | Setter (if needed)    | Yes       |
| Cross-field     | —                         | `build()`             | —         |

---

**← Prev:** [`04_Static_Nested_Builder.md`](./04_Static_Nested_Builder.md)  
**Next →** [`06_Fluent_Builder.md`](./06_Fluent_Builder.md)
