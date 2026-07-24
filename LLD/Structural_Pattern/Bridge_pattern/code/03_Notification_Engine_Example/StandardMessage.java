public class StandardMessage extends NotificationMessage {
    public StandardMessage(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void sendNotification(String body, String recipient) {
        System.out.println("\n--- Sending Standard Notification ---");
        messageSender.sendMessage(body, recipient);
    }
}
