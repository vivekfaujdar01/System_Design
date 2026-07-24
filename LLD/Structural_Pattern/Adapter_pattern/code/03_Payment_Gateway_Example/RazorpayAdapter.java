public class RazorpayAdapter implements PaymentProcessor {
    private final Razorpay razorpay;

    public RazorpayAdapter(Razorpay razorpay) {
        this.razorpay = razorpay;
    }

    @Override
    public void pay(double amount) {
        razorpay.makePayment(amount);
    }
}
