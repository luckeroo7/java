import java.util.*;

// 2. ПАРАМЕТРИЗОВАННЫЙ КЛАСС

class Wrapper<T> // T — фиктивный тип, заменяется реальным при создании объекта, мб любым ссылочным типом: Integer, String, MyClass и тд
{
    private T item;

    public Wrapper(T item) 
    {
        this.item = item;
    }

    public void setItem(T item) { this.item = item; }
    public T getItem() { return item; }

    @Override
    public String toString() 
    {
        return "Wrapper[" + item + "]";
    }
}

// 3. ПАРАМЕТРИЗОВАННЫЙ КЛАСС С НЕСКОЛЬКИМИ ПАРАМЕТРАМИ

class Pair<K, V> //пара: ключ и значение
{
    private final K key;
    private final V value;

    public Pair(K key, V value) 
    {
        this.key   = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }

    @Override
    public String toString() 
    {
        return "(" + key + " -> " + value + ")";
    }
}

// 4. ПАРАМЕТРИЗОВАННЫЙ ИНТЕРФЕЙС

interface Printable<T> 
{
    void print(T item);
    String format(T item);
}

// Реализация интерфейса: подставляем String вместо T.
class ConsolePrinter implements Printable<String> 
{
    @Override
    public void print(String item) 
    {
        System.out.println("  >> " + format(item));
    }

    @Override
    public String format(String item) 
    {
        return "[" + item.toUpperCase() + "]";
    }
}

// 6. ОГРАНИЧЕНИЯ ТИПОВ (extends)

class NumBox<T extends Number> // можно положить только Number и его подтипы (Integer, Double и тд)
{
    private final T value; // Внутри класса доступны все методы Number

    public NumBox(T value) { this.value = value; }

    public T getValue() { return value; }
    public double toDouble() { return value.doubleValue(); }
    public boolean isPositive() { return value.doubleValue() > 0; }

    @Override
    public String toString() 
    {
        return "NumBox[" + value + "]";
    }
}


// 5. и 7. ПАРАМЕТРИЗОВАННЫЕ МЕТОДЫ + WILDCARD

class Utils 
{
    // 5. Параметризованный метод 
    // <T> перед возвращаемым типом — собственный параметр метода, не зависит от класса

    public static <T> void printArray(T[] array) 
    {
        System.out.print("  [ ");
        for (T el : array) System.out.print(el + " ");
        System.out.println("]");
    }

    // Параметризованный метод, возвращающий значение
    public static <T> T getFirst(List<T> list) 
    {
        if (list.isEmpty()) throw new NoSuchElementException();
        return list.get(0);
    }

    // свап двух элементов списка
    public static <T> void swap(List<T> list, int i, int j) 
    {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    // 7.1 Wildcard: <? extends T>
    // принимает List<Integer>, List<Double>, List<Float> и тд
    // читать можно, писать - нет, компилятор не знает точный тип
    public static double sum(List<? extends Number> list) 
    {
        double total = 0;
        for (Number n : list) total += n.doubleValue();
        return total;
    }

    // 7.2 Wildcard: <? super Integer> 
    // принимает List<Integer>, List<Number>, List<Object>.
    // добавлять Integer безопасно в любой из них
    public static void fillWithZeros(List<? super Integer> list, int count) 
    {
        for (int i = 0; i < count; i++) list.add(0);
    }

    // 7.3 Wildcard: <?> (Unbounded)
    // тип не важенб, просто считаем размер
    public static int countElements(List<?> list) 
    {
        return list.size();
    }
}


//main

public class GenericsDemo 
{
    static void section(String title) 
    {
        System.out.println();
        System.out.println(" ––––––––––––––––––––––––––––––––––––––––––––– ");
        System.out.printf( "|  %-43s|%n", title);
        System.out.println(" ––––––––––––––––––––––––––––––––––––––––––––– ");
    }

    static void step(String text) 
    {
        System.out.println(" – " + text);
    }

