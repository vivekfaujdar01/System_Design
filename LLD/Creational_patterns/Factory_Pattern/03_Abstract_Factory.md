# 03 – Abstract Factory Pattern

> **Study order:** Read after `02_Factory_Method.md`.  
> Also a **GoF Creational Pattern**.  
> Think of it as "a factory of factories" — it creates **families of related objects**.

---

## 1. What Problem Does It Solve?

Factory Method creates **one product** per factory hierarchy.  
But what if you need to create **multiple related products that must be used together**?

**Example:** A UI toolkit that supports both **Windows** and **macOS** themes:
- Windows needs: `WindowsButton` + `WindowsCheckbox` + `WindowsTextBox`
- macOS needs: `MacButton` + `MacCheckbox` + `MacTextBox`

You must never mix families (e.g., `WindowsButton` with `MacCheckbox`). That's what Abstract Factory enforces.

---

## 2. Core Idea

> *"Provide an interface for creating families of related or dependent objects without specifying their concrete classes."* — GoF

```
GUIFactory (interface)
├── createButton()    → Button
├── createCheckbox()  → Checkbox
└── createTextBox()   → TextBox

WindowsFactory                    MacFactory
├── createButton()   → WinButton  ├── createButton()   → MacButton
├── createCheckbox() → WinCheckbox├── createCheckbox() → MacCheckbox
└── createTextBox()  → WinTextBox └── createTextBox()  → MacTextBox
```

The client is given **one factory** (either `WindowsFactory` or `MacFactory`) and calls the abstract methods.  
It never instantiates any concrete class directly.

---

## 3. Full Structure Diagram

```
┌────────────────────────────────────────────────────────────┐
│                   <<interface>>                            │
│                    GUIFactory                              │
│  + createButton()    → Button                              │
│  + createCheckbox()  → Checkbox                            │
│  + createTextBox()   → TextBox                             │
└──────────────────┬─────────────────────────────────────────┘
                   │
       ┌───────────┴───────────┐
       │                       │
┌──────┴───────┐       ┌───────┴──────┐
│WindowsFactory│       │  MacFactory  │
│createButton  │       │createButton  │
│ → WinButton  │       │ → MacButton  │
│createCheckbox│       │createCheckbox│
│ → WinCheckbox│       │ → MacCheckbox│
└──────────────┘       └──────────────┘

Product Hierarchies:
  Button   ← WindowsButton,   MacButton
  Checkbox ← WindowsCheckbox, MacCheckbox
  TextBox  ← WindowsTextBox,  MacTextBox
```

---

## 4. Code Example (Java) – UI Component Toolkit

### 4.1 Abstract Products

```java
// Button.java
public interface Button {
    String render();
    String onClick();
}

// Checkbox.java
public interface Checkbox {
    String render();
    String onToggle();
}

// TextBox.java
public interface TextBox {
    String render();
    String onInput(String text);
}
```

### 4.2 Concrete Products – Windows Family

```java
// WindowsButton.java
public class WindowsButton implements Button {
    @Override public String render()   { return "🪟  [Win Button] rendered with sharp corners"; }
    @Override public String onClick()  { return "🪟  [Win Button] clicked – ripple effect"; }
}

// WindowsCheckbox.java
public class WindowsCheckbox implements Checkbox {
    @Override public String render()    { return "🪟  [Win Checkbox] rendered as square box"; }
    @Override public String onToggle()  { return "🪟  [Win Checkbox] toggled – checkmark ✓"; }
}

// WindowsTextBox.java
public class WindowsTextBox implements TextBox {
    @Override public String render()              { return "🪟  [Win TextBox] rendered with flat border"; }
    @Override public String onInput(String text)  { return "🪟  [Win TextBox] input: '" + text + "'"; }
}
```

### 4.3 Concrete Products – macOS Family

```java
// MacButton.java
public class MacButton implements Button {
    @Override public String render()   { return "🍎  [Mac Button] rendered with rounded corners"; }
    @Override public String onClick()  { return "🍎  [Mac Button] clicked – glow effect"; }
}

// MacCheckbox.java
public class MacCheckbox implements Checkbox {
    @Override public String render()    { return "🍎  [Mac Checkbox] rendered as rounded box"; }
    @Override public String onToggle()  { return "🍎  [Mac Checkbox] toggled – smooth animation"; }
}

// MacTextBox.java
public class MacTextBox implements TextBox {
    @Override public String render()              { return "🍎  [Mac TextBox] rendered with shadow border"; }
    @Override public String onInput(String text)  { return "🍎  [Mac TextBox] input: '" + text + "'"; }
}
```

### 4.4 Abstract Factory Interface

```java
// GUIFactory.java
public interface GUIFactory {
    /**
     * Abstract Factory Interface.
     * Declares creation methods for each distinct product type in the family.
     */
    Button   createButton();
    Checkbox createCheckbox();
    TextBox  createTextBox();
}
```

### 4.5 Concrete Factories

```java
// WindowsFactory.java
public class WindowsFactory implements GUIFactory {
    @Override public Button   createButton()   { return new WindowsButton(); }
    @Override public Checkbox createCheckbox() { return new WindowsCheckbox(); }
    @Override public TextBox  createTextBox()  { return new WindowsTextBox(); }
}

// MacFactory.java
public class MacFactory implements GUIFactory {
    @Override public Button   createButton()   { return new MacButton(); }
    @Override public Checkbox createCheckbox() { return new MacCheckbox(); }
    @Override public TextBox  createTextBox()  { return new MacTextBox(); }
}
```

### 4.6 Application (Client)

