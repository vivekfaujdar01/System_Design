public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Organic Sugar";
    }

    @Override
    public double getCost() {
        return super.getCost() + 10.0;
    }
}
