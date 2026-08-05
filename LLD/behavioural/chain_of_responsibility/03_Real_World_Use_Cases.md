# 🌍 Real-World Use Cases & Applications

The **Chain of Responsibility Pattern** is heavily used in production systems, framework internals, and system design interviews. Below are 4 major real-world implementations.

---

## 1. Web Application Request Middleware / Filter Pipeline

### Industry Context
Every modern web framework (Spring Security, Jakarta Servlet Filters, Express.js in Node.js, ASP.NET Core Middleware) uses Chain of Responsibility to handle HTTP requests.

```
Incoming Request
       │
       ▼
┌──────────────────┐
│  LoggingFilter   │  (Logs IP, Timestamp, HTTP Verb)
└────────┬─────────┘
         │ pass to next
         ▼
┌──────────────────┐
│   RateLimiter    │  (Checks Redis token bucket; short-circuits if > 100 req/min)
└────────┬─────────┘
         │ pass to next
         ▼
┌──────────────────┐
│  AuthHeaderFilter│  (Validates JWT Bearer Token, extracts User Principal)
└────────┬─────────┘
         │ pass to next
         ▼
┌──────────────────┐
│  RoleAuthFilter  │  (Ensures User has REQUIRED_ROLE = 'ADMIN')
└────────┬─────────┘
         │ pass to next
         ▼
[ Endpoint Controller ] (Executes actual API logic)
```

### Code Example (HTTP Middleware Pipeline)

```java
// HttpRequest.java
public class HttpRequest {
    private final String path;
    private final String token;
    private final String clientIp;
    private final String userRole;
    private boolean isHandled = false;

    public HttpRequest(String path, String token, String clientIp, String userRole) {
        this.path = path;
        this.token = token;
        this.clientIp = clientIp;
        this.userRole = userRole;
    }

    public String getPath() { return path; }
    public String getToken() { return token; }
    public String getClientIp() { return clientIp; }
    public String getUserRole() { return userRole; }
    public void markHandled() { this.isHandled = true; }
    public boolean isHandled() { return isHandled; }
}

// Middleware.java
public abstract class Middleware {
    private Middleware next;

    public static Middleware link(Middleware first, Middleware... chain) {
        Middleware head = first;
        for (Middleware nextInChain : chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    public abstract boolean check(HttpRequest request);

    protected boolean checkNext(HttpRequest request) {
        if (next == null) {
            return true; // Reached end of chain cleanly
        }
        return next.check(request);
    }
}

// AuthenticationMiddleware.java
public class AuthenticationMiddleware extends Middleware {
    @Override
    public boolean check(HttpRequest request) {
        if (request.getToken() == null || !request.getToken().startsWith("Bearer ")) {
            System.out.println("❌ 401 Unauthorized: Invalid or missing JWT token.");
            return false; // Short-circuit chain!
        }
        System.out.println("✅ Authentication Passed.");
        return checkNext(request);
    }
}

// RoleCheckMiddleware.java
public class RoleCheckMiddleware extends Middleware {
    private final String requiredRole;

    public RoleCheckMiddleware(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public boolean check(HttpRequest request) {
        if (!requiredRole.equalsIgnoreCase(request.getUserRole())) {
            System.out.println("❌ 403 Forbidden: Required role '" + requiredRole + "', found '" + request.getUserRole() + "'");
            return false; // Short-circuit!
        }
        System.out.println("✅ Role Authorization Passed.");
        return checkNext(request);
    }
}
```

---

## 2. ATM Cash Dispensation System

### Industry Context
When a user requests cash withdrawal at an ATM, the ATM algorithm must break down the requested amount into available bill denominations ($100, $50, $20, $10).

```
   [Withdrawal Request: $280]
              │
              ▼
    ┌──────────────────┐
    │  $100 Bill Dispenser │  -> Dispenses 2 x $100 ($200). Remainder: $80
    └─────────┬────────┘
              │ pass $80
              ▼
    ┌──────────────────┐
    │  $50 Bill Dispenser  │  -> Dispenses 1 x $50 ($50). Remainder: $30
    └─────────┬────────┘
              │ pass $30
              ▼
    ┌──────────────────┐
    │  $20 Bill Dispenser  │  -> Dispenses 1 x $20 ($20). Remainder: $10
    └─────────┬────────┘
              │ pass $10
              ▼
    ┌──────────────────┐
    │  $10 Bill Dispenser  │  -> Dispenses 1 x $10 ($10). Remainder: $0
    └──────────────────┘
```

### Code Example (ATM Dispenser Chain)

