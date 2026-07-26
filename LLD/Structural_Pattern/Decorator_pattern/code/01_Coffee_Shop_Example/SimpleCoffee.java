public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Plain Black Coffee";
    }

    @Override
    public double getCost() {
        return 100.0;
    }
}
