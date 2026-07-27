# Module 3: Real-World Examples of Facade Pattern

The Facade Pattern is omnipresent in standard libraries, database drivers, security frameworks, and operating system bootloaders.

---

## 1. Enterprise Practical Scenarios

| Domain | Facade Class | Subsystem Classes | Real-World Benefit |
| :--- | :--- | :--- | :--- |
| **Banking ATM** | `AtmFacade` | `AccountService`, `PinVerifier`, `CashDispenser`, `TransactionLogger` | Single interface for cash withdrawal and balance inquiries |
| **Logging Abstraction** | `SLF4J` (`LoggerFactory`) | `Log4j2`, `Logback`, `java.util.logging` | Unified logging API; underlying log engine can be swapped without code changes |
| **Database Frameworks** | Spring `JdbcTemplate` | `Connection`, `PreparedStatement`, `ResultSet`, `SQLException` | Replaces 30 lines of JDBC boilerplate with one-liner query methods |
| **Operating System** | Bootloader / Kernel Facade | CPU, Memory Controller, Disk Subsystem, PCIe Devices | Provides unified OS initialization routine during system startup |

---

## 2. Complete Java Example: Banking ATM Services Facade

### Problem Scenario
An ATM machine must perform multiple checks during a cash withdrawal:
1. Verify Card PIN security (`PinVerifier`).
2. Verify account balance availability (`AccountService`).
3. Dispense paper currency bills (`CashDispenser`).
4. Record audit transaction logs (`TransactionLogger`).

```
                     +-----------------------+
                     |       AtmFacade       |
                     +-----------------------+
                     | + withdrawCash(...)   |
                     +-----------------------+
                                 │
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
+------------------+    +------------------+    +------------------+
|   PinVerifier    |    |  AccountService  |    |  CashDispenser   | ... (TransactionLogger)
+------------------+    +------------------+    +------------------+
```

### Complete Java Implementation

#### Step 1: Subsystem Classes (`PinVerifier.java`, `AccountService.java`, `CashDispenser.java`, `TransactionLogger.java`)

```java
// Subsystem 1: PIN Verifier
public class PinVerifier {
    public boolean verifyPin(String cardNumber, int pin) {
        System.out.println("  [PIN Verifier] Verifying PIN for Card: " + cardNumber);
        return pin == 4321; // Valid PIN condition
    }
}

// Subsystem 2: Account Service
public class AccountService {
    private double balance = 25000.0;

    public boolean hasSufficientBalance(double amount) {
        System.out.println("  [Account Service] Checking balance. Current balance: ₹" + balance);
        return balance >= amount;
    }

    public void deductAmount(double amount) {
        balance -= amount;
        System.out.println("  [Account Service] Deducted ₹" + amount + ". Remaining balance: ₹" + balance);
    }
}

// Subsystem 3: Cash Dispenser
public class CashDispenser {
    public void dispenseCash(double amount) {
        System.out.println("  [Cash Dispenser] Dispensing ₹" + amount + " in currency notes...");
        System.out.println("  [Cash Dispenser] Please collect your cash from the tray. 💵");
    }
}

// Subsystem 4: Transaction Logger
public class TransactionLogger {
    public void logTransaction(String cardNumber, String type, double amount, String status) {
        System.out.println("  [Audit Logger] LOGGED: Card " + cardNumber + " | Type: " + type + " | Amount: ₹" + amount + " | Status: " + status);
    }
}
```

#### Step 2: Facade Class (`AtmFacade.java`)

```java
// ATM Facade coordinating all banking subsystems
public class AtmFacade {
    private final PinVerifier pinVerifier;
    private final AccountService accountService;
    private final CashDispenser cashDispenser;
    private final TransactionLogger logger;

    public AtmFacade() {
        this.pinVerifier = new PinVerifier();
        this.accountService = new AccountService();
        this.cashDispenser = new CashDispenser();
        this.logger = new TransactionLogger();
    }

    public void withdrawCash(String cardNumber, int pin, double amount) {
        System.out.println("=== ATM CASH WITHDRAWAL REQUEST ===");

        // Step 1: Verify PIN
        if (!pinVerifier.verifyPin(cardNumber, pin)) {
            System.out.println("❌ ERROR: Invalid PIN entered.");
            logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "FAILED_INVALID_PIN");
            return;
        }

        // Step 2: Check Balance
        if (!accountService.hasSufficientBalance(amount)) {
            System.out.println("❌ ERROR: Insufficient account funds.");
            logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "FAILED_INSUFFICIENT_FUNDS");
            return;
        }

        // Step 3: Deduct Money
        accountService.deductAmount(amount);

        // Step 4: Dispense Cash
        cashDispenser.dispenseCash(amount);

        // Step 5: Log Successful Transaction
        logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "SUCCESS");

        System.out.println("=== WITHDRAWAL COMPLETE. THANK YOU FOR BANKING WITH US! ===\n");
    }
}
```

#### Step 3: Main Demonstration Execution (`Main.java`)

```java
public class Main {
    public static void main(String[] args) {
        AtmFacade atm = new AtmFacade();

        // Transaction 1: Successful Withdrawal
        atm.withdrawCash("CARD-9876-XXXX", 4321, 5000.0);

        // Transaction 2: Invalid PIN Attempt
        atm.withdrawCash("CARD-9876-XXXX", 1111, 2000.0);
    }
}
```

### Execution Output
```text
=== ATM CASH WITHDRAWAL REQUEST ===
  [PIN Verifier] Verifying PIN for Card: CARD-9876-XXXX
  [Account Service] Checking balance. Current balance: ₹25000.0
  [Account Service] Deducted ₹5000.0. Remaining balance: ₹20000.0
  [Cash Dispenser] Dispensing ₹5000.0 in currency notes...
  [Cash Dispenser] Please collect your cash from the tray. 💵
  [Audit Logger] LOGGED: Card CARD-9876-XXXX | Type: WITHDRAWAL | Amount: ₹5000.0 | Status: SUCCESS
=== WITHDRAWAL COMPLETE. THANK YOU FOR BANKING WITH US! ===

=== ATM CASH WITHDRAWAL REQUEST ===
  [PIN Verifier] Verifying PIN for Card: CARD-9876-XXXX
❌ ERROR: Invalid PIN entered.
  [Audit Logger] LOGGED: Card CARD-9876-XXXX | Type: WITHDRAWAL | Amount: ₹2000.0 | Status: FAILED_INVALID_PIN
```

---

## 3. Facade Pattern in Java Frameworks: Spring `JdbcTemplate`

Without Spring's `JdbcTemplate` facade, raw Java JDBC code requires managing connections, statements, transactions, and error handling manually:

```java
// WITHOUT FACADE: 25+ lines of boilerplate JDBC code
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    conn = dataSource.getConnection();
    stmt = conn.prepareStatement("SELECT name FROM users WHERE id = ?");
    stmt.setInt(1, 101);
    rs = stmt.executeQuery();
    if (rs.next()) { String name = rs.getString("name"); }
} catch (SQLException e) {
    // Handle exception & rollback
} finally {
    if (rs != null) rs.close();
    if (stmt != null) stmt.close();
    if (conn != null) conn.close();
}

// WITH SPRING JdbcTemplate FACADE: One single clean line!
String name = jdbcTemplate.queryForObject("SELECT name FROM users WHERE id = ?", String.class, 101);
```

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/03_Banking_ATM_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Facade_pattern/code/03_Banking_ATM_Example).
