# Module 1: Facade Design Pattern - Introduction

## 1. Real-Life Analogy

### Analogy 1: Home Theater System
Imagine you bought a high-end Home Theater setup containing multiple complex devices:
* DVD / Blu-ray Player
* HD Projector
* Surround Sound Amplifier & Speakers
* Motorized Projection Screen
* Dimmable Smart Theater Lights

When you want to watch a movie:
* **Without a Unified Remote (No Facade)**: You have to pick up 5 different remotes: turn on lights, dim them to 10%, lower the screen, turn on the projector, set input to HDMI, power on the amplifier, set volume to 50%, turn on the DVD player, insert disc, and press play!
* **With a Single-Button "Watch Movie" Remote (Facade)**: You press **one single button** ("Watch Movie"). The single remote controller handles all 10 steps behind the scenes automatically!

```
                    +-----------------------------+
                    |    HomeTheaterFacade        |
                    +-----------------------------+
                    | + watchMovie("Inception")   |
                    | + endMovie()                |
                    +-----------------------------+
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         ▼                         ▼                         ▼
  +--------------+          +--------------+          +--------------+
  |  DvdPlayer   |          |  Projector   |          |  Amplifier   | ... (Subsystems)
  +--------------+          +--------------+          +--------------+
```

### Analogy 2: Hotel Concierge Desk
When staying at a luxury hotel:
* Rather than personally finding the housekeeping supervisor for extra towels, calling the chef for room service, negotiating with local taxi drivers, and contacting the manager for late checkout...
* You talk to **one person**—the **Hotel Concierge** at the reception desk. The concierge acts as a **Facade**, coordinating all underlying hotel departments for you.

---

## 2. The Software Problem

Without the Facade Pattern, client code becomes tightly coupled to dozens of complex subsystem classes:

```java
// BAD: Client code forced to manage complex subsystem orchestration manually
DvdPlayer dvd = new DvdPlayer();
Projector projector = new Projector();
Amplifier amp = new Amplifier();
TheaterLights lights = new TheaterLights();
Screen screen = new Screen();

lights.dim(10);
screen.down();
projector.on();
projector.setInput("DVD");
amp.on();
amp.setVolume(5);
dvd.on();
dvd.play("Inception");
```

**Why this is bad**:
1. **High Coupling**: Client code must know about every single subsystem class, method, and initialization order.
2. **Code Duplication**: Every place in the application that needs to watch a movie must repeat this entire 10-step sequence.
3. **Fragile Code**: Changing any subsystem signature breaks all client code directly.

---

## 3. Core Definition & Intent

> **Definition**: The **Facade Design Pattern** provides a simplified, unified interface to a set of interfaces in a complex subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

* **Intent**: Hide complex subsystem interactions behind a single, easy-to-use interface.
* **Category**: Structural Design Pattern.
* **GoF Definition**: "Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use."

---

## 4. Components of Facade Pattern

```
                             +-------------------+
                             |      Client       |
                             +-------------------+
                                       │
                                       ▼
                             +-------------------+
                             |      Facade       |
                             +-------------------+
                             | + watchMovie()    |
                             | + endMovie()      |
                             +-------------------+
                                       │
            ┌──────────────────────────┼──────────────────────────┐
            ▼                          ▼                          ▼
  +-------------------+      +-------------------+      +-------------------+
  | Subsystem Class A |      | Subsystem Class B |      | Subsystem Class C |
  +-------------------+      +-------------------+      +-------------------+
```

1. **Facade**: The unified coordinator class that knows which subsystem classes are responsible for a request and delegates client calls to the appropriate subsystem objects.
2. **Subsystem Classes**: The complex underlying classes (e.g., `DvdPlayer`, `Projector`, `Amplifier`) that perform actual work. They are unaware of the Facade and hold no references to it.
3. **Client**: Interacts strictly with the `Facade` class rather than calling subsystem objects directly.

---

## 5. Complete Java Code Example: Home Theater

### Step 1: Subsystem Classes (`DvdPlayer.java`, `Projector.java`, `Amplifier.java`, `TheaterLights.java`, `Screen.java`)

```java
// Subsystem 1: DVD Player
public class DvdPlayer {
    public void on() { System.out.println("  [DVD Player] Power ON"); }
    public void play(String movie) { System.out.println("  [DVD Player] Playing movie: '" + movie + "'"); }
    public void stop() { System.out.println("  [DVD Player] Stopped movie"); }
    public void off() { System.out.println("  [DVD Player] Power OFF"); }
}

// Subsystem 2: Projector
public class Projector {
    public void on() { System.out.println("  [Projector] Power ON"); }
    public void setInput(String input) { System.out.println("  [Projector] Setting input source to " + input); }
    public void wideScreenMode() { System.out.println("  [Projector] Aspect ratio set to 16:9 Widescreen"); }
    public void off() { System.out.println("  [Projector] Power OFF"); }
}

// Subsystem 3: Surround Sound Amplifier
public class Amplifier {
    public void on() { System.out.println("  [Amplifier] Power ON"); }
    public void setSurroundSound() { System.out.println("  [Amplifier] 7.1 Dolby Surround Sound enabled"); }
    public void setVolume(int level) { System.out.println("  [Amplifier] Volume level set to " + level); }
    public void off() { System.out.println("  [Amplifier] Power OFF"); }
}

// Subsystem 4: Dimmable Lights
public class TheaterLights {
    public void dim(int level) { System.out.println("  [Lights] Dimming lights to " + level + "%"); }
    public void on() { System.out.println("  [Lights] Lights set to 100% Full Brightness"); }
}

// Subsystem 5: Motorized Screen
public class Screen {
    public void down() { System.out.println("  [Screen] Lowering projection screen"); }
    public void up() { System.out.println("  [Screen] Raising projection screen to ceiling"); }
}
```

