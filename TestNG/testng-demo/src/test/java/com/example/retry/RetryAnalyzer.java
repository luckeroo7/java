package com.example.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * =============================================================
 * СЛАЙД 8: Перезапуск упавших тестов (Retry Analyzer)
 * =============================================================
 *
 * TestNG поддерживает автоматический перезапуск упавших тестов
 * двумя способами:
 *
 * 1. testng-failed.xml — файл с упавшими тестами, создаётся автоматически.
 *    Запустите его повторно: mvn test -Dsurefire.suiteXmlFiles=target/surefire-reports/testng-failed.xml
 *
 * 2. IRetryAnalyzer — программный перезапуск «на месте»:
 *    Если тест упал, TestNG вызывает retry() — если возвращает true,
 *    тест будет запущен снова (до maxRetries раз).
 *
 * Подключение к тесту: @Test(retryAnalyzer = RetryAnalyzer.class)
 * Подключение глобально: через IAnnotationTransformer (см. GlobalRetryTransformer.java)
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRIES = 2; // максимум 2 повторных попытки

    /**
     * Вызывается TestNG при каждом провале теста.
     *
     * @param result результат упавшего теста
     * @return true  — запустить тест ещё раз
     *         false — окончательно провалить тест
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            System.out.printf("  [Retry] Попытка %d/%d для метода: %s%n",
                    retryCount, MAX_RETRIES,
                    result.getMethod().getMethodName());
            return true;
        }
        System.out.printf("  [Retry] Исчерпаны все %d попытки для: %s%n",
                MAX_RETRIES,
                result.getMethod().getMethodName());
        return false;
    }
}
