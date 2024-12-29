import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTask {
    public static void main(String[] args) {
        // Перше завдання: сума двох рандомних чисел
        CompletableFuture<Integer> sumTask = CompletableFuture.supplyAsync(() -> {
            int a = ThreadLocalRandom.current().nextInt(1, 100);
            int b = ThreadLocalRandom.current().nextInt(1, 100);
            System.out.println("Перше завдання: Сума " + a + " + " + b);
            return a + b;
        });

        // Друге завдання: добуток двох рандомних чисел
        CompletableFuture<Integer> productTask = CompletableFuture.supplyAsync(() -> {
            int x = ThreadLocalRandom.current().nextInt(1, 100);
            int y = ThreadLocalRandom.current().nextInt(1, 100);
            System.out.println("Друге завдання: Добуток " + x + " * " + y);
            return x * y;
        });

        // Об'єднання результатів за допомогою thenCombine()
        CompletableFuture<String> combinedResult = sumTask.thenCombine(productTask, (sum, product) -> {
            System.out.println("Об'єднання результатів: сума = " + sum + ", добуток = " + product);
            return "Результати: Сума = " + sum + ", Добуток = " + product;
        });

        // Використання allOf(): обробляємо обидва завдання разом
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(sumTask, productTask);
        allTasks.thenRun(() -> System.out.println("Всі завдання завершені."));

        // Використання anyOf(): обробляємо перший завершений результат
        CompletableFuture<Object> anyTask = CompletableFuture.anyOf(sumTask, productTask);
        anyTask.thenAccept(result -> System.out.println("Перше завершене завдання дало результат: " + result));

        // Виведення результатів
        try {
            System.out.println("Результат об'єднання: " + combinedResult.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

