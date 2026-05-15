public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Command Line!");
        if (args.length > 0) {
            System.out.println("Arguments received:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("  args[" + i + "] = " + args[i]);
            }
        }
    }
}