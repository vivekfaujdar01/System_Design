# Module 3: Real-World Examples of Decorator Pattern

The Decorator Pattern is foundational across core system standard libraries (e.g., Java I/O streams), web frameworks (middleware chains), and security pipelines.

---

## 1. Enterprise Practical Scenarios

| Domain | Base Component | Concrete Component | Decorators | Real-World Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **Java I/O Streams** | `InputStream` / `Reader` | `FileInputStream` / `FileReader` | `BufferedInputStream`, `GZIPInputStream`, `CipherInputStream` | Adds buffering, compression, or encryption dynamically to byte streams |
| **Data Processing Pipeline** | `DataSource` | `FileDataSource` | `EncryptionDecorator`, `CompressionDecorator` | Transparently encrypts and compresses file data before writing to disk |
| **Web Middleware** | `HttpRequest` | `BasicHttpRequest` | `AuthenticationDecorator`, `LoggingDecorator`, `RateLimitingDecorator` | Wraps web requests with security checks and metrics tracking |
| **UI Components** | `Widget` | `TextView` | `BorderDecorator`, `ScrollbarDecorator` | Dynamically draws borders or scrollbars around UI text components |

---

## 2. Complete Java Example: Data Encryption & Compression Pipeline

### Problem Scenario
A secure storage system needs to write sensitive data to disk:
* Raw file storage (`FileDataSource`) writes plain text.
* Security requirements demand **Base64 Encryption** before writing.
* Storage optimization requires **Compression** before writing.
* Different customers require different pipeline combinations (e.g., Encrypt only, Compress only, or Encrypt + Compress).

```
                      [ EncryptionDecorator ]
                                │
                         wraps  ▼
                      [ CompressionDecorator ]
                                │
                         wraps  ▼
                     [ FileDataSource (Base) ]
```

### Complete Java Implementation

#### Step 1: Component Interface (`DataSource.java`)
```java
// Component Interface
public interface DataSource {
    void writeData(String data);
    String readData();
}
```

#### Step 2: Concrete Component (`FileDataSource.java`)
```java
// Concrete Base Component (Writes raw data)
public class FileDataSource implements DataSource {
    private String storedData = "";

    @Override
    public void writeData(String data) {
        this.storedData = data;
        System.out.println("  [Disk Write 💾] Writing raw bytes to file storage: " + storedData);
    }

    @Override
    public String readData() {
        System.out.println("  [Disk Read 📖] Reading raw bytes from file storage.");
        return storedData;
    }
}
```

#### Step 3: Base Decorator (`DataSourceDecorator.java`)
```java
// Abstract Base Decorator wrapping a DataSource reference
public abstract class DataSourceDecorator implements DataSource {
    protected final DataSource wrappee; // The wrapped DataSource!

    public DataSourceDecorator(DataSource source) {
        this.wrappee = source;
    }

    @Override
    public void writeData(String data) {
        wrappee.writeData(data);
    }

    @Override
    public String readData() {
        return wrappee.readData();
    }
}
```

#### Step 4: Concrete Decorators (`EncryptionDecorator.java` & `CompressionDecorator.java`)

```java
import java.util.Base64;

// Concrete Decorator 1: Base64 Encryption / Decryption
public class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        System.out.println("  [Encryption 🔒] Encrypting plain data using Base64...");
        String encrypted = Base64.getEncoder().encodeToString(data.getBytes());
        super.writeData(encrypted);
    }

    @Override
    public String readData() {
        String encrypted = super.readData();
        System.out.println("  [Decryption 🔓] Decrypting Base64 data back to plain text...");
        return new String(Base64.getDecoder().decode(encrypted));
    }
}

// Concrete Decorator 2: Compression / Decompression
public class CompressionDecorator extends DataSourceDecorator {
    public CompressionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        System.out.println("  [Compression 🗜️] Compressing data string...");
        String compressed = "COMPRESSED(" + data + ")";
        super.writeData(compressed);
    }

    @Override
    public String readData() {
        String compressed = super.readData();
        System.out.println("  [Decompression 🔓] Decompressing data string...");
        return compressed.replace("COMPRESSED(", "").replace(")", "");
    }
}
```

#### Step 5: Main Demonstration Execution (`Main.java`)
```java
public class Main {
    public static void main(String[] args) {
        String sensitivePayload = "Confidential Banking Secret Key: 9876-ABCD-4321";

        System.out.println("=== 1. PLAIN FILE WRITE ===");
        DataSource plainSource = new FileDataSource();
        plainSource.writeData(sensitivePayload);

        System.out.println("\n=== 2. ENCRYPTED + COMPRESSED PIPELINE ===");
        // Wrap FileDataSource -> Compression -> Encryption
        DataSource secureSource = new EncryptionDecorator(
                                       new CompressionDecorator(
                                           new FileDataSource()
                                       )
                                   );

        // Write Operations (Encrypts first, then Compresses, then Writes to file)
        System.out.println("--> Executing Pipeline Write:");
        secureSource.writeData(sensitivePayload);

        // Read Operations (Reads file, Decompresses, then Decrypts)
        System.out.println("\n--> Executing Pipeline Read:");
        String recoveredData = secureSource.readData();
        System.out.println("Final Recovered Data: " + recoveredData);
    }
}
```

### Execution Output
```text
=== 1. PLAIN FILE WRITE ===
  [Disk Write 💾] Writing raw bytes to file storage: Confidential Banking Secret Key: 9876-ABCD-4321

=== 2. ENCRYPTED + COMPRESSED PIPELINE ===
--> Executing Pipeline Write:
  [Encryption 🔒] Encrypting plain data using Base64...
  [Compression 🗜️] Compressing data string...
  [Disk Write 💾] Writing raw bytes to file storage: COMPRESSED(Q29uZmlkZW50aWFsIEJhbmtpbmcgU2VjcmV0IEtleTogOTg3Ni1BQkNELTQzMjE=)

--> Executing Pipeline Read:
  [Disk Read 📖] Reading raw bytes from file storage.
  [Decompression 🔓] Decompressing data string...
  [Decryption 🔓] Decrypting Base64 data back to plain text...
Final Recovered Data: Confidential Banking Secret Key: 9876-ABCD-4321
```

---

## 3. Decorator Pattern in Java Standard Library (`java.io`)

The JDK `java.io` package is the most famous example of the Decorator Pattern in production:

```java
// Combining File I/O + Buffering + GZIP Decompression in Java
InputStream fileStream = new FileInputStream("data.gz"); // Concrete Component
InputStream bufferedStream = new BufferedInputStream(fileStream); // Decorator 1
InputStream gzipStream = new GZIPInputStream(bufferedStream); // Decorator 2

int data = gzipStream.read(); // Execution chains transparently across all 3 wrappers!
```

---

> 📂 **Source Code Location**: Standalone runnable Java code for this module is located in [code/03_Data_Stream_Pipeline_Example/](file:///home/faujdar/Desktop/System_Design/LLD/Structural_Pattern/Decorator_pattern/code/03_Data_Stream_Pipeline_Example).
