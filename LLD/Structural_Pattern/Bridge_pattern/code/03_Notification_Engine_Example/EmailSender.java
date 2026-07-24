public class EmailSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[EMAIL Channel 📧] Sending to " + destination + ": " + message);
    }
}
