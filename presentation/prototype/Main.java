public class Main {
    public static void main(String[] args) {
        Document original = new Document(
                "Отчет 2025",
                "Содержание отчета...",
                "Дёмкин"
        );

        Document copy = original.clone();
        copy.setTitle("Отчет 2025 (копия)");

        System.out.println("Оригинал: " + original);
        System.out.println("Копия: " + copy);
    }
}