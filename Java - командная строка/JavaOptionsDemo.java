public class JavaOptionsDemo {
    public static void main(String[] args) {
        System.out.println("=== System Properties (-D) ===");
        System.out.println("user.name: " + System.getProperty("user.name"));
        System.out.println("my.custom.property: " + System.getProperty("my.custom.property", "not set"));

        System.out.println("\n=== Memory Info ===");
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        System.out.println("Max memory (JVM): " + maxMemory / (1024 * 1024) + " MB");
        System.out.println("Total memory: " + totalMemory / (1024 * 1024) + " MB");

        System.out.println("\n=== Arguments ===");
        if (args.length == 0) {
            System.out.println("No arguments passed");
        } else {
            for (int i = 0; i < args.length; i++) {
                System.out.println("arg[" + i + "] = " + args[i]);
            }
        }
    }
}