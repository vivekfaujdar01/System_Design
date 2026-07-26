public class Main {
    public static void main(String[] args) {
        System.out.println("=== PIZZERIA POS ORDER SYSTEM ===");

        Pizza orderA = new PlainPizza();
        System.out.println("\nOrder A: " + orderA.getDescription());
        System.out.println("Cost: ₹" + orderA.getCost());

        Pizza orderB = new PlainPizza();
        orderB = new ExtraCheeseDecorator(orderB);
        orderB = new ExtraCheeseDecorator(orderB);
        System.out.println("\nOrder B: " + orderB.getDescription());
        System.out.println("Cost: ₹" + orderB.getCost());

        Pizza orderC = new JalapenoDecorator(
                           new MushroomDecorator(
                               new ExtraCheeseDecorator(
                                   new PlainPizza()
                               )
                           )
                       );
        System.out.println("\nOrder C: " + orderC.getDescription());
        System.out.println("Cost: ₹" + orderC.getCost());
    }
}
