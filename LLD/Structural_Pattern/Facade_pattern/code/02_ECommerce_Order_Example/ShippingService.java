public class ShippingService {
    public String createShippingLabel(String productId, String destinationAddress) {
        String trackingId = "TRK-54321";
        System.out.println("  [Shipping Service] Generated courier tracking ID: " + trackingId + " to " + destinationAddress);
        return trackingId;
    }
}
