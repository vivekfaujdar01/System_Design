public class StripeAdapter implements PaymentProcessor {
    private final Stripe stripe;

    public StripeAdapter(Stripe stripe) {
        this.stripe = stripe;
    }

    @Override
    public void pay(double amount) {
        stripe.processTransaction(amount);
    }
}
