# 02 – Factory Method Pattern

> **Study order:** Read after `01_Simple_Factory.md`.  
> This is a **proper GoF Creational Pattern** (Gang of Four, 1994).  
> It fixes Simple Factory's Open/Closed violation by delegating creation to subclasses.

---

## 1. What Problem Does It Solve?

Simple Factory had **one factory class that knew every product**. Adding a new product required editing that class — violating OCP.

**Factory Method** says:  
> *"Define an interface for creating an object, but let subclasses decide which class to instantiate."*

The *creation responsibility* moves from a single factory into **subclass overrides of a creator method**.

---

## 2. Core Idea

```
           ┌─────────────────────────────────────┐
           │        Creator (Abstract Class)      │
           │  + createNotification() → Notification ◄── override this │
           │  + notify(String msg)                │
           └──────────────┬──────────────────────┘
                          │
              ┌───────────┴───────────┐
              │                       │
   ┌──────────┴──────┐     ┌──────────┴──────┐
   │  EmailService   │     │   SMSService    │
   │createNotification      createNotification
   │ → EmailNotif.   │     │ → SMSNotif.     │
   └─────────────────┘     └─────────────────┘
              │                       │
   ┌──────────┴──────┐     ┌──────────┴──────┐
   │EmailNotification│     │ SMSNotification │
   └─────────────────┘     └─────────────────┘
```

The **creator base class** contains business logic that calls `createNotification()`.  
Each **concrete creator subclass** overrides it to return its specific product.

---

## 3. Structure / Participants

| Participant         | Role                                                                      |
|---------------------|---------------------------------------------------------------------------|
| **Product**         | Interface for objects the factory creates                                 |
| **ConcreteProduct** | Specific implementation of Product                                        |
| **Creator**         | Abstract class with the factory method and higher-level operations        |
| **ConcreteCreator** | Overrides the factory method to return a specific `ConcreteProduct`       |

---

## 4. Code Example (Java) – Notification System

### 4.1 Product Interface

```java
// Notification.java
public interface Notification {
    void send(String message);
}
```

### 4.2 Concrete Products

```java
// EmailNotification.java
public class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("📧  [EMAIL]  " + message);
    }
}

// SMSNotification.java
public class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("📱  [SMS]    " + message);
    }
}

// PushNotification.java
public class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("🔔  [PUSH]   " + message);
    }
}
```

### 4.3 Creator (Abstract Class)

```java
// NotificationService.java
public abstract class NotificationService {

    /**
     * Factory Method — subclasses override this to return their product.
     */
    public abstract Notification createNotification();

    /**
     * Business logic that relies on the factory method.
     * Does NOT care which concrete Notification is returned.
     */
    public void notify(String message) {
        Notification notification = createNotification(); // polymorphic call
        notification.send(message);
    }
}
```

### 4.4 Concrete Creators

```java
// EmailService.java
public class EmailService extends NotificationService {
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }
}

// SMSService.java
public class SMSService extends NotificationService {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}

// PushService.java
public class PushService extends NotificationService {
    @Override
    public Notification createNotification() {
        return new PushNotification();
    }
}
```

### 4.5 Client Code

```java
// Main.java
import java.util.List;

public class Main {

    // Client only knows about the Creator abstraction
    static void sendAlert(NotificationService service, String msg) {
        service.notify(msg);
    }

    public static void main(String[] args) {
        List<NotificationService> services = List.of(
            new EmailService(),
            new SMSService(),
            new PushService()
        );

        String alert = "Server CPU usage exceeded 90%!";
        for (NotificationService service : services) {
            sendAlert(service, alert);
        }
    }
}
```

**Output:**
```
📧  [EMAIL]  Server CPU usage exceeded 90%!
📱  [SMS]    Server CPU usage exceeded 90%!
🔔  [PUSH]   Server CPU usage exceeded 90%!
```

> Adding a `SlackNotification` means:
> 1. Create `SlackNotification implements Notification` ✅
> 2. Create `SlackService extends NotificationService` ✅
> 3. **Zero changes to existing code** ✅ ← OCP satisfied!

---

## 5. File Structure for This Example

```
factory_method/
├── Notification.java          ← Product interface
├── EmailNotification.java     ← Concrete product
├── SMSNotification.java       ← Concrete product
├── PushNotification.java      ← Concrete product
├── NotificationService.java   ← Creator (abstract class with factory method)
├── EmailService.java          ← Concrete creator
├── SMSService.java            ← Concrete creator
├── PushService.java           ← Concrete creator
└── Main.java                  ← Client
```

---

## 6. Step-by-Step Execution Trace

```
Main calls sendAlert(new EmailService(), "msg")
  └─► EmailService.notify("msg")            [inherited from NotificationService]
        └─► this.createNotification()       [polymorphic dispatch]
              └─► EmailService.createNotification()
                    └─► return new EmailNotification()
        └─► notification.send("msg")
              └─► EmailNotification.send()  → prints "📧 [EMAIL] msg"
```

---

## 7. Real-World Java Examples

| Domain              | Creator                     | Factory Method            | Product                |
|---------------------|-----------------------------|---------------------------|------------------------|
| Java Collections    | `AbstractList<E>`           | `iterator()`              | `Iterator<E>`          |
| JDBC                | `Driver`                    | `connect()`               | `Connection`           |
| Spring Framework    | `FactoryBean<T>`            | `getObject()`             | Any Spring bean        |
| JUnit 5             | `Extension`                 | `resolveParameter()`      | Test parameter         |
| Servlet API         | `HttpServlet`               | `service()`               | `HttpServletResponse`  |

---

## 8. Pros and Cons

| ✅ Pros                                                     | ❌ Cons                                                       |
|-------------------------------------------------------------|---------------------------------------------------------------|
| Follows **Open/Closed Principle** – extend without modifying | Class hierarchy grows: one creator subclass per product type  |
| Follows **Single Responsibility** – creation separated       | Can feel over-engineered for simple use cases                 |
| Client code depends only on abstractions                    | Requires understanding of inheritance and polymorphism        |
| Easy to add new products                                    | Base Creator may accumulate too many factory methods over time|

---

## 9. Factory Method vs Simple Factory

| Aspect               | Simple Factory                      | Factory Method                        |
|----------------------|-------------------------------------|---------------------------------------|
| Structure            | One static factory class            | Abstract creator + concrete subclasses|
| OCP compliance       | ❌ Violates (must edit factory)      | ✅ Satisfies (extend via subclass)     |
| Extensibility        | Low                                 | High                                  |
| Complexity           | Low                                 | Medium                                |
| GoF pattern?         | ❌ No                                | ✅ Yes                                 |
| Java keyword used    | `static` method                     | `abstract` method                     |

---

## 10. Key Takeaway

> Factory Method = **inheritance-based extensibility**.  
> The *creator* defines the skeleton; *subclasses* plug in the concrete product.  
> **"Don't call us, we'll call you"** — the framework calls `createNotification()`; you just implement it.

---

**← Prev:** [`01_Simple_Factory.md`](./01_Simple_Factory.md)  
**Next →** [`03_Abstract_Factory.md`](./03_Abstract_Factory.md)
