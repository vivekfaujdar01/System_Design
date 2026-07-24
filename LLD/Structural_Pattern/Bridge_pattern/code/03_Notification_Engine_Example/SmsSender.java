public class SmsSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[SMS Channel 📱] Sending to " + destination + ": " + message);
    }
}
