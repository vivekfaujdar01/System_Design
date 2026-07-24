public class PushSender implements MessageSender {
    @Override
    public void sendMessage(String message, String destination) {
        System.out.println("[PUSH Channel 🔔] Sending to " + destination + ": " + message);
    }
}
