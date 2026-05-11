import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.flywaydb.core.Flyway;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


class HibernateUtil {
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {}

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Настройки подключения
                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/shop_db");
                configuration.setProperty("hibernate.connection.username", "postgres");
                configuration.setProperty("hibernate.connection.password", "admin");

                // Настройки пула c3p0
                configuration.setProperty("hibernate.c3p0.min_size", "2");
                configuration.setProperty("hibernate.c3p0.max_size", "10");
                configuration.setProperty("hibernate.c3p0.timeout", "300");
                configuration.setProperty("hibernate.c3p0.max_statements", "50");
                configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");

                // Настройки Hibernate
              //  configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                configuration.setProperty("hibernate.show_sql", "true");      // Показывать сгенерированный SQL
                configuration.setProperty("hibernate.format_sql", "true");    // Форматировать SQL
                configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
                configuration.setProperty("hibernate.current_session_context_class", "thread");

                // Регистрируем сущность
                configuration.addAnnotatedClass(Order.class);

                sessionFactory = configuration.buildSessionFactory();

            } catch (Throwable ex) {
                throw new ExceptionInInitializerError("Ошибка Hibernate: " + ex);
            }
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}


@Entity
@Table(name = "orders")
class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Конструкторы
    public Order() {} // Обязательный для Hibernate

    public Order(Long customerId, BigDecimal totalAmount, String deliveryAddress) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customerId=%d, status=%s, totalAmount=%s, address='%s', createdAt=%s}",
                id, customerId, status, totalAmount, deliveryAddress, createdAt);
    }
}

enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

class OrderDAO {
    private final SessionFactory sessionFactory;

    public OrderDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // CREATE — сохраняем Java-объект (ИСПРАВЛЕНО!)
    public Long save(Order order) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.persist(order);  // Hibernate сам установит ID в объект order
            session.getTransaction().commit();
            return order.getId();     // Возвращаем ID из сохранённого объекта
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось сохранить заказ", e);
        }
    }

    // READ — находим по ID (возвращаем Java-объект)
    public Optional<Order> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Order order = session.find(Order.class, id);  // find() вместо get()
            session.getTransaction().commit();
            return Optional.ofNullable(order);
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось найти заказ по id: " + id, e);
        }
    }

    // READ — все заказы клиента (используем Criteria API)
    public List<Order> findByCustomerId(Long customerId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            // Используем Criteria API — полностью объектно-ориентированный подход
            List<Order> orders = session.createQuery(
                            "FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC",
                            Order.class)
                    .setParameter("customerId", customerId)
                    .getResultList();

            session.getTransaction().commit();
            return orders;

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось получить заказы клиента: " + customerId, e);
        }
    }

    // READ — все заказы
    public List<Order> findAll() {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Order> orders = session.createQuery("FROM Order", Order.class).getResultList();
            session.getTransaction().commit();
            return orders;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось получить все заказы", e);
        }
    }

    // UPDATE — обновляем Java-объект (Hibernate сам обнаружит изменения!)
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            // Загружаем Java-объект
            Order order = session.find(Order.class, orderId);
            if (order == null) {
                throw new RuntimeException("Заказ не найден с id: " + orderId);
            }

            // Меняем статус у Java-объекта
            order.setStatus(newStatus);

            // Hibernate сам сгенерирует UPDATE при коммите!
            // Никакого UPDATE SQL не пишем!
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось обновить статус заказа", e);
        }
    }

    // UPDATE — полное обновление объекта
    public void update(Order order) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.merge(order);  // merge() обновляет существующий объект
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось обновить заказ", e);
        }
    }

    // DELETE — удаляем Java-объект
    public void delete(Long orderId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            // Загружаем Java-объект
            Order order = session.find(Order.class, orderId);
            if (order == null) {
                throw new RuntimeException("Заказ не найден с id: " + orderId);
            }

            // Удаляем Java-объект
            session.remove(order);  // remove() вместо delete()

            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось удалить заказ", e);
        }
    }

    // STATS — агрегатные функции (всё равно нужен HQL, но это минимум)
    public OrderStats getStats() {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            // Единственное место, где нужен HQL — агрегатные функции
            Object[] result = session.createQuery(
                            "SELECT COUNT(o), COALESCE(SUM(o.totalAmount), 0) FROM Order o",
                            Object[].class)
                    .getSingleResult();

            Long totalOrders = (Long) result[0];
            BigDecimal totalRevenue = (BigDecimal) result[1];

            session.getTransaction().commit();
            return new OrderStats(totalOrders, totalRevenue);

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Не удалось получить статистику заказов", e);
        }
    }

    // DTO для статистики
    public static class OrderStats {
        public final long totalOrders;
        public final BigDecimal totalRevenue;

        public OrderStats(long totalOrders, BigDecimal totalRevenue) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
        }
    }
}


