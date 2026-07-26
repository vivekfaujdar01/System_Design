public class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Thin Crust Plain Pizza";
    }

    @Override
    public double getCost() {
        return 250.0;
    }
}
