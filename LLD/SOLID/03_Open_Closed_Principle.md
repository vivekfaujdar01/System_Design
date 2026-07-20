# O — Open/Closed Principle (OCP)

> **"Software entities (classes, modules, functions) should be open for extension, but closed for modification."**
> — Bertrand Meyer, popularized by Robert C. Martin

---

## 📖 Definition

The **Open/Closed Principle** states that you should be able to **add new functionality** to a system **without changing existing, tested code**.

- **Open for Extension** → New behavior can be added
- **Closed for Modification** → Existing code is not changed

The key mechanism: **Abstraction (interfaces/abstract classes) + Polymorphism**

---

## 🧠 Why Does It Matter?

When you modify existing code to add new features:
- You risk introducing bugs in already working code
- You need to re-test all existing functionality
- The system becomes fragile over time

OCP says: **"Don't touch what works. Extend it instead."**

---

## ❌ Bad Example (Violates OCP)

```java
class DiscountCalculator {
    public double calculate(String customerType, double price) {
        if (customerType.equals("Regular")) {
            return price * 0.90;       // 10% discount
        } else if (customerType.equals("Premium")) {
            return price * 0.80;       // 20% discount
        } else if (customerType.equals("VIP")) {
            return price * 0.70;       // 30% discount
        }
        // Adding a new type → must MODIFY this class ❌
        return price;
    }
}
```

**Problem**: Every time a new customer type is added, you must modify `DiscountCalculator`. This violates OCP.

---

## ✅ Good Example (Follows OCP)

```java
// Define an abstraction
interface Discount {
    double apply(double price);
}

// Existing implementations — never touched again
class RegularDiscount implements Discount {
    public double apply(double price) { return price * 0.90; }
}
class PremiumDiscount implements Discount {
    public double apply(double price) { return price * 0.80; }
}

// New type? Just ADD a new class — don't modify existing ones ✅
class VIPDiscount implements Discount {
    public double apply(double price) { return price * 0.70; }
}

// Calculator never needs to change
class DiscountCalculator {
    public double calculate(Discount discount, double price) {
        return discount.apply(price);
    }
}
```

---

## 🔍 How to Identify OCP Violations

Look for:
- Large `if-else` chains based on type/category
- `switch` statements that grow with new features
- Methods that frequently need to be edited when new types are added

**Rule of thumb**: If adding a new feature requires modifying an existing class, OCP might be violated.

---

## 🌍 Real-World Analogies

| Analogy | OCP Applied |
|--------|-------------|
| Plugin system | Add new plugins without modifying the host app |
| Payment gateway | Add PayPal, UPI without modifying the checkout class |
| Shape area calculator | Add new shapes without touching the calculator |
| Sorting algorithms | Add new sorting strategy without modifying the sorter |

---

## 🔧 Common Techniques to Achieve OCP

1. **Interfaces / Abstract Classes** — Define contracts, extend with new implementations
2. **Strategy Pattern** — Encapsulate algorithms behind an interface
3. **Template Method Pattern** — Define skeleton, subclasses fill in the steps
4. **Decorator Pattern** — Add behavior without modifying original class

---

## 📌 Key Takeaways

- OCP reduces the risk of introducing bugs in tested code
- It enforces the use of abstraction to decouple behavior from implementation
- Doesn't mean you can **never** modify a class — fix bugs, do refactor
- When requirements change, prefer **adding** new code over **changing** old code
- OCP is the key driver of the **Strategy** and **Factory** patterns

---

## 🔗 Relationship with Other Principles

- OCP builds on **SRP** — a class with one responsibility is easier to extend
- OCP is enabled by **DIP** — depending on abstractions makes extension possible
- The **Strategy Pattern** is the textbook OCP implementation

---

## 📄 See Code Example

➡️ [`04_Open_Closed_Principle.java`](./04_Open_Closed_Principle.java)

---

*Previous: [01_Single_Responsibility_Principle.md](./01_Single_Responsibility_Principle.md)*
*Next: [05_Liskov_Substitution_Principle.md](./05_Liskov_Substitution_Principle.md)*
