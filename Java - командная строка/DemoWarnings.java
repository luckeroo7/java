class DemoWarnings {
    String unusedField = "Это поле никто не использует";
    int anotherUnusedField = 100;

    @Deprecated
    static void oldMethod() {
        System.out.println("Это устаревший метод");
    }

    void unusedMethod() {
        System.out.println("Этот метод никто не вызывает");
    }

    public static void main(String[] args) {
        int unusedVariable = 42;

        oldMethod();

        System.out.println("DemoWarnings скомпилирован успешно");

        java.util.List list = new java.util.ArrayList();
        list.add("raw type usage");
    }
}