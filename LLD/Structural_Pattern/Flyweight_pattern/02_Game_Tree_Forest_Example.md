# Module 2: Complete Java Implementation (Game Forest & Text Editor)

Let's examine a complete end-to-end implementation of the Flyweight Pattern in Java using two real-world scenarios:
1. **Scenario A**: Rendering a 3D Game Forest with 1,000,000 trees.
2. **Scenario B**: A Text Editor document rendering styled characters.

---

## Scenario A: 3D Game Forest Rendering

### 1. Problem Statement

A open-world video game needs to render **1,000,000 trees** across a massive terrain map. Each tree has:
* **Intrinsic State**: Tree Name, Color, 3D Mesh Geometry data, and Texture Image data (Heavy: ~15 MB per unique tree type).
* **Extrinsic State**: X-coordinate, Y-coordinate (Lightweight: 8 bytes each).

Without Flyweight, instantiating 1,000,000 tree objects consumes **gigabytes of RAM** and crashes the JVM with `java.lang.OutOfMemoryError`.

---

### 2. Implementation Step-by-Step

#### Step 1: The Flyweight Class (`TreeType.java`)
Stores the **Intrinsic State** (Name, Color, Texture, Mesh) and contains the rendering logic.

```java
// Flyweight Class: Stores heavy, immutable, shared intrinsic data
public class TreeType {
    private final String name;
    private final String color;
    private final String textureData;  // Represents heavy image/texture buffer
    private final String meshModelData; // Represents heavy 3D geometry mesh

    public TreeType(String name, String color, String textureData, String meshModelData) {
        this.name = name;
        this.color = color;
        this.textureData = textureData;
        this.meshModelData = meshModelData;
        System.out.println("  [Flyweight Created] New TreeType loaded into memory: " + name + " (" + color + ")");
    }

    // Extrinsic state (x, y coordinates) is passed into the operation method at runtime
    public void draw(int x, int y) {
        // In a real game engine, this sends 3D mesh rendering commands to GPU shader
        System.out.println("Drawing '" + name + "' tree at position (" + x + ", " + y + ") using shared texture [" + textureData + "]");
    }

    // Getters for Intrinsic properties
    public String getName() { return name; }
    public String getColor() { return color; }
}
```

---

#### Step 2: The Flyweight Factory (`TreeFactory.java`)
Manages the flyweight object pool (`Map<String, TreeType>`). Ensures flyweights are reused whenever possible.

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight Factory: Manages object pool and ensures instances are shared
public class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String textureData, String meshModelData) {
        String key = name + "_" + color;
        
        // Return cached instance if available
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, textureData, meshModelData));
        }
        
        return treeTypes.get(key);
    }

    public static int getCacheSize() {
        return treeTypes.size();
    }
}
```

---

#### Step 3: The Context Class (`Tree.java`)
Stores the **Extrinsic State** (X, Y position coordinates) and holds a reference to the shared `TreeType` Flyweight.

```java
// Context Class: Holds unique extrinsic state (coordinates) and reference to Flyweight
public class Tree {
    private final int x;
    private final int y;
    private final TreeType type; // Shared reference to Flyweight

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        // Pass extrinsic state (x, y) to the flyweight method
        type.draw(x, y);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public TreeType getType() { return type; }
}
```

---

#### Step 4: The Client / Manager (`Forest.java`)
Manages thousands of `Tree` context objects and delegates drawing tasks.

```java
import java.util.ArrayList;
import java.util.List;

// Client Manager: Handles forest container
public class Forest {
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String textureData, String meshModelData) {
        // Retrieve or create shared Flyweight from factory
        TreeType type = TreeFactory.getTreeType(name, color, textureData, meshModelData);
        
        // Create lightweight Context object
        Tree tree = new Tree(x, y, type);
        trees.add(tree);
    }

    public void drawForest() {
        for (Tree tree : trees) {
            tree.draw();
        }
    }

    public int getTotalTrees() {
        return trees.size();
    }
}
```

---

#### Step 5: Main Execution (`Main.java`)

```java
public class Main {
    public static void main(String[] args) {
        Forest forest = new Forest();

        System.out.println("=== PLANTING 1,000,000 TREES IN FOREST ===");

        // Plant 500,000 Oak Trees (Green)
        for (int i = 0; i < 500000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Oak", "Green", "oak_texture_4k.png", "oak_mesh_3d.obj");
        }

        // Plant 300,000 Pine Trees (Dark Green)
        for (int i = 0; i < 300000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Pine", "Dark Green", "pine_texture_4k.png", "pine_mesh_3d.obj");
        }

        // Plant 200,000 Cherry Blossom Trees (Pink)
        for (int i = 0; i < 200000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Cherry", "Pink", "cherry_texture_4k.png", "cherry_mesh_3d.obj");
        }

        System.out.println("\n=== MEMORY SAVINGS SUMMARY ===");
        System.out.println("Total Tree Context Objects Created : " + forest.getTotalTrees());
        System.out.println("Total Flyweight TreeType Objects Shared: " + TreeFactory.getCacheSize());

        // Measure JVM Heap Usage
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println("Total Heap Memory Used            : ~" + usedMemory + " MB");
    }
}
```

---

### 3. Execution Output

```text
=== PLANTING 1,000,000 TREES IN FOREST ===
  [Flyweight Created] New TreeType loaded into memory: Oak (Green)
  [Flyweight Created] New TreeType loaded into memory: Pine (Dark Green)
  [Flyweight Created] New TreeType loaded into memory: Cherry (Pink)

=== MEMORY SAVINGS SUMMARY ===
Total Tree Context Objects Created : 1000000
Total Flyweight TreeType Objects Shared: 3
Total Heap Memory Used            : ~32 MB
```

---

## Scenario B: Text Editor Document Formatting

### 1. Structure

* **Flyweight (`CharacterStyle.java`)**: Font Family, Font Size, Bold/Italic flags, Color (Intrinsic State).
* **Flyweight Factory (`StyleFactory.java`)**: Caches shared `CharacterStyle` instances.
* **Context (`TextCharacter.java`)**: Character glyph char `c`, Row index, Column index (Extrinsic State).

```java
// Flyweight: Shared formatting style
public class CharacterStyle {
    private final String fontFamily;
    private final int fontSize;
    private final boolean isBold;
    private final String colorHex;

    public CharacterStyle(String fontFamily, int fontSize, boolean isBold, String colorHex) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.colorHex = colorHex;
    }

    public void render(char symbol, int row, int col) {
        System.out.println("Char '" + symbol + "' at (" + row + "," + col + ") styled with [" + fontFamily + ", " + fontSize + "pt, " + colorHex + "]");
    }
}
```

---

## 4. Key Design Observations

1. **Massive Memory Reduction**: Instead of 1,000,000 heavy objects containing duplicate textures, only **3 Flyweight instances** were allocated in memory.
2. **Context Separation**: The `Tree` class acts as a lightweight proxy storing only `(x, y)` position coordinates and an 8-byte reference pointer.
3. **Thread Safety Requirement**: The Flyweight object (`TreeType`) must be **immutable** (`final` fields) so multiple threads reading the same instance cause no race conditions.
