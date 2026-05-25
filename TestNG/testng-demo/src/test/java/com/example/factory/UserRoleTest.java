package com.example.factory;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 3 / 11: @Factory — Фабрика тест-объектов
 * =============================================================
 *
 * @Factory позволяет создавать несколько экземпляров тестового класса
 * с разными параметрами конструктора. Каждый экземпляр выполняет
 * ВСЕ свои тестовые методы с «собственными» данными.
 *
 * Разница @Factory vs @DataProvider:
 *   @DataProvider — параметризует один метод (строки данных → вызовы метода)
 *   @Factory      — параметризует весь класс (наборы данных → экземпляры класса)
 *
 * Когда @Factory предпочтительнее:
 *   - Нужно тестировать одно поведение с несколькими конфигурациями
 *   - Каждый «вариант» требует нескольких тестовых методов
 *   - Хочется изолировать состояние (каждый экземпляр независим)
 */
public class UserRoleTest {

    private final String username;
    private final String password;
    private final String role;        // "admin" | "editor" | "viewer"
    private UserService userService;

    // Конструктор принимает параметры, которые задаёт фабрика
    public UserRoleTest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // -------------------------------------------------------------------------
    // Фабричный метод: создаёт три экземпляра класса с разными параметрами.
    // TestNG запустит ВСЕ @Test-методы для каждого экземпляра.
    // -------------------------------------------------------------------------

    @Factory
    public static Object[] createInstances() {
        return new Object[] {
            new UserRoleTest("admin_user",  "adminPass1",   "admin"),
            new UserRoleTest("editor_user", "editorPass2",  "editor"),
            new UserRoleTest("viewer_user", "viewerPass3",  "viewer"),
        };
    }

    // -------------------------------------------------------------------------
    // Настройка: каждый экземпляр получает свой UserService
    // -------------------------------------------------------------------------

    @BeforeClass
    public void setUp() {
        userService = new UserService();
        userService.startServer();
        userService.register(username, password);
        System.out.printf("%n  [Factory] Подготовка экземпляра для роли '%s' (user: %s)%n",
                role, username);
    }

    @AfterClass
    public void tearDown() {
        userService.stopServer();
    }

    // =========================================================================
    // Тесты выполняются для КАЖДОГО экземпляра с его данными
    // =========================================================================

    @Test(description = "Пользователь должен успешно войти в систему")
    public void loginTest() {
        System.out.printf("  [Factory / %s] Тест входа%n", role);
        boolean result = userService.login(username, password);
        Assert.assertTrue(result,
                String.format("Пользователь '%s' (роль: %s) должен успешно войти", username, role));
    }

    @Test(description = "Пользователь должен существовать после регистрации")
    public void userExistsTest() {
        System.out.printf("  [Factory / %s] Тест существования пользователя%n", role);
        Assert.assertTrue(userService.userExists(username),
                String.format("Пользователь '%s' должен существовать", username));
    }

    @Test(description = "Вход с неверным паролем должен быть отклонён")
    public void wrongPasswordTest() {
        System.out.printf("  [Factory / %s] Тест неверного пароля%n", role);
        boolean result = userService.login(username, "WRONG_PASSWORD");
        Assert.assertFalse(result,
                String.format("Вход для '%s' с неверным паролем должен быть отклонён", username));
    }

    @Test(description = "Роль проверяется корректно (имитация)")
    public void roleCheckTest() {
        System.out.printf("  [Factory / %s] Тест проверки роли%n", role);
        // Имитация логики ролей: просто проверяем, что роль не пустая
        Assert.assertNotNull(role, "Роль не должна быть null");
        Assert.assertFalse(role.isBlank(), "Роль не должна быть пустой строкой");
    }
}
