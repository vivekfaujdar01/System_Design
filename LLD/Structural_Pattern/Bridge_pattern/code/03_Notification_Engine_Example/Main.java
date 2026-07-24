public class Main {
    public static void main(String[] args) {
        NotificationMessage smsNotification = new StandardMessage(new SmsSender());
        smsNotification.sendNotification("Your OTP is 482910.", "+91-9876543210");

        NotificationMessage emailNotification = new StandardMessage(new EmailSender());
        emailNotification.sendNotification("Welcome to System Design Portal!", "user@example.com");

        NotificationMessage urgentPush = new UrgentMessage(new PushSender());
        urgentPush.sendNotification("Security alert: Login from unknown device!", "Device_ID_9921");

        NotificationMessage urgentSms = new UrgentMessage(new SmsSender());
        urgentSms.sendNotification("Server CPU usage exceeded 95% threshold!", "+91-9999988888");
    }
}
