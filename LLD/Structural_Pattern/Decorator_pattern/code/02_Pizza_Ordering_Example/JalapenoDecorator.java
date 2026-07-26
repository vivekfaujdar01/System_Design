public class JalapenoDecorator extends PizzaDecorator {
    public JalapenoDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Spicy Jalapeños";
    }

    @Override
    public double getCost() {
        return super.getCost() + 35.0;
    }
}
