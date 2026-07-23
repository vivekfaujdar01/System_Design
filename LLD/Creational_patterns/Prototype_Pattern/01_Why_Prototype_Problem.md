# 01 – Why Do We Need Prototype? (The Problem)

> **Study order:** Start here. Understand the pain-point that Prototype solves before looking at the pattern itself.

---

## 1. The Core Problem: Expensive & Complex Object Creation

Sometimes creating an object from scratch is:

| Reason | Example |
|--------|---------|
| **Costly** | Reading config from DB / file every time |
| **Slow** | Network call, parsing XML/JSON, complex computation |
| **Complex** | Object has dozens of fields, nested objects, mandatory setup steps |
| **State-dependent** | You want a snapshot of an object *at a certain point in time* |

---

## 2. A Concrete Scenario

Imagine you are building an HR system. An `Employee` object is loaded from a database — it has personal details, a list of assigned projects, a skill set, an address object, and role permissions.

```java
// ❌ The painful way — creating from scratch every time
Employee emp = new Employee();
emp.setName("Alice");
emp.setAge(30);
emp.setDepartment("Engineering");

Address addr = new Address();
addr.setCity("Bangalore");
addr.setCountry("India");
emp.setAddress(addr);

List<String> skills = new ArrayList<>();
skills.add("Java");
skills.add("System Design");
emp.setSkills(skills);

// ... 10 more fields, DB lookups, permission checks ...
```

Now suppose the manager wants to **onboard 50 similar employees** (same department, same address, same skill template). Repeating this setup 50 times is:

- ❌ **Verbose** — boilerplate everywhere
- ❌ **Error-prone** — easy to miss a field
- ❌ **Slow** — DB/network calls duplicated
- ❌ **Fragile** — adding a new field requires updating every creation site

---

## 3. Naive Workaround: Copy Constructor

```java
// Attempt: copy constructor
public Employee(Employee source) {
    this.name       = source.name;
    this.age        = source.age;
    this.department = source.department;
    this.address    = source.address;   // ⚠️ still shares the same Address object!
    this.skills     = source.skills;    // ⚠️ still shares the same List!
}
```

Problems with the copy constructor approach:

1. **Shallow copy** — nested mutable objects (Address, List) are shared, not duplicated.
2. **Coupled to concrete type** — the caller must know the exact class to invoke the right constructor.
3. **Interface problem** — if you only have an `interface` reference, you can't call `new ConcreteEmployee(source)`.
4. **Not polymorphic** — you cannot clone an object through an abstraction.

---

## 4. What We Actually Want

> **"Give me a fresh, independent copy of this object — without knowing its exact class, and without re-running the expensive setup."**e

```
Original  ──clone()──►  Copy
   │                      │
   │   same values        │   independent state
   │   different memory   │
```

This is precisely what the **Prototype Pattern** delivers.

---

## 5. Real-world Pain Points That Trigger Prototype

| Situation | Why Prototype Helps |
|-----------|---------------------|
| Game enemies/NPCs with the same base stats | Clone a template enemy, tweak HP/AI |
| Document templates (Word, Keynote) | "Duplicate slide" = prototype clone |
| Config objects loaded once from disk | Clone instead of re-parsing |
| Undo / snapshot systems | Store cloned state, restore on undo |
| Test data builders | Clone base object, vary one field per test |

---

## 6. Key Takeaway

> Without Prototype, duplicating complex objects means either **painful boilerplate**, **unsafe shallow copies**, or **tight coupling** to concrete classes.  
> Prototype centralises "how to copy myself" inside the object itself, making cloning polymorphic, safe, and cheap.

---

**Next →** [`02_What_is_Prototype_Pattern.md`](./02_What_is_Prototype_Pattern.md)
