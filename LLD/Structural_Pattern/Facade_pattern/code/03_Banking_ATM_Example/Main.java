public class Main {
    public static void main(String[] args) {
        AtmFacade atm = new AtmFacade();

        atm.withdrawCash("CARD-9876-XXXX", 4321, 5000.0);
        atm.withdrawCash("CARD-9876-XXXX", 1111, 2000.0);
    }
}
