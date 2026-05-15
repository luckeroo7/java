public class DemoOutputDir {
    public static void main(String[] args) {
        System.out.println("Класс был скомпилирован в другую директорию");

        String path = DemoOutputDir.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        System.out.println("Путь к .class файлу: " + path);
    }
}