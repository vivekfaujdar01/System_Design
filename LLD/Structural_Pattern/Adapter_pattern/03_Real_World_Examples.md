# Module 3: Real-World Examples of Adapter Pattern

The Adapter Pattern is extensively used in standard libraries, open-source frameworks, and enterprise software.

---

## 1. Practical Real-World Scenarios

| Scenario | Client | Target | Adaptee | Adapter |
| :--- | :--- | :--- | :--- | :--- |
| **Hardware** | Laptop | USB Interface | SD Card | USB SD Card Reader |
| **Charging** | Mobile Phone | USB Type-C | Micro USB Charger | Micro USB to Type-C Converter |
| **Language** | English Speaker | English | Japanese Speaker | Translator / Interpreter |
| **Payments** | E-commerce System | `PaymentProcessor` | `Razorpay`, `Paypal`, `Stripe` | `RazorpayAdapter`, `PaypalAdapter` |

---

## 2. Complete Java Example: Multi-Gateway Payment System

### Problem
An e-commerce platform defines a standard `PaymentProcessor` interface expecting `pay(double amount)`. However:
* **Razorpay SDK** provides `makePayment(double amount)`
* **PayPal SDK** provides `sendMoney(double amount)`
* **Stripe SDK** provides `processTransaction(double amount)`

Using the Adapter Pattern, the client code can execute payments across any gateway without changing a single line of business logic.

### Structure Diagram
```
                      +-------------------+
                      | PaymentProcessor  |  <--- Target Interface
                      +-------------------+
                      | + pay(amount)     |
                      +-------------------+
                                ^
                                |
         +----------------------+----------------------+
         |                                             |
+-------------------+                         +-------------------+
|  RazorpayAdapter  |                         |   PaypalAdapter   |
+-------------------+                         +-------------------+
| - razorpay        |                         | - paypal          |
| + pay(amount)     |                         | + pay(amount)     |
+-------------------+                         +-------------------+
          |                                             |
          v wraps                                       v wraps
+-------------------+                         +-------------------+
|     Razorpay      |                         |      Paypal       |
| (makePayment())   |                         | (sendMoney())     |
+-------------------+                         +-------------------+
```

### Complete Code & Program Flow

#### Step 1: Target Interface (`PaymentProcessor.java`)
```java
public interface PaymentProcessor {
    void pay(double amount);
}
```

#### Step 2: Incompatible Third-Party Libraries (Adaptees)

```java
// Third-Party Library 1: Razorpay (Adaptee 1)
public class Razorpay {
    public void makePayment(double amount) {
        System.out.println("Payment of ₹" + amount + " processed successfully via Razorpay.");
    }
}

// Third-Party Library 2: PayPal (Adaptee 2)
public class Paypal {
    public void sendMoney(double amount) {
        System.out.println("Payment of $" + amount + " processed successfully via PayPal.");
    }
}

// Third-Party Library 3: Stripe (Adaptee 3)
public class Stripe {
    public void processTransaction(double amount) {
        System.out.println("Payment of $" + amount + " processed successfully via Stripe.");
    }
}
```

#### Step 3: Adapter Implementations

```java
// Adapter 1: Razorpay Adapter
public class RazorpayAdapter implements PaymentProcessor {
    private final Razorpay razorpay;

    public RazorpayAdapter(Razorpay razorpay) {
        this.razorpay = razorpay;
    }

    @Override
    public void pay(double amount) {
        // Delegates pay() to Razorpay's makePayment()
        razorpay.makePayment(amount);
    }
}

// Adapter 2: PayPal Adapter
public class PaypalAdapter implements PaymentProcessor {
    private final Paypal paypal;

    public PaypalAdapter(Paypal paypal) {
        this.paypal = paypal;
    }

    @Override
    public void pay(double amount) {
        // Delegates pay() to Paypal's sendMoney()
        paypal.sendMoney(amount);
    }
}

// Adapter 3: Stripe Adapter
public class StripeAdapter implements PaymentProcessor {
    private final Stripe stripe;

    public StripeAdapter(Stripe stripe) {
        this.stripe = stripe;
    }

    @Override
    public void pay(double amount) {
        // Delegates pay() to Stripe's processTransaction()
        stripe.processTransaction(amount);
    }
}
```

#### Step 4: Main Demo Execution (`Main.java`)

```java
public class Main {
    public static void main(String[] args) {
        // Client interacts ONLY with PaymentProcessor target interface
        PaymentProcessor razorpayProcessor = new RazorpayAdapter(new Razorpay());
        razorpayProcessor.pay(1500.0);

        PaymentProcessor paypalProcessor = new PaypalAdapter(new Paypal());
        paypalProcessor.pay(250.0);

        PaymentProcessor stripeProcessor = new StripeAdapter(new Stripe());
        stripeProcessor.pay(499.99);
    }
}
```

### Execution Output
```text
Payment of ₹1500.0 processed successfully via Razorpay.
Payment of $250.0 processed successfully via PayPal.
Payment of $499.99 processed successfully via Stripe.
```

### Program Flow Diagram
```text
main()
  │
  ├──► razorpayProcessor.pay(1500.0) 
  │      └──► RazorpayAdapter.pay() ──► Razorpay.makePayment() ──► Prints Razorpay output
  │
  ├──► paypalProcessor.pay(250.0) 
  │      └──► PaypalAdapter.pay()   ──► Paypal.sendMoney()     ──► Prints PayPal output
  │
  └──► stripeProcessor.pay(499.99) 
         └──► StripeAdapter.pay()   ──► Stripe.processTransaction() ──► Prints Stripe output
```

---

## 3. Adapter Pattern in Java Standard Library (JDK)

### 1. `java.util.Arrays#asList()`
Adapts a raw array into a `List` interface view.
```java
String[] array = {"Apple", "Banana", "Cherry"};
List<String> list = Arrays.asList(array);
```
* **Adaptee**: Array (`String[]`)
* **Target**: `java.util.List`
* **Adapter**: Internal `Arrays.ArrayList` wrapper class.

### 2. `java.io.InputStreamReader`
Adapts a byte input stream (`InputStream`) to a character reader stream (`Reader`).
```java
InputStream inputStream = System.in; // Byte stream
Reader reader = new InputStreamReader(inputStream); // Character reader adapter
BufferedReader bufferedReader = new BufferedReader(reader);
```
* **Adaptee**: `InputStream` (processes raw bytes)
* **Target**: `Reader` (processes characters)
* **Adapter**: `InputStreamReader`

### 3. `java.io.OutputStreamWriter`
Adapts a byte output stream (`OutputStream`) to a character writer stream (`Writer`).
```java
OutputStream outputStream = new FileOutputStream("output.txt");
Writer writer = new OutputStreamWriter(outputStream);
```

---

## 4. Adapter Pattern in Enterprise Frameworks (Spring)

### Spring Security Integration Example
When integrating legacy authentication or custom employee services into Spring Security:
* **Target**: Spring Security's `UserDetailsService` (`loadUserByUsername()`)
* **Adaptee**: Custom enterprise `EmployeeService` (`findEmployeeById()`)
* **Adapter**: `EmployeeToUserDetailsServiceAdapter` implementing `UserDetailsService`.

This avoids altering the existing database schemas or employee service logic while allowing seamless integration with Spring's security ecosystem.

---

> 📂 **Source Code Location**: The individual standalone Java code files for this module can be found in [code/03_Payment_Gateway_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Adapter_pattern/code/03_Payment_Gateway_Example).
