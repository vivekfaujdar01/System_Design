# Module 3: Real-World Examples of Bridge Pattern

The Bridge Pattern is foundational across enterprise frameworks, operating systems, and database drivers.

---

## 1. Enterprise Practical Scenarios

| Domain | Abstraction ($M$) | Implementor ($N$) | Bridge Advantage |
| :--- | :--- | :--- | :--- |
| **Notification Engine** | Normal Message, Urgent Message, Encryption Message | SMS, Email, Push Notification, WhatsApp | Sends any message type over any communication channel dynamically |
| **Database Drivers (JDBC)** | Standard Java SQL API (`Connection`, `Statement`) | MySQL, PostgreSQL, Oracle, SQLite Drivers | Java code stays database-independent; database drivers switch seamlessly |
| **Cross-Platform UI** | Window, Dialog, Button Abstractions | Windows Win32 API, MacOS Cocoa, Linux X11 | GUI controls render on any OS platform |
| **Graphics Engines** | 2D/3D Rendering Engine (Mesh, Lighting) | OpenGL, DirectX, Vulkan, Metal APIs | Rendering algorithms stay independent of graphics card APIs |

---

## 2. Complete Java Example: Enterprise Notification Engine

### Problem Scenario
An enterprise platform needs to send different types of notifications:
* **Message Types (Abstraction)**: `NotificationMessage` (Standard), `UrgentNotificationMessage` (Priority with retry alerts).
* **Delivery Channels (Implementor)**: `SmsSender`, `EmailSender`, `PushNotificationSender`.

Using Bridge Pattern, any message type can be transmitted via any delivery channel without subclass explosion!

```
                  +---------------------------+                         +---------------------------+
                  |    NotificationMessage    | ── Bridge (has-a) ────► |       MessageSender       |
                  +---------------------------+                         +---------------------------+
                  | # sender: MessageSender   |                         | + sendMessage(msg, dest)  |
                  | + send(msg, dest)         |                         +---------------------------+
                  +---------------------------+                                       ^
                                ^                                                     │
         ┌──────────────────────┴──────────────────────┐             ┌────────────────┼────────────────┐
         │                                             │             │                │                │
+-------------------+                       +-------------------+ +-------------+ +---------------+ +-------------+
| StandardMessage   |                       | UrgentMessage     | | SmsSender   | | EmailSender   | | PushSender  |
+-------------------+                       +-------------------+ +-------------+ +---------------+ +-------------+
```

### Complete Java Implementation

#### Step 1: Implementor Interface (`MessageSender.java`)
```java
// Implementor Interface
public interface MessageSender {
    void sendMessage(String message, String destination);
}
```

#### Step 2: Concrete Implementors (`SmsSender.java`, `EmailSender.java`, `PushSender.java`)
```java
// Concrete Implementor 1: SMS
public class SmsSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[SMS Channel 📱] Sending to " + destination + ": " + message);
    }
}

// Concrete Implementor 2: Email
public class EmailSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[EMAIL Channel 📧] Sending to " + destination + ": " + message);
    }
}

// Concrete Implementor 3: Push Notification
public class PushSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[PUSH Channel 🔔] Sending to " + destination + ": " + message);
    }
}
```

#### Step 3: Abstraction (`NotificationMessage.java`)
```java
// Abstraction holding the Bridge reference to MessageSender
public abstract class NotificationMessage {
    protected final MessageSender messageSender; // The Bridge!

    public NotificationMessage(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public abstract void sendNotification(String body, String recipient);
}
```

#### Step 4: Refined Abstractions (`StandardMessage.java` & `UrgentMessage.java`)
```java
// Refined Abstraction 1: Standard Notification
public class StandardMessage extends NotificationMessage {
    public StandardMessage(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void sendNotification(String body, String recipient) {
        System.out.println("\n--- Sending Standard Notification ---");
        messageSender.sendMessage(body, recipient);
    }
}

// Refined Abstraction 2: Urgent High-Priority Notification
public class UrgentMessage extends NotificationMessage {
    public UrgentMessage(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void sendNotification(String body, String recipient) {
        System.out.println("\n--- Sending URGENT HIGH-PRIORITY Notification ---");
        String urgentBody = "🚨 [URGENT] " + body;
        // Sends primary alert + secondary confirmation trace
        messageSender.sendMessage(urgentBody, recipient);
        System.out.println("--> Urgent delivery confirmation logged to audit trail.");
    }
}
```

#### Step 5: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        // Send Standard Message via SMS
        NotificationMessage smsNotification = new StandardMessage(new SmsSender());
        smsNotification.sendNotification("Your OTP is 482910.", "+91-9876543210");

        // Send Standard Message via Email
        NotificationMessage emailNotification = new StandardMessage(new EmailSender());
        emailNotification.sendNotification("Welcome to System Design Portal!", "user@example.com");

        // Send Urgent Message via Push Notification
        NotificationMessage urgentPush = new UrgentMessage(new PushSender());
        urgentPush.sendNotification("Security alert: Login from unknown device!", "Device_ID_9921");

        // Send Urgent Message via SMS
        NotificationMessage urgentSms = new UrgentMessage(new SmsSender());
        urgentSms.sendNotification("Server CPU usage exceeded 95% threshold!", "+91-9999988888");
    }
}
```

### Execution Output
```text
--- Sending Standard Notification ---
[SMS Channel 📱] Sending to +91-9876543210: Your OTP is 482910.

--- Sending Standard Notification ---
[EMAIL Channel 📧] Sending to user@example.com: Welcome to System Design Portal!

--- Sending URGENT HIGH-PRIORITY Notification ---
[PUSH Channel 🔔] Sending to Device_ID_9921: 🚨 [URGENT] Security alert: Login from unknown device!
--> Urgent delivery confirmation logged to audit trail.

--- Sending URGENT HIGH-PRIORITY Notification ---
[SMS Channel 📱] Sending to +91-9999988888: 🚨 [URGENT] Server CPU usage exceeded 95% threshold!
--> Urgent delivery confirmation logged to audit trail.
```

---

## 3. Bridge Pattern in Java Ecosystem: JDBC Architecture

Java Database Connectivity (JDBC) is the most famous example of the Bridge Pattern built into the Java Standard Library.

```text
Java Application ──► JDBC API Abstraction (java.sql.DriverManager / Connection / Statement)
                              │
                              ▼ Bridge Interface (java.sql.Driver)
         ┌────────────────────┼────────────────────┐
         │                    │                    │
MySQL JDBC Driver    PostgreSQL JDBC Driver   Oracle JDBC Driver
```

* **Abstraction**: `java.sql.Connection`, `java.sql.Statement`. Application code calls `connection.createStatement()`.
* **Implementor**: `java.sql.Driver` provided by database vendor JARs (MySQL, PostgreSQL, Oracle).
* **Benefit**: Changing database vendors requires changing only the database connection URL string in configuration. Zero application code rewrites!

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/03_Notification_Engine_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Bridge_pattern/code/03_Notification_Engine_Example).
