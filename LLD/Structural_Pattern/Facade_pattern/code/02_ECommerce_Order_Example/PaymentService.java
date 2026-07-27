public class PaymentService {
    public boolean processPayment(String accountId, double amount) {
        System.out.println("  [Payment Service] Charging ₹" + amount + " to account: " + accountId);
        System.out.println("  [Payment Service] Payment transaction SUCCESSFUL.");
        return true;
    }
}
