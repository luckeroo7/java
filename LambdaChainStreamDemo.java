import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LambdaChainStreamDemo {

    public static void main(String[] args) {
        // ========== 1. Лямбда, меняющая "локальную переменную" ==========
        // В Java локальная переменная, используемая в лямбде, должна быть effectively final.
        // Чтобы изменить её значение, используем обёртку (массив или Atomic-класс)
        System.out.println("=== Лямбда, меняющая локальную переменную ===");

        // Способ 1: массив из одного элемента
        int[] counter = {0};
        Runnable increment = () -> counter[0]++;
        increment.run();
        increment.run();
        System.out.println("Значение counter (через массив): " + counter[0]); // 2

        // Способ 2: AtomicInteger
        AtomicInteger atomicCounter = new AtomicInteger(0);
        Runnable atomicIncrement = () -> atomicCounter.incrementAndGet();
        atomicIncrement.run();
        atomicIncrement.run();
        System.out.println("Значение atomicCounter: " + atomicCounter.get()); // 2

        // Ошибка: обычная локальная переменная не может быть изменена
        // int x = 0;
        // Runnable bad = () -> x++; // compilation error

        System.out.println("\n=== Цепочка ответственности (Chain of Responsibility) ===");
        // ========== 2. Паттерн "Цепочка ответственности" ==========
        // Создаём обработчики: проверка аутентификации, проверка прав, логирование
        Handler authHandler = new AuthHandler();
        Handler permissionHandler = new PermissionHandler();
        Handler logHandler = new LogHandler();

        // Строим цепочку: auth -> permission -> log
        authHandler.setNext(permissionHandler);
        permissionHandler.setNext(logHandler);

        // Пробрасываем запросы
        System.out.println("Запрос 1: пользователь с ролью ADMIN");
        authHandler.handle(new Request("ADMIN", true));

        System.out.println("\nЗапрос 2: пользователь без аутентификации");
        authHandler.handle(new Request("GUEST", false));

        System.out.println("\n=== Промежуточные и терминальные операции Stream API ===");
        // ========== 3. Stream API: промежуточные и терминальные операции ==========
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Промежуточные операции (lazy): filter, map, limit, sorted, peek
        // Терминальные операции (eager): forEach, collect, reduce, count, anyMatch, findFirst и т.д.

        // Пример 1: filter (промеж.) + map (промеж.) + forEach (термин.)
        System.out.println("Чётные числа, умноженные на 10:");
        numbers.stream()
                .filter(n -> n % 2 == 0)               // промежуточная
                .map(n -> n * 10)                      // промежуточная
                .forEach(System.out::println);         // терминальная

        // Пример 2: collect (термин.) после map и limit
        List<String> squares = numbers.stream()
                .map(n -> n * n)                       // промежуточная
                .limit(5)                              // промежуточная
                .map(Object::toString)                 // промежуточная
                .collect(Collectors.toList());         // терминальная
        System.out.println("Квадраты первых 5 чисел: " + squares);

        // Пример 3: reduce (термин.) для суммы
        int sum = numbers.stream()
                .filter(n -> n > 5)                    // промежуточная
                .reduce(0, Integer::sum);              // терминальная
        System.out.println("Сумма чисел > 5: " + sum);

        // Пример 4: count (термин.) после фильтрации
        long count = numbers.stream()
                .skip(3)                               // промежуточная
                .filter(n -> n % 3 == 0)               // промежуточная
                .count();                              // терминальная
        System.out.println("Количество чисел, кратных 3, после пропуска первых 3: " + count);

        // Пример 5: findFirst (термин.) с цепочкой промежуточных
        Optional<Integer> first = numbers.stream()
                .sorted((a, b) -> b - a)               // промежуточная (обратная сортировка)
                .peek(x -> System.out.print("[" + x + "] ")) // промежуточная (отладка)
                .findFirst();                          // терминальная
        System.out.println("\nПервый элемент после сортировки по убыванию: " + first.orElse(null));
    }

    // ========== Реализация цепочки ответственности ==========
    static abstract class Handler {
        protected Handler next;

        public void setNext(Handler next) {
            this.next = next;
        }

        public void handle(Request request) {
            if (canHandle(request)) {
                process(request);
            } else if (next != null) {
                next.handle(request);
            } else {
                System.out.println("Ни один обработчик не смог обработать запрос.");
            }
        }

        protected abstract boolean canHandle(Request request);
        protected abstract void process(Request request);
    }

    static class Request {
        String role;
        boolean isAuthenticated;

        Request(String role, boolean isAuthenticated) {
            this.role = role;
            this.isAuthenticated = isAuthenticated;
        }
    }

    static class AuthHandler extends Handler {
        @Override
        protected boolean canHandle(Request request) {
            return !request.isAuthenticated;
        }

        @Override
        protected void process(Request request) {
            System.out.println("AuthHandler: Пользователь не аутентифицирован. Запрос отклонён.");
        }
    }

    static class PermissionHandler extends Handler {
        @Override
        protected boolean canHandle(Request request) {
            return request.isAuthenticated && !"ADMIN".equals(request.role);
        }

        @Override
        protected void process(Request request) {
            System.out.println("PermissionHandler: Недостаточно прав (нужна роль ADMIN). Запрос отклонён.");
        }
    }

    static class LogHandler extends Handler {
        @Override
        protected boolean canHandle(Request request) {
            return request.isAuthenticated && "ADMIN".equals(request.role);
        }

        @Override
        protected void process(Request request) {
            System.out.println("LogHandler: Пользователь ADMIN аутентифицирован. Запрос выполнен. (Логируем успех)");
        }
    }
}