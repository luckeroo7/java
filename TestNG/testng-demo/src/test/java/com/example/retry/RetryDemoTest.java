package com.example.retry;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================
 * СЛАЙД 8: Демонстрация перезапуска тестов
 * =============================================================
 *
 * Показывает, как RetryAnalyzer перезапускает упавшие тесты.
 *
 * В реальных проектах retry полезен для:
 *   - Нестабильных UI-тестов (flaky tests)
 *   - Тестов с сетевыми запросами (intermittent failures)
 *   - Тестов, зависящих от внешних сервисов
 */
public class RetryDemoTest {

    // Счётчик — имитирует нестабильный тест, который со временем проходит
    private static final AtomicInteger callCounter = new AtomicInteger(0);

    /**
     * Этот тест падает при первом вызове и проходит при втором.
     * RetryAnalyzer автоматически перезапустит его 1 раз → PASS.
     *
     * Вывод в консоли:
     *   [Retry] Попытка 1/2 для метода: flakyNetworkTest
     *   [flakyNetworkTest] Попытка #2 → PASS
     */
    @Test(retryAnalyzer = RetryAnalyzer.class,
          description = "Нестабильный тест: упадёт при первом вызове, пройдёт при втором")
    public void flakyNetworkTest() {
        int attempt = callCounter.incrementAndGet();
        System.out.printf("  [flakyNetworkTest] Попытка #%d%n", attempt);

        if (attempt < 2) {
            Assert.fail("Имитация нестабильного сетевого сбоя на попытке #" + attempt);
        }
        // При attempt >= 2 тест проходит
        System.out.println("  [flakyNetworkTest] PASS на попытке #" + attempt);
    }

    /**
     * Этот тест всегда падает — RetryAnalyzer сделает 2 повтора и затем сдастся.
     * В отчёте будет FAIL, но в консоли видны все 3 попытки.
     *
     * Вывод:
     *   [Retry] Попытка 1/2 ...
     *   [Retry] Попытка 2/2 ...
     *   [Retry] Исчерпаны все 2 попытки ...
     */
    @Test(retryAnalyzer = RetryAnalyzer.class,
          description = "Постоянно падающий тест: retry не спасёт, но мы увидим все попытки")
    public void alwaysFailingTest() {
        System.out.println("  [alwaysFailingTest] Выполнение — всегда падает");
        Assert.fail("Этот тест всегда падает (демонстрация исчерпания retry)");
    }

    /**
     * Обычный тест без retry — для сравнения поведения.
     */
    @Test(description = "Стабильный тест без retry — всегда проходит")
    public void stableTest() {
        System.out.println("  [stableTest] Стабильный тест — всегда PASS");
        Assert.assertTrue(true);
    }
}
