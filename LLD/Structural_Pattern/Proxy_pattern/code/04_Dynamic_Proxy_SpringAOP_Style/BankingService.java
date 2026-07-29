public interface BankingService {
    void transferMoney(String fromAccount, String toAccount, double amount);
    double getBalance(String accountId);
}
