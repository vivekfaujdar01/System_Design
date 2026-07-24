# Module 1: Bridge Design Pattern - Introduction

## 1. Real-Life Analogy

### Analogy 1: Universal Remote Control & Electronic Devices
Imagine you have different electronic devices:
* **Devices**: TV, Radio, AC, Projector.
* **Remote Controls**: Basic Remote, Advanced Smart Remote (with voice & touch support).

If every single remote control had to be hardcoded specifically for each device:
```
BasicTvRemote, AdvancedTvRemote, BasicRadioRemote, AdvancedRadioRemote, BasicAcRemote, AdvancedAcRemote ...
```
You would end up with a huge number of tightly coupled remote classes.

Instead, we separate the system into two independent hierarchies:
1. **Abstraction**: The Remote Control interface (Basic vs. Advanced).
2. **Implementation**: The Device interface (TV, Radio, AC).

```
   [ Remote Control (Abstraction) ]  <======= Bridge (HAS-A) ======>  [ Device (Implementation) ]
              │                                                                 │
     ┌────────┴────────┐                                               ┌────────┴────────┐
     │                 │                                               │                 │
BasicRemote     AdvancedRemote                                       TvDevice        RadioDevice
```

The **Bridge** connects them via object composition (`HAS-A` relationship). Now you can add a new remote feature or a new device independently without multiplying classes!

---

## 2. The Combinatorial Explosion Problem ($M \times N$ Problem)

Suppose you have:
* **2 Abstractions** (e.g., `BasicRemote`, `AdvancedRemote`)
* **3 Devices** (e.g., `TV`, `Radio`, `AC`)

### Without Bridge Pattern (Pure Inheritance)
You need $2 \times 3 = 6$ subclasses:
* `BasicTvRemote`, `BasicRadioRemote`, `BasicAcRemote`
* `AdvancedTvRemote`, `AdvancedRadioRemote`, `AdvancedAcRemote`

If you add 2 new devices (Smart TV, Soundbar) and 1 new remote (Voice Remote):
* Subclasses needed: $3 \times 5 = 15$ classes!

### With Bridge Pattern (Composition over Inheritance)
You decouple Abstraction from Implementation:
* **Remotes**: $3$ classes (`RemoteControl`, `BasicRemote`, `AdvancedRemote`)
* **Devices**: $5$ classes (`Device`, `TV`, `Radio`, `AC`, `SmartTV`, `Soundbar`)
* Total classes needed: $3 + 5 = 8$ classes!

> **Core Formula**: Bridge reduces class proliferation from $M \times N$ to $M + N$.

---

## 3. Core Definition & Intent

> **Definition**: The **Bridge Design Pattern** decouples an abstraction from its implementation so that the two can vary independently.

* **Intent**: Avoid a permanent binding between an abstraction and its implementation.
* **Category**: Structural Design Pattern.
* **GoF Definition**: "Decouple an abstraction from its implementation so that the two can vary independently."

---

## 4. Components of Bridge Pattern

```
                 +-----------------------+                         +-------------------------+
                 |     Abstraction       | ────── Bridge (has-a) ─►|      Implementor        |
                 +-----------------------+                         +-------------------------+
                 | # implementor         |                         | + operationImpl()       |
                 | + feature()           |                         +-------------------------+
                 +-----------------------+                                      ^
                             ^                                                  |
                             | extends                                          | implements
                 +-----------------------+                         +-------------------------+
                 |  RefinedAbstraction   |                         |   ConcreteImplementor   |
                 +-----------------------+                         +-------------------------+
                 | + feature()           |                         | + operationImpl()       |
                 +-----------------------+                         +-------------------------+
```

1. **Abstraction**: Defines the high-level control interface and maintains a reference to the `Implementor`.
2. **Refined Abstraction**: Extends the `Abstraction` to provide variants of high-level control features.
3. **Implementor**: Defines the low-level interface for all concrete implementation classes.
4. **Concrete Implementor**: Implements the `Implementor` interface for specific platform/device hardware or algorithms.

---

## 5. Complete Java Code Example: Remote Control & Device

