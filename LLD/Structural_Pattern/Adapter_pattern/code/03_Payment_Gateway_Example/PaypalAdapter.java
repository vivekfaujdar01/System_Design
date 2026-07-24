public class PaypalAdapter implements PaymentProcessor {
    private final Paypal paypal;

    public PaypalAdapter(Paypal paypal) {
        this.paypal = paypal;
    }

    @Override
    public void pay(double amount) {
        paypal.sendMoney(amount);
    }
}
