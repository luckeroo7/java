package com.example.parallel;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * =============================================================
 * СЛАЙД 7: Параллельное выполнение тестов
 * =============================================================
 *
 * Режимы параллелизма (задаются в testng.xml через атрибут parallel):
 *
 *   parallel="methods"   — каждый @Test-метод в отдельном потоке
 *   parallel="classes"   — каждый тест-класс в отдельном потоке
 *   parallel="tests"     — каждый <test> из XML в отдельном потоке
 *   parallel="instances" — каждый экземпляр класса в отдельном потоке
 *
 * thread-count="N" — максимальное число одновременных потоков.
 *
 * Параллелизм на уровне метода (@Test):
 *   threadPoolSize + invocationCount:
 *   invocationCount=10, threadPoolSize=3 → 10 запусков, не более 3 одновременно
 *
 * ВАЖНО: при параллельных тестах нужно следить за потокобезопасностью.
 * Используйте ThreadLocal для хранения объектов, специфичных для потока.
 */
public class ParallelMethodsTest {

    // Собираем имена потоков, которые исполняли тесты.
    // Collections.synchronizedSet — потокобезопасная Set.
    private static final Set<String> threadNames =
            Collections.synchronizedSet(new HashSet<>());

    // =========================================================================
    // При parallel="methods" эти методы выполняются в РАЗНЫХ потоках одновременно
    // =========================================================================

    @Test(description = "Параллельный метод A")
    public void parallelMethodA() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        threadNames.add(thread);
        System.out.printf("  [Parallel A] Поток: %s%n", thread);
        Thread.sleep(100); // имитация работы
        Assert.assertNotNull(thread);
    }

    @Test(description = "Параллельный метод B")
    public void parallelMethodB() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        threadNames.add(thread);
        System.out.printf("  [Parallel B] Поток: %s%n", thread);
        Thread.sleep(100);
        Assert.assertNotNull(thread);
    }

    @Test(description = "Параллельный метод C")
    public void parallelMethodC() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        threadNames.add(thread);
        System.out.printf("  [Parallel C] Поток: %s%n", thread);
        Thread.sleep(100);
        Assert.assertNotNull(thread);
    }

    @Test(description = "Параллельный метод D")
    public void parallelMethodD() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        threadNames.add(thread);
        System.out.printf("  [Parallel D] Поток: %s%n", thread);
        Thread.sleep(100);
        Assert.assertNotNull(thread);
    }

    // =========================================================================
    // invocationCount + threadPoolSize: 6 вызовов, не более 3 одновременно
    // =========================================================================

    @Test(invocationCount = 6,
          threadPoolSize = 3,
          description = "6 вызовов метода в пуле из 3 потоков")
    public void stressTest() {
        String thread = Thread.currentThread().getName();
        System.out.printf("  [StressTest invocation] Поток: %s%n", thread);
        // Здесь можно проверять производительность или состояние под нагрузкой
        Assert.assertTrue(true);
    }

    // =========================================================================
    // ThreadLocal: безопасное хранение данных, специфичных для потока
    // =========================================================================

    /**
     * ThreadLocal гарантирует, что каждый поток работает со своей копией данных.
     * Это устраняет гонку данных (race condition) при параллельном выполнении.
     */
    private static final ThreadLocal<String> threadLocalContext = new ThreadLocal<>();

    @Test(description = "Демонстрация ThreadLocal при параллельном запуске")
    public void threadLocalDemo() {
        // Каждый поток устанавливает своё значение
        threadLocalContext.set("context_for_" + Thread.currentThread().getName());

        String value = threadLocalContext.get();
        System.out.printf("  [ThreadLocal] Поток: %s, значение: %s%n",
                Thread.currentThread().getName(), value);

        Assert.assertNotNull(value, "ThreadLocal не должен быть null");
        Assert.assertTrue(value.startsWith("context_for_"),
                "Значение должно принадлежать текущему потоку");

        // Очищаем, чтобы не было утечки памяти
        threadLocalContext.remove();
    }
}
