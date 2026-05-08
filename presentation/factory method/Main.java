public class Main {
    public static void main(String[] args) {
        NotificationManager manager = new NotificationManager();

        manager.sendNotification("email", "Ваш заказ отправлен на email");
        manager.sendNotification("sms", "Ваш код подтверждения отправлен по SMS");
    }
}