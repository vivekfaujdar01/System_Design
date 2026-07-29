public class BankingServiceImpl implements BankingService {
    @Override
    public void transferMoney(String fromAccount, String toAccount, double amount) {
        System.out.println("  [BankingServiceImpl] Executing SQL: Debit $" + amount + " from " + fromAccount + " and Credit to " + toAccount);
        if (amount > 10000) {
            throw new IllegalArgumentException("Transfer limit exceeded! Max transfer amount is $10,000");
        }
    }

    @Override
    public double getBalance(String accountId) {
        System.out.println("  [BankingServiceImpl] Executing SQL: SELECT balance FROM accounts WHERE id='" + accountId + "'");
        return 45500.75;
    }
}
