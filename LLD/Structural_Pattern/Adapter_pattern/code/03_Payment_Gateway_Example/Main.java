public class Main {
    public static void main(String[] args) {
        PaymentProcessor razorpay = new RazorpayAdapter(new Razorpay());
        razorpay.pay(1500.00);

        PaymentProcessor paypal = new PaypalAdapter(new Paypal());
        paypal.pay(250.00);

        PaymentProcessor stripe = new StripeAdapter(new Stripe());
        stripe.pay(499.99);
    }
}
