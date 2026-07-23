# 04 – Real-World Example: Game Character Spawner

> **Scenario:** A 2-D game engine needs to spawn many enemy characters quickly. Each enemy type has a complex configuration (stats, AI settings, sprite paths, ability list). Loading all of that from disk every time a new enemy spawns would be prohibitively slow.

---

## 1. Problem Setup

```
GameLevel needs 30 Goblins + 5 Trolls + 1 Boss

Without Prototype:                   With Prototype:
──────────────────────               ─────────────────────────
for each of 30 goblins:              goblinTemplate = loadFromDisk();  // once
  load stats from config file        for each of 30 goblins:
  parse sprite path                    goblin = goblinTemplate.clone() // fast!
  build ability list                   goblin.setPosition(x, y)
  set AI behaviour                     goblin.setId(nextId())
  set random position                  spawnEnemy(goblin)
  spawnEnemy(goblin)

→ 30 disk reads, 30 parse cycles      → 1 disk read, 29 clones
```

---

## 2. Class Design

```
        «interface»
        GameCharacter
        ─────────────
      + clone() : GameCharacter
      + spawn()
      + getInfo() : String
            ▲
            │ implements
    ┌───────┴───────┐
  Goblin           Troll
  ──────           ─────
  - name           - name
  - hp             - hp
  - damage         - damage
  - abilities      - abilities  (List<String>)
  - position       - position   (Point)
  + clone()        + clone()

        PrototypeRegistry
        ──────────────────
        - templates : Map<String, GameCharacter>
        + register(key, prototype)
        + spawn(key) : GameCharacter     ← always returns a clone
```

---

## 3. Full Java Code

### 3.1 GameCharacter Interface

```java
// GameCharacter.java
public interface GameCharacter {
    GameCharacter clone();
    String getInfo();
}
```

### 3.2 Point (simple value object)

```java
// Point.java
public class Point implements Cloneable {
    public int x, y;

    public Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public Point clone() {
        try { return (Point) super.clone(); }
        catch (CloneNotSupportedException e) { throw new AssertionError(e); }
    }

    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}
```

### 3.3 Goblin (ConcretePrototype)

```java
// Goblin.java
import java.util.ArrayList;
import java.util.List;

public class Goblin implements GameCharacter, Cloneable {
    private String       name;
    private int          hp;
    private int          damage;
    private List<String> abilities;
    private Point        position;

    public Goblin(String name, int hp, int damage, List<String> abilities, Point position) {
        this.name      = name;
        this.hp        = hp;
        this.damage    = damage;
        this.abilities = new ArrayList<>(abilities);
        this.position  = position;
    }

    /** Deep clone — abilities list and position are mutable. */
    @Override
    public Goblin clone() {
        try {
            Goblin copy      = (Goblin) super.clone();
            copy.abilities   = new ArrayList<>(this.abilities);
            copy.position    = this.position.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public void setPosition(int x, int y) { position.x = x; position.y = y; }
    public void setName(String name)      { this.name = name; }

    @Override
    public String getInfo() {
        return String.format("Goblin[name=%s, hp=%d, dmg=%d, pos=%s, abilities=%s]",
                name, hp, damage, position, abilities);
    }
}
```

### 3.4 Troll (ConcretePrototype)

```java
// Troll.java
import java.util.ArrayList;
import java.util.List;

public class Troll implements GameCharacter, Cloneable {
    private String       name;
    private int          hp;
    private int          damage;
    private List<String> abilities;
    private Point        position;

    public Troll(String name, int hp, int damage, List<String> abilities, Point position) {
        this.name      = name;
        this.hp        = hp;
        this.damage    = damage;
        this.abilities = new ArrayList<>(abilities);
        this.position  = position;
    }

    /** Deep clone */
    @Override
    public Troll clone() {
        try {
            Troll copy     = (Troll) super.clone();
            copy.abilities = new ArrayList<>(this.abilities);
            copy.position  = this.position.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public void setPosition(int x, int y) { position.x = x; position.y = y; }
    public void setName(String name)      { this.name = name; }

    @Override
    public String getInfo() {
        return String.format("Troll[name=%s, hp=%d, dmg=%d, pos=%s, abilities=%s]",
                name, hp, damage, position, abilities);
    }
}
```

### 3.5 Prototype Registry