    public static void main(String[] args) 
    {
        // 1. ПРОБЛЕМА БЕЗ ДЖЕНЕРИКОВ
        section("1. ПРОБЛЕМА БЕЗ ДЖЕНЕРИКОВ");

        // Сырой список - компилятор принимает что угодно.
        List rawList = new ArrayList();
        rawList.add("hello");
        rawList.add(42);   // добавили Integer - все норм
        rawList.add(3.14); // и Double тоже прошёл

        step("Добавили в сырой список: String, Integer, Double — все нормально");
        step("Пытаемся прочитать как String...");

        try {
            String s = (String) rawList.get(1); // Integer в String - ошибка
        } catch (ClassCastException e) {
            System.out.println("    ошибка ClassCastException: " + e.getMessage());
            System.out.println("    обнаружена в рантайме — слишком поздно");
        }

        // с дженериками — ошибка была бы на этапе компиляции
        List<String> safeList = new ArrayList<>();
        safeList.add("hello");
        // safeList.add(42);  //  это не скомпилируется!!!
        String s = safeList.get(0); 
        step("с дженериком: List<String> — ошибки не будет");
        System.out.println("    Получили: \"" + s + "\"");

        // 2. ПАРАМЕТРИЗОВАННЫЙ КЛАСС Wrapper<T>

        section("2. ПАРАМЕТРИЗОВАННЫЙ КЛАСС Wrapper<T>");

        // При создании T заменяется на реальный тип
        Wrapper<Integer> intBox = new Wrapper<>(100); //<>
        Wrapper<String> strBox = new Wrapper<String>("Привет"); //<String>

        step("Wrapper<Integer>: " + intBox);
        step("Wrapper<String>:  " + strBox);

        intBox.setItem(200);
        step("После setItem(200): " + intBox);

        // один класс — разные типы, без дублирования кода
        Wrapper<List<String>> listBox = new Wrapper<>(List.of("a", "b", "c"));
        step("Wrapper<List<String>>: " + listBox);

        // 3. НЕСКОЛЬКО ПАРАМЕТРОВ
        section("3. НЕСКОЛЬКО ПАРАМЕТРОВ: Pair<K, V>");

        Pair<String, Integer> grade = new Pair<>("Алексей", 5);
        Pair<Integer, Double> coord = new Pair<>(42, 3.14);
        Pair<String, Boolean> flag  = new Pair<>("enabled", true);

        step("Оценка:      " + grade);
        step("Координата:  " + coord);
        step("Флаг:        " + flag);

        // вложенная пара
        Pair<Pair<Integer, Integer>, String> nested = new Pair<>(new Pair<>(1, 2), "точка");
        step("Вложенная:   " + nested);

        // 4. ПАРАМЕТРИЗОВАННЫЙ ИНТЕРФЕЙС
        section("4. ПАРАМЕТРИЗОВАННЫЙ ИНТЕРФЕЙС Printable<T>");

        Printable<String> printer = new ConsolePrinter();
        step("Вызов printer.print(\"java generics\"):");
        printer.print("java generics");
        step("Вызов printer.format(\"hello\") = " + printer.format("hello"));

        // скрытая реализация с другим типом — Integer:
        Printable<Integer> intPrinter = new Printable<Integer>() 
        {
            @Override public void print(Integer item) 
            {
                System.out.println("  >> число: " + item + " (hex: " + Integer.toHexString(item) + ")");
            }
            @Override public String format(Integer item) { return "0x" + Integer.toHexString(item); }
        };

        step("Printable<Integer>:");
        intPrinter.print(255);
        intPrinter.print(16);

        // 5. ПАРАМЕТРИЗОВАННЫЕ МЕТОДЫ
        section("5. ПАРАМЕТРИЗОВАННЫЕ МЕТОДЫ");

        Integer[] intArr = {3, 1, 4, 1, 5, 9};
        String[]  strArr = {"apple", "banana", "mango"};
        Double[]  dblArr = {1.1, 2.2, 3.3};

        step("printArray(Integer[]): ");
        Utils.printArray(intArr);
        step("printArray(String[]): ");
        Utils.printArray(strArr);
        step("printArray(Double[]): ");
        Utils.printArray(dblArr);

        // один метод работает со всеми тремя типами без перегрузок

        List<String> names = new ArrayList<>(List.of("Иван", "Мария", "Пётр"));
        step("getFirst([Иван, Мария, Пётр]) = " + Utils.getFirst(names));

        step("До swap(0,2): " + names);
        Utils.swap(names, 0, 2);
        step("После swap(0,2): " + names);

        // 6. ОГРАНИЧЕНИЯ ТИПОВ (extends)
        section("6. ОГРАНИЧЕНИЯ ТИПОВ: <T extends Number>");

        NumBox<Integer> nb1 = new NumBox<>(42);
        NumBox<Double>  nb2 = new NumBox<>(-3.14);
        NumBox<Float>   nb3 = new NumBox<>(2.718f);
        // NumBox<String> nb4 = new NumBox<>("x"); // это не скомпилируется!!!

        step(nb1 + "  toDouble=" + nb1.toDouble() + "  positive=" + nb1.isPositive());
        step(nb2 + "  toDouble=" + nb2.toDouble() + "  positive=" + nb2.isPositive());
        step(nb3 + "  toDouble=" + nb3.toDouble() + "  positive=" + nb3.isPositive());

        // Благодаря extends Number внутри NumBox доступны doubleValue(), intValue() и другие методы Number 

        // 7. WILDCARD: <?>, <? extends T>, <? super T>
        section("7. WILDCARD");

        // 7.1 <? extends Number> (читаем, не пишем)
        System.out.println("  [? extends Number] — принимаем любые числовые списки:");

        List<Integer> ints    = List.of(1, 2, 3, 4, 5);
        List<Double>  doubles = List.of(1.5, 2.5, 3.0);
        List<Float>   floats  = List.of(0.5f, 1.5f);

        // один метод sum() работает со всеми тремя:
        step("sum(List<Integer> " + ints    + ") = " + Utils.sum(ints));
        step("sum(List<Double>  " + doubles + ") = " + Utils.sum(doubles));
        step("sum(List<Float>   " + floats  + ") = " + Utils.sum(floats));

        // 7.2 <? super Integer> (пишем) 
        System.out.println();
        System.out.println("  [? super Integer] — добавляем в список с широким типом:");

        List<Integer> intDest = new ArrayList<>();
        List<Number>  numDest = new ArrayList<>();
        List<Object>  objDest = new ArrayList<>();

        // все три принимает fillWithZeros, потому что каждый из них суперкласс Integer или сам Integer:
        Utils.fillWithZeros(intDest, 3);
        Utils.fillWithZeros(numDest, 2);
        Utils.fillWithZeros(objDest, 1);

        step("List<Integer> после fillWithZeros(3): " + intDest);
        step("List<Number>  после fillWithZeros(2): " + numDest);
        step("List<Object>  после fillWithZeros(1): " + objDest);

        // 7.3 <?> (только читаем как Object)
        System.out.println();
        System.out.println("  [<?>] — любой тип, только чтение как Object:");

        step("countElements(" + ints    + ") = " + Utils.countElements(ints));
        step("countElements(" + doubles + ") = " + Utils.countElements(doubles));
        step("countElements(" + names   + ") = " + Utils.countElements(names));

        // ИТОГ
        section("ИТОГ");
        System.out.println("  один класс Wrapper<T>   — работает с Integer, String, List...");
        System.out.println("  один класс Pair<K,V>    — любые комбинации типов");
        System.out.println("  один метод printArray() — Integer[], String[], Double[]");
        System.out.println("  один метод sum()        — List<Integer>, List<Double>, List<Float>");
        System.out.println();
        System.out.println("  все ошибки типов — на этапе компиляции, а не в рантайме");
        System.out.println();
    }
}
