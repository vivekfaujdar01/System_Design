# Module 2: Organization Hierarchy Example

Corporate organization charts are classic real-world representations of part-whole tree hierarchies.

---

## 1. Problem Statement

Suppose an enterprise HR portal needs to compute salary budgets across departments, divisions, and teams:
* Individual contributors (e.g., Developers, Designers) earn fixed base salaries.
* Managers lead teams consisting of developers, designers, or other managers.
* The company hierarchy is organized as a tree structure:

```
Engineering Department (Manager / Composite) ──► Total Budget: ₹45,00,000
 ├── Frontend Team (Manager / Composite) ──────► Budget: ₹18,00,000
 │    ├── Developer A (Leaf) ──────────────────► ₹10,00,000
 │    └── Designer A (Leaf) ───────────────────► ₹8,00,000
 └── Backend Team (Manager / Composite) ───────► Budget: ₹27,00,000
      ├── Developer B (Leaf) ──────────────────► ₹15,00,000
      └── Developer C (Leaf) ──────────────────► ₹12,00,000
```

With the **Composite Pattern**, calling `getSalary()` on an individual Developer or the entire Engineering Department uses the exact same interface!

---

## 2. Complete Step-by-Step Java Implementation

### Step 1: Component Interface (`EmployeeComponent.java`)
```java
// Component Interface
public interface EmployeeComponent {
    String getName();
    String getRole();
    double getSalary();
    void showDetails();
}
```

### Step 2: Leaf Implementations (`Developer.java` & `Designer.java`)
```java
// Leaf Component 1: Developer
public class Developer implements EmployeeComponent {
    private final String name;
    private final double salary;

    public Developer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "Software Engineer"; }
    @Override public double getSalary() { return salary; }

    @Override
    public void showDetails() {
        System.out.println("  💻 Developer: " + name + " | Role: " + getRole() + " | Salary: ₹" + salary);
    }
}

// Leaf Component 2: Designer
public class Designer implements EmployeeComponent {
    private final String name;
    private final double salary;

    public Designer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "UI/UX Designer"; }
    @Override public double getSalary() { return salary; }

    @Override
    public void showDetails() {
        System.out.println("  🎨 Designer: " + name + " | Role: " + getRole() + " | Salary: ₹" + salary);
    }
}
```

### Step 3: Composite Implementation (`Manager.java`)
```java
import java.util.ArrayList;
import java.util.List;

// Composite Component: Manager (Can manage Employees or other Managers)
public class Manager implements EmployeeComponent {
    private final String name;
    private final String department;
    private final List<EmployeeComponent> subordinates = new ArrayList<>();

    public Manager(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public void addSubordinate(EmployeeComponent employee) {
        subordinates.add(employee);
    }

    public void removeSubordinate(EmployeeComponent employee) {
        subordinates.remove(employee);
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "Manager of " + department; }

    @Override
    public double getSalary() {
        double totalBudget = 0;
        for (EmployeeComponent subordinate : subordinates) {
            totalBudget += subordinate.getSalary(); // Recursive delegation!
        }
        return totalBudget;
    }

    @Override
    public void showDetails() {
        System.out.println("\n👔 Manager: " + name + " [" + department + " Department]");
        for (EmployeeComponent subordinate : subordinates) {
            subordinate.showDetails(); // Recursive call!
        }
    }
}
```

### Step 4: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Individual Frontend Team Members
        EmployeeComponent dev1 = new Developer("Rahul Sharma", 1200000);
        EmployeeComponent designer1 = new Designer("Priya Verma", 900000);

        // Frontend Team Lead (Composite)
        Manager frontendManager = new Manager("Amit Patel", "Frontend Lead");
        frontendManager.addSubordinate(dev1);
        frontendManager.addSubordinate(designer1);

        // Individual Backend Team Members
        EmployeeComponent dev2 = new Developer("Vikram Singh", 1500000);
        EmployeeComponent dev3 = new Developer("Neha Gupta", 1400000);

        // Backend Team Lead (Composite)
        Manager backendManager = new Manager("Suresh Kumar", "Backend Lead");
        backendManager.addSubordinate(dev2);
        backendManager.addSubordinate(dev3);

        // VP of Engineering (Top-Level Composite)
        Manager vpEngineering = new Manager("Rajesh Iyer", "VP Engineering");
        vpEngineering.addSubordinate(frontendManager);
        vpEngineering.addSubordinate(backendManager);

        // Print Full Org Chart
        System.out.println("=== COMPANY ORGANIZATION CHART ===");
        vpEngineering.showDetails();

        // Calculate Salary Budgets Uniformly
        System.out.println("\n=== SALARY BUDGET CALCULATIONS ===");
        System.out.println("Frontend Team Budget: ₹" + frontendManager.getSalary());
        System.out.println("Backend Team Budget:  ₹" + backendManager.getSalary());
        System.out.println("Total VP Engineering Org Budget: ₹" + vpEngineering.getSalary());
    }
}
```

### Execution Output
```text
=== COMPANY ORGANIZATION CHART ===

👔 Manager: Rajesh Iyer [VP Engineering Department]

👔 Manager: Amit Patel [Frontend Lead Department]
  💻 Developer: Rahul Sharma | Role: Software Engineer | Salary: ₹1200000.0
  🎨 Designer: Priya Verma | Role: UI/UX Designer | Salary: ₹900000.0

👔 Manager: Suresh Kumar [Backend Lead Department]
  💻 Developer: Vikram Singh | Role: Software Engineer | Salary: ₹1500000.0
  💻 Developer: Neha Gupta | Role: Software Engineer | Salary: ₹1400000.0

=== SALARY BUDGET CALCULATIONS ===
Frontend Team Budget: ₹2100000.0
Backend Team Budget:  ₹2900000.0
Total VP Engineering Org Budget: ₹5000000.0
```

---

## 3. Control Flow & Calculation Breakdown

```text
vpEngineering.getSalary()
  │
  ├──► frontendManager.getSalary()
  │      ├──► dev1.getSalary()      ──► ₹12,00,000
  │      └──► designer1.getSalary() ──► ₹9,00,000
  │      └──► Subtotal: ₹21,00,000
  │
  └──► backendManager.getSalary()
         ├──► dev2.getSalary()      ──► ₹15,00,000
         └──► dev3.getSalary()      ──► ₹14,00,000
         └──► Subtotal: ₹29,00,000
  │
  └──► Total VP Org Salary: ₹50,00,000
```

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/02_Organization_Hierarchy_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Composite_pattern/code/02_Organization_Hierarchy_Example).
