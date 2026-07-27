public class InventoryService {
    public boolean checkStock(String productId, int quantity) {
        System.out.println("  [Inventory Service] Checking stock for Product ID: " + productId);
        return true;
    }

    public void reserveStock(String productId, int quantity) {
        System.out.println("  [Inventory Service] Reserved " + quantity + " unit(s) of " + productId);
    }
}
