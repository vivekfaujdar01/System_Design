public class ExtraCheeseDecorator extends PizzaDecorator {
    public ExtraCheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Extra Mozzarella Cheese";
    }

    @Override
    public double getCost() {
        return super.getCost() + 50.0;
    }
}
