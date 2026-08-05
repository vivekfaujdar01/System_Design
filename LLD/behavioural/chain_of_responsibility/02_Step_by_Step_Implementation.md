# 🛠️ Chain of Responsibility - Step-by-Step Implementation Guide

In this guide, we will step through constructing a **Chain of Responsibility Pattern** solution from scratch. 

We will build a **Multi-Level Support Ticket Escalation System** where tickets are processed based on their severity level:
* `LOW` severity -> Handled by **Level 1 Support (Junior Agent)**
* `MEDIUM` severity -> Handled by **Level 2 Support (Senior Engineer)**
* `HIGH` severity -> Handled by **Level 3 Support (Tech Lead / Architect)**
* `CRITICAL` severity -> Handled by **Level 4 Support (VP of Engineering)**

---

## Step 1: Define the Domain Models & Request Context

First, define the request object containing all necessary context (Severity Level, Description, Ticket ID, Status).

### Java Implementation

```java
// SupportTicket.java
public enum Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

public class SupportTicket {
    private final String id;
    private final String title;
    private final Severity severity;
    private boolean isHandled;
    private String resolvedBy;

    public SupportTicket(String id, String title, Severity severity) {
        this.id = id;
        this.title = title;
        this.severity = severity;
        this.isHandled = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Severity getSeverity() { return severity; }
    public boolean isHandled() { return isHandled; }
    
    public void markResolved(String resolverName) {
        this.isHandled = true;
        this.resolvedBy = resolverName;
    }

    @Override
    public String toString() {
        return "Ticket[" + id + " | " + severity + "]: '" + title + 
               "' -> " + (isHandled ? "RESOLVED by " + resolvedBy : "UNRESOLVED");
    }
}
```

---

## Step 2: Create the Abstract Handler Interface & Base Class

The Abstract Handler defines:
1. Field storing `nextHandler`.
2. `setNext(Handler next)` method (returns `Handler` to enable fluent chaining).
3. `handle(SupportTicket ticket)` template method or abstract method.

```java
// SupportHandler.java
public abstract class SupportHandler {
    protected SupportHandler nextHandler;

    // Fluent method to link handlers: handlerA.setNext(handlerB).setNext(handlerC);
    public SupportHandler setNext(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // Returns next handler to allow method chaining!
    }

    // Processing template method
    public void handleRequest(SupportTicket ticket) {
        if (canHandle(ticket)) {
            process(ticket);
        } else if (nextHandler != null) {
            System.out.println("[" + getHandlerName() + "] Cannot handle ticket (" + ticket.getSeverity() + 
                               "). Escalating to next tier...");
            nextHandler.handleRequest(ticket);
        } else {
            System.out.println("[END OF CHAIN] Ticket " + ticket.getId() + " could not be handled by any support tier!");
        }
    }

    protected abstract boolean canHandle(SupportTicket ticket);
    protected abstract void process(SupportTicket ticket);
    protected abstract String getHandlerName();
}
```

---

## Step 3: Implement Concrete Handlers

Each Concrete Handler implements the domain logic for its level of responsibility.

### Concrete Handler 1: Level 1 Support

```java
// Level1Support.java
public class Level1Support extends SupportHandler {
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getSeverity() == Severity.LOW;
    }

    @Override
    protected void process(SupportTicket ticket) {
        System.out.println("[Level 1 Support] Resolving basic ticket: " + ticket.getTitle());
        ticket.markResolved("Level 1 Support Agent");
    }

    @Override
    protected String getHandlerName() {
        return "Level 1 Support";
    }
}
```

### Concrete Handler 2: Level 2 Support

```java
// Level2Support.java
public class Level2Support extends SupportHandler {
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getSeverity() == Severity.MEDIUM;
    }

    @Override
    protected void process(SupportTicket ticket) {
        System.out.println("[Level 2 Support] Investigating code issue: " + ticket.getTitle());
        ticket.markResolved("Level 2 Senior Engineer");
    }

    @Override
    protected String getHandlerName() {
        return "Level 2 Support";
    }
}
```

### Concrete Handler 3: Level 3 Support

```java
// Level3Support.java
public class Level3Support extends SupportHandler {
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getSeverity() == Severity.HIGH;
    }

    @Override
    protected void process(SupportTicket ticket) {
        System.out.println("[Level 3 Support] Patching architecture bug: " + ticket.getTitle());
        ticket.markResolved("Level 3 Tech Lead");
    }

    @Override
    protected String getHandlerName() {
        return "Level 3 Support";
    }
}
```

### Concrete Handler 4: Level 4 Support (Critical Escort)

```java
// Level4Support.java
public class Level4Support extends SupportHandler {
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getSeverity() == Severity.CRITICAL;
    }

    @Override
    protected void process(SupportTicket ticket) {
        System.out.println("[Level 4 Executive Support] Handling major outrage/incident: " + ticket.getTitle());
        ticket.markResolved("VP of Engineering");
    }

    @Override
    protected String getHandlerName() {
        return "Level 4 Executive Support";
    }
}
```

---

## Step 4: Assemble the Chain & Client Execution

### Chain Builder Helper

```java
// SupportChainFactory.java
public class SupportChainFactory {
    public static SupportHandler createSupportChain() {
        SupportHandler l1 = new Level1Support();
        SupportHandler l2 = new Level2Support();
        SupportHandler l3 = new Level3Support();
        SupportHandler l4 = new Level4Support();

        // Building chain: L1 -> L2 -> L3 -> L4
        l1.setNext(l2)
          .setNext(l3)
          .setNext(l4);

        return l1; // Return head of the chain
    }
}
```

