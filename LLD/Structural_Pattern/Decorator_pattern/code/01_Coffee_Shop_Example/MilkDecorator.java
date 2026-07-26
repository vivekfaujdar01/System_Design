public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Steamed Milk";
    }

    @Override
    public double getCost() {
        return super.getCost() + 30.0;
    }
}
