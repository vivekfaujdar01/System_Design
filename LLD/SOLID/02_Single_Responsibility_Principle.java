// ============================================================
//  SOLID Principle #1 — Single Responsibility Principle (SRP)
//  "A class should have only ONE reason to change."
// ============================================================

// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Violates SRP
//  The Invoice class has 3 responsibilities:
//  1. Business logic (calculateTotal)
//  2. Presentation (printInvoice)
//  3. Persistence (saveToDatabase)
// ─────────────────────────────────────────────

class BadInvoice {
    private String customerName;
    private double amount;

    public BadInvoice(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    // Responsibility 1: Business Logic
    public double calculateTotal() {
        double tax = amount * 0.18; // 18% GST
        return amount + tax;
    }

    // Responsibility 2: Presentation — should NOT be here
    public void printInvoice() {
        System.out.println("========== INVOICE ==========");
        System.out.println("Customer : " + customerName);
        System.out.println("Amount   : " + amount);
        System.out.println("Total    : " + calculateTotal());
        System.out.println("=============================");
    }

    // Responsibility 3: Persistence — should NOT be here
    public void saveToDatabase() {
        System.out.println("Saving invoice for " + customerName + " to database...");
        // database logic here
    }
}


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Follows SRP
//  Each class has exactly ONE responsibility.
// ─────────────────────────────────────────────

// Responsibility 1: Business Logic only
class Invoice {
    private String customerName;
    private double amount;

    public Invoice(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    // Only business logic here
    public double calculateTotal() {
        double tax = amount * 0.18; // 18% GST
        return amount + tax;
    }
}


// Responsibility 2: Presentation only
class InvoicePrinter {
    public void print(Invoice invoice) {
        System.out.println("========== INVOICE ==========");
        System.out.println("Customer : " + invoice.getCustomerName());
        System.out.println("Amount   : " + invoice.getAmount());
        System.out.println("Total    : " + invoice.calculateTotal());
        System.out.println("=============================");
    }
}


// Responsibility 3: Persistence only
class InvoiceRepository {
    public void save(Invoice invoice) {
        System.out.println("Saving invoice for ["
                + invoice.getCustomerName()
                + "] with total ₹"
                + invoice.calculateTotal()
                + " to database...");
        // In real code: DB connection and insert query
    }
}


// ─────────────────────────────────────────────
//  Main — Demo
// ─────────────────────────────────────────────

class Single_Responsibility_Principle {

    public static void main(String[] args) {

        System.out.println("=== ❌ BAD DESIGN (Violates SRP) ===\n");
        BadInvoice badInvoice = new BadInvoice("Alice", 1000.0);
        badInvoice.printInvoice();
        badInvoice.saveToDatabase();

        System.out.println("\n=== ✅ GOOD DESIGN (Follows SRP) ===\n");

        // Each class has a single job
        Invoice invoice = new Invoice("Bob", 2000.0);

        InvoicePrinter printer = new InvoicePrinter();
        printer.print(invoice);

        InvoiceRepository repository = new InvoiceRepository();
        repository.save(invoice);
    }
}

/*
    EXPECTED OUTPUT:
    =================
    === ❌ BAD DESIGN (Violates SRP) ===

    ========== INVOICE ==========
    Customer : Alice
    Amount   : 1000.0
    Total    : 1180.0
    =============================
    Saving invoice for Alice to database...

    === ✅ GOOD DESIGN (Follows SRP) ===

    ========== INVOICE ==========
    Customer : Bob
    Amount   : 2000.0
    Total    : 2360.0
    =============================
    Saving invoice for [Bob] with total ₹2360.0 to database...
*/
