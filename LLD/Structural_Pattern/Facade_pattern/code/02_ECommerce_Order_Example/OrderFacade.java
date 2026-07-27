public class OrderFacade {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notification;

    public OrderFacade() {
        this.inventory = new InventoryService();
        this.payment = new PaymentService();
        this.shipping = new ShippingService();
        this.notification = new NotificationService();
    }

    public boolean placeOrder(String productId, int quantity, double pricePerUnit, 
                             String userAccount, String email, String address) {
        System.out.println("=== PROCESSING E-COMMERCE ORDER FOR: " + productId + " ===");

        if (!inventory.checkStock(productId, quantity)) {
            System.out.println("❌ Order Failed: Product out of stock.");
            return false;
        }

        inventory.reserveStock(productId, quantity);

        double totalAmount = pricePerUnit * quantity;
        boolean paymentSuccess = payment.processPayment(userAccount, totalAmount);
        if (!paymentSuccess) {
            System.out.println("❌ Order Failed: Payment declined.");
            return false;
        }

        String trackingId = shipping.createShippingLabel(productId, address);

        String orderId = "ORD-48291";
        notification.sendOrderConfirmation(email, orderId, trackingId);

        System.out.println("=== SUCCESS: ORDER #" + orderId + " PLACED SUCCESSFULLY! ===\n");
        return true;
    }
}
