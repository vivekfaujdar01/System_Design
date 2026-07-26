# Module 1: Composite Design Pattern - Introduction

## 1. Real-Life Analogy

### Analogy 1: File System (Files and Folders)
Think about how your operating system manages files and folders on your computer:
* A **File** is an individual item (Leaf). It has a size (e.g., 5 MB).
* A **Folder** is a container (Composite). It can hold both Files and other Folders.

```
Root Directory (Folder / Composite) ──► 25 MB
 ├── document.pdf (File / Leaf) ─────► 5 MB
 ├── image.png (File / Leaf) ────────► 10 MB
 └── SubFolder (Folder / Composite) ─► 10 MB
      └── data.csv (File / Leaf) ────► 10 MB
```

If you right-click on either a **File** or a **Folder** and choose **"Get Size"**, the system calculates the size seamlessly:
* For a **File**, it returns its own size.
* For a **Folder**, it recursively sums the sizes of everything inside it.

The client (the OS GUI) treats individual files and folders **uniformly**.

### Analogy 2: Shipping Boxes & Products
Imagine buying items on Amazon:
* Small products (Hammer, Phone Charger) are placed in small boxes.
* Small boxes along with larger products (Book) are placed into a medium box.
* Medium boxes are packed into a big shipping crate.

When calculating total shipping cost or total weight, you don't treat boxes and products differently—you simply calculate the cost/weight of the top-level container recursively.

---

## 2. The Software Problem

Without the Composite Pattern, client code has to constantly check types using `instanceof` or `if-else` blocks:

```java
// BAD: Client code forced to treat leaves and containers differently
for (Object item : items) {
    if (item instanceof FileLeaf) {
        totalSize += ((FileLeaf) item).getSize();
    } else if (item instanceof DirectoryComposite) {
        totalSize += ((DirectoryComposite) item).calculateTotalSize();
    }
}
```

**Why this is bad**:
1. Violates the **Open/Closed Principle (OCP)**: Adding new container types requires modifying client loops everywhere.
2. Clutters client code with nested type-checking and type-casting logic.

---

## 3. Core Definition & Intent

> **Definition**: The **Composite Design Pattern** composes objects into tree structures to represent part-whole hierarchies. It allows clients to treat individual objects (`Leaf`) and compositions of objects (`Composite`) uniformly.

* **Intent**: Represent part-whole hierarchies as tree structures where individual objects and composite containers implement the same interface.
* **Category**: Structural Design Pattern.
* **GoF Definition**: "Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly."

---

## 4. Components of Composite Pattern

```
                        +---------------------------+
                        |   Component (Interface)   |
                        +---------------------------+
                        | + showDetails()           |
                        | + getSize()               |
                        +---------------------------+
                                      ^
                                      │
              ┌───────────────────────┴───────────────────────┐
              │                                               │
+---------------------------+                   +---------------------------+
|       Leaf Object         |                   |     Composite Object      |
+---------------------------+                   +---------------------------+
| + showDetails()           |                   | - children: List<Comp>    |
| + getSize()               |                   | + add(Component c)        |
+---------------------------+                   | + remove(Component c)     |
                                                | + showDetails()           |
                                                | + getSize()               |
                                                +---------------------------+
```

1. **Component**: Common interface or abstract class for all objects in the tree (both leaves and composites).
2. **Leaf**: Represents leaf nodes with no children. Defines behavior for primitive elements.
3. **Composite**: Represents container nodes that hold child components (leaves or sub-composites). Delegates work to child components recursively.
4. **Client**: Interacts with all elements strictly through the `Component` interface.

---

## 5. Complete Java Code Example: File System

### Step 1: Component Interface (`FileSystemComponent.java`)
```java
// Component Interface representing both Files and Directories
public interface FileSystemComponent {
    void showDetails();
    long getSize();
}
```

### Step 2: Leaf Implementation (`FileLeaf.java`)
```java
// Leaf Node (No child components)
public class FileLeaf implements FileSystemComponent {
    private final String fileName;
    private final long sizeInBytes;

    public FileLeaf(String fileName, long sizeInBytes) {
        this.fileName = fileName;
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public void showDetails() {
        System.out.println("  📄 File: " + fileName + " (" + sizeInBytes + " bytes)");
    }

    @Override
    public long getSize() {
        return sizeInBytes;
    }
}
```

