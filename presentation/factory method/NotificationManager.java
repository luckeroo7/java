public class NotificationManager {

    public void sendNotification(String type, String message) {
        NotificationFactory factory = createFactory(type);
        Notification notification = factory.createNotification();
        notification.send(message);
    }

    private NotificationFactory createFactory(String type) {
        if (type.equalsIgnoreCase("email")) {
            return new EmailNotificationFactory();
        } else if (type.equalsIgnoreCase("sms")) {
            return new SMSNotificationFactory();
        } else {
            throw new IllegalArgumentException("Неизвестный тип уведомления: " + type);
        }
    }
}