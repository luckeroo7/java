# TestNG Demo Project

Подробный пример работы фреймворка **TestNG**, соответствующий слайдам презентации.

---

## Структура проекта

```
testng-demo/
├── pom.xml
└── src/test/
    ├── java/com/example/
    │   ├── UserService.java                          # «Продакшн-код» для тестирования
    │   │
    │   ├── basics/
    │   │   ├── AnnotationLifecycleTest.java          # Слайд 3: @Before*/After* аннотации
    │   │   ├── TestAttributesTest.java               # Слайд 4: Атрибуты @Test
    │   │   └── DependencyTest.java                   # Слайд 8: dependsOnMethods
    │   │
    │   ├── dataprovider/
    │   │   └── DataProviderTest.java                 # Слайд 5: @DataProvider
    │   │
    │   ├── groups/
    │   │   └── GroupsTest.java                       # Слайд 6: Группы тестов
    │   │
    │   ├── parallel/
    │   │   └── ParallelMethodsTest.java              # Слайд 7: Параллельное выполнение
    │   │
    │   ├── assertions/
    │   │   └── AssertionsTest.java                   # Слайд 10: Assert и SoftAssert
    │   │
    │   ├── factory/
    │   │   └── UserRoleTest.java                     # Слайд 3/11: @Factory
    │   │
    │   ├── retry/
    │   │   ├── RetryAnalyzer.java                    # Слайд 8: IRetryAnalyzer
    │   │   ├── GlobalRetryTransformer.java           # Слайд 9: IAnnotationTransformer
    │   │   └── RetryDemoTest.java                    # Демо перезапуска
    │   │
    │   └── listeners/
    │       ├── CustomTestListener.java               # Слайд 9: ITestListener
    │       └── ListenerDemoTest.java                 # Демо листенера
    │
    └── resources/
        └── testng.xml                                # Главный конфиг TestNG
```

---

## Запуск

### Все тесты (через Maven + testng.xml)
```bash
mvn test
```

### Только smoke-тесты
```bash
mvn test -Dgroups=smoke
```

### Только конкретный класс
```bash
mvn test -Dtest=DataProviderTest
```

### Повтор упавших тестов (после первого запуска)
```bash
mvn test -Dsurefire.suiteXmlFiles=target/surefire-reports/testng-failed.xml
```

---

## Что демонстрирует каждый файл

| Файл | Тема (слайд) | Ключевые концепции |
|------|-------------|-------------------|
| `AnnotationLifecycleTest` | Слайд 3 | `@BeforeSuite/After`, `@BeforeClass/After`, `@BeforeMethod/After` |
| `TestAttributesTest` | Слайд 4 | `priority`, `enabled`, `timeOut`, `invocationCount`, `expectedExceptions` |
| `DependencyTest` | Слайд 8 | `dependsOnMethods`, `alwaysRun`, цепочка шагов |
| `DataProviderTest` | Слайд 5 | `@DataProvider`, параметризация, `parallel=true` |
| `GroupsTest` | Слайд 6 | `groups`, `@BeforeGroups`, `dependsOnGroups`, testng.xml `<include>`/`<exclude>` |
| `ParallelMethodsTest` | Слайд 7 | `parallel="methods"`, `threadPoolSize`, `ThreadLocal` |
| `AssertionsTest` | Слайд 10 | `Assert`, `SoftAssert`, `assertThrows`, `assertEqualsNoOrder` |
| `UserRoleTest` | Слайд 3/11 | `@Factory` — параметризация всего класса |
| `RetryAnalyzer` | Слайд 8 | `IRetryAnalyzer` — программный retry |
| `GlobalRetryTransformer` | Слайд 9 | `IAnnotationTransformer` — глобальный retry |
| `CustomTestListener` | Слайд 9 | `ITestListener` — логирование событий |
| `testng.xml` | Слайд 7 | `<suite>`, `<test>`, `<groups>`, `parallel`, `thread-count`, `<listeners>` |

---

## Отчёты

После запуска откройте `target/surefire-reports/index.html` для HTML-отчёта.
Встроенный отчёт TestNG находится в `target/surefire-reports/`.

---

## Зависимости

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
</dependency>
```
