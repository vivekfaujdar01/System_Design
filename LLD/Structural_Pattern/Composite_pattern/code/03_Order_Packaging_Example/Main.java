public class Main {
    public static void main(String[] args) {
        OrderComponent phone = new ProductItem("iPhone 15 Pro", 134900.0);
        OrderComponent screenGuard = new ProductItem("Tempered Glass Screen Guard", 999.0);
        OrderComponent charger = new ProductItem("20W USB-C Power Adapter", 1900.0);
        OrderComponent magSafeWallet = new ProductItem("Leather MagSafe Wallet", 5900.0);

        BoxContainer giftBox = new BoxContainer("Premium Accessory Gift Box", 150.0);
        giftBox.add(screenGuard);
        giftBox.add(magSafeWallet);

        BoxContainer mainShippingCrate = new BoxContainer("Outer Shipping Container", 300.0);
        mainShippingCrate.add(phone);
        mainShippingCrate.add(charger);
        mainShippingCrate.add(giftBox);

        System.out.println("=== SHIPMENT PACKING MANIFEST ===");
        mainShippingCrate.printPackingList("");

        System.out.println("\n=== COST BREAKDOWN ===");
        System.out.println("Gift Box Subtotal (with packaging): ₹" + giftBox.getPrice());
        System.out.println("Total Shipment Order Price: ₹" + mainShippingCrate.getPrice());
    }
}
