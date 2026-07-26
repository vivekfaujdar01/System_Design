public class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Whipped Cream";
    }

    @Override
    public double getCost() {
        return super.getCost() + 40.0;
    }
}
