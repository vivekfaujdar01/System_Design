public class Main {
    public static void main(String[] args) {
        System.out.println("=== JDK DYNAMIC PROXY (SPRING AOP STYLE) DEMO ===");

        // 1. Create real service instance
        BankingService realService = new BankingServiceImpl();

        // 2. Wrap real service in Java Dynamic Proxy using InvocationHandler
        BankingService proxyService = LoggingAndTransactionHandler.createProxy(realService, BankingService.class);

        System.out.println("Generated Dynamic Proxy Class: " + proxyService.getClass().getName());

        // 3. Invoke read-only operation
        double balance = proxyService.getBalance("ACC-98765");
        System.out.println("Returned Balance: $" + balance);

        // 4. Invoke valid transactional write operation
        try {
            proxyService.transferMoney("ACC-111", "ACC-222", 2500.0);
        } catch (Exception e) {
            System.err.println("Main Caught Exception: " + e.getMessage());
        }

        // 5. Invoke failing transactional write operation (Triggers AOP Rollback)
        try {
            proxyService.transferMoney("ACC-111", "ACC-222", 50000.0);
        } catch (Exception e) {
            System.err.println("Main Caught Expected Exception: " + e.getMessage());
        }
    }
}
