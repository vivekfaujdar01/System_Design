// ============================================================
//  SOLID Principle #4 — Interface Segregation Principle (ISP)
//  "Clients should not be forced to depend on interfaces
//   they do not use."
// ============================================================

// ─────────────────────────────────────────────
//  ❌ BAD DESIGN — Violates ISP
//  One fat interface forces all implementors to implement
//  methods they don't need or can't support.
// ─────────────────────────────────────────────

// Fat "God interface" — too many unrelated methods ❌
interface BadMultiFunctionPrinter {
    void print(String document);
    void scan(String document);
    void fax(String document);
    void copy(String document);
    void staple(String document);
    void emailDocument(String document);
}

// SimplePrinter can only print — forced to implement everything else ❌
class BadSimplePrinter implements BadMultiFunctionPrinter {

    @Override
    public void print(String document) {
        System.out.println("Printing: " + document);
    }

    @Override
    public void scan(String document) {
        // SimplePrinter has no scanner — forced implementation ❌
        throw new UnsupportedOperationException("SimplePrinter cannot scan!");
    }

    @Override
    public void fax(String document) {
        // SimplePrinter has no fax — forced implementation ❌
        throw new UnsupportedOperationException("SimplePrinter cannot fax!");
    }

    @Override
    public void copy(String document) {
        // SimplePrinter cannot copy either — forced ❌
        throw new UnsupportedOperationException("SimplePrinter cannot copy!");
    }

    @Override
    public void staple(String document) {
        throw new UnsupportedOperationException("SimplePrinter cannot staple!");
    }

    @Override
    public void emailDocument(String document) {
        throw new UnsupportedOperationException("SimplePrinter cannot email!");
    }
}


// ─────────────────────────────────────────────
//  ✅ GOOD DESIGN — Follows ISP
//  Small, focused interfaces — each device implements
//  only what it actually supports.
// ─────────────────────────────────────────────

// Segregated interfaces — each is a single capability
interface Printable {
    void print(String document);
}

interface Scannable {
    void scan(String document);
}

interface Faxable {
    void fax(String document);
}

interface Copyable {
    void copy(String document);
}

interface Stapleable {
    void staple(String document);
}

interface Emailable {
    void emailDocument(String document);
}


// Simple printer — only implements what it supports ✅
class SimplePrinter implements Printable {

    @Override
    public void print(String document) {
        System.out.println("[SimplePrinter] Printing: " + document);
    }
}


// Office printer — can print, scan, and copy ✅
class OfficePrinter implements Printable, Scannable, Copyable {

    @Override
    public void print(String document) {
        System.out.println("[OfficePrinter] Printing: " + document);
    }

    @Override
    public void scan(String document) {
        System.out.println("[OfficePrinter] Scanning: " + document);
    }

    @Override
    public void copy(String document) {
        System.out.println("[OfficePrinter] Copying: " + document);
    }
}


// Enterprise all-in-one — implements everything it supports ✅
class EnterpriseMultiFunctionPrinter
        implements Printable, Scannable, Faxable, Copyable, Stapleable, Emailable {

    @Override
    public void print(String document) {
        System.out.println("[Enterprise] Printing: " + document);
    }

    @Override
    public void scan(String document) {
        System.out.println("[Enterprise] Scanning: " + document);
    }

    @Override
    public void fax(String document) {
        System.out.println("[Enterprise] Faxing: " + document);
    }

    @Override
    public void copy(String document) {
        System.out.println("[Enterprise] Copying: " + document);
    }

    @Override
    public void staple(String document) {
        System.out.println("[Enterprise] Stapling: " + document);
    }

    @Override
    public void emailDocument(String document) {
        System.out.println("[Enterprise] Emailing: " + document);
    }
}


// ─────────────────────────────────────────────
//  ✅ ANOTHER EXAMPLE — Worker Roles
// ─────────────────────────────────────────────

interface Workable {
    void work();
}

interface Feedable {
    void eat();
}

interface Restable {
    void rest();
}

// Human worker needs all capabilities ✅
class HumanWorker implements Workable, Feedable, Restable {

    private String name;

    public HumanWorker(String name) {
        this.name = name;
    }

