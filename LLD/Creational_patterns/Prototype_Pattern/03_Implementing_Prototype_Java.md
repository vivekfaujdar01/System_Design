# 03 – Implementing Prototype in Java (Deep vs Shallow Copy)

> **Source code lives in:** `src/shallow/` and `src/deep/`

---

## 1. The Domain Model

We use an **Employee** object with:
- Primitive / String fields → always safe to copy
- A mutable `Address` object → the deep-vs-shallow copy battleground
- A mutable `List<String> skills` → same battleground

```
Employee
├── name        : String
├── age         : int
├── department  : String
├── address     : Address          ← mutable nested object
└── skills      : List<String>     ← mutable collection
```

---

## 2. Shallow Copy Implementation

> **Rule:** `super.clone()` copies field values as-is.  
> For reference types this means **both objects point to the same memory address**.

### File layout — `src/shallow/`

```
src/shallow/
├── Address.java          ← shared mutable object (not cloned)
├── EmployeeShallow.java  ← implements Cloneable, uses super.clone()
└── MainShallow.java      ← demonstrates the shared-reference problem
```

### `src/shallow/Address.java`
```java
package shallow;

public class Address {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city    = city;
        this.country = country;
    }

    // Getters & Setters
    public String getCity()    { return city; }
    public String getCountry() { return country; }

    public void setCity(String city)       { this.city    = city; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}
```

### `src/shallow/EmployeeShallow.java`
```java
package shallow;

import java.util.List;

public class EmployeeShallow implements Cloneable {
    private String       name;
    private int          age;
    private String       department;
    private Address      address;   // ⚠️ mutable nested object
    private List<String> skills;    // ⚠️ mutable collection

    public EmployeeShallow(String name, int age, String department,
                           Address address, List<String> skills) {
        this.name       = name;
        this.age        = age;
        this.department = department;
        this.address    = address;
        this.skills     = skills;
    }

    /**
     * SHALLOW COPY — only the top-level object is duplicated.
     * 'address' and 'skills' still refer to the SAME objects in memory.
     */
    @Override
    public EmployeeShallow clone() {
        try {
            return (EmployeeShallow) super.clone();  // field-by-field copy
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Should never happen", e);
        }
    }

    // Getters & Setters
    public String       getName()       { return name; }
    public int          getAge()        { return age; }
    public String       getDepartment() { return department; }
    public Address      getAddress()    { return address; }
    public List<String> getSkills()     { return skills; }

    public void setName(String name)             { this.name       = name; }
    public void setAge(int age)                  { this.age        = age; }
    public void setDepartment(String department) { this.department = department; }
    public void setAddress(Address address)      { this.address    = address; }
    public void setSkills(List<String> skills)   { this.skills     = skills; }

    @Override
    public String toString() {
        return String.format("Employee{name='%s', age=%d, dept='%s', address=%s, skills=%s}",
                name, age, department, address, skills);
    }
}
```

### `src/shallow/MainShallow.java`
```java
package shallow;

import java.util.ArrayList;
import java.util.Arrays;

public class MainShallow {
    public static void main(String[] args) {

        // --- Create original ---
        Address      addr   = new Address("Bangalore", "India");
        List<String> skills = new ArrayList<>(Arrays.asList("Java", "SQL"));
        EmployeeShallow original = new EmployeeShallow("Alice", 30, "Engineering", addr, skills);

        // --- Shallow clone ---
        EmployeeShallow clone = original.clone();
        clone.setName("Bob");                    // Safe — String is immutable

        // ⚠️ Mutating the nested Address through the CLONE affects the ORIGINAL too!
        clone.getAddress().setCity("Mumbai");

        // ⚠️ Adding a skill via the CLONE modifies the ORIGINAL's list too!
        clone.getSkills().add("Kubernetes");

        System.out.println("=== Shallow Copy Demo ===");
        System.out.println("Original : " + original);
        System.out.println("Clone    : " + clone);

        System.out.println();
        System.out.println("Same Address object? " +
                (original.getAddress() == clone.getAddress()));   // true  ← shared!
        System.out.println("Same Skills list?   " +
                (original.getSkills() == clone.getSkills()));     // true  ← shared!
    }
}
```

**Expected Output:**
```
=== Shallow Copy Demo ===
Original : Employee{name='Alice', age=30, dept='Engineering', address=Mumbai, India, skills=[Java, SQL, Kubernetes]}
Clone    : Employee{name='Bob',   age=30, dept='Engineering', address=Mumbai, India, skills=[Java, SQL, Kubernetes]}

Same Address object? true
Same Skills list?   true
```

> 🔴 **Problem exposed:** Changing the clone's city ("Bangalore" → "Mumbai") changed the original's city too!

---

## 3. Deep Copy Implementation

> **Rule:** Every mutable nested object is also cloned so original and copy are completely independent.

### File layout — `src/deep/`