### Main Client Application

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        SupportHandler supportChain = SupportChainFactory.createSupportChain();

        System.out.println("=== TEST CASE 1: Low Severity Ticket ===");
        SupportTicket t1 = new SupportTicket("TCK-001", "Password reset request", Severity.LOW);
        supportChain.handleRequest(t1);
        System.out.println("Result: " + t1 + "\n");

        System.out.println("=== TEST CASE 2: High Severity Ticket ===");
        SupportTicket t2 = new SupportTicket("TCK-002", "Database deadlock in production", Severity.HIGH);
        supportChain.handleRequest(t2);
        System.out.println("Result: " + t2 + "\n");

        System.out.println("=== TEST CASE 3: Critical Outage Ticket ===");
        SupportTicket t3 = new SupportTicket("TCK-003", "Data center power loss", Severity.CRITICAL);
        supportChain.handleRequest(t3);
        System.out.println("Result: " + t3 + "\n");
    }
}
```

---

## Console Execution Output

```text
=== TEST CASE 1: Low Severity Ticket ===
[Level 1 Support] Resolving basic ticket: Password reset request
Result: Ticket[TCK-001 | LOW]: 'Password reset request' -> RESOLVED by Level 1 Support Agent

=== TEST CASE 2: High Severity Ticket ===
[Level 1 Support] Cannot handle ticket (HIGH). Escalating to next tier...
[Level 2 Support] Cannot handle ticket (HIGH). Escalating to next tier...
[Level 3 Support] Patching architecture bug: Database deadlock in production
Result: Ticket[TCK-002 | HIGH]: 'Database deadlock in production' -> RESOLVED by Level 3 Tech Lead

=== TEST CASE 3: Critical Outage Ticket ===
[Level 1 Support] Cannot handle ticket (CRITICAL). Escalating to next tier...
[Level 2 Support] Cannot handle ticket (CRITICAL). Escalating to next tier...
[Level 3 Support] Cannot handle ticket (CRITICAL). Escalating to next tier...
[Level 4 Executive Support] Handling major outrage/incident: Data center power loss
Result: Ticket[TCK-003 | CRITICAL]: 'Data center power loss' -> RESOLVED by VP of Engineering
```

---

## Step 5: C++ Full Working Reference

```cpp
#include <iostream>
#include <memory>
#include <string>

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL };

class SupportTicket {
    std::string id;
    std::string title;
    Severity severity;
    bool isHandled = false;
    std::string resolvedBy;

public:
    SupportTicket(std::string id, std::string title, Severity severity)
        : id(id), title(title), severity(severity) {}

    std::string getId() const { return id; }
    std::string getTitle() const { return title; }
    Severity getSeverity() const { return severity; }
    bool getIsHandled() const { return isHandled; }

    void markResolved(const std::string& resolver) {
        isHandled = true;
        resolvedBy = resolver;
    }

    void printStatus() const {
        std::cout << "Ticket [" << id << "] -> " 
                  << (isHandled ? ("RESOLVED by " + resolvedBy) : "UNRESOLVED") << std::endl;
    }
};

class SupportHandler {
protected:
    std::shared_ptr<SupportHandler> nextHandler;

public:
    virtual ~SupportHandler() = default;

    std::shared_ptr<SupportHandler> setNext(std::shared_ptr<SupportHandler> next) {
        this->nextHandler = next;
        return next;
    }

    void handleRequest(SupportTicket& ticket) {
        if (canHandle(ticket)) {
            process(ticket);
        } else if (nextHandler) {
            std::cout << "[" << getHandlerName() << "] Escalating to next level...\n";
            nextHandler->handleRequest(ticket);
        } else {
            std::cout << "[END OF CHAIN] Ticket could not be resolved.\n";
        }
    }

protected:
    virtual bool canHandle(const SupportTicket& ticket) = 0;
    virtual void process(SupportTicket& ticket) = 0;
    virtual std::string getHandlerName() = 0;
};

class Level1Support : public SupportHandler {
protected:
    bool canHandle(const SupportTicket& ticket) override {
        return ticket.getSeverity() == Severity::LOW;
    }
    void process(SupportTicket& ticket) override {
        std::cout << "[Level 1] Fixed basic query: " << ticket.getTitle() << "\n";
        ticket.markResolved("L1 Agent");
    }
    std::string getHandlerName() override { return "Level 1 Support"; }
};

class Level2Support : public SupportHandler {
protected:
    bool canHandle(const SupportTicket& ticket) override {
        return ticket.getSeverity() == Severity::MEDIUM;
    }
    void process(SupportTicket& ticket) override {
        std::cout << "[Level 2] Fixed system bug: " << ticket.getTitle() << "\n";
        ticket.markResolved("L2 Engineer");
    }
    std::string getHandlerName() override { return "Level 2 Support"; }
};

int main() {
    auto l1 = std::make_shared<Level1Support>();
    auto l2 = std::make_shared<Level2Support>();

    l1->setNext(l2);

    SupportTicket ticket1("TCK-101", "UI glitch", Severity::LOW);
    l1->handleRequest(ticket1);
    ticket1.printStatus();

    SupportTicket ticket2("TCK-102", "API 500 error", Severity::MEDIUM);
    l1->handleRequest(ticket2);
    ticket2.printStatus();

    return 0;
}
```

---

## Key Design Considerations During Step-by-Step Build

1. **Default Link Return**: Having `setNext()` return `nextHandler` allows fluent chaining: `h1.setNext(h2).setNext(h3).setNext(h4);`.
2. **Handling End of Chain**: Always handle the case where `nextHandler == null`. You can throw an exception, log an unhandled request event, or return a default fallback response.
3. **Decoupled Chain Assembly**: Avoid forcing the client code to assemble the chain manually. Use a Factory or Dependency Injection Container to supply the initialized head handler.
