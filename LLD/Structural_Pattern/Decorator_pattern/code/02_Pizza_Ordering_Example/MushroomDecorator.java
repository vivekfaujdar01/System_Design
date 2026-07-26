public class MushroomDecorator extends PizzaDecorator {
    public MushroomDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Fresh Button Mushrooms";
    }

    @Override
    public double getCost() {
        return super.getCost() + 40.0;
    }
}
