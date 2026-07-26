public class ProductItem implements OrderComponent {
    private final String name;
    private final double price;

    public ProductItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override public String getName() { return name; }
    @Override public double getPrice() { return price; }

    @Override
    public void printPackingList(String indent) {
        System.out.println(indent + "└─ Item: " + name + " | Price: ₹" + price);
    }
}