```java
// Currency.java
public class Currency {
    private int amount;

    public Currency(int amount) {
        this.amount = amount;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}

// DispenseChain.java
public interface DispenseChain {
    void setNextChain(DispenseChain nextChain);
    void dispense(Currency cur);
}

// Dollar100Dispenser.java
public class Dollar100Dispenser implements DispenseChain {
    private DispenseChain chain;

    @Override
    public void setNextChain(DispenseChain nextChain) {
        this.chain = nextChain;
    }

    @Override
    public void dispense(Currency cur) {
        if (cur.getAmount() >= 100) {
            int num = cur.getAmount() / 100;
            int remainder = cur.getAmount() % 100;
            System.out.println("Dispensing " + num + " x $100 note(s)");
            if (remainder != 0 && this.chain != null) {
                this.chain.dispense(new Currency(remainder));
            }
        } else if (this.chain != null) {
            this.chain.dispense(cur);
        }
    }
}

// Dollar50Dispenser.java
public class Dollar50Dispenser implements DispenseChain {
    private DispenseChain chain;

    @Override
    public void setNextChain(DispenseChain nextChain) {
        this.chain = nextChain;
    }

    @Override
    public void dispense(Currency cur) {
        if (cur.getAmount() >= 50) {
            int num = cur.getAmount() / 50;
            int remainder = cur.getAmount() % 50;
            System.out.println("Dispensing " + num + " x $50 note(s)");
            if (remainder != 0 && this.chain != null) {
                this.chain.dispense(new Currency(remainder));
            }
        } else if (this.chain != null) {
            this.chain.dispense(cur);
        }
    }
}
```

---

## 3. Hierarchical Logging Framework (e.g. Log4j / Logback)

### Industry Context
In logging frameworks like Log4j, loggers are chained by log severity level (`DEBUG < INFO < WARN < ERROR < FATAL`).
When a log message is printed at `ERROR` level, all loggers configured for `ERROR` and above handle it (e.g., standard console logger, file logger, Slack alert webhook logger).

```
   [Log Message: ERROR - DB connection pool exhausted]
                           │
                           ▼
          ┌──────────────────────────────────┐
          │  ConsoleLogger (Level: DEBUG)    │ -> Logs to System.out
          └────────────────┬─────────────────┘
                           │ pass next
                           ▼
          ┌──────────────────────────────────┐
          │   FileLogger (Level: INFO)       │ -> Writes to /var/log/app.log
          └────────────────┬─────────────────┘
                           │ pass next
                           ▼
          ┌──────────────────────────────────┐
          │  EmailAlertLogger (Level: ERROR) │ -> Sends alert email to DevOps
          └──────────────────────────────────┘
```

### Code Example (Logger Chain)

```java
public abstract class AbstractLogger {
    public static int INFO = 1;
    public static int DEBUG = 2;
    public static int ERROR = 3;

    protected int level;
    protected AbstractLogger nextLogger;

    public void setNextLogger(AbstractLogger nextLogger) {
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

    abstract protected void write(String message);
}

public class ConsoleLogger extends AbstractLogger {
    public ConsoleLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("STANDARD CONSOLE LOG: " + message);
    }
}

public class ErrorLogger extends AbstractLogger {
    public ErrorLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.err.println("CRITICAL ERROR LOG: " + message);
    }
}
```

---

## 4. E-Commerce Order Processing & Discount Pipeline

### Industry Context
When an order is placed on Amazon or Shopify:
1. **Stock Validation Handler**: Checks if warehouse has items in stock.
2. **Promo Code / Coupon Handler**: Calculates discount if valid coupon applied.
3. **Tax Calculator Handler**: Applies state/country VAT or Sales Tax.
4. **Shipping Fee Handler**: Calculates delivery fee based on address/weight.
5. **Payment Processing Handler**: Charges credit card/UPI.

---

## Summary of Use Cases

| Domain | Request Passed | Chain Handlers | Outcome |
|--------|---------------|----------------|---------|
| **Web Middleware** | `HttpRequest` | Auth -> RateLimit -> CORS -> RoleCheck | Accepts or Rejects HTTP Request |
| **Banking / ATM** | `Currency` ($ Amount) | $100 -> $50 -> $20 -> $10 Dispenser | Dispenses exact bills |
| **Logging Systems** | `LogMessage` & Level | ConsoleLogger -> FileLogger -> SlackLogger | Multi-destination logging |
| **E-Commerce** | `OrderContext` | Inventory -> Discount -> Tax -> Payment | Final invoice total & checkout |
