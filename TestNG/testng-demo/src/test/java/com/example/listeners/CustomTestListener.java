package com.example.listeners;

import org.testng.*;

/**
 * =============================================================
 * СЛАЙД 9: Listeners — слушатели событий TestNG
 * =============================================================
 *
 * ITestListener — самый часто используемый интерфейс.
 * Методы вызываются на каждое событие теста: старт, успех, провал, пропуск.
 *
 * Типичные применения:
 *   - Логирование результатов в файл / Slack / Allure
 *   - Скриншоты при падении (Selenium)
 *   - Подсветка шагов в отчёте
 *   - Сбор метрик
 *
 * Как подключить:
 *   1. В testng.xml: <listeners><listener class-name="...CustomListener"/></listeners>
 *   2. На классе:    @Listeners(CustomListener.class)
 *   3. Программно:   TestNG tng = new TestNG(); tng.addListener(new CustomListener());
 */
public class CustomTestListener implements ITestListener {

    // Вызывается один раз перед первым тестом в ITestContext (тег <test> в XML)
    @Override
    public void onStart(ITestContext context) {
        System.out.printf("%n╔══════════════════════════════════════════╗%n");
        System.out.printf("║  [Listener] Начало: %-20s ║%n", context.getName());
        System.out.printf("╚══════════════════════════════════════════╝%n");
    }

    // Вызывается один раз после последнего теста в ITestContext
    @Override
    public void onFinish(ITestContext context) {
        System.out.printf("%n╔══════════════════════════════════════════╗%n");
        System.out.printf("║  [Listener] Конец:  %-20s ║%n", context.getName());
        System.out.printf("║  Пройдено: %-4d  Упало: %-4d  Пропущено: %-4d ║%n",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
        System.out.printf("╚══════════════════════════════════════════╝%n%n");
    }

    // Перед каждым тестовым методом
    @Override
    public void onTestStart(ITestResult result) {
        System.out.printf("  ▶ СТАРТ:  %s%n", getTestName(result));
    }

    // Тест прошёл успешно
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.printf("  ✔ УСПЕХ:  %s  (%.0f мс)%n",
                getTestName(result), getElapsedMs(result));
    }

    // Тест упал (assertion или exception)
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.printf("  ✘ ПРОВАЛ: %s  (%.0f мс)%n",
                getTestName(result), getElapsedMs(result));
        System.out.printf("    Причина: %s%n",
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "неизвестно");

        // В Selenium-проектах здесь обычно делают скриншот:
        // takeScreenshot(result.getName());
    }

    // Тест пропущен (зависимость упала или тест отключён)
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.printf("  ⚠ ПРОПУСК: %s%n", getTestName(result));
    }

    // Тест упал, но вписался в successPercentage
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.printf("  ~ ПРОВАЛ (в допуске): %s%n", getTestName(result));
    }

    // -------------------------------------------------------------------------
    // Вспомогательные методы
    // -------------------------------------------------------------------------

    private String getTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "#" + result.getMethod().getMethodName();
    }

    private double getElapsedMs(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
