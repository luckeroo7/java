package com.example.groups;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 6: Группы тестов (Test Groups)
 * =============================================================
 *
 * Группы позволяют разбить тесты по категориям и запускать только нужные.
 *
 * Стратегия групп в этом проекте:
 *   "smoke"      — быстрые тесты «дым» (запускаем при каждом коммите)
 *   "regression" — полный регрессионный набор (запускаем перед релизом)
 *   "fast"       — тесты, выполняющиеся < 1 сек
 *   "slow"       — долгие тесты (интеграционные)
 *   "auth"       — всё, что связано с аутентификацией
 *
 * В testng.xml можно указать:
 *   <include name="smoke"/>   — запускать только smoke-тесты
 *   <exclude name="slow"/>    — исключить медленные тесты
 *
 * Метод может принадлежать нескольким группам одновременно.
 */
public class GroupsTest {

    private UserService userService;

    // @BeforeGroups выполняется ОДИН РАЗ перед первым тестом из указанной группы
    @BeforeGroups(groups = {"smoke", "regression", "auth"})
    public void setUpGroups() {
        System.out.println("\n  @BeforeGroups: Инициализация UserService");
        userService = new UserService();
        userService.startServer();
        // Регистрируем базового пользователя для тестов входа
        userService.register("testUser", "testPass");
    }

    // @AfterGroups выполняется ОДИН РАЗ после последнего теста из указанной группы
    @AfterGroups(groups = {"smoke", "regression", "auth"})
    public void tearDownGroups() {
        System.out.println("  @AfterGroups: Остановка UserService\n");
        userService.stopServer();
    }

    // =========================================================================
    // Smoke-тесты: быстрые, базовые проверки работоспособности
    // =========================================================================

    @Test(groups = {"smoke", "fast"},
          description = "Базовая проверка: сервер запущен")
    public void serverIsUp() {
        System.out.println("  [smoke, fast] serverIsUp");
        Assert.assertTrue(userService.isServerRunning());
    }

    @Test(groups = {"smoke", "fast", "auth"},
          description = "Базовая проверка: успешный вход")
    public void basicLoginWorks() {
        System.out.println("  [smoke, fast, auth] basicLoginWorks");
        Assert.assertTrue(userService.login("testUser", "testPass"));
    }

    // =========================================================================
    // Regression-тесты: более полная проверка бизнес-логики
    // =========================================================================

    @Test(groups = {"regression", "auth"},
          description = "Регрессия: неверный пароль отклоняется")
    public void wrongPasswordRejected() {
        System.out.println("  [regression, auth] wrongPasswordRejected");
        Assert.assertFalse(userService.login("testUser", "badPass"));
    }

    @Test(groups = {"regression", "auth"},
          description = "Регрессия: несуществующий пользователь не проходит вход")
    public void unknownUserRejected() {
        System.out.println("  [regression, auth] unknownUserRejected");
        Assert.assertFalse(userService.login("ghost", "testPass"));
    }

    @Test(groups = {"regression", "fast"},
          description = "Регрессия: счётчик пользователей растёт при регистрации")
    public void userCountIncrementsOnRegister() {
        System.out.println("  [regression, fast] userCountIncrementsOnRegister");
        int before = userService.getUserCount();
        userService.register("newUser_" + System.nanoTime(), "newPass99");
        Assert.assertEquals(userService.getUserCount(), before + 1);
    }

    // =========================================================================
    // «Медленный» тест — помечен группой "slow"
    // В testng.xml можно написать <exclude name="slow"/> и он не запустится
    // =========================================================================

    @Test(groups = {"slow", "regression"},
          description = "Имитация долгой операции (500 мс)")
    public void slowIntegrationCheck() throws InterruptedException {
        System.out.println("  [slow, regression] Долгая операция...");
        Thread.sleep(500);
        Assert.assertTrue(userService.isServerRunning(), "Сервер должен быть жив после паузы");
    }

    // =========================================================================
    // dependsOnGroups: тест запускается только если ВСЕ тесты группы прошли
    // =========================================================================

    @Test(groups = {"regression"},
          dependsOnGroups = {"smoke"},
          description = "Этот тест запустится только если все smoke-тесты прошли")
    public void deleteUserAfterSmoke() {
        System.out.println("  [regression, dependsOnGroups=smoke] Удаление пользователя");
        userService.register("toDelete", "pass1234");
        boolean deleted = userService.deleteUser("toDelete");
        Assert.assertTrue(deleted, "Удаление должно вернуть true");
        Assert.assertFalse(userService.userExists("toDelete"), "Пользователь должен быть удалён");
    }
}
