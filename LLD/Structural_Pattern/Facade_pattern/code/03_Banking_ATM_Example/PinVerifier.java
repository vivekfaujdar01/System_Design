public class PinVerifier {
    public boolean verifyPin(String cardNumber, int pin) {
        System.out.println("  [PIN Verifier] Verifying PIN for Card: " + cardNumber);
        return pin == 4321;
    }
}
