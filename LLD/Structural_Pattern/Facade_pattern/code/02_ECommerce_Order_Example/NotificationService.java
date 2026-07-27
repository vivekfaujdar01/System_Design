public class NotificationService {
    public void sendOrderConfirmation(String email, String orderId, String trackingId) {
        System.out.println("  [Notification Service] Sending confirmation email to " + email);
        System.out.println("  [Notification Service] Email Content: Order #" + orderId + " confirmed. Track at: " + trackingId);
    }
}
