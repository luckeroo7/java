public class Calculator {
    static {
        System.loadLibrary("JNIDemoNative");
    }

    public native int add(int a, int b);

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        int result = calc.add(5, 3);
        System.out.println("5 + 3 = " + result);
    }
}