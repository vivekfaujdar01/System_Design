public class Main {
    public static void main(String[] args) {
        System.out.println("=== PROTECTION / SECURITY PROXY DEMO ===");

        DatabaseExecutor realExecutor = new RealDatabaseExecutor();
        DatabaseExecutor proxyExecutor = new ProtectionProxyDatabaseExecutor(realExecutor);

        User admin = new User("AliceAdmin", User.Role.ADMIN);
        User johnUser = new User("JohnDev", User.Role.USER);
        User guest = new User("AnonymousVisitor", User.Role.GUEST);

        // Test 1: Admin executing SELECT and UPDATE
        try {
            System.out.println(proxyExecutor.executeQuery(admin, "SELECT * FROM users"));
            System.out.println(proxyExecutor.executeQuery(admin, "UPDATE users SET status='ACTIVE' WHERE id=1"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test 2: Standard User executing SELECT vs DELETE
        try {
            System.out.println(proxyExecutor.executeQuery(johnUser, "SELECT * FROM products"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            System.out.println(proxyExecutor.executeQuery(johnUser, "DELETE FROM products WHERE id=5"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }

        // Test 3: Guest attempting SELECT
        try {
            System.out.println(proxyExecutor.executeQuery(guest, "SELECT * FROM public_posts"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }

        // Test 4: Admin attempting destructive DROP DATABASE query
        try {
            System.out.println(proxyExecutor.executeQuery(admin, "DROP DATABASE production_db"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }

        // Test 5: Rate Limit Trigger
        try {
            System.out.println("\nExecuting queries to test Rate Limiter for " + johnUser.getUsername() + ":");
            System.out.println(proxyExecutor.executeQuery(johnUser, "SELECT * FROM table1"));
            System.out.println(proxyExecutor.executeQuery(johnUser, "SELECT * FROM table2"));
            System.out.println(proxyExecutor.executeQuery(johnUser, "SELECT * FROM table3")); // Exceeds limit
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
    }
}