```java
// CharacterRegistry.java
import java.util.HashMap;
import java.util.Map;

public class CharacterRegistry {
    private final Map<String, GameCharacter> templates = new HashMap<>();

    /** Register a pre-configured prototype. */
    public void register(String key, GameCharacter prototype) {
        templates.put(key, prototype);
    }

    /**
     * Always returns a fresh clone — the caller receives an independent object.
     * The template stored in the registry is never exposed directly.
     */
    public GameCharacter spawn(String key) {
        GameCharacter prototype = templates.get(key);
        if (prototype == null) throw new IllegalArgumentException("Unknown enemy: " + key);
        return prototype.clone();
    }
}
```

### 3.6 Game Client (Main)

```java
// GameMain.java
import java.util.Arrays;

public class GameMain {
    public static void main(String[] args) {

        // ── Build templates once (expensive operation simulated) ───────────────
        Goblin goblinTemplate = new Goblin(
                "Goblin-Template", 50, 10,
                Arrays.asList("Sneak", "PoisonArrow"),
                new Point(0, 0)
        );

        Troll trollTemplate = new Troll(
                "Troll-Template", 200, 35,
                Arrays.asList("Regenerate", "Boulder-Throw"),
                new Point(0, 0)
        );

        // ── Register templates ─────────────────────────────────────────────────
        CharacterRegistry registry = new CharacterRegistry();
        registry.register("goblin", goblinTemplate);
        registry.register("troll",  trollTemplate);

        // ── Spawn enemies cheaply from registry ────────────────────────────────
        System.out.println("=== Spawning Enemies ===\n");

        for (int i = 0; i < 3; i++) {
            Goblin g = (Goblin) registry.spawn("goblin");
            g.setName("Goblin-" + (i + 1));
            g.setPosition(10 * i, 20 * i);
            System.out.println(g.getInfo());
        }

        System.out.println();

        Troll t1 = (Troll) registry.spawn("troll");
        t1.setName("Troll-Alpha");
        t1.setPosition(100, 50);
        System.out.println(t1.getInfo());

        // ── Verify template is untouched ───────────────────────────────────────
        System.out.println("\n=== Template Integrity ===");
        System.out.println("Goblin template: " + goblinTemplate.getInfo());
        System.out.println("Troll  template: " + trollTemplate.getInfo());
    }
}
```

**Expected Output:**
```
=== Spawning Enemies ===

Goblin[name=Goblin-1, hp=50, dmg=10, pos=(0, 0),  abilities=[Sneak, PoisonArrow]]
Goblin[name=Goblin-2, hp=50, dmg=10, pos=(10, 20), abilities=[Sneak, PoisonArrow]]
Goblin[name=Goblin-3, hp=50, dmg=10, pos=(20, 40), abilities=[Sneak, PoisonArrow]]

Troll[name=Troll-Alpha, hp=200, dmg=35, pos=(100, 50), abilities=[Regenerate, Boulder-Throw]]

=== Template Integrity ===
Goblin template: Goblin[name=Goblin-Template, hp=50, dmg=10, pos=(0, 0), abilities=[Sneak, PoisonArrow]]
Troll  template: Troll[name=Troll-Template,  hp=200, dmg=35, pos=(0, 0), abilities=[Regenerate, Boulder-Throw]]
```

---

## 4. Other Real-World Usages of Prototype

| Domain | How Prototype Is Used |
|--------|-----------------------|
| **Word processors** | "Duplicate slide / page" — clones a document section |
| **IDEs** | "Duplicate run configuration" — clones an existing config object |
| **Spring Framework** | `scope="prototype"` — each `getBean()` returns a fresh clone |
| **JPA / Hibernate** | Detached entities cloned to create new records |
| **Java AWT / Swing** | `Color`, `Font` — cloned to adjust one attribute |
| **Test fixtures** | `ObjectMother` / Builder pattern — clone base object, vary per test |
| **CI/CD pipelines** | Clone a pipeline template, change branch/env variables |

---

## 5. Sequence Diagram

```
Client              Registry              Template (Prototype)        Clone
  │                    │                         │                      │
  │── spawn("goblin") ►│                         │                      │
  │                    │── template.clone() ────►│                      │
  │                    │                         │── copy = super.clone()  │
  │                    │                         │── copy.abilities = new List(...)
  │                    │                         │── copy.position  = position.clone()
  │                    │                         │──────────────────────►│
  │◄────────────────── clone ───────────────────────────────────────────│
  │                    │                         │                      │
  │── clone.setPosition(10, 20) ─────────────────────────────────────► │
  │── clone.setName("Goblin-1") ─────────────────────────────────────► │
```

---

**Next →** [`05_Advantages_Disadvantages_When_to_Use.md`](./05_Advantages_Disadvantages_When_to_Use.md)
