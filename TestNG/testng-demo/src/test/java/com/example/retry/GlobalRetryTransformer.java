package com.example.retry;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * =============================================================
 * СЛАЙД 9: IAnnotationTransformer — глобальный Retry Analyzer
 * =============================================================
 *
 * IAnnotationTransformer позволяет программно изменять аннотации @Test
 * ВО ВРЕМЯ выполнения, не трогая исходный код.
 *
 * Здесь мы используем его, чтобы подключить RetryAnalyzer ко ВСЕМ тестам
 * без указания retryAnalyzer=... в каждом @Test.
 *
 * Подключение в testng.xml:
 *   <listeners>
 *     <listener class-name="com.example.retry.GlobalRetryTransformer"/>
 *   </listeners>
 */
public class GlobalRetryTransformer implements IAnnotationTransformer {

    /**
     * Вызывается для каждого тестового метода перед его запуском.
     * Здесь можно изменить любой атрибут @Test.
     */
    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        // Подключаем RetryAnalyzer ко всем тестам глобально
        if (annotation.getRetryAnalyzer() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