### Step 1: Implementor Interface (`Device.java`)
```java
// Low-level Implementor interface
public interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    String getDeviceName();
}
```

### Step 2: Concrete Implementors (`TvDevice.java` & `RadioDevice.java`)
```java
// Concrete Implementor 1: TV
public class TvDevice implements Device {
    private boolean on = false;
    private int volume = 30;

    @Override public boolean isEnabled() { return on; }
    @Override public void enable() { on = true; }
    @Override public void disable() { on = false; }
    @Override public int getVolume() { return volume; }
    @Override public void setVolume(int percent) { this.volume = percent; }
    @Override public String getDeviceName() { return "TV"; }
}

// Concrete Implementor 2: Radio
public class RadioDevice implements Device {
    private boolean on = false;
    private int volume = 15;

    @Override public boolean isEnabled() { return on; }
    @Override public void enable() { on = true; }
    @Override public void disable() { on = false; }
    @Override public int getVolume() { return volume; }
    @Override public void setVolume(int percent) { this.volume = percent; }
    @Override public String getDeviceName() { return "Radio"; }
}
```

### Step 3: Abstraction (`RemoteControl.java`)
```java
// High-level Abstraction holding the Bridge reference to Implementor
public class RemoteControl {
    protected final Device device; // The Bridge!

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
            System.out.println(device.getDeviceName() + " is now powered OFF.");
        } else {
            device.enable();
            System.out.println(device.getDeviceName() + " is now powered ON.");
        }
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
        System.out.println(device.getDeviceName() + " volume increased to " + device.getVolume() + "%.");
    }

    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
        System.out.println(device.getDeviceName() + " volume decreased to " + device.getVolume() + "%.");
    }
}
```

### Step 4: Refined Abstraction (`AdvancedRemoteControl.java`)
```java
// Refined Abstraction extending high-level controls
public class AdvancedRemoteControl extends RemoteControl {

    public AdvancedRemoteControl(Device device) {
        super(device);
    }

    public void mute() {
        device.setVolume(0);
        System.out.println(device.getDeviceName() + " is MUTED.");
    }
}
```

### Step 5: Main Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Testing Basic Remote with TV ===");
        Device tv = new TvDevice();
        RemoteControl basicRemote = new RemoteControl(tv);
        basicRemote.togglePower();
        basicRemote.volumeUp();

        System.out.println("\n=== Testing Advanced Remote with Radio ===");
        Device radio = new RadioDevice();
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);
        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        advancedRemote.mute();
    }
}
```

### Output
```text
=== Testing Basic Remote with TV ===
TV is now powered ON.
TV volume increased to 40%.

=== Testing Advanced Remote with Radio ===
Radio is now powered ON.
Radio volume increased to 25%.
Radio is MUTED.
```

---

## 6. Program Control Flow

```text
main()
  │
  ├──► Basic Remote + TV:
  │      basicRemote.togglePower() ──► device.isEnabled() / enable() ──► TV turned ON
  │      basicRemote.volumeUp()    ──► device.setVolume(40)         ──► TV volume 40%
  │
  └──► Advanced Remote + Radio:
         advancedRemote.togglePower() ──► device.enable()          ──► Radio turned ON
         advancedRemote.volumeUp()    ──► device.setVolume(25)     ──► Radio volume 25%
         advancedRemote.mute()        ──► device.setVolume(0)      ──► Radio MUTED
```

---

## 7. Key Advantages & Disadvantages

### Advantages
1. **Decouples Abstraction and Implementation**: Abstraction and implementation can evolve completely independently.
2. **Open/Closed Principle (OCP)**: You can introduce new abstractions and implementations without breaking existing code.
3. **Single Responsibility Principle (SRP)**: High-level logic stays in the abstraction; platform-specific details stay in implementors.
4. **Prevents Class Explosion**: Replaces exponential $M \times N$ inheritance hierarchies with linear $M + N$ composition relationships.

### Disadvantages
1. **Increased Initial Complexity**: Introduces additional interfaces and abstract classes, making initial project setup slightly more complex for simple applications.

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/01_Remote_Control_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Bridge_pattern/code/01_Remote_Control_Example).