```
src/deep/
├── Address.java        ← now implements Cloneable
├── EmployeeDeep.java   ← explicitly clones Address and skills list
└── MainDeep.java       ← demonstrates full independence
```

### `src/deep/Address.java`
```java
package deep;

public class Address implements Cloneable {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city    = city;
        this.country = country;
    }

    /** Deep-clone is trivial here — both fields are Strings (immutable). */
    @Override
    public Address clone() {
        try {
            return (Address) super.clone();   // Strings are immutable → safe
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public String getCity()    { return city; }
    public String getCountry() { return country; }

    public void setCity(String city)       { this.city    = city; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}
```

### `src/deep/EmployeeDeep.java`
```java
package deep;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDeep implements Cloneable {
    private String       name;
    private int          age;
    private String       department;
    private Address      address;   // mutable — must deep-clone
    private List<String> skills;    // mutable — must deep-clone

    public EmployeeDeep(String name, int age, String department,
                        Address address, List<String> skills) {
        this.name       = name;
        this.age        = age;
        this.department = department;
        this.address    = address;
        this.skills     = skills;
    }

    /**
     * DEEP COPY — the clone gets its own independent Address and skills list.
     * Mutating one object will NOT affect the other.
     */
    @Override
    public EmployeeDeep clone() {
        try {
            EmployeeDeep copy = (EmployeeDeep) super.clone();

            // Deep-clone each mutable nested field
            copy.address = this.address.clone();                  // new Address object
            copy.skills  = new ArrayList<>(this.skills);          // new List copy

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Should never happen", e);
        }
    }

    // Getters & Setters
    public String       getName()       { return name; }
    public int          getAge()        { return age; }
    public String       getDepartment() { return department; }
    public Address      getAddress()    { return address; }
    public List<String> getSkills()     { return skills; }

    public void setName(String name)             { this.name       = name; }
    public void setAge(int age)                  { this.age        = age; }
    public void setDepartment(String department) { this.department = department; }
    public void setAddress(Address address)      { this.address    = address; }
    public void setSkills(List<String> skills)   { this.skills     = skills; }

    @Override
    public String toString() {
        return String.format("Employee{name='%s', age=%d, dept='%s', address=%s, skills=%s}",
                name, age, department, address, skills);
    }
}
```

### `src/deep/MainDeep.java`
```java
package deep;

import java.util.ArrayList;
import java.util.Arrays;

public class MainDeep {
    public static void main(String[] args) {

        // --- Create original ---
        Address      addr   = new Address("Bangalore", "India");
        List<String> skills = new ArrayList<>(Arrays.asList("Java", "SQL"));
        EmployeeDeep original = new EmployeeDeep("Alice", 30, "Engineering", addr, skills);

        // --- Deep clone ---
        EmployeeDeep clone = original.clone();
        clone.setName("Bob");

        // Mutating the clone's address does NOT affect the original ✅
        clone.getAddress().setCity("Mumbai");

        // Adding a skill to the clone does NOT affect the original's list ✅
        clone.getSkills().add("Kubernetes");

        System.out.println("=== Deep Copy Demo ===");
        System.out.println("Original : " + original);
        System.out.println("Clone    : " + clone);

        System.out.println();
        System.out.println("Same Address object? " +
                (original.getAddress() == clone.getAddress()));   // false ✅
        System.out.println("Same Skills list?   " +
                (original.getSkills() == clone.getSkills()));     // false ✅
    }
}
```

**Expected Output:**
```
=== Deep Copy Demo ===
Original : Employee{name='Alice', age=30, dept='Engineering', address=Bangalore, India, skills=[Java, SQL]}
Clone    : Employee{name='Bob',   age=30, dept='Engineering', address=Mumbai, India, skills=[Java, SQL, Kubernetes]}

Same Address object? false
Same Skills list?   false
```

> 🟢 **Fully independent** — modifying the clone leaves the original untouched.

---

## 4. Side-by-Side Comparison

| Aspect | Shallow Copy | Deep Copy |
|--------|-------------|-----------|
| **How** | `super.clone()` only | `super.clone()` + clone each mutable field |
| **Speed** | ✅ Faster | ❌ Slightly slower |
| **Memory** | ✅ Less (shared nested objects) | ❌ More (each clone has its own copies) |
| **Independence** | ❌ Nested objects are shared | ✅ Fully independent |
| **Risk** | ❌ Unintended side-effects | ✅ Safe mutation |
| **When to use** | Nested objects are immutable or read-only | Nested objects are mutable |

---

## 5. Alternative: Copy via Serialization

For deeply nested object graphs, Java serialization can automate deep copying:

```java
public EmployeeDeep deepCopyViaSerialization() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    out = new ObjectOutputStream(bos);
    out.writeObject(this);

    ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream     in  = new ObjectInputStream(bis);
    return (EmployeeDeep) in.readObject();
}
```

> Trade-offs: Requires `Serializable`, slower, but handles any depth automatically.

---

**Next →** [`04_Real_World_Example.md`](./04_Real_World_Example.md)
