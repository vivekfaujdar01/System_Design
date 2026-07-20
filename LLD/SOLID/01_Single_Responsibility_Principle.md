# S — Single Responsibility Principle (SRP)

> **"A class should have only one reason to change."**
> — Robert C. Martin

---

## 📖 Definition

The **Single Responsibility Principle** states that every class, module, or function should have **responsibility over a single part** of the program's functionality, and that responsibility should be entirely encapsulated by the class.

In simple terms: **One class = One job.**

---

## 🧠 Why Does It Matter?

When a class has multiple responsibilities:
- A change in one responsibility may break the other
- The class becomes harder to understand and maintain
- Testing becomes complex — you must test all responsibilities together
- Code reuse becomes difficult

---

## ❌ Bad Example (Violates SRP)

```java
class Invoice {
    public void calculateTotal() { ... }   // Responsibility 1: Business logic
    public void printInvoice() { ... }     // Responsibility 2: Presentation
    public void saveToDatabase() { ... }   // Responsibility 3: Persistence
}
```

**Problem**: This `Invoice` class has 3 reasons to change:
1. Business logic changes → modify `calculateTotal()`
2. Print format changes → modify `printInvoice()`
3. Database schema changes → modify `saveToDatabase()`

---

## ✅ Good Example (Follows SRP)

```java
class Invoice {
    public void calculateTotal() { ... }   // Only business logic
}

class InvoicePrinter {
    public void printInvoice(Invoice invoice) { ... }  // Only presentation
}

class InvoiceRepository {
    public void save(Invoice invoice) { ... }  // Only persistence
}
```

**Now**: Each class has only ONE reason to change.

---

## 🔍 How to Identify SRP Violations

Ask yourself: **"What does this class do?"**

- If the answer contains the word **"and"**, it likely violates SRP.
  - ❌ "It calculates the invoice **and** prints it **and** saves it."
  - ✅ "It calculates the invoice."

---

## 🌍 Real-World Analogies

| Analogy | SRP Violation | SRP Applied |
|--------|--------------|-------------|
| Restaurant | One chef cooks, serves, cleans, and does accounting | Separate chef, waiter, cleaner, accountant |
| Newspaper | One person writes, edits, prints, and distributes | Separate roles for each |
| Hospital | One doctor diagnoses, operates, bills, and manages records | Specialist for each task |

---

## 📌 Key Takeaways

- SRP is about **cohesion** — related things stay together, unrelated things are separated
- It is NOT about having a class with one method — it's about one **responsibility**
- A responsibility is a "reason to change", not just a function
- Apply SRP at class, method, and module levels
- Leads to smaller, focused, testable, and reusable classes

---

## 🔗 Relationship with Other Principles

- SRP sets the foundation for all other SOLID principles
- Violating SRP often leads to violating OCP and LSP
- Applying SRP makes Dependency Injection easier (DIP)

---

## 📄 See Code Example

➡️ [`02_Single_Responsibility_Principle.java`](./02_Single_Responsibility_Principle.java)

---

*Next: [03_Open_Closed_Principle.md](./03_Open_Closed_Principle.md)*