### Step 3: Composite Implementation (`DirectoryComposite.java`)
```java
import java.util.ArrayList;
import java.util.List;

// Composite Node (Contains child components)
public class DirectoryComposite implements FileSystemComponent {
    private final String directoryName;
    private final List<FileSystemComponent> children = new ArrayList<>();

    public DirectoryComposite(String directoryName) {
        this.directoryName = directoryName;
    }

    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public void showDetails() {
        System.out.println("📁 Directory: " + directoryName);
        for (FileSystemComponent child : children) {
            child.showDetails(); // Recursive call!
        }
    }

    @Override
    public long getSize() {
        long totalSize = 0;
        for (FileSystemComponent child : children) {
            totalSize += child.getSize(); // Recursive delegation!
        }
        return totalSize;
    }
}
```

### Step 4: Main Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Create Leaf Files
        FileSystemComponent file1 = new FileLeaf("resume.pdf", 500_000);
        FileSystemComponent file2 = new FileLeaf("cover_letter.docx", 200_000);
        FileSystemComponent file3 = new FileLeaf("profile_pic.png", 1_500_000);
        FileSystemComponent file4 = new FileLeaf("project_demo.mp4", 15_000_000);

        // Create Composite Sub-Directory
        DirectoryComposite docsDirectory = new DirectoryComposite("My_Documents");
        docsDirectory.addComponent(file1);
        docsDirectory.addComponent(file2);

        // Create Composite Root Directory
        DirectoryComposite rootDirectory = new DirectoryComposite("Root_Folder");
        rootDirectory.addComponent(docsDirectory);
        rootDirectory.addComponent(file3);
        rootDirectory.addComponent(file4);

        // Display Structure
        System.out.println("=== FILE SYSTEM HIERARCHY ===");
        rootDirectory.showDetails();

        // Calculate Total Size Uniformly
        System.out.println("\n=== TOTAL CALCULATED SIZE ===");
        System.out.println("Total Root Directory Size: " + rootDirectory.getSize() + " bytes");
        System.out.println("Documents Sub-Directory Size: " + docsDirectory.getSize() + " bytes");
    }
}
```

### Output
```text
=== FILE SYSTEM HIERARCHY ===
📁 Directory: Root_Folder
📁 Directory: My_Documents
  📄 File: resume.pdf (500000 bytes)
  📄 File: cover_letter.docx (200000 bytes)
  📄 File: profile_pic.png (1500000 bytes)
  📄 File: project_demo.mp4 (15000000 bytes)

=== TOTAL CALCULATED SIZE ===
Total Root Directory Size: 17200000 bytes
Documents Sub-Directory Size: 700000 bytes
```

---

## 6. Program Control & Recursion Flow

```text
rootDirectory.getSize()
  │
  ├──► docsDirectory.getSize() (Composite)
  │      ├──► file1.getSize() ──► 500,000 bytes
  │      └──► file2.getSize() ──► 200,000 bytes
  │      └──► Returns sum: 700,000 bytes
  │
  ├──► file3.getSize() ──► 1,500,000 bytes
  └──► file4.getSize() ──► 15,000,000 bytes
  │
  └──► Total Root Sum: 17,200,000 bytes
```

---

## 7. Key Advantages & Disadvantages

### Advantages
1. **Uniform Treatment**: Clients treat complex trees and simple leaves using the exact same method calls.
2. **Open/Closed Principle (OCP)**: New leaf or composite types can be introduced without modifying existing client code.
3. **Simplifies Client Code**: Eliminates complex `if-else` or `instanceof` conditional chains.
4. **Flexible Tree Construction**: Structures can be built dynamically at runtime.

### Disadvantages
1. **Over-Generalized Interface**: Making the component interface too general can make it hard to restrict which components can be added to specific composites.
2. **Runtime Type Safety**: If child-management methods (`add`, `remove`) are declared in the base `Component` interface, calling them on a `Leaf` may throw a runtime `UnsupportedOperationException`.

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/01_File_System_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Composite_pattern/code/01_File_System_Example).
