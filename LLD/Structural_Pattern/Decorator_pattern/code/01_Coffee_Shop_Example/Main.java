public class Main {
    public static void main(String[] args) {
        Coffee coffee1 = new SimpleCoffee();
        System.out.println("Order 1: " + coffee1.getDescription());
        System.out.println("Total Cost: ₹" + coffee1.getCost());

        System.out.println("\n--- Preparing Order 2 ---");
        Coffee coffee2 = new SimpleCoffee();
        coffee2 = new MilkDecorator(coffee2);
        coffee2 = new SugarDecorator(coffee2);
        System.out.println("Order 2: " + coffee2.getDescription());
        System.out.println("Total Cost: ₹" + coffee2.getCost());

        System.out.println("\n--- Preparing Order 3 ---");
        Coffee coffee3 = new WhipDecorator(
                            new SugarDecorator(
                                new MilkDecorator(
                                    new SimpleCoffee()
                                )
                            )
                        );
        System.out.println("Order 3: " + coffee3.getDescription());
        System.out.println("Total Cost: ₹" + coffee3.getCost());
    }
}
