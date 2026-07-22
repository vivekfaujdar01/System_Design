# Module 11 – Builder in Java Libraries

> **Study order:** Read after `10_Builder_vs_All_Factories.md`.  
> See how the pattern appears in real Java code you use every day.

---

## Table of Contents
1. [StringBuilder](#1-stringbuilder)
2. [Lombok @Builder](#2-lombok-builder)
3. [HttpRequest.Builder (Java 11+)](#3-httprequestbuilder-java-11)
4. [Stream.Builder](#4-streambuilder)
5. [Other Notable Builders](#5-other-notable-builders)
6. [Comparison Table](#6-comparison-table)

---

## 1. StringBuilder

`StringBuilder` is the simplest and most-used Builder in Java — you use it every day without thinking about it.

### How It Follows the Builder Pattern

```java
// StringBuilder is a MUTABLE builder for the IMMUTABLE String product

StringBuilder sb = new StringBuilder();   // Create Builder
sb.append("Hello");                        // Step 1
sb.append(", ");                           // Step 2
sb.append("World");                        // Step 3
sb.append("!");                            // Step 4
String result = sb.toString();             // build() → returns immutable String
```

### Fluent (Method Chaining) Style

```java
// Each append() returns 'this' — enabling chaining
String result = new StringBuilder()
    .append("Hello")
    .append(", ")
    .append("World")
    .append("!")
    .toString();   // final build step
```

### Key Parallels

| Builder Pattern Concept | StringBuilder Equivalent |
|-------------------------|--------------------------|
| Builder class           | `StringBuilder`          |
| Product                 | `String`                 |
| Setter methods          | `append()`, `insert()`, `delete()`, `replace()` |
| `build()`               | `toString()`             |
| Fluent API (return this)| `append()` returns `StringBuilder` |
| Immutable product       | `String` is immutable    |

### Important Methods

```java
StringBuilder sb = new StringBuilder("Hello");

sb.append(" World");         // Appends at end     → "Hello World"
sb.insert(5, ",");           // Inserts at index   → "Hello, World"
sb.delete(5, 6);             // Deletes range      → "Hello World"
sb.replace(6, 11, "Java");   // Replaces range     → "Hello Java"
sb.reverse();                // Reverses           → "avaJ olleH"
sb.deleteCharAt(0);          // Removes one char
int len = sb.length();       // Current length
char c  = sb.charAt(0);      // Char at position

String result = sb.toString();  // ← Final build step!
```

### Why Not String Concatenation?

```java
// ❌ String concatenation creates a NEW String object on every +
// For 1000 iterations this creates 1000 temporary Strings → heap pollution!
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;   // new String each time!
}

// ✅ StringBuilder reuses ONE mutable buffer → O(n) vs O(n²)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);   // modifies in place
}
String result = sb.toString();   // single String creation at end
```

---

## 2. Lombok @Builder

Lombok's `@Builder` auto-generates a complete static nested builder class at compile time — eliminating all the boilerplate you wrote in Modules 3 and 4.

### Setup (Maven)

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

### What You Write

```java
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Employee {
    private final String name;
    private final int    age;
    private final String department;
    private final String designation;
    private final double salary;
    private final String email;
    private final boolean isRemote;
}
```

### What Lombok Generates (behind the scenes)

```java
// Lombok auto-generates this equivalent code at compile time:
public class Employee {
    private final String name;
    private final int    age;
    private final String department;
    private final String designation;
    private final double salary;
    private final String email;
    private final boolean isRemote;

    // All-args constructor (private for builder use)
    private Employee(String name, int age, String department,
                     String designation, double salary,
                     String email, boolean isRemote) {
        this.name        = name;
        this.age         = age;
        this.department  = department;
        this.designation = designation;
        this.salary      = salary;
        this.email       = email;
        this.isRemote    = isRemote;
    }

    // Static builder() method — entry point
    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    // Static nested Builder class
    public static class EmployeeBuilder {
        private String name;
        private int    age;
        private String department;
        private String designation;
        private double salary;
        private String email;
        private boolean isRemote;

        public EmployeeBuilder name(String name)          { this.name = name; return this; }
        public EmployeeBuilder age(int age)               { this.age = age; return this; }
        public EmployeeBuilder department(String dept)    { this.department = dept; return this; }
        public EmployeeBuilder designation(String des)    { this.designation = des; return this; }
        public EmployeeBuilder salary(double salary)      { this.salary = salary; return this; }
        public EmployeeBuilder email(String email)        { this.email = email; return this; }
        public EmployeeBuilder isRemote(boolean remote)   { this.isRemote = remote; return this; }

        public Employee build() { return new Employee(name, age, department, designation, salary, email, isRemote); }
    }
}
```

### Usage with Lombok

```java
// Client usage — identical to hand-written Builder
Employee emp = Employee.builder()     // ← static builder() entry point
    .name("Priya Sharma")
    .age(28)
    .department("Engineering")
    .designation("Senior SDE")
    .salary(120000)
    .email("priya@company.com")
    .isRemote(true)
    .build();

System.out.println(emp);
// Output: Employee(name=Priya Sharma, age=28, department=Engineering, ...)
```

### Useful Lombok Builder Annotations

```java
// Default values for fields
@Builder.Default
private String department = "General";

// Mandatory fields via @NonNull (throws NullPointerException if null)
@NonNull
private String name;

// Copy builder — create new object based on existing (withXxx pattern)
@Builder(toBuilder = true)
public class Employee { ... }

// Usage of toBuilder:
Employee updated = existingEmp.toBuilder()
    .salary(150000)   // only change salary
    .build();         // all other fields remain the same
```

---

## 3. HttpRequest.Builder (Java 11+)

Java 11 introduced `java.net.http.HttpRequest` with a built-in Builder — a perfect production example.

### API Structure

```java
// java.net.http.HttpRequest (immutable)
// java.net.http.HttpRequest.Builder (fluent builder)

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
```

### Examples

```java
// ── GET Request ────────────────────────────────────────────────
HttpRequest getRequest = HttpRequest.newBuilder()           // static factory returns Builder
    .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
    .GET()                                                  // method
    .header("Accept", "application/json")                  // optional header
    .timeout(Duration.ofSeconds(10))                       // optional timeout
    .build();                                              // ← returns immutable HttpRequest

// ── POST Request ───────────────────────────────────────────────
HttpRequest postRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .POST(HttpRequest.BodyPublishers.ofString(
        "{\"name\": \"Alice\", \"age\": 25}"
    ))
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer eyJhbGci...")
    .timeout(Duration.ofSeconds(30))
    .build();

// ── Send the request ───────────────────────────────────────────
HttpClient client = HttpClient.newHttpClient();

try {
    HttpResponse<String> response = client.send(
        getRequest,
        HttpResponse.BodyHandlers.ofString()
    );
    System.out.println("Status : " + response.statusCode());
    System.out.println("Body   : " + response.body());
} catch (Exception e) {
    e.printStackTrace();
}
```

### Key Design Points

```
HttpRequest.newBuilder()   → static method returns the Builder (not new Builder())
.GET() / .POST() / .PUT() → sets the HTTP method
.header(key, val)          → can be called multiple times (adds multiple headers)
.build()                   → produces an IMMUTABLE HttpRequest object

Once built:
  HttpRequest is immutable — cannot change method, URL, or headers.
  This is exactly the Builder + Immutability pattern from Module 8!
```

---

## 4. Stream.Builder

`Stream.Builder<T>` lets you build a `Stream` element by element — useful when you can't use `Stream.of()`.

### API

```java
import java.util.stream.Stream;

Stream.Builder<String> streamBuilder = Stream.builder();   // Create Builder

streamBuilder.accept("Apple");    // add elements one by one
streamBuilder.accept("Banana");
streamBuilder.accept("Cherry");

// Conditionally add
if (System.currentTimeMillis() % 2 == 0) {
    streamBuilder.accept("Date");
}

Stream<String> stream = streamBuilder.build();   // ← build() finalises the stream

stream.filter(s -> s.startsWith("A") || s.startsWith("B"))
      .map(String::toUpperCase)
      .forEach(System.out::println);
// Output: APPLE   BANANA
```

### After build() — Stream is consumed

```java
Stream.Builder<Integer> builder = Stream.builder();
builder.add(1).add(2).add(3);   // .add() returns builder (fluent!)

Stream<Integer> stream = builder.build();
stream.forEach(System.out::println);   // ✅ first consumption — OK

// ❌ Once build() is called, the builder is "closed"
// builder.accept(4);   // throws IllegalStateException!
```

### Practical Use Case

```java
// Building a stream from an Iterator (external data source)
Stream.Builder<String> builder = Stream.builder();
Iterator<String> externalSource = getRecordsFromDatabase();

while (externalSource.hasNext()) {
    builder.accept(externalSource.next());
}

Stream<String> records = builder.build();
records.map(String::trim).distinct().sorted().forEach(System.out::println);
```

---

## 5. Other Notable Builders

### 5.1 OkHttp – Request.Builder

```java
import okhttp3.*;

OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()           // Builder
    .url("https://api.github.com/users/octocat")
    .get()
    .addHeader("User-Agent", "MyApp/1.0")
    .build();                                     // Immutable Request

Response response = client.newCall(request).execute();
```

### 5.2 Retrofit – Retrofit.Builder

```java
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

Retrofit retrofit = new Retrofit.Builder()        // Builder
    .baseUrl("https://api.example.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build();                                     // Immutable Retrofit

ApiService service = retrofit.create(ApiService.class);
```

### 5.3 Guava ImmutableList.Builder

```java
import com.google.common.collect.ImmutableList;

ImmutableList<String> fruits = ImmutableList.<String>builder()
    .add("Apple")
    .add("Banana")
    .addAll(anotherList)
    .build();   // Immutable list — cannot add/remove after build()
```

### 5.4 AlertDialog.Builder (Android)

```java
// Android development — classic Builder usage
new AlertDialog.Builder(context)
    .setTitle("Confirm Action")
    .setMessage("Are you sure you want to delete this item?")
    .setPositiveButton("Yes", (dialog, which) -> deleteItem())
    .setNegativeButton("Cancel", null)
    .setCancelable(false)
    .show();   // ← build() equivalent in Android
```

### 5.5 ProcessBuilder (Java Standard Library)

```java
// Build and start an OS process
ProcessBuilder pb = new ProcessBuilder("javac", "MyClass.java")
    .directory(new File("/home/user/projects"))
    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
    .redirectError(ProcessBuilder.Redirect.INHERIT);

Process process = pb.start();   // ← build + execute
int exitCode = process.waitFor();
```

---

## 6. Comparison Table

| Library / API          | Builder Class           | Product (Immutable) | Entry Point                |
|------------------------|-------------------------|---------------------|----------------------------|
| Java Standard          | `StringBuilder`         | `String`            | `new StringBuilder()`      |
| Java 11+               | `HttpRequest.Builder`   | `HttpRequest`       | `HttpRequest.newBuilder()` |
| Java Streams           | `Stream.Builder<T>`     | `Stream<T>`         | `Stream.builder()`         |
| Lombok                 | Generated `XxxBuilder`  | `Xxx` (your class)  | `Xxx.builder()`            |
| OkHttp                 | `Request.Builder`       | `Request`           | `new Request.Builder()`    |
| Retrofit               | `Retrofit.Builder`      | `Retrofit`          | `new Retrofit.Builder()`   |
| Guava                  | `ImmutableList.Builder` | `ImmutableList<T>`  | `ImmutableList.builder()`  |
| Android                | `AlertDialog.Builder`   | `AlertDialog`       | `new AlertDialog.Builder()`|
| Java OS                | `ProcessBuilder`        | `Process`           | `new ProcessBuilder(...)`  |

---

**← Prev:** [`10_Builder_vs_All_Factories.md`](./10_Builder_vs_All_Factories.md)  
**Next →** [`12_Real_World_Examples.md`](./12_Real_World_Examples.md)
