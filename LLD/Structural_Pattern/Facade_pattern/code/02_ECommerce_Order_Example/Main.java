public class Main {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();

        orderFacade.placeOrder(
            "LAPTOP-MACBOOK-M3",
            1,
            149900.0,
            "CARD_4111_2222_3333",
            "customer@example.com",
            "123 Tech Park, Whitefield, Bangalore"
        );
    }
}
