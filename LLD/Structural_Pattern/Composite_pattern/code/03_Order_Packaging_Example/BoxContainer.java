import java.util.ArrayList;
import java.util.List;

public class BoxContainer implements OrderComponent {
    private final String boxName;
    private final double packagingCost;
    private final List<OrderComponent> contents = new ArrayList<>();

    public BoxContainer(String boxName, double packagingCost) {
        this.boxName = boxName;
        this.packagingCost = packagingCost;
    }

    public void add(OrderComponent item) {
        contents.add(item);
    }

    public void remove(OrderComponent item) {
        contents.remove(item);
    }

    @Override public String getName() { return boxName; }

    @Override
    public double getPrice() {
        double total = packagingCost;
        for (OrderComponent item : contents) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public void printPackingList(String indent) {
        System.out.println(indent + "📦 Box: " + boxName + " (Packaging Fee: ₹" + packagingCost + ")");
        for (OrderComponent item : contents) {
            item.printPackingList(indent + "    ");
        }
    }
}
