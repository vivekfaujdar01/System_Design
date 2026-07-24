public class UrgentMessage extends NotificationMessage {
    public UrgentMessage(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void sendNotification(String body, String recipient) {
        System.out.println("\n--- Sending URGENT HIGH-PRIORITY Notification ---");
        String urgentBody = "🚨 [URGENT] " + body;
        messageSender.sendMessage(urgentBody, recipient);
        System.out.println("--> Urgent delivery confirmation logged to audit trail.");
    }
}
