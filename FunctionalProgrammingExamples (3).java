import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Примеры функционального программирования в Java
 * Все примеры из презентации в одном файле.
 *
 * Запуск: javac FunctionalProgrammingExamples.java && java FunctionalProgrammingExamples
 * Требуется: Java 11+
 */
public class FunctionalProgrammingExamples {

    // ─────────────────────────────────────────────────────────────────────────
    // Вспомогательный класс для примеров со Stream API
    // ─────────────────────────────────────────────────────────────────────────
    static class Employee {
        private final String name;
        private final int age;

        Employee(String name, int age) {
            this.name = name;
            this.age  = age;
        }

        public String getName() { return name; }
        public int    getAge()  { return age;  }

        @Override
        public String toString() {
            return name + " (" + age + ")";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 3: Функциональные интерфейсы
    // ─────────────────────────────────────────────────────────────────────────
    @FunctionalInterface
    interface Greeter {
        String greet(String name);
    }

    static void slide3_FunctionalInterfaces() {
        printHeader("СЛАЙД 3: Функциональные интерфейсы");

        // Реализация функционального интерфейса лямбдой
        Greeter g = name -> "Привет, " + name + "!";

        System.out.println(g.greet("Мир"));
        System.out.println(g.greet("Java"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 4: Основные встроенные интерфейсы
    // ─────────────────────────────────────────────────────────────────────────
    static void slide4_BuiltInInterfaces() {
        printHeader("СЛАЙД 4: Встроенные функциональные интерфейсы");

        // Function<T, R> — преобразование T → R
        Function<String, Integer> strLen = s -> s.length();
        System.out.println("Function  | длина \"hello\" = " + strLen.apply("hello"));

        // Predicate<T> — проверка условия
        Predicate<Integer> isPositive = n -> n > 0;
        System.out.println("Predicate | 5 > 0 ? " + isPositive.test(5));
        System.out.println("Predicate | -3 > 0 ? " + isPositive.test(-3));

        // Consumer<T> — действие без результата
        Consumer<String> printer = System.out::println;
        System.out.print("Consumer  | ");
        printer.accept("сообщение принято");

        // Supplier<T> — поставщик значения
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("элемент");
        System.out.println("Supplier  | новый список: " + newList);

        // BiFunction<T, U, R> — два входа, один выход
        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;
        System.out.println("BiFunction | 10 + 32 = " + sum.apply(10, 32));

        // UnaryOperator<T> — T → T
        UnaryOperator<String> toUpper = String::toUpperCase;
        System.out.println("UnaryOp   | \"java\" → " + toUpper.apply("java"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 5: Ссылки на методы
    // ─────────────────────────────────────────────────────────────────────────
    static void slide5_MethodReferences() {
        printHeader("СЛАЙД 5: Ссылки на методы (Method References)");

        List<String> languages = List.of("java", "python", "kotlin", "scala");

        // Ссылка на статический метод: ClassName::staticMethod
        System.out.println("Статический метод (Math::abs):");
        List<Integer> numbers = List.of(-3, 7, -1, 4, -9);
        numbers.stream()
               .map(Math::abs)           // эквивалент: n -> Math.abs(n)
               .forEach(System.out::println);

        // Ссылка на метод экземпляра через класс: ClassName::instanceMethod
        System.out.println("\nМетод экземпляра через класс (String::toUpperCase):");
        languages.stream()
                 .map(String::toUpperCase)  // эквивалент: s -> s.toUpperCase()
                 .forEach(System.out::println);

        // Ссылка на метод экземпляра конкретного объекта: instance::method
        String prefix = "★ ";
        System.out.println("\nМетод конкретного объекта (prefix::concat):");
        languages.stream()
                 .map(prefix::concat)       // эквивалент: s -> prefix.concat(s)
                 .forEach(System.out::println);

        // Ссылка на конструктор: ClassName::new
        System.out.println("\nКонструктор (ArrayList::new):");
        Supplier<List<String>> factory = ArrayList::new;
        List<String> list = factory.get();
        list.add("создано через ссылку на конструктор");
        System.out.println(list);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 7: Цепочки операций Stream API
    // ─────────────────────────────────────────────────────────────────────────
    static void slide7_StreamPipeline() {
        printHeader("СЛАЙД 7: Цепочки операций Stream API");

        List<Employee> employees = List.of(
            new Employee("Анна",   28),
            new Employee("Борис",  35),
            new Employee("Виктор", 42),
            new Employee("Галина", 31)
        );

        System.out.println("Все сотрудники: " + employees);
        System.out.println();

        // Найти имена тех, кто старше 30, отсортировать, вывести через запятую
        String result = employees
            .stream()                              // 1. Создать стрим
            .filter(e -> e.getAge() > 30)          // 2. Промежуточная: фильтр
            .map(Employee::getName)                // 3. Промежуточная: map
            .sorted()                              // 4. Промежуточная: сортировка
            .collect(Collectors.joining(", "));    // 5. Терминальная

        System.out.println("Старше 30 лет (по алфавиту): " + result);

        // Дополнительные примеры операций
        System.out.println("\nДополнительные примеры:");

        long count = employees.stream()
            .filter(e -> e.getAge() > 30)
            .count();
        System.out.println("Количество сотрудников старше 30: " + count);

        Optional<Employee> oldest = employees.stream()
            .max(Comparator.comparingInt(Employee::getAge));
        oldest.ifPresent(e -> System.out.println("Самый старший: " + e));

        double avgAge = employees.stream()
            .mapToInt(Employee::getAge)
            .average()
            .orElse(0);
        System.out.printf("Средний возраст: %.1f%n", avgAge);

        Map<Boolean, List<Employee>> partitioned = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.getAge() > 30));
        System.out.println("До 30 включительно: " + partitioned.get(false));
        System.out.println("Старше 30:          " + partitioned.get(true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 8: Параллельные стримы
    // ─────────────────────────────────────────────────────────────────────────
    static void slide8_ParallelStreams() {
        printHeader("СЛАЙД 8: Параллельные стримы");

        // Обычный стрим
        long count = IntStream.rangeClosed(1, 1_000_000)
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Обычный стрим    | чётных от 1 до 1 000 000: " + count);

        // Параллельный стрим — добавить .parallel()
        long countP = IntStream.rangeClosed(1, 1_000_000)
            .parallel()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Параллельный     | чётных от 1 до 1 000 000: " + countP);

        // Замер времени
        long start = System.currentTimeMillis();
        IntStream.rangeClosed(1, 10_000_000).filter(n -> n % 3 == 0).count();
        long seqTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        IntStream.rangeClosed(1, 10_000_000).parallel().filter(n -> n % 3 == 0).count();
        long parTime = System.currentTimeMillis() - start;

        System.out.println("\nОбработка 10 млн элементов:");
        System.out.println("Последовательно: " + seqTime + " мс");
        System.out.println("Параллельно:     " + parTime + " мс");
        System.out.println("Ускорение:       " + String.format("%.1fx", (double) seqTime / Math.max(parTime, 1)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 9: Optional — борьба с null
    // ─────────────────────────────────────────────────────────────────────────
    static void slide9_Optional() {
        printHeader("СЛАЙД 9: Optional");

        // --- Без Optional (опасный стиль) ---
        System.out.println("--- Без Optional ---");
        String nameWithNull = null;
        if (nameWithNull != null) {
            System.out.println(nameWithNull.toUpperCase());
        } else {
            System.out.println("Неизвестно");
        }

        // --- С Optional ---
        System.out.println("\n--- С Optional ---");
        Optional.ofNullable(nameWithNull)
            .map(String::toUpperCase)
            .ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Неизвестно")
            );

        // --- Основные методы Optional ---
        System.out.println("\n--- Методы Optional ---");

        Optional<String> present = Optional.of("Java");
        Optional<String> empty   = Optional.empty();

        System.out.println("isPresent:   " + present.isPresent() + " / " + empty.isPresent());
        System.out.println("isEmpty:     " + present.isEmpty()   + " / " + empty.isEmpty());
        System.out.println("orElse:      " + empty.orElse("значение по умолчанию"));
        System.out.println("orElseGet:   " + empty.orElseGet(() -> "вычисленное значение"));
        System.out.println("map:         " + present.map(String::toLowerCase));
        System.out.println("filter pass: " + present.filter(s -> s.length() > 3));
        System.out.println("filter fail: " + present.filter(s -> s.length() > 10));

        // Цепочка с Optional — поиск первого длинного имени
        System.out.println("\n--- Пример: поиск в коллекции ---");
        List<String> names = List.of("Анна", "Борис", "Александр", "Ян");
        Optional<String> longName = names.stream()
            .filter(s -> s.length() > 5)
            .findFirst();

        String result = longName
            .map(String::toUpperCase)
            .orElse("длинных имён нет");

        System.out.println("Первое имя длиннее 5 букв: " + result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // СЛАЙД 10: Неизменяемость и чистые функции
    // ─────────────────────────────────────────────────────────────────────────
    static void slide10_ImmutabilityAndPureFunctions() {
        printHeader("СЛАЙД 10: Неизменяемость и чистые функции");

        // --- Изменяемый vs неизменяемый список ---
        System.out.println("--- Изменяемый список ---");
        List<String> mutable = new ArrayList<>(List.of("a", "b"));
        mutable.add("c");
        System.out.println("После add: " + mutable);

        System.out.println("\n--- Неизменяемый список (Java 9+) ---");
        List<String> immutable = List.of("a", "b");
        System.out.println("Список: " + immutable);
        try {
            immutable.add("c");
        } catch (UnsupportedOperationException e) {
            System.out.println("Ошибка: UnsupportedOperationException — изменить нельзя");
        }

        // --- Нечистая функция ---
        System.out.println("\n--- Нечистая функция (меняет внешнее состояние) ---");
        int[] total = {0};                          // обходной приём для демонстрации
        Consumer<Integer> addToTotal = n -> total[0] += n;
        addToTotal.accept(10);
        addToTotal.accept(20);
        System.out.println("total после двух вызовов: " + total[0]);
        System.out.println("Проблема: результат зависит от внешнего состояния");

        // --- Чистая функция ---
        System.out.println("\n--- Чистая функция ---");
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("add(10, 20) = " + add.apply(10, 20));
        System.out.println("add(10, 20) = " + add.apply(10, 20));  // всегда один результат
        System.out.println("Всегда одинаковый результат для одних и тех же аргументов");

        // --- Composing функций ---
        System.out.println("\n--- Композиция функций ---");
        Function<String, String> trim       = String::trim;
        Function<String, String> toUpper    = String::toUpperCase;
        Function<String, String> addBracket = s -> "[" + s + "]";

        Function<String, String> pipeline = trim.andThen(toUpper).andThen(addBracket);
        System.out.println(pipeline.apply("   hello world   "));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ПАТТЕРН СТРАТЕГИЯ (Strategy Pattern)
    // ═════════════════════════════════════════════════════════════════════════

    // ── Классический подход: интерфейс + отдельные классы ────────────────────

    interface DiscountStrategy {
        double apply(double price);
    }

    // Три отдельных класса — много кода, одинаковая структура
    static class NoDiscount implements DiscountStrategy {
        @Override
        public double apply(double price) { return price; }
    }

    static class PercentDiscount implements DiscountStrategy {
        private final double percent;
        PercentDiscount(double percent) { this.percent = percent; }
        @Override
        public double apply(double price) { return price * (1 - percent / 100); }
    }

    static class FixedDiscount implements DiscountStrategy {
        private final double amount;
        FixedDiscount(double amount) { this.amount = amount; }
        @Override
        public double apply(double price) { return Math.max(0, price - amount); }
    }

    // ── Сервис, использующий стратегию ───────────────────────────────────────

    static class OrderService {
        private final DiscountStrategy strategy;

        OrderService(DiscountStrategy strategy) {
            this.strategy = strategy;
        }

        double calculateTotal(double price) {
            return strategy.apply(price);
        }
    }

    // ── Расширенный сервис: стратегия через Function<Double, Double> ──────────
    // Функциональный интерфейс Function полностью совместим с DiscountStrategy,
    // потому что оба имеют ровно один метод (Double) -> Double.
    // Здесь мы не создаём интерфейс вообще — используем стандартный Function.

    static class FunctionalOrderService {
        private final Function<Double, Double> strategy;

        FunctionalOrderService(Function<Double, Double> strategy) {
            this.strategy = strategy;
        }

        double calculateTotal(double price) {
            return strategy.apply(price);
        }

        // Метод для создания составной стратегии из нескольких скидок подряд
        static Function<Double, Double> combine(
                Function<Double, Double> first,
                Function<Double, Double> second) {
            return first.andThen(second);
        }
    }

    static void pattern_Strategy() {
        printHeader("ПАТТЕРН СТРАТЕГИЯ — классический (ООП-стиль)");

        double price = 1000.0;

        // Три разные стратегии — три разных класса
        OrderService noDiscount      = new OrderService(new NoDiscount());
        OrderService percentDiscount = new OrderService(new PercentDiscount(15));
        OrderService fixedDiscount   = new OrderService(new FixedDiscount(200));

        System.out.printf("Без скидки:        %.2f руб.%n", noDiscount.calculateTotal(price));
        System.out.printf("Скидка 15%%:        %.2f руб.%n", percentDiscount.calculateTotal(price));
        System.out.printf("Скидка 200 руб.:   %.2f руб.%n", fixedDiscount.calculateTotal(price));

        // ── Функциональный стиль: стратегии — это лямбды ─────────────────────
        printHeader("ПАТТЕРН СТРАТЕГИЯ — функциональный (лямбды)");

        // Каждая стратегия — просто лямбда типа Function<Double, Double>.
        // Никаких классов, никакого implements.
        Function<Double, Double> noDisc      = p -> p;
        Function<Double, Double> percent15   = p -> p * 0.85;
        Function<Double, Double> fixed200    = p -> Math.max(0, p - 200);
        Function<Double, Double> vipDiscount = p -> p * 0.70;

        FunctionalOrderService svc1 = new FunctionalOrderService(noDisc);
        FunctionalOrderService svc2 = new FunctionalOrderService(percent15);
        FunctionalOrderService svc3 = new FunctionalOrderService(fixed200);
        FunctionalOrderService svc4 = new FunctionalOrderService(vipDiscount);

        System.out.printf("Без скидки:        %.2f руб.%n", svc1.calculateTotal(price));
        System.out.printf("Скидка 15%%:        %.2f руб.%n", svc2.calculateTotal(price));
        System.out.printf("Скидка 200 руб.:   %.2f руб.%n", svc3.calculateTotal(price));
        System.out.printf("VIP (30%%):         %.2f руб.%n", svc4.calculateTotal(price));

        // ── Составная стратегия: сначала 15%, потом ещё минус 100 руб. ────────
        printHeader("ПАТТЕРН СТРАТЕГИЯ — составная стратегия (andThen)");

        Function<Double, Double> combined =
            FunctionalOrderService.combine(percent15, p -> p - 100);

        FunctionalOrderService svcCombined = new FunctionalOrderService(combined);
        System.out.printf("Цена до скидок:    %.2f руб.%n", price);
        System.out.printf("После 15%% + 100р.: %.2f руб.%n", svcCombined.calculateTotal(price));

        // ── Стратегии в Map: выбор по ключу ───────────────────────────────────
        printHeader("ПАТТЕРН СТРАТЕГИЯ — реестр стратегий в Map");

        // Вместо большого switch/if-else — Map, где ключ это название стратегии
        Map<String, Function<Double, Double>> registry = new HashMap<>();
        registry.put("NONE",    p -> p);
        registry.put("PERCENT", p -> p * 0.85);
        registry.put("FIXED",   p -> Math.max(0, p - 200));
        registry.put("VIP",     p -> p * 0.70);
        registry.put("STUDENT", p -> p * 0.80);

        // Выбираем стратегию по коду — например, из запроса или настройки
        List<String> codes = List.of("NONE", "PERCENT", "VIP", "STUDENT", "UNKNOWN");

        for (String code : codes) {
            double result = registry
                .getOrDefault(code, p -> p)          // дефолт: без скидки
                .apply(price);
            System.out.printf("Стратегия %-10s → %.2f руб.%n", code, result);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ (Lazy Evaluation)
    // ═════════════════════════════════════════════════════════════════════════

    // Вспомогательный: имитация «дорогой» операции с логированием
    static double expensiveCalculation(String label) {
        System.out.println("  [ВЫЧИСЛЕНИЕ] " + label + " — выполняется сейчас");
        return Math.random() * 100;
    }

    static void lazy_Evaluation() {

        // ── 1. Eager vs Lazy: orElse vs orElseGet ────────────────────────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — orElse vs orElseGet");

        Optional<Double> presentValue = Optional.of(42.0);

        System.out.println("--- orElse: аргумент ВСЕГДА вычисляется ---");
        // expensiveCalculation вызовется, даже если Optional содержит значение!
        double r1 = presentValue.orElse(expensiveCalculation("orElse (eager)"));
        System.out.println("  Результат: " + r1);

        System.out.println("\n--- orElseGet: аргумент вычисляется ТОЛЬКО если Optional пуст ---");
        // Supplier вызывается лишь при отсутствии значения — ленивость!
        double r2 = presentValue.orElseGet(() -> expensiveCalculation("orElseGet (lazy)"));
        System.out.println("  Результат: " + r2);
        System.out.println("  → Вычисление не было вызвано, потому что значение есть");

        // ── 2. Ленивость Stream: цепочка не работает без терминальной ─────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — ленивость стрима");

        System.out.println("Создаём стрим и добавляем промежуточные операции...");
        Stream<Integer> lazyStream = Stream.of(1, 2, 3, 4, 5)
            .filter(n -> {
                System.out.println("  filter: проверяем " + n);
                return n % 2 != 0;
            })
            .map(n -> {
                System.out.println("  map:    умножаем " + n + " на 10");
                return n * 10;
            });

        System.out.println("Стрим создан. Терминальная операция ещё не вызвана.");
        System.out.println("Вызываем .findFirst() — терминальная операция:");
        // Только сейчас начинаются вычисления — и только до первого подходящего
        Optional<Integer> first = lazyStream.findFirst();
        System.out.println("Первый нечётный × 10 = " + first.orElse(-1));
        System.out.println("→ filter и map не обработали ВСЕ элементы — остановились на первом подходящем");

        // ── 3. Ленивая инициализация через Supplier ───────────────────────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — ленивая инициализация (Supplier)");

        // Eager: конфигурация создаётся немедленно при запуске программы
        System.out.println("Eager-инициализация:");
        String eagerConfig = loadConfig("eager");   // вызывается сразу

        // Lazy: конфигурация создаётся только при первом обращении
        System.out.println("\nLazy-инициализация (объявляем Supplier):");
        Supplier<String> lazyConfig = () -> loadConfig("lazy");  // ещё не вызвана
        System.out.println("  Supplier объявлен, но loadConfig ещё не вызван");
        System.out.println("  Вызываем .get():");
        String cfg = lazyConfig.get();  // вызывается здесь
        System.out.println("  Конфиг получен: " + cfg);

        // ── 4. Ленивый кэш через computeIfAbsent ──────────────────────────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — ленивый кэш (computeIfAbsent)");

        Map<String, Double> cache = new HashMap<>();

        // computeIfAbsent: вычисляет значение ТОЛЬКО если ключа нет в карте
        System.out.println("Первый запрос 'USD' (ключа нет — вычисляем):");
        double rate1 = cache.computeIfAbsent("USD",
            key -> { System.out.println("  [ЗАПРОС К API] курс для " + key); return 90.5; });
        System.out.println("  Курс: " + rate1);

        System.out.println("\nВторой запрос 'USD' (ключ есть — берём из кэша):");
        double rate2 = cache.computeIfAbsent("USD",
            key -> { System.out.println("  [ЗАПРОС К API] курс для " + key); return 90.5; });
        System.out.println("  Курс: " + rate2);
        System.out.println("  → Лямбда не вызвалась — значение взято из кэша");

        // ── 5. Бесконечный ленивый стрим ──────────────────────────────────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — бесконечный стрим");

        // Stream.iterate генерирует элементы лениво — по одному, по требованию.
        // Без limit() этот стрим никогда не завершится, но он и не стартует без
        // терминальной операции — поэтому объявить его безопасно.
        Stream<Long> fibonacci = Stream.iterate(
            new long[]{0, 1},
            f -> new long[]{f[1], f[0] + f[1]}
        ).map(f -> f[0]);

        System.out.println("Первые 10 чисел Фибоначчи (из бесконечного стрима):");
        String fibResult = fibonacci
            .limit(10)                            // ограничиваем — берём только 10
            .map(String::valueOf)
            .collect(Collectors.joining(", "));
        System.out.println("  " + fibResult);

        // ── 6. Ленивость и short-circuit операции ─────────────────────────────
        printHeader("ЛЕНИВЫЕ ВЫЧИСЛЕНИЯ — short-circuit (anyMatch, findFirst)");

        // anyMatch останавливается при первом совпадении —
        // не проверяет оставшиеся элементы
        System.out.println("Ищем первое число > 3 в потоке [1, 2, 3, 4, 5]:");
        boolean found = Stream.of(1, 2, 3, 4, 5)
            .filter(n -> {
                System.out.println("  проверяем: " + n);
                return n > 3;
            })
            .anyMatch(n -> true);
        System.out.println("Найдено: " + found);
        System.out.println("→ Проверка остановилась на 4, элемент 5 не трогался");
    }

    // Вспомогательный: имитация загрузки конфигурации
    static String loadConfig(String label) {
        System.out.println("  [ЗАГРУЗКА] конфигурация (" + label + ") считывается с диска...");
        return "config_value_" + label;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Вопрос 4: что если вызвать терминальную операцию дважды?
    // ─────────────────────────────────────────────────────────────────────────
    static void bonus_StreamReuse() {
        printHeader("БОНУС: Стрим нельзя использовать дважды");

        Stream<String> stream = Stream.of("a", "b", "c");
        System.out.println("Первый вызов: " + stream.count());

        try {
            System.out.println("Второй вызов: " + stream.count());
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Вопрос 5: effectively final в лямбдах
    // ─────────────────────────────────────────────────────────────────────────
    static void bonus_EffectivelyFinal() {
        printHeader("БОНУС: Effectively final в лямбдах");

        String prefix = "Привет, ";               // effectively final — не меняется
        Function<String, String> greet = name -> prefix + name;
        System.out.println(greet.apply("Мир"));

        // Это не скомпилируется:
        // prefix = "Hi, ";  // ← ошибка компиляции: переменная перестала быть effectively final
        // Function<String, String> greet2 = name -> prefix + name;

        System.out.println("Переменная prefix не изменялась → effectively final → лямбда работает");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Утилиты
    // ─────────────────────────────────────────────────────────────────────────
    static void printHeader(String title) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Точка входа
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        
        slide3_FunctionalInterfaces();
        slide4_BuiltInInterfaces();
        slide5_MethodReferences();
        slide7_StreamPipeline();
        slide8_ParallelStreams();
        slide9_Optional();
        slide10_ImmutabilityAndPureFunctions();
        pattern_Strategy();
        lazy_Evaluation();
        bonus_StreamReuse();
        bonus_EffectivelyFinal();

        System.out.println("\n" + "═".repeat(60));
        System.out.println("  Все примеры выполнены успешно!");
        System.out.println("═".repeat(60));
    }
}