```java
// Application.java
public class Application {
    /**
     * Client code.
     * Works with factories and products ONLY through abstract interfaces.
     * Completely unaware of Windows vs Mac.
     */
    private final Button   button;
    private final Checkbox checkbox;
    private final TextBox  textBox;

    public Application(GUIFactory factory) {
        this.button   = factory.createButton();
        this.checkbox = factory.createCheckbox();
        this.textBox  = factory.createTextBox();
    }

    public void renderUI() {
        System.out.println(button.render());
        System.out.println(checkbox.render());
        System.out.println(textBox.render());
    }

    public void simulateInteraction() {
        System.out.println(button.onClick());
        System.out.println(checkbox.onToggle());
        System.out.println(textBox.onInput("Hello, World!"));
    }
}
```

### 4.7 Entry Point

```java
// Main.java
public class Main {

    /**
     * The ONLY place where the concrete factory is chosen
     * (e.g., based on config, OS detection, or environment variable).
     */
    static GUIFactory getFactory(String osType) {
        switch (osType.toLowerCase()) {
            case "windows": return new WindowsFactory();
            case "mac":     return new MacFactory();
            default: throw new IllegalArgumentException("Unknown OS: " + osType);
        }
    }

    public static void main(String[] args) {
        String[] osList = {"windows", "mac"};

        for (String os : osList) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("  Running on: " + os.toUpperCase());
            System.out.println("=".repeat(50));

            GUIFactory factory = getFactory(os);
            Application app = new Application(factory);

            System.out.println("\n[Rendering UI]");
            app.renderUI();

            System.out.println("\n[User Interactions]");
            app.simulateInteraction();
        }
    }
}
```

**Output:**
```
==================================================
  Running on: WINDOWS
==================================================

[Rendering UI]
🪟  [Win Button] rendered with sharp corners
🪟  [Win Checkbox] rendered as square box
🪟  [Win TextBox] rendered with flat border

[User Interactions]
🪟  [Win Button] clicked – ripple effect
🪟  [Win Checkbox] toggled – checkmark ✓
🪟  [Win TextBox] input: 'Hello, World!'

==================================================
  Running on: MAC
==================================================

[Rendering UI]
🍎  [Mac Button] rendered with rounded corners
🍎  [Mac Checkbox] rendered as rounded box
🍎  [Mac TextBox] rendered with shadow border

[User Interactions]
🍎  [Mac Button] clicked – glow effect
🍎  [Mac Checkbox] toggled – smooth animation
🍎  [Mac TextBox] input: 'Hello, World!'
```

---

## 5. File Structure for This Example

```
abstract_factory/
├── Button.java            ← Abstract product
├── Checkbox.java          ← Abstract product
├── TextBox.java           ← Abstract product
├── WindowsButton.java     ← Concrete product (Windows family)
├── WindowsCheckbox.java   ← Concrete product (Windows family)
├── WindowsTextBox.java    ← Concrete product (Windows family)
├── MacButton.java         ← Concrete product (Mac family)
├── MacCheckbox.java       ← Concrete product (Mac family)
├── MacTextBox.java        ← Concrete product (Mac family)
├── GUIFactory.java        ← Abstract Factory interface
├── WindowsFactory.java    ← Concrete Factory (Windows)
├── MacFactory.java        ← Concrete Factory (Mac)
├── Application.java       ← Client
└── Main.java              ← Entry point
```

---

## 6. Adding a New Product Family (e.g., Linux)

You only need to add — **zero changes to existing code**:

```java
// New concrete products
public class LinuxButton   implements Button   { ... }
public class LinuxCheckbox implements Checkbox { ... }
public class LinuxTextBox  implements TextBox  { ... }

// New concrete factory
public class LinuxFactory implements GUIFactory {
    @Override public Button   createButton()   { return new LinuxButton(); }
    @Override public Checkbox createCheckbox() { return new LinuxCheckbox(); }
    @Override public TextBox  createTextBox()  { return new LinuxTextBox(); }
}
```

> ✅ `Application`, `GUIFactory`, and all existing factories remain untouched.  
> This perfectly satisfies the **Open/Closed Principle**.

---

## 7. Real-World Java Examples

| Domain               | Abstract Factory              | Families                                      |
|----------------------|-------------------------------|-----------------------------------------------|
| Java AWT             | `Toolkit`                     | Windows, GTK, Motif UI components             |
| JDBC                 | `Connection`                  | MySQL, PostgreSQL, Oracle adapters            |
| Spring Security      | `AuthenticationManagerFactory`| LDAP, JDBC, In-memory auth managers          |
| JPA / Hibernate      | `EntityManagerFactory`        | MySQL, H2, Oracle persistence contexts        |
| Java XML             | `DocumentBuilderFactory`      | Xerces, Woodstox parser implementations       |

---

## 8. Pros and Cons

| ✅ Pros                                              | ❌ Cons                                               |
|------------------------------------------------------|-------------------------------------------------------|
| Guarantees **product family consistency**            | Hard to add new *product types* (affects all factories)|
| Follows OCP (new families without modifying clients) | Many classes and interfaces — high boilerplate         |
| Follows SRP (each factory has one responsibility)    | Complex to understand at first glance                 |
| Client stays clean and abstract                      | Overkill for simple creation scenarios                |

---

## 9. Key Takeaway

> Abstract Factory = **Factory Method × N** (for N related product types).  
> Use it when you need to create **families of related objects** that must be used together,  
> and you want to **enforce consistency** across the family.

---

**← Prev:** [`02_Factory_Method.md`](./02_Factory_Method.md)  
**Next →** [`04_Comparison_and_Summary.md`](./04_Comparison_and_Summary.md)
