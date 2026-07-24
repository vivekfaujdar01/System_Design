public abstract class NotificationMessage {
    protected final MessageSender messageSender;

    public NotificationMessage(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public abstract void sendNotification(String body, String recipient);
}
