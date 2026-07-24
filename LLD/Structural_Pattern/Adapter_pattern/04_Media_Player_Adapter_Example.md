# Module 4: Media Player Adapter (Classic Complete Example)

The **Media Player Adapter** is one of the most classic and widely asked interview implementations of the Adapter Pattern.

---

## 1. Problem Statement

Suppose you are building a media player software system.

### Initial Requirement
Play basic **MP3** files using a simple interface:
```java
public interface MediaPlayer {
    void play(String audioType, String fileName);
}
```

### New Requirement
Users want support for advanced media formats:
* **MP4**
* **VLC**

Third-party libraries supply separate player implementations with distinct, incompatible APIs:
* `Mp4Player` with `playMp4(String fileName)`
* `VlcPlayer` with `playVlc(String fileName)`

---

## 2. Inflexible Approach (Anti-Pattern)

```java
// BAD: Modifying AudioPlayer with conditional checks for every new format
public class BadAudioPlayer implements MediaPlayer {
    private Mp4Player mp4Player = new Mp4Player();
    private VlcPlayer vlcPlayer = new VlcPlayer();

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing MP3: " + fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            mp4Player.playMp4(fileName);
        } else if (audioType.equalsIgnoreCase("vlc")) {
            vlcPlayer.playVlc(fileName);
        } // Every new format forces modifications here! (Violates OCP)
    }
}
```
**Why this is bad**:
1. Violates the **Open/Closed Principle (OCP)**.
2. Tightly couples `AudioPlayer` directly to specific third-party library classes.

---

## 3. Adapter Pattern Solution & Program Flow

We introduce an `AdvancedMediaPlayer` interface and a `MediaAdapter` to bridge the `MediaPlayer` client interface with advanced player adaptees.

### Structure Diagram
```
                     +--------------------+
                     |    MediaPlayer     |   <--- Target Interface
                     +--------------------+
                     | + play(type, file) |
                     +--------------------+
                               ^
                               |
               +---------------+---------------+
               |                               |
     +-------------------+           +-------------------+
     |    AudioPlayer    |           |   MediaAdapter    |  <--- Adapter
     +-------------------+           +-------------------+
     | + play(type, file)|           | - advancedPlayer  |
     +-------------------+           | + play(type, file)|
                                     +-------------------+
                                               |
                                               | has-a / delegates
                                               v
                                 +---------------------------+
                                 |    AdvancedMediaPlayer    |  <--- Adaptee Interface
                                 +---------------------------+
                                 | + playVlc(fileName)       |
                                 | + playMp4(fileName)       |
                                 +---------------------------+
                                               ^
                                               |
                               +---------------+---------------+
                               |                               |
                     +-------------------+           +-------------------+
                     |     Mp4Player     |           |     VlcPlayer     |
                     +-------------------+           +-------------------+
                     | + playMp4(file)   |           | + playVlc(file)   |
                     +-------------------+           +-------------------+
```

---

## 4. Complete Step-by-Step Java Implementation

### Step 1: Target Interface (`MediaPlayer.java`)
```java
public interface MediaPlayer {
    void play(String audioType, String fileName);
}
```

### Step 2: Adaptee Interface & Concrete Players (`AdvancedMediaPlayer.java`, `Mp4Player.java`, `VlcPlayer.java`)
```java
// Adaptee Interface
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete Adaptee 1: MP4 Player
public class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Not supported by MP4 Player
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}

// Concrete Adaptee 2: VLC Player
public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
        // Not supported by VLC Player
    }
}
```

### Step 3: Media Adapter Class (`MediaAdapter.java`)
```java
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMusicPlayer;

    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer.playMp4(fileName);
        }
    }
}
```

### Step 4: Client Implementation (`AudioPlayer.java`)
```java
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;

    @Override
    public void play(String audioType, String fileName) {
        // Built-in support for MP3
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing MP3 file: " + fileName);
        } 
        // MediaAdapter provides support for advanced formats
        else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } 
        else {
            System.out.println("Invalid media format: " + audioType + " is not supported.");
        }
    }
}
```

### Step 5: Main Demonstration (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();

        audioPlayer.play("mp3", "beyond_the_horizon.mp3");
        audioPlayer.play("mp4", "alone.mp4");
        audioPlayer.play("vlc", "far_away.vlc");
        audioPlayer.play("avi", "mind_me.avi");
    }
}
```

### Execution Output
```text
Playing MP3 file: beyond_the_horizon.mp3
Playing MP4 file: alone.mp4
Playing VLC file: far_away.vlc
Invalid media format: avi is not supported.
```

---

## 5. Control Flow Analysis

```text
1. MP3 Format Call:
   main() ──► audioPlayer.play("mp3", "beyond_the_horizon.mp3")
            └──► Handled directly inside AudioPlayer (Prints "Playing MP3...")

2. MP4 Format Call:
   main() ──► audioPlayer.play("mp4", "alone.mp4")
            └──► Creates MediaAdapter("mp4") [Instantiates Mp4Player]
            └──► mediaAdapter.play("mp4", "alone.mp4")
                   └──► Mp4Player.playMp4("alone.mp4") [Prints "Playing MP4..."]

3. VLC Format Call:
   main() ──► audioPlayer.play("vlc", "far_away.vlc")
            └──► Creates MediaAdapter("vlc") [Instantiates VlcPlayer]
            └──► mediaAdapter.play("vlc", "far_away.vlc")
                   └──► VlcPlayer.playVlc("far_away.vlc") [Prints "Playing VLC..."]

4. AVI Format Call:
   main() ──► audioPlayer.play("avi", "mind_me.avi")
            └──► Format unsupported (Prints "Invalid media format...")
```

---

## 6. Architectural Evaluation & Refactoring (ISP Principle)

### Potential Flaw in Teaching Example
Notice that in `Mp4Player`, the method `playVlc()` is left empty because `Mp4Player` doesn't support VLC. This slightly violates the **Interface Segregation Principle (ISP)**.

### Refactored Production Design (ISP Compliant)
Instead of forcing a bloated single `AdvancedMediaPlayer` interface:
```java
public interface VlcPlayable {
    void playVlc(String fileName);
}

public interface Mp4Playable {
    void playMp4(String fileName);
}

public class Mp4Player implements Mp4Playable {
    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing MP4: " + fileName);
    }
}

public class VlcPlayer implements VlcPlayable {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC: " + fileName);
    }
}
```
Now, each player implements *only* what it actually supports!

---

> 📂 **Source Code Location**: The individual standalone Java code files for this module can be found in [code/04_Media_Player_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Adapter_pattern/code/04_Media_Player_Example).
