public class AtmFacade {
    private final PinVerifier pinVerifier;
    private final AccountService accountService;
    private final CashDispenser cashDispenser;
    private final TransactionLogger logger;

    public AtmFacade() {
        this.pinVerifier = new PinVerifier();
        this.accountService = new AccountService();
        this.cashDispenser = new CashDispenser();
        this.logger = new TransactionLogger();
    }

    public void withdrawCash(String cardNumber, int pin, double amount) {
        System.out.println("=== ATM CASH WITHDRAWAL REQUEST ===");

        if (!pinVerifier.verifyPin(cardNumber, pin)) {
            System.out.println("❌ ERROR: Invalid PIN entered.");
            logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "FAILED_INVALID_PIN");
            return;
        }

        if (!accountService.hasSufficientBalance(amount)) {
            System.out.println("❌ ERROR: Insufficient account funds.");
            logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "FAILED_INSUFFICIENT_FUNDS");
            return;
        }

        accountService.deductAmount(amount);
        cashDispenser.dispenseCash(amount);
        logger.logTransaction(cardNumber, "WITHDRAWAL", amount, "SUCCESS");

        System.out.println("=== WITHDRAWAL COMPLETE. THANK YOU FOR BANKING WITH US! ===\n");
    }
}
