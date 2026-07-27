public class TransactionLogger {
    public void logTransaction(String cardNumber, String type, double amount, String status) {
        System.out.println("  [Audit Logger] LOGGED: Card " + cardNumber + " | Type: " + type + " | Amount: ₹" + amount + " | Status: " + status);
    }
}