### Step 2: Facade Class (`HomeTheaterFacade.java`)

```java
// Facade orchestrating all 5 subsystem classes
public class HomeTheaterFacade {
    private final DvdPlayer dvd;
    private final Projector projector;
    private final Amplifier amp;
    private final TheaterLights lights;
    private final Screen screen;

    public HomeTheaterFacade(DvdPlayer dvd, Projector projector, Amplifier amp, TheaterLights lights, Screen screen) {
        this.dvd = dvd;
        this.projector = projector;
        this.amp = amp;
        this.lights = lights;
        this.screen = screen;
    }

    // High-level method 1: Start Movie
    public void watchMovie(String movie) {
        System.out.println("=== PREPARING THEATER FOR MOVIE: " + movie + " ===");
        lights.dim(10);
        screen.down();
        projector.on();
        projector.setInput("DVD Player");
        projector.wideScreenMode();
        amp.on();
        amp.setSurroundSound();
        amp.setVolume(25);
        dvd.on();
        dvd.play(movie);
        System.out.println("=== MOVIE IS NOW PLAYING! ENJOY! ===\n");
    }

    // High-level method 2: End Movie
    public void endMovie() {
        System.out.println("=== SHUTTING DOWN HOME THEATER ===");
        dvd.stop();
        dvd.off();
        amp.off();
        projector.off();
        screen.up();
        lights.on();
        System.out.println("=== THEATER SHUTDOWN COMPLETE ===\n");
    }
}
```

### Step 3: Main Execution (`Main.java`)

```java
public class Main {
    public static void main(String[] args) {
        // Instantiate Subsystem Objects
        DvdPlayer dvd = new DvdPlayer();
        Projector projector = new Projector();
        Amplifier amp = new Amplifier();
        TheaterLights lights = new TheaterLights();
        Screen screen = new Screen();

        // Instantiate Facade
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(dvd, projector, amp, lights, screen);

        // Client executes high-level actions with ONE method call!
        homeTheater.watchMovie("Interstellar");
        homeTheater.endMovie();
    }
}
```

### Output
```text
=== PREPARING THEATER FOR MOVIE: Interstellar ===
  [Lights] Dimming lights to 10%
  [Screen] Lowering projection screen
  [Projector] Power ON
  [Projector] Setting input source to DVD Player
  [Projector] Aspect ratio set to 16:9 Widescreen
  [Amplifier] Power ON
  [Amplifier] 7.1 Dolby Surround Sound enabled
  [Amplifier] Volume level set to 25
  [DVD Player] Power ON
  [DVD Player] Playing movie: 'Interstellar'
=== MOVIE IS NOW PLAYING! ENJOY! ===

=== SHUTTING DOWN HOME THEATER ===
  [DVD Player] Stopped movie
  [DVD Player] Power OFF
  [Amplifier] Power OFF
  [Projector] Power OFF
  [Screen] Raising projection screen to ceiling
  [Lights] Lights set to 100% Full Brightness
=== THEATER SHUTDOWN COMPLETE ===
```

---

## 6. Program Control & Delegation Flow

```text
main()
  │
  ├──► homeTheater.watchMovie("Interstellar")
  │      ├──► lights.dim(10)
  │      ├──► screen.down()
  │      ├──► projector.on() / setInput() / wideScreenMode()
  │      ├──► amp.on() / setSurroundSound() / setVolume(25)
  │      └──► dvd.on() / play("Interstellar")
  │
  └──► homeTheater.endMovie()
         ├──► dvd.stop() / off()
         ├──► amp.off()
         ├──► projector.off()
         ├──► screen.up()
         └──► lights.on()
```

---

## 7. Key Advantages & Disadvantages

### Advantages
1. **Loose Coupling**: Decouples client code from complex subsystem implementations.
2. **Simplifies API**: Provides a single, convenient entry point for common use cases.
3. **Improves Maintainability**: Subsystem internal logic can be refactored without breaking client code.
4. **Does Not Block Direct Access**: Advanced clients can still access low-level subsystem objects directly if fine-grained control is needed.

### Disadvantages
1. **Risk of God Object**: If not designed carefully, a Facade can grow into a bloated "God Class" coupled to every class in an application.
2. **Additional Abstraction Layer**: Adds another layer of indirection to the codebase.

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/01_Home_Theater_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Facade_pattern/code/01_Home_Theater_Example).
