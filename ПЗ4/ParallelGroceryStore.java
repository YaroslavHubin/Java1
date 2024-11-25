import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelGroceryStore {

    private static final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private static final List<String> products = Arrays.asList("Молоко", "Хліб", "Яйця", "Сир", "Овочі");
    private static final Map<Integer, List<String>> customerPurchases = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Робота магазину розпочалася!");

        // 1. Замовлення та доставка продуктів
        List<CompletableFuture<Void>> orderTasks = new ArrayList<>();
        for (String product : products) {
            orderTasks.add(orderAndDeliverProduct(product));
        }
        CompletableFuture<Void> allOrders = CompletableFuture.allOf(orderTasks.toArray(new CompletableFuture[0]));

        // 2. Очікуємо завершення замовлень
        allOrders.thenRunAsync(() -> System.out.println("Усі продукти доставлено та доступні у магазині.")).join();

        // 3. Симуляція клієнтів
        CompletableFuture<Void> customerSimulation = simulateCustomers(5);
        customerSimulation.join();

        System.out.println("\nРобота магазину завершена! Залишки:");
        stock.forEach((product, count) -> System.out.println(product + ": " + count.get() + " шт."));

        System.out.println("\nПокупки клієнтів:");
        customerPurchases.forEach((customerId, purchases) -> {
            System.out.println("Клієнт " + customerId + ": " + purchases);
        });
    }

    private static CompletableFuture<Void> orderAndDeliverProduct(String product) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Замовлення продукту: " + product);
            simulateDelay(1000 + random.nextInt(2000));
            int quantity = 10 + random.nextInt(20);
            System.out.println("Доставка продукту: " + product + " (Кількість: " + quantity + ")");
            return new AbstractMap.SimpleEntry<>(product, quantity);
        }).thenApplyAsync(entry -> {
            // Додаткова обробка кількості продукту
            int increasedQuantity = entry.getValue() + 5; // Наприклад, додаємо 5 одиниць до кожного продукту
            System.out.println("Збільшено кількість продукту " + entry.getKey() + ": " + increasedQuantity);
            return new AbstractMap.SimpleEntry<>(entry.getKey(), increasedQuantity);
        }).thenAcceptAsync(entry -> {
            stock.put(entry.getKey(), new AtomicInteger(entry.getValue()));
            System.out.println("Продукт " + entry.getKey() + " додано на склад (Кількість: " + entry.getValue() + ")");
        });
    }

    private static CompletableFuture<Void> simulateCustomers(int numberOfCustomers) {
        List<CompletableFuture<Void>> customerTasks = new ArrayList<>();
        for (int i = 1; i <= numberOfCustomers; i++) {
            int customerId = i;
            customerTasks.add(CompletableFuture.runAsync(() -> serveCustomer(customerId)));
        }
        return CompletableFuture.allOf(customerTasks.toArray(new CompletableFuture[0]));
    }

    private static void serveCustomer(int customerId) {
        System.out.println("Клієнт " + customerId + " увійшов до магазину.");
        List<String> purchases = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // Клієнт бере до 3 продуктів
            String product = products.get(random.nextInt(products.size()));
            int amount = 1 + random.nextInt(5); // Клієнт бере від 1 до 5 одиниць
            stock.computeIfPresent(product, (key, currentStock) -> {
                if (currentStock.get() >= amount) {
                    currentStock.addAndGet(-amount);
                    purchases.add(product + " (" + amount + " шт.)");
                    System.out.println("Клієнт " + customerId + " взяв " + amount + " шт. продукту: " + product);
                } else {
                    System.out.println("Клієнт " + customerId + " хотів взяти " + amount + " шт. продукту: " + product + ", але недостатньо.");
                }
                return currentStock;
            });
            simulateDelay(500 + random.nextInt(500));
        }
        customerPurchases.put(customerId, purchases); // Зберігаємо покупки клієнта
        System.out.println("Клієнт " + customerId + " завершив покупки.");
    }

    private static void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
