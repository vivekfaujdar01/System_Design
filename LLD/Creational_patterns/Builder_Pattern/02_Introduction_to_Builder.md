# Module 02 – Introduction to Builder Pattern

> **Study order:** Read after `01_Why_Builder_Pattern.md`.  
> This module gives you the formal definition, real-life analogy, UML, participants, and internal working.

---

## Table of Contents
1. [Definition](#1-definition)
2. [Real-Life Analogy](#2-real-life-analogy)
3. [UML Diagram](#3-uml-diagram)
4. [Participants](#4-participants)
5. [How It Works Internally](#5-how-it-works-internally)
6. [Pattern Classification](#6-pattern-classification)

---

## 1. Definition

### GoF Definition (Gang of Four, 1994)
> *"Separate the construction of a complex object from its representation so that the same construction process can create different representations."*

### In Plain English
> Builder is a pattern that lets you build a **complex object step-by-step**.  
> You call a series of methods on a **Builder** object to configure it,  
> and then call `build()` to get the final **Product** — fully constructed and validated.

### Key Insight
- The **what to build** (Product) is separated from the **how to build it** (Builder + Director).
- The same director + different builders = **different products** from the same recipe.

---

## 2. Real-Life Analogy

### 🏠 Building a House

Think of constructing a house:

```
Architect (Director)    → Knows the steps: foundation → walls → roof → interiors
Construction Team (Builder) → Knows HOW to do each step for a specific house type
House (Product)         → The final result

Brick House Team: lays brick walls, concrete roof
Wooden House Team: lays wooden panels, shingle roof

SAME architect's blueprint (same steps)
DIFFERENT teams (different builders)
= DIFFERENT houses (different products)
```

### 🍕 Ordering a Pizza

```
You (Client)      → Tell the cashier what you want
Cashier (Director)→ Writes down your order step by step
Kitchen (Builder) → Actually makes each component
Pizza (Product)   → Final assembled pizza

Step 1: buildBase("Large")
Step 2: buildSauce("Tomato")
Step 3: buildCheese("Mozzarella")
Step 4: buildTopping("Pepperoni")
Step 5: build() → delivers your pizza
```

### 🚗 Configuring a Car Online

```
Car Configurator Website (Director)
  → chooseEngine()
  → choosePaint()
  → chooseInterior()
  → chooseWheels()
  → build()  → sends order to factory

Same website (Director)
+ SUV configuration (ConcreteBuilder A) = SUV
+ Sedan configuration (ConcreteBuilder B) = Sedan
```

---

## 3. UML Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client                                   │
│  1. Creates ConcreteBuilder                                     │
│  2. Passes it to Director                                       │
│  3. Calls director.construct()                                  │
│  4. Gets Product from builder.getResult()                       │
└────────────────────────┬────────────────────────────────────────┘
                         │
              ┌──────────▼───────────┐
              │       Director       │
              │ - builder: Builder   │◄─────────────────────┐
              │ + construct()        │                      │
              └──────────────────────┘                      │
                                                            │ uses
              ┌─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────┐
│                <<interface>>  Builder                   │
│  + buildPartA()                                         │
│  + buildPartB()                                         │
│  + buildPartC()                                         │
│  + getResult() : Product                                │
└───────────────────────────┬─────────────────────────────┘
                            │ implements
              ┌─────────────┴──────────────┐
              │                            │
┌─────────────┴──────────┐  ┌─────────────┴──────────┐
│   ConcreteBuilderA     │  │   ConcreteBuilderB     │
│ - product: ProductA    │  │ - product: ProductB    │
│ + buildPartA()         │  │ + buildPartA()         │
│ + buildPartB()         │  │ + buildPartB()         │
│ + buildPartC()         │  │ + buildPartC()         │
│ + getResult()          │  │ + getResult()          │
└────────────┬───────────┘  └──────────────┬─────────┘
             │ creates                      │ creates
             ▼                             ▼
       ┌─────────────┐              ┌─────────────┐
       │   ProductA  │              │   ProductB  │
       └─────────────┘              └─────────────┘
```

---

## 4. Participants

### 4.1 Product

```java
/**
 * PRODUCT — The complex object being constructed.
 *
 * Characteristics:
 * - Usually has many fields (some mandatory, some optional)
 * - Should be IMMUTABLE after construction (all fields final, no setters)
 * - Has a private constructor (only the Builder can instantiate it)
 * - Typically has only getters
 */
public final class House {
    // All fields are final — immutable after build()
    private final String foundation;
    private final String walls;
    private final String roof;
    private final int    numWindows;
    private final boolean hasGarage;
    private final boolean hasSwimmingPool;

    // Private constructor — enforces creation ONLY through Builder
    private House(HouseBuilder builder) {
        this.foundation      = builder.foundation;
        this.walls           = builder.walls;
        this.roof            = builder.roof;
        this.numWindows      = builder.numWindows;
        this.hasGarage       = builder.hasGarage;
        this.hasSwimmingPool = builder.hasSwimmingPool;
    }

    // Only getters — no setters!
    public String  getFoundation()      { return foundation; }
    public String  getWalls()           { return walls; }
    public String  getRoof()            { return roof; }
    public int     getNumWindows()      { return numWindows; }
    public boolean isHasGarage()        { return hasGarage; }
    public boolean isHasSwimmingPool()  { return hasSwimmingPool; }

    @Override
    public String toString() {
        return String.format(
            "House{foundation='%s', walls='%s', roof='%s', windows=%d, garage=%s, pool=%s}",
            foundation, walls, roof, numWindows, hasGarage, hasSwimmingPool
        );
    }
}
```

### 4.2 Builder (Interface)

```java
/**
 * BUILDER INTERFACE — Declares all construction steps.
 *
 * Characteristics:
 * - One method per construction step
 * - Returns the Builder (for fluent chaining) or void
 * - getResult() returns the completed Product
 * - Concrete builders implement this to build specific products
 */
public interface HouseBuilder {
    HouseBuilder buildFoundation(String foundation);
    HouseBuilder buildWalls(String walls);
    HouseBuilder buildRoof(String roof);
    HouseBuilder buildWindows(int count);
    HouseBuilder buildGarage(boolean hasGarage);
    HouseBuilder buildPool(boolean hasPool);
    House getResult();
}
```

### 4.3 Concrete Builder

```java
/**
 * CONCRETE BUILDER — Implements the Builder interface.
 *
 * Characteristics:
 * - Holds the Product being assembled (intermediate state)
 * - Each step sets a field on the Product (or a sub-component)
 * - getResult() finalises and returns the Product
 * - Different ConcreteBuilders = different Product configurations
 */
public class BrickHouseBuilder implements HouseBuilder {
    // Intermediate state fields (package-private for House's constructor access)
    String  foundation;
    String  walls;
    String  roof;
    int     numWindows;
    boolean hasGarage;
    boolean hasSwimmingPool;

    @Override
    public HouseBuilder buildFoundation(String foundation) {
        this.foundation = foundation;
        return this;
    }

    @Override
    public HouseBuilder buildWalls(String walls) {
        this.walls = walls;
        return this;
    }

    @Override
    public HouseBuilder buildRoof(String roof) {
        this.roof = roof;
        return this;
    }

    @Override
    public HouseBuilder buildWindows(int count) {
        this.numWindows = count;
        return this;
    }

    @Override
    public HouseBuilder buildGarage(boolean hasGarage) {
        this.hasGarage = hasGarage;
        return this;
    }

    @Override
    public HouseBuilder buildPool(boolean hasPool) {
        this.hasSwimmingPool = hasPool;
        return this;
    }

    @Override
    public House getResult() {
        return new House(this);   // passes itself to House's private constructor
    }
}
```

### 4.4 Director (Optional)

```java
/**
 * DIRECTOR — Orchestrates the Builder steps in a specific sequence.
 *
 * Characteristics:
 * - Knows the STEP ORDER, not the step implementation details
 * - Works with any builder that implements the HouseBuilder interface
 * - Same Director + different Builders = different Products
 * - Director is OPTIONAL — client can call builder steps directly
 */
public class HouseDirector {
    private HouseBuilder builder;

    public HouseDirector(HouseBuilder builder) {
        this.builder = builder;
    }

    // Recipe: how to build a luxury house
    public House constructLuxuryHouse() {
        return builder
            .buildFoundation("Deep concrete foundation")
            .buildWalls("Double-glazed brick walls")
            .buildRoof("Spanish tile roof")
            .buildWindows(12)
            .buildGarage(true)
            .buildPool(true)
            .getResult();
    }

    // Recipe: how to build a basic house
    public House constructBasicHouse() {
        return builder
            .buildFoundation("Standard concrete")
            .buildWalls("Single brick")
            .buildRoof("Metal sheet")
            .buildWindows(4)
            .buildGarage(false)
            .buildPool(false)
            .getResult();
    }
}
```

---

## 5. How It Works Internally

### Step-by-Step Flow

```
① Client creates a ConcreteBuilder
   BrickHouseBuilder builder = new BrickHouseBuilder();

② Client creates Director (passing the builder)
   HouseDirector director = new HouseDirector(builder);

③ Director calls build steps in the correct order
   director.constructLuxuryHouse()
     → builder.buildFoundation("Deep concrete")
     → builder.buildWalls("Double-glazed brick")
     → builder.buildRoof("Spanish tile")
     → builder.buildWindows(12)
     → builder.buildGarage(true)
     → builder.buildPool(true)
     → builder.getResult()

④ Each step stores data inside the Builder object itself
   (builder acts as a temporary accumulator of state)

⑤ getResult() passes the fully-configured builder to Product's private constructor
   return new House(this);   // 'this' = the builder with all fields set

⑥ Product's constructor reads values from the builder
   this.foundation = builder.foundation;
   this.walls      = builder.walls;
   ... etc.

⑦ Client receives the fully-formed, immutable Product
   House house = director.constructLuxuryHouse();
```

### Memory Model During Construction

```
BEFORE build():
  Builder object (mutable, temporary):
    foundation = "Deep concrete"
    walls      = "Double-glazed brick"
    roof       = "Spanish tile"
    windows    = 12
    garage     = true
    pool       = true

AFTER build():
  House object (immutable, permanent):
    final foundation = "Deep concrete"
    final walls      = "Double-glazed brick"
    final roof       = "Spanish tile"
    final windows    = 12
    final garage     = true
    final pool       = true

  Builder object → discarded (garbage collected)
```

---

## 6. Pattern Classification

| Property        | Value                                                              |
|-----------------|--------------------------------------------------------------------|
| **Category**    | Creational (GoF)                                                   |
| **Scope**       | Object                                                             |
| **Intent**      | Separate complex construction from its representation             |
| **Also Known As**| Fluent Builder, Static Inner Builder, Effective Java Builder      |
| **Related To**  | Abstract Factory (creates families), Factory Method (creates types)|
| **Java Keyword**| `abstract` class / `interface` for Builder; `final` fields in Product |

---

**← Prev:** [`01_Why_Builder_Pattern.md`](./01_Why_Builder_Pattern.md)  
**Next →** [`03_First_Builder_Implementation.md`](./03_First_Builder_Implementation.md)