    @Override
    public void work() {
        System.out.println(name + " is working...");
    }

    @Override
    public void eat() {
        System.out.println(name + " is eating lunch...");
    }

    @Override
    public void rest() {
        System.out.println(name + " is taking a break...");
    }
}

// Robot worker — only works, doesn't eat or rest ✅ (no forced implementation)
class RobotWorker implements Workable {

    private String id;

    public RobotWorker(String id) {
        this.id = id;
    }

    @Override
    public void work() {
        System.out.println("Robot[" + id + "] is working 24/7...");
    }
}


// ─────────────────────────────────────────────
//  Service classes that depend only on what they need
// ─────────────────────────────────────────────

class PrintService {
    // Only depends on Printable — not forced to know about scan/fax ✅
    public void printDocument(Printable printer, String doc) {
        printer.print(doc);
    }
}

class ScanService {
    // Only depends on Scannable ✅
    public void scanDocument(Scannable scanner, String doc) {
        scanner.scan(doc);
    }
}

class WorkManager {
    // Only depends on Workable ✅
    public void assignWork(Workable worker) {
        worker.work();
    }
}


// ─────────────────────────────────────────────
//  Main — Demo
// ─────────────────────────────────────────────

class Interface_Segregation_Principle {

    public static void main(String[] args) {

        // ─── BAD DESIGN ───
        System.out.println("=== ❌ BAD DESIGN (Violates ISP) ===\n");

        BadSimplePrinter badPrinter = new BadSimplePrinter();
        badPrinter.print("Report.pdf");  // Works fine

        try {
            badPrinter.scan("Report.pdf");  // Throws! ❌
        } catch (UnsupportedOperationException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // ─── GOOD DESIGN ───
        System.out.println("\n=== ✅ GOOD DESIGN (Follows ISP) ===\n");

        SimplePrinter simple = new SimplePrinter();
        OfficePrinter office = new OfficePrinter();
        EnterpriseMultiFunctionPrinter enterprise = new EnterpriseMultiFunctionPrinter();

        PrintService printService = new PrintService();
        ScanService  scanService  = new ScanService();

        System.out.println("--- Simple Printer ---");
        printService.printDocument(simple, "Letter.pdf");

        System.out.println("\n--- Office Printer ---");
        printService.printDocument(office, "Report.pdf");
        scanService.scanDocument(office, "Contract.pdf");
        office.copy("Policy.pdf");

        System.out.println("\n--- Enterprise Printer ---");
        printService.printDocument(enterprise, "Manual.pdf");
        scanService.scanDocument(enterprise, "Invoice.pdf");
        enterprise.fax("LegalDoc.pdf");
        enterprise.staple("Presentation.pdf");
        enterprise.emailDocument("Summary.pdf");

        System.out.println("\n--- Workers ---");
        WorkManager manager = new WorkManager();
        HumanWorker human = new HumanWorker("Alice");
        RobotWorker robot  = new RobotWorker("R2D2");

        manager.assignWork(human);   // ✅ Human works
        human.eat();                  // ✅ Human eats
        human.rest();                 // ✅ Human rests

        manager.assignWork(robot);   // ✅ Robot works
        // robot.eat() — doesn't exist, no forced implementation ✅
    }
}

/*
    EXPECTED OUTPUT:
    =================
    === ❌ BAD DESIGN (Violates ISP) ===

    Printing: Report.pdf
    ERROR: SimplePrinter cannot scan!

    === ✅ GOOD DESIGN (Follows ISP) ===

    --- Simple Printer ---
    [SimplePrinter] Printing: Letter.pdf

    --- Office Printer ---
    [OfficePrinter] Printing: Report.pdf
    [OfficePrinter] Scanning: Contract.pdf
    [OfficePrinter] Copying: Policy.pdf

    --- Enterprise Printer ---
    [Enterprise] Printing: Manual.pdf
    [Enterprise] Scanning: Invoice.pdf
    [Enterprise] Faxing: LegalDoc.pdf
    [Enterprise] Stapling: Presentation.pdf
    [Enterprise] Emailing: Summary.pdf

    --- Workers ---
    Alice is working...
    Alice is eating lunch...
    Alice is taking a break...
    Robot[R2D2] is working 24/7...
*/
