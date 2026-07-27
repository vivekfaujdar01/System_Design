public class AccountService {
    private double balance = 25000.0;

    public boolean hasSufficientBalance(double amount) {
        System.out.println("  [Account Service] Checking balance. Current balance: ₹" + balance);
        return balance >= amount;
    }

    public void deductAmount(double amount) {
        balance -= amount;
        System.out.println("  [Account Service] Deducted ₹" + amount + ". Remaining balance: ₹" + balance);
    }
}
