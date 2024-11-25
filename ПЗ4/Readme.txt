Цей код симулює роботу продуктового магазину, використовуючи класи CompletableFuture для асинхронного виконання завдань.
Нижче пояснено, як і чому використовуються методи runAsync(), supplyAsync(), thenApplyAsync(), thenAcceptAsync(), thenRunAsync().

supplyAsync запускає асинхронне завдання, яке створює пару (продукт, кількість).

-return CompletableFuture.supplyAsync(() -> {
    System.out.println("Замовлення продукту: " + product);
    simulateDelay(1000 + random.nextInt(2000));
    int quantity = 10 + random.nextInt(20);
    System.out.println("Доставка продукту: " + product + " (Кількість: " + quantity + ")");
    return new AbstractMap.SimpleEntry<>(product, quantity);
});


thenApplyAsync обробляє результат (збільшує кількість продукту).

-}).thenApplyAsync(entry -> {
    int increasedQuantity = entry.getValue() + 5;
    System.out.println("Збільшено кількість продукту " + entry.getKey() + ": " + increasedQuantity);
    return new AbstractMap.SimpleEntry<>(entry.getKey(), increasedQuantity);
});

thenAcceptAsync додає оброблений результат на склад.

-}).thenAcceptAsync(entry -> {
    stock.put(entry.getKey(), new AtomicInteger(entry.getValue()));
    System.out.println("Продукт " + entry.getKey() + " додано на склад (Кількість: " + entry.getValue() + ")");
});

runAsync запускає асинхронну обробку клієнтів, що купують продукти з наявного складу.

-customerTasks.add(CompletableFuture.runAsync(() -> serveCustomer(customerId)));

thenRunAsync сигналізує про завершення попередніх задач.

-allOrders.thenRunAsync(() -> System.out.println("Усі продукти доставлено та доступні у магазині.")).join();