public class ShopApplication {
    public static void main(String[] args) {
        SessionFactory sessionFactory = null;

        try {
            // Настройка DataSource для Flyway
            com.zaxxer.hikari.HikariConfig hikariConfig = new com.zaxxer.hikari.HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/shop_db");
            hikariConfig.setUsername("postgres");
            hikariConfig.setPassword("admin");
            hikariConfig.setMaximumPoolSize(10);

            com.zaxxer.hikari.HikariDataSource dataSource = new com.zaxxer.hikari.HikariDataSource(hikariConfig);

            // Flyway миграции (создаёт таблицу)
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("filesystem:src/main/resources/db/migration")
                    .load();
            flyway.migrate();
            System.out.println("Миграции выполнены");

            // Hibernate SessionFactory
            sessionFactory = HibernateUtil.getSessionFactory();
            System.out.println("Hibernate ORM инициализирован");

            // Работа с ORM
            OrderDAO orderDAO = new OrderDAO(sessionFactory);

            // CREATE — создаём Java-объект и сохраняем
            System.out.println("\nCREATE");
            Order newOrder = new Order(1L, new BigDecimal("1599.99"), "ул. Пушкина, 10");
            Long orderId = orderDAO.save(newOrder);
            System.out.println("Заказ создан: ID=" + orderId);
            System.out.println("Объект: " + newOrder);

            // READ — находим по ID
            System.out.println("\nREAD");
            Optional<Order> found = orderDAO.findById(orderId);
            found.ifPresent(order -> System.out.println("Найден заказ: " + order));

            // UPDATE — меняем статус у Java-объекта
            System.out.println("\nUPDATE");
            orderDAO.updateStatus(orderId, OrderStatus.SHIPPED);
            System.out.println("Статус изменен на SHIPPED");

            // Проверяем обновление
            Order updatedOrder = orderDAO.findById(orderId).get();
            System.out.println("После обновления: " + updatedOrder);

            // LIST — получаем все заказы клиента
            System.out.println("\nLIST");
            List<Order> customerOrders = orderDAO.findByCustomerId(1L);
            System.out.println("Заказов у клиента: " + customerOrders.size());
            customerOrders.forEach(System.out::println);

            // STATS — статистика
            System.out.println("\nSTATS");
            OrderDAO.OrderStats stats = orderDAO.getStats();
            System.out.println("Всего заказов: " + stats.totalOrders);
            System.out.println("Общая выручка: " + stats.totalRevenue);

            // DELETE — удаляем
            System.out.println("\nDELETE");
            orderDAO.delete(orderId);
            System.out.println("Заказ удален");

            // Проверяем, что удалилось
            boolean exists = orderDAO.findById(orderId).isPresent();
            System.out.println("Заказ существует после удаления: " + exists);

            dataSource.close();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) {
                HibernateUtil.closeSessionFactory();
                System.out.println("\nSessionFactory закрыт");
            }
        }
    }
}