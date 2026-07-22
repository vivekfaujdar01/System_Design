# Module 07 – Director Class

> **Study order:** Read after `06_Fluent_Builder.md`.  
> The Director is the optional GoF participant that orchestrates construction steps.

---

## Table of Contents
1. [Why Director Exists](#1-why-director-exists)
2. [What Director Does (and Does Not Do)](#2-what-director-does-and-does-not-do)
3. [When to Use the Director](#3-when-to-use-the-director)
4. [When to Skip the Director](#4-when-to-skip-the-director)
5. [Complete Example – Report Generator](#5-complete-example--report-generator)
6. [Director with Multiple Recipes](#6-director-with-multiple-recipes)
7. [Director as a Configuration Registry](#7-director-as-a-configuration-registry)
8. [Summary](#8-summary)

---

## 1. Why Director Exists

Without a Director, the **client code** is responsible for calling builder steps in the right order:

```java
// Without Director — client must know the ORDER of steps
Computer pc = new Computer.Builder("i9", "32GB")
    .storage("2TB")
    .gpu("RTX 4090")
    .os("Windows 11")
    .bluetooth(true)
    .build();
```

This is fine for a simple object. But what if:
- The construction has **many steps** and a **specific required order**?
- The **same configuration** is needed in **multiple places** in your codebase?
- You want to **reuse the same recipe** (sequence of steps) with different builders?

That's when a **Director** adds value:

```java
// With Director — client only specifies WHAT to build (which builder)
// Director knows HOW to build it (which steps, in what order)
ComputerDirector director = new ComputerDirector(new GamingComputerBuilder());
Computer gamingPC = director.constructHighEnd();
```

**The Director encapsulates the construction sequence — just like a real director tells actors how to perform.**

---

## 2. What Director Does (and Does Not Do)

```
Director DOES:
  ✅ Call builder methods in the correct order
  ✅ Choose which builder methods to call (e.g., skip optional steps for basic models)
  ✅ Provide named "presets" or "recipes" (constructHighEnd, constructBudget, constructOffice)
  ✅ Hide construction complexity from the client
  ✅ Allow the SAME recipe to work with DIFFERENT builders

Director does NOT:
  ❌ Know HOW each step is implemented (that's the ConcreteBuilder's job)
  ❌ Hold any product state itself
  ❌ Create the product directly — only guides the builder
  ❌ Need to know the product type — only knows the builder interface
```

---

## 3. When to Use the Director

✅ Use Director when:

| Situation                                              | Example                                      |
|-------------------------------------------------------|----------------------------------------------|
| Many ordered steps that clients should not manage     | Building documents with header → body → footer|
| Same construction sequence used in multiple places    | "Luxury car" config used in showroom + website|
| You want to provide "named presets"                   | `constructBasic()`, `constructPremium()`      |
| Step order is business-critical and must not vary     | Legal document with mandatory section order   |
| Different builders must follow the same recipe        | `PdfReportBuilder` and `HtmlReportBuilder` — same steps, different output |

---

## 4. When to Skip the Director

❌ Skip Director when:

| Situation                                              | Better Approach                              |
|-------------------------------------------------------|----------------------------------------------|
| Simple object with few optional fields                | Just use the Builder directly at call site    |
| Construction steps are always the same (one recipe)   | Put the recipe in a factory method            |
| Client needs full control over step selection         | Let client use Builder fluent API directly   |
| Modern APIs (OkHttp, Retrofit, etc.)                  | These skip Director — Builder is enough      |

> **Rule of thumb:** If your codebase never calls the Director more than once, skip it.  
> If it's called in many places with the same sequence, add it.

---

## 5. Complete Example – Report Generator

Different report formats (PDF, HTML, CSV) share the same logical structure:  
`Header → Summary → Body Table → Footer`

```java
// ── Report (Product) ──────────────────────────────────────────
// Report.java
public final class Report {
    private final String header;
    private final String summary;
    private final String body;
    private final String footer;
    private final String format;

    private Report(Builder builder) {
        this.header  = builder.header;
        this.summary = builder.summary;
        this.body    = builder.body;
        this.footer  = builder.footer;
        this.format  = builder.format;
    }

    @Override
    public String toString() {
        return "\n======= " + format + " REPORT =======\n" +
               "[HEADER]  " + header  + "\n" +
               "[SUMMARY] " + summary + "\n" +
               "[BODY]    " + body    + "\n" +
               "[FOOTER]  " + footer  + "\n" +
               "================================";
    }

    public static class Builder {
        String header;
        String summary;
        String body;
        String footer;
        String format;

        public Builder header(String header)   { this.header = header; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder body(String body)       { this.body = body; return this; }
        public Builder footer(String footer)   { this.footer = footer; return this; }
        public Builder format(String format)   { this.format = format; return this; }

        public Report build() {
            if (header == null) throw new IllegalStateException("Report header is required.");
            return new Report(this);
        }
    }
}
```

```java
// ── ReportBuilder Interface ───────────────────────────────────
// ReportBuilder.java
public interface ReportBuilder {
    void buildHeader(String title);
    void buildSummary(String data);
    void buildBody(String[][] tableData);
    void buildFooter(String author);
    Report getResult();
}
```

```java
// ── Concrete Builder A: PDF ───────────────────────────────────
// PdfReportBuilder.java
public class PdfReportBuilder implements ReportBuilder {
    private final Report.Builder builder = new Report.Builder().format("PDF");

    @Override
    public void buildHeader(String title) {
        builder.header("PDF Header | Title: " + title + " | Page 1");
    }

    @Override
    public void buildSummary(String data) {
        builder.summary("PDF Summary: " + data + " [Formatted for print]");
    }

    @Override
    public void buildBody(String[][] tableData) {
        StringBuilder sb = new StringBuilder("PDF Table:\n");
        for (String[] row : tableData) {
            sb.append("  | ").append(String.join(" | ", row)).append(" |").append("\n");
        }
        builder.body(sb.toString().trim());
    }

    @Override
    public void buildFooter(String author) {
        builder.footer("Confidential — Author: " + author + " — Do not distribute");
    }

    @Override
    public Report getResult() {
        return builder.build();
    }
}
```

```java
// ── Concrete Builder B: HTML ──────────────────────────────────
// HtmlReportBuilder.java
public class HtmlReportBuilder implements ReportBuilder {
    private final Report.Builder builder = new Report.Builder().format("HTML");

    @Override
    public void buildHeader(String title) {
        builder.header("<h1>" + title + "</h1><hr/>");
    }

    @Override
    public void buildSummary(String data) {
        builder.summary("<p class='summary'>" + data + "</p>");
    }

    @Override
    public void buildBody(String[][] tableData) {
        StringBuilder sb = new StringBuilder("<table><tbody>\n");
        for (String[] row : tableData) {
            sb.append("  <tr><td>").append(String.join("</td><td>", row)).append("</td></tr>\n");
        }
        sb.append("</tbody></table>");
        builder.body(sb.toString());
    }

    @Override
    public void buildFooter(String author) {
        builder.footer("<footer>Author: <b>" + author + "</b></footer>");
    }

    @Override
    public Report getResult() {
        return builder.build();
    }
}
```

```java
// ── Director ──────────────────────────────────────────────────
// ReportDirector.java
public class ReportDirector {
    private final ReportBuilder builder;

    public ReportDirector(ReportBuilder builder) {
        this.builder = builder;
    }

    /**
     * The Director knows: header → summary → body → footer.
     * It does NOT know whether the output is PDF or HTML.
     * That's the ConcreteBuilder's concern.
     */
    public Report constructSalesReport() {
        String[][] salesData = {
            {"Product", "Units Sold", "Revenue"},
            {"Laptop",  "120",        "₹84,00,000"},
            {"Phone",   "350",        "₹52,50,000"},
            {"Tablet",  "80",         "₹16,00,000"}
        };

        builder.buildHeader("Monthly Sales Report — July 2025");
        builder.buildSummary("Total Revenue: ₹1,52,50,000 | Growth: +12% MoM");
        builder.buildBody(salesData);
        builder.buildFooter("Riya Sharma, Sales Head");

        return builder.getResult();
    }

    public Report constructMinimalReport() {
        // Only header and footer — skip summary and body for a brief report
        builder.buildHeader("Quick Status Update");
        builder.buildSummary("All systems operational.");
        builder.buildBody(new String[][]{{"Status", "OK"}});
        builder.buildFooter("Ops Team");

        return builder.getResult();
    }
}
```

```java
// ── Client ─────────────────────────────────────────────────────
// Main.java
public class Main {
    public static void main(String[] args) {

        // Same Director — different builder → different output format
        ReportDirector pdfDirector  = new ReportDirector(new PdfReportBuilder());
        ReportDirector htmlDirector = new ReportDirector(new HtmlReportBuilder());

        Report pdfReport  = pdfDirector.constructSalesReport();
        Report htmlReport = htmlDirector.constructSalesReport();

        System.out.println(pdfReport);
        System.out.println(htmlReport);
    }
}
```

**Output:**
```
======= PDF REPORT =======
[HEADER]  PDF Header | Title: Monthly Sales Report — July 2025 | Page 1
[SUMMARY] PDF Summary: Total Revenue: ₹1,52,50,000 | Growth: +12% MoM [Formatted for print]
[BODY]    PDF Table:
  | Product | Units Sold | Revenue |
  | Laptop | 120 | ₹84,00,000 |
  | Phone | 350 | ₹52,50,000 |
  | Tablet | 80 | ₹16,00,000 |
[FOOTER]  Confidential — Author: Riya Sharma, Sales Head — Do not distribute
================================

======= HTML REPORT =======
[HEADER]  <h1>Monthly Sales Report — July 2025</h1><hr/>
[SUMMARY] <p class='summary'>Total Revenue: ₹1,52,50,000 | Growth: +12% MoM</p>
[BODY]    <table><tbody>
  <tr><td>Product</td><td>Units Sold</td><td>Revenue</td></tr>
  ...
[FOOTER]  <footer>Author: <b>Riya Sharma, Sales Head</b></footer>
================================
```

---

## 6. Director with Multiple Recipes

```java
public class HouseDirector {
    private HouseBuilder builder;

    public HouseDirector(HouseBuilder builder) {
        this.builder = builder;
    }

    // Recipe 1: Full luxury house
    public House constructLuxury() {
        return builder
            .foundation("Deep reinforced concrete")
            .walls("Double-insulated brick")
            .roof("Spanish terracotta tiles")
            .windows(16)
            .garage(true)
            .pool(true)
            .build();
    }

    // Recipe 2: Budget house
    public House constructBudget() {
        return builder
            .foundation("Standard concrete")
            .walls("Single brick")
            .roof("Corrugated metal")
            .windows(4)
            .garage(false)
            .pool(false)
            .build();
    }

    // Recipe 3: Office building (no pool, many windows)
    public House constructOffice() {
        return builder
            .foundation("Pile foundation")
            .walls("Glass and steel frame")
            .roof("Flat concrete")
            .windows(50)
            .garage(true)
            .pool(false)
            .build();
    }
}

// Client: swap builders easily
HouseDirector director;

director = new HouseDirector(new BrickHouseBuilder());
House brickLuxury = director.constructLuxury();

director = new HouseDirector(new WoodenHouseBuilder());
House woodBudget = director.constructBudget();
```

---

## 7. Director as a Configuration Registry

In modern Java apps, the Director concept is often expressed as a **factory method** or **config class**:

```java
public class ComputerPresets {
    // These static methods act as "Directors" for specific presets
    public static Computer gamingPC() {
        return new Computer.Builder("Intel i9-13900K", "64GB DDR5")
            .storage("4TB NVMe SSD")
            .gpu("NVIDIA RTX 4090")
            .os("Windows 11")
            .bluetooth(true)
            .build();
    }

    public static Computer officePC() {
        return new Computer.Builder("Intel i5-12400", "16GB DDR4")
            .storage("512GB SSD")
            .os("Ubuntu LTS")
            .build();
    }

    public static Computer serverNode() {
        return new Computer.Builder("AMD EPYC 7763", "512GB ECC")
            .storage("10TB RAID")
            .os("CentOS Stream")
            .bluetooth(false)
            .build();
    }
}

// Client usage — clean and intention-revealing
Computer pc = ComputerPresets.gamingPC();
```

---

## 8. Summary

| Aspect             | With Director                         | Without Director                     |
|--------------------|---------------------------------------|--------------------------------------|
| Step order control | Director enforces it                  | Client must know the order           |
| Code reuse         | Same sequence in one place            | Sequence repeated everywhere         |
| Coupling           | Client depends on Director only       | Client depends on all builder steps  |
| Flexibility        | Swap builders without touching client | Client must be updated               |
| Complexity         | More classes (Director added)         | Simpler structure                    |
| Modern usage       | Common in frameworks & libraries      | Common in application code           |

---

**← Prev:** [`06_Fluent_Builder.md`](./06_Fluent_Builder.md)  
**Next →** [`08_Immutable_Objects.md`](./08_Immutable_Objects.md)
