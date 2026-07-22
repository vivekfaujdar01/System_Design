# Module 12 – Real-World Examples

> **Study order:** Read after `11_Builder_in_Java_Libraries.md`.  
> Five complete, production-style Builder implementations to cement your understanding.

---

## Table of Contents
1. [Pizza Builder](#1-pizza-builder)
2. [Computer Builder](#2-computer-builder)
3. [Car Builder](#3-car-builder)
4. [Employee Builder](#4-employee-builder)
5. [House Builder](#5-house-builder)

---

## 1. Pizza Builder

```java
// Pizza.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pizza {

    public enum Size   { SMALL, MEDIUM, LARGE, EXTRA_LARGE }
    public enum Crust  { THIN, THICK, STUFFED, GLUTEN_FREE }

    private final Size         size;          // mandatory
    private final Crust        crust;         // mandatory
    private final boolean      extraCheese;
    private final boolean      extraSauce;
    private final List<String> toppings;      // defensive copy needed

    private Pizza(Builder builder) {
        this.size        = builder.size;
        this.crust       = builder.crust;
        this.extraCheese = builder.extraCheese;
        this.extraSauce  = builder.extraSauce;
        this.toppings    = Collections.unmodifiableList(
            new ArrayList<>(builder.toppings)
        );
    }

    public Size         getSize()        { return size; }
    public Crust        getCrust()       { return crust; }
    public boolean      hasExtraCheese() { return extraCheese; }
    public boolean      hasExtraSauce()  { return extraSauce; }
    public List<String> getToppings()    { return toppings; }

    @Override
    public String toString() {
        return "Pizza {" +
               "\n  size        = " + size +
               "\n  crust       = " + crust +
               "\n  extraCheese = " + extraCheese +
               "\n  extraSauce  = " + extraSauce +
               "\n  toppings    = " + toppings +
               "\n}";
    }

    // ── Builder ──────────────────────────────────────────────────
    public static class Builder {
        private final Size  size;
        private final Crust crust;

        private boolean      extraCheese = false;
        private boolean      extraSauce  = false;
        private List<String> toppings    = new ArrayList<>();

        public Builder(Size size, Crust crust) {
            if (size  == null) throw new IllegalArgumentException("Size is required.");
            if (crust == null) throw new IllegalArgumentException("Crust type is required.");
            this.size  = size;
            this.crust = crust;
        }

        public Builder extraCheese()           { this.extraCheese = true; return this; }
        public Builder extraSauce()            { this.extraSauce  = true; return this; }

        public Builder topping(String topping) {
            if (topping == null || topping.isBlank())
                throw new IllegalArgumentException("Topping name cannot be blank.");
            this.toppings.add(topping);
            return this;
        }

        public Pizza build() {
            if (toppings.size() > 6)
                throw new IllegalStateException("Maximum 6 toppings allowed. Got: " + toppings.size());
            return new Pizza(this);
        }
    }
}

// ── Client ────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {
        Pizza vegSupreme = new Pizza.Builder(Pizza.Size.LARGE, Pizza.Crust.THIN)
            .topping("Mushrooms")
            .topping("Bell Peppers")
            .topping("Olives")
            .topping("Onions")
            .extraCheese()
            .build();
        System.out.println("=== Veg Supreme ===");
        System.out.println(vegSupreme);

        Pizza margherita = new Pizza.Builder(Pizza.Size.MEDIUM, Pizza.Crust.THICK)
            .extraSauce()
            .build();
        System.out.println("\n=== Margherita ===");
        System.out.println(margherita);
    }
}
```

**Output:**
```
=== Veg Supreme ===
Pizza {
  size        = LARGE
  crust       = THIN
  extraCheese = true
  extraSauce  = false
  toppings    = [Mushrooms, Bell Peppers, Olives, Onions]
}

=== Margherita ===
Pizza {
  size        = MEDIUM
  crust       = THICK
  extraCheese = false
  extraSauce  = true
  toppings    = []
}
```

---

## 2. Computer Builder

```java
// Computer.java
public final class Computer {

    public enum RAM     { RAM_8GB, RAM_16GB, RAM_32GB, RAM_64GB }
    public enum Storage { SSD_256GB, SSD_512GB, SSD_1TB, SSD_2TB, HDD_1TB, HDD_2TB }

    private final String  cpu;
    private final RAM     ram;
    private final Storage storage;
    private final String  gpu;
    private final String  operatingSystem;
    private final boolean bluetooth;
    private final boolean wifi;
    private final int     usbPorts;

    private Computer(Builder b) {
        this.cpu             = b.cpu;
        this.ram             = b.ram;
        this.storage         = b.storage;
        this.gpu             = b.gpu;
        this.operatingSystem = b.operatingSystem;
        this.bluetooth       = b.bluetooth;
        this.wifi            = b.wifi;
        this.usbPorts        = b.usbPorts;
    }

    @Override
    public String toString() {
        return "Computer {" +
               "\n  CPU  = " + cpu +
               "\n  RAM  = " + ram +
               "\n  Disk = " + storage +
               "\n  GPU  = " + gpu +
               "\n  OS   = " + operatingSystem +
               "\n  BT   = " + bluetooth +
               "\n  WiFi = " + wifi +
               "\n  USB  = " + usbPorts + " ports" +
               "\n}";
    }

    // ── Builder ──────────────────────────────────────────────────
    public static class Builder {
        // Mandatory
        private final String  cpu;
        private final RAM     ram;
        // Optional
        private Storage storage         = Storage.SSD_512GB;
        private String  gpu             = "Intel Integrated";
        private String  operatingSystem = "Ubuntu LTS";
        private boolean bluetooth       = true;
        private boolean wifi            = true;
        private int     usbPorts        = 4;

        public Builder(String cpu, RAM ram) {
            if (cpu == null || cpu.isBlank())
                throw new IllegalArgumentException("CPU specification required.");
            if (ram == null)
                throw new IllegalArgumentException("RAM specification required.");
            this.cpu = cpu;
            this.ram = ram;
        }

        public Builder storage(Storage s)    { this.storage = s; return this; }
        public Builder gpu(String g)         { this.gpu = g; return this; }
        public Builder os(String os)         { this.operatingSystem = os; return this; }
        public Builder bluetooth(boolean bt) { this.bluetooth = bt; return this; }
        public Builder wifi(boolean w)       { this.wifi = w; return this; }
        public Builder usbPorts(int ports)   {
            if (ports < 0 || ports > 12)
                throw new IllegalArgumentException("USB ports must be 0–12.");
            this.usbPorts = ports;
            return this;
        }

        public Computer build() {
            // Validate: dedicated GPU needs at least 16GB RAM
            if (!gpu.equals("Intel Integrated") && ram == RAM.RAM_8GB)
                throw new IllegalStateException(
                    "Dedicated GPU '" + gpu + "' requires at least 16GB RAM.");
            return new Computer(this);
        }
    }

    // ── Presets (Director pattern as static methods) ─────────────
    public static class Presets {
        public static Computer gamingPC() {
            return new Builder("Intel Core i9-13900K", RAM.RAM_32GB)
                .storage(Storage.SSD_2TB)
                .gpu("NVIDIA RTX 4090 24GB")
                .os("Windows 11 Pro")
                .usbPorts(8)
                .build();
        }
        public static Computer officePC() {
            return new Builder("Intel Core i5-12400", RAM.RAM_16GB)
                .storage(Storage.SSD_512GB)
                .os("Ubuntu 22.04 LTS")
                .build();
        }
        public static Computer serverNode() {
            return new Builder("AMD EPYC 7763", RAM.RAM_64GB)
                .storage(Storage.HDD_2TB)
                .bluetooth(false)
                .wifi(false)
                .usbPorts(2)
                .build();
        }
    }
}

// ── Client ────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Gaming PC ===");
        System.out.println(Computer.Presets.gamingPC());

        System.out.println("\n=== Office PC ===");
        System.out.println(Computer.Presets.officePC());

        System.out.println("\n=== Custom Build ===");
        Computer custom = new Computer.Builder("AMD Ryzen 9 7950X", Computer.RAM.RAM_64GB)
            .storage(Computer.Storage.SSD_2TB)
            .gpu("AMD Radeon RX 7900 XTX")
            .os("Fedora 39")
            .usbPorts(6)
            .build();
        System.out.println(custom);
    }
}
```

---

## 3. Car Builder

```java
// Car.java
public final class Car {

    public enum FuelType     { PETROL, DIESEL, ELECTRIC, HYBRID }
    public enum Transmission { MANUAL, AUTOMATIC, CVT }

    private final String       brand;
    private final String       model;
    private final int          year;
    private final FuelType     fuelType;
    private final Transmission transmission;
    private final String       color;
    private final int          seatingCapacity;
    private final double       engineCC;
    private final boolean      sunroof;
    private final boolean      heatedSeats;
    private final int          airbags;

    private Car(Builder b) {
        this.brand           = b.brand;
        this.model           = b.model;
        this.year            = b.year;
        this.fuelType        = b.fuelType;
        this.transmission    = b.transmission;
        this.color           = b.color;
        this.seatingCapacity = b.seatingCapacity;
        this.engineCC        = b.engineCC;
        this.sunroof         = b.sunroof;
        this.heatedSeats     = b.heatedSeats;
        this.airbags         = b.airbags;
    }

    @Override
    public String toString() {
        return "Car {" +
               "\n  brand        = " + brand + " " + model +
               "\n  year         = " + year +
               "\n  fuel         = " + fuelType +
               "\n  transmission = " + transmission +
               "\n  color        = " + color +
               "\n  seats        = " + seatingCapacity +
               "\n  engine       = " + engineCC + "cc" +
               "\n  sunroof      = " + sunroof +
               "\n  heatedSeats  = " + heatedSeats +
               "\n  airbags      = " + airbags +
               "\n}";
    }

    // ── Builder ──────────────────────────────────────────────────
    public static class Builder {
        // Mandatory
        private final String brand;
        private final String model;
        private final int    year;
        // Optional
        private FuelType     fuelType        = FuelType.PETROL;
        private Transmission transmission    = Transmission.MANUAL;
        private String       color           = "White";
        private int          seatingCapacity = 5;
        private double       engineCC        = 1500;
        private boolean      sunroof         = false;
        private boolean      heatedSeats     = false;
        private int          airbags         = 2;

        public Builder(String brand, String model, int year) {
            if (brand == null || brand.isBlank()) throw new IllegalArgumentException("Brand required.");
            if (model == null || model.isBlank()) throw new IllegalArgumentException("Model required.");
            if (year < 1886 || year > 2100)       throw new IllegalArgumentException("Invalid year: " + year);
            this.brand = brand;
            this.model = model;
            this.year  = year;
        }

        public Builder fuelType(FuelType f)       { this.fuelType = f; return this; }
        public Builder transmission(Transmission t){ this.transmission = t; return this; }
        public Builder color(String c)            { this.color = c; return this; }
        public Builder seatingCapacity(int s)     { this.seatingCapacity = s; return this; }
        public Builder engineCC(double e)         { this.engineCC = e; return this; }
        public Builder sunroof(boolean s)         { this.sunroof = s; return this; }
        public Builder heatedSeats(boolean h)     { this.heatedSeats = h; return this; }
        public Builder airbags(int a)             {
            if (a < 0) throw new IllegalArgumentException("Airbags cannot be negative.");
            this.airbags = a;
            return this;
        }

        public Car build() {
            // Electric cars don't have an engine displacement
            if (fuelType == FuelType.ELECTRIC && engineCC > 0) {
                engineCC = 0;   // auto-correct for electric
            }
            if (airbags < 2) {
                throw new IllegalStateException("Safety regulation: minimum 2 airbags required.");
            }
            return new Car(this);
        }
    }
}

// ── Client ────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {
        Car tesla = new Car.Builder("Tesla", "Model 3", 2024)
            .fuelType(Car.FuelType.ELECTRIC)
            .transmission(Car.Transmission.AUTOMATIC)
            .color("Pearl White")
            .seatingCapacity(5)
            .sunroof(true)
            .heatedSeats(true)
            .airbags(8)
            .build();
        System.out.println("=== Tesla Model 3 ===");
        System.out.println(tesla);

        Car tata = new Car.Builder("Tata", "Nexon", 2023)
            .color("Flame Red")
            .fuelType(Car.FuelType.DIESEL)
            .transmission(Car.Transmission.AUTOMATIC)
            .airbags(6)
            .sunroof(true)
            .build();
        System.out.println("\n=== Tata Nexon ===");
        System.out.println(tata);
    }
}
```

---

## 4. Employee Builder

```java
// Employee.java
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Employee {

    public enum Department { ENGINEERING, MARKETING, FINANCE, HR, LEGAL, OPERATIONS }
    public enum Level      { INTERN, JUNIOR, MID, SENIOR, LEAD, MANAGER, DIRECTOR }

    private final String       employeeId;
    private final String       name;
    private final String       email;
    private final Department   department;
    private final Level        level;
    private final double       salary;
    private final LocalDate    joiningDate;
    private final String       reportingManager;
    private final boolean      isRemote;
    private final List<String> skills;

    private Employee(Builder b) {
        this.employeeId      = b.employeeId;
        this.name            = b.name;
        this.email           = b.email;
        this.department      = b.department;
        this.level           = b.level;
        this.salary          = b.salary;
        this.joiningDate     = b.joiningDate;
        this.reportingManager= b.reportingManager;
        this.isRemote        = b.isRemote;
        this.skills          = Collections.unmodifiableList(new ArrayList<>(b.skills));
    }

    public String       getEmployeeId()       { return employeeId; }
    public String       getName()             { return name; }
    public String       getEmail()            { return email; }
    public Department   getDepartment()       { return department; }
    public Level        getLevel()            { return level; }
    public double       getSalary()           { return salary; }
    public LocalDate    getJoiningDate()      { return joiningDate; }
    public String       getReportingManager() { return reportingManager; }
    public boolean      isRemote()            { return isRemote; }
    public List<String> getSkills()           { return skills; }

    @Override
    public String toString() {
        return "Employee {" +
               "\n  ID      = " + employeeId +
               "\n  Name    = " + name +
               "\n  Email   = " + email +
               "\n  Dept    = " + department +
               "\n  Level   = " + level +
               "\n  Salary  = ₹" + salary +
               "\n  Joined  = " + joiningDate +
               "\n  Manager = " + (reportingManager != null ? reportingManager : "N/A") +
               "\n  Remote  = " + isRemote +
               "\n  Skills  = " + skills +
               "\n}";
    }

    // ── Builder ──────────────────────────────────────────────────
    public static class Builder {
        // Mandatory
        private final String     employeeId;
        private final String     name;
        private final String     email;
        private final Department department;
        // Optional
        private Level      level            = Level.JUNIOR;
        private double     salary           = 300000.0;
        private LocalDate  joiningDate      = LocalDate.now();
        private String     reportingManager = null;
        private boolean    isRemote         = false;
        private List<String> skills         = new ArrayList<>();

        public Builder(String employeeId, String name, String email, Department department) {
            if (employeeId == null || employeeId.isBlank())
                throw new IllegalArgumentException("Employee ID required.");
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Name required.");
            if (email == null || !email.contains("@"))
                throw new IllegalArgumentException("Valid email required.");
            if (department == null)
                throw new IllegalArgumentException("Department required.");
            this.employeeId = employeeId;
            this.name       = name;
            this.email      = email;
            this.department = department;
        }

        public Builder level(Level l)            { this.level = l; return this; }
        public Builder salary(double s)          { if(s<0) throw new IllegalArgumentException("Salary cannot be negative."); this.salary = s; return this; }
        public Builder joiningDate(LocalDate d)  { this.joiningDate = d; return this; }
        public Builder reportingManager(String m){ this.reportingManager = m; return this; }
        public Builder isRemote(boolean r)       { this.isRemote = r; return this; }
        public Builder skill(String skill)       { this.skills.add(skill); return this; }

        public Employee build() {
            if (level == Level.MANAGER && reportingManager == null)
                throw new IllegalStateException("Manager-level employees must have a reporting manager.");
            if (level == Level.INTERN && salary > 100000)
                throw new IllegalStateException("Intern salary cannot exceed ₹1,00,000. Got: " + salary);
            return new Employee(this);
        }
    }
}

// ── Client ────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {
        Employee seniorDev = new Employee.Builder(
                "EMP-2024-001", "Priya Sharma", "priya@company.com",
                Employee.Department.ENGINEERING)
            .level(Employee.Level.SENIOR)
            .salary(180000)
            .joiningDate(LocalDate.of(2021, 3, 15))
            .reportingManager("Arjun Mehta")
            .isRemote(true)
            .skill("Java").skill("Spring Boot").skill("Kubernetes")
            .build();
        System.out.println(seniorDev);

        Employee intern = new Employee.Builder(
                "EMP-2024-099", "Rohan Gupta", "rohan@company.com",
                Employee.Department.HR)
            .level(Employee.Level.INTERN)
            .salary(50000)
            .skill("Communication").skill("MS Excel")
            .build();
        System.out.println(intern);
    }
}
```

---

## 5. House Builder

```java
// House.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class House {

    public enum FoundationType { SLAB, PILE, STRIP, PAD }
    public enum RoofType       { FLAT, GABLED, HIPPED, MANSARD }

    private final String         address;
    private final FoundationType foundationType;
    private final int            floors;
    private final String         wallMaterial;
    private final RoofType       roofType;
    private final int            bedrooms;
    private final int            bathrooms;
    private final double         areaSqFt;
    private final boolean        hasGarage;
    private final boolean        hasGarden;
    private final boolean        hasSwimmingPool;
    private final boolean        hasSolarPanels;
    private final List<String>   amenities;

    private House(Builder b) {
        this.address        = b.address;
        this.foundationType = b.foundationType;
        this.floors         = b.floors;
        this.wallMaterial   = b.wallMaterial;
        this.roofType       = b.roofType;
        this.bedrooms       = b.bedrooms;
        this.bathrooms      = b.bathrooms;
        this.areaSqFt       = b.areaSqFt;
        this.hasGarage      = b.hasGarage;
        this.hasGarden      = b.hasGarden;
        this.hasSwimmingPool= b.hasSwimmingPool;
        this.hasSolarPanels = b.hasSolarPanels;
        this.amenities      = Collections.unmodifiableList(new ArrayList<>(b.amenities));
    }

    @Override
    public String toString() {
        return "House {" +
               "\n  Address    = " + address +
               "\n  Foundation = " + foundationType +
               "\n  Floors     = " + floors +
               "\n  Walls      = " + wallMaterial +
               "\n  Roof       = " + roofType +
               "\n  Bedrooms   = " + bedrooms +
               "\n  Bathrooms  = " + bathrooms +
               "\n  Area       = " + areaSqFt + " sq.ft" +
               "\n  Garage     = " + hasGarage +
               "\n  Garden     = " + hasGarden +
               "\n  Pool       = " + hasSwimmingPool +
               "\n  Solar      = " + hasSolarPanels +
               "\n  Amenities  = " + amenities +
               "\n}";
    }

    // ── Builder ──────────────────────────────────────────────────
    public static class Builder {
        // Mandatory
        private final String  address;
        private final double  areaSqFt;
        // Optional
        private FoundationType foundationType  = FoundationType.SLAB;
        private int            floors          = 1;
        private String         wallMaterial    = "Brick";
        private RoofType       roofType        = RoofType.FLAT;
        private int            bedrooms        = 2;
        private int            bathrooms       = 1;
        private boolean        hasGarage       = false;
        private boolean        hasGarden       = false;
        private boolean        hasSwimmingPool = false;
        private boolean        hasSolarPanels  = false;
        private List<String>   amenities       = new ArrayList<>();

        public Builder(String address, double areaSqFt) {
            if (address == null || address.isBlank())
                throw new IllegalArgumentException("Address is required.");
            if (areaSqFt <= 0)
                throw new IllegalArgumentException("Area must be positive. Got: " + areaSqFt);
            this.address  = address;
            this.areaSqFt = areaSqFt;
        }

        public Builder foundation(FoundationType f) { this.foundationType = f; return this; }
        public Builder floors(int f)                { this.floors = f; return this; }
        public Builder wallMaterial(String w)       { this.wallMaterial = w; return this; }
        public Builder roofType(RoofType r)         { this.roofType = r; return this; }
        public Builder bedrooms(int b)              { this.bedrooms = b; return this; }
        public Builder bathrooms(int b)             { this.bathrooms = b; return this; }
        public Builder hasGarage(boolean g)         { this.hasGarage = g; return this; }
        public Builder hasGarden(boolean g)         { this.hasGarden = g; return this; }
        public Builder hasSwimmingPool(boolean p)   { this.hasSwimmingPool = p; return this; }
        public Builder hasSolarPanels(boolean s)    { this.hasSolarPanels = s; return this; }
        public Builder amenity(String a)            { this.amenities.add(a); return this; }

        public House build() {
            if (bathrooms > bedrooms)
                throw new IllegalStateException("Bathrooms cannot exceed bedrooms.");
            if (floors > 5 && foundationType == FoundationType.SLAB)
                throw new IllegalStateException("Buildings above 5 floors need PILE or STRIP foundation.");
            if (hasSwimmingPool && areaSqFt < 2000)
                throw new IllegalStateException("Swimming pool requires at least 2000 sq.ft area.");
            return new House(this);
        }
    }
}

// ── Client ────────────────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {

        House villa = new House.Builder("Plot 12, Green Valley, Bangalore", 5000.0)
            .foundation(House.FoundationType.PILE)
            .floors(3)
            .wallMaterial("Double-insulated brick")
            .roofType(House.RoofType.GABLED)
            .bedrooms(5)
            .bathrooms(4)
            .hasGarage(true)
            .hasGarden(true)
            .hasSwimmingPool(true)
            .hasSolarPanels(true)
            .amenity("Home Theater")
            .amenity("Modular Kitchen")
            .amenity("Smart Home System")
            .build();
        System.out.println("=== Villa ===");
        System.out.println(villa);

        House apartment = new House.Builder("Flat 301, Sunrise Towers, Mumbai", 850.0)
            .bedrooms(2)
            .bathrooms(2)
            .floors(1)
            .amenity("Gym Access")
            .build();
        System.out.println("\n=== Apartment ===");
        System.out.println(apartment);
    }
}
```

---

**← Prev:** [`11_Builder_in_Java_Libraries.md`](./11_Builder_in_Java_Libraries.md)  
**Next →** [`13_Interview_Questions.md`](./13_Interview_Questions.md)
