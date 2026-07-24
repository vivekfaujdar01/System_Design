/**
 * VARIANT 6 — Enum Singleton (Recommended by Joshua Bloch — Effective Java, Item 3)
 *
 * The most robust Singleton implementation in Java.
 * The JVM treats each enum constant as a guaranteed single instance.
 *
 * Automatically handles:
 *   ✅ Thread safety       — JVM initialises enum constants exactly once
 *   ✅ Serialization       — JVM's built-in enum serialization always returns INSTANCE
 *   ✅ Reflection attacks  — Cannot create a second enum via reflection (throws IllegalArgumentException)
 *   ✅ Cloning             — Enums cannot be cloned
 *
 * Simulates a real-world Database Connection manager.
 *
 * Use when: you need the safest possible Singleton with zero boilerplate.
 */
public enum DatabaseConnection {

    INSTANCE;   // ← The single instance; enum guarantees only one exists in the JVM

    private final String url;
    private final String user;
    private final String password;

    // Enum constructors are implicitly private
    DatabaseConnection() {
        this.url      = "jdbc:mysql://localhost:3306/production_db";
        this.user     = "admin";
        this.password = "s3cr3t";
        System.out.println("[DatabaseConnection] Initialized — connection settings loaded.");
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getUrl()      { return url; }
    public String getUser()     { return user; }
    public String getPassword() { return password; }

    /**
     * Simulates executing a SQL query through the singleton connection.
     */
    public void executeQuery(String sql) {
        System.out.println("[DB @ " + System.identityHashCode(this) + "] Executing: " + sql);
    }

    public void showInfo() {
        System.out.println("[DatabaseConnection] instance hashCode = " + System.identityHashCode(this)
                + " | url=" + url + " | user=" + user + " | password=" + password);
    }
}
