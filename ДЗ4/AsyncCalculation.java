import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class AsyncCalculation {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Асинхронно генеруємо послідовність чисел
        CompletableFuture<Void> generationTask = CompletableFuture.runAsync(() -> {
            long taskStartTime = System.currentTimeMillis();
            System.out.println("Generating sequence of numbers...");
            List<Double> numbers = generateRandomNumbers(20, 0.5, 99.5);
            System.out.println("Sequence generated: " + numbers);
            System.out.println("Generation task completed in " + (System.currentTimeMillis() - taskStartTime) + " ms");

            // Передаємо послідовність у наступне завдання
            processSequence(numbers).thenAcceptAsync(result -> {
                long processStartTime = System.currentTimeMillis();
                System.out.println("\nResult of the calculation: " + result);
                System.out.println("Processing task completed in " + (System.currentTimeMillis() - processStartTime) + " ms");
            }).thenRunAsync(() -> {
                System.out.println("\nAll tasks completed in " + (System.currentTimeMillis() - startTime) + " ms");
            });
        });

        // Очікуємо завершення всіх задач
        generationTask.join();
    }

    // Метод для генерації випадкових чисел
    private static List<Double> generateRandomNumbers(int count, double min, double max) {
        Random random = new Random();
        List<Double> numbers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            numbers.add(min + (max - min) * random.nextDouble());
        }
        return numbers;
    }

    // Асинхронна обробка послідовності
    private static CompletableFuture<Double> processSequence(List<Double> numbers) {
        return CompletableFuture.supplyAsync(() -> {
            double result = 0.0;
            for (int i = 0; i < numbers.size() - 1; i++) {
                result += numbers.get(i) * numbers.get(i + 1);
            }
            return result;
        });
    }
}

