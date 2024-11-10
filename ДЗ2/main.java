import java.util.*;
import java.util.concurrent.*;

public class main {
    public static void main(String[] args) {

        // Користувацький ввід даних
        Scanner console = new Scanner(System.in);

        System.out.print("Початкова межа для чисел в масиві (оберіть одне значення від 0.5 до 99.5): ");
        double minRange = Double.parseDouble(console.nextLine().replace(',', '.'));

        System.out.print("Кінцева межа для чисел в масиві (оберіть одне значення від 0.5 до 99.5): ");
        double maxRange = Double.parseDouble(console.nextLine().replace(',', '.'));

        System.out.print("Кількість елементів у масиві (оберіть одне значення від 40 до 60): ");
        int arraySize = Integer.parseInt(console.nextLine());

        System.out.print("Кількість самих масивів для обробки: ");
        int numberOfArrays = Integer.parseInt(console.nextLine());

        console.close();

        if (minRange >= 0.5 && minRange < maxRange && maxRange <= 99.5 && arraySize >= 40 && arraySize <= 60) {
            ExecutorService executor = Executors.newFixedThreadPool(15);

            long startTime = System.currentTimeMillis();

            for (int arrayIndex = 0; arrayIndex < numberOfArrays; arrayIndex++) {
                // Генеруємо новий масив
                double[] numbers = ArrayProcessor.generateRandomArray(arraySize, minRange, maxRange);
                System.out.println("Generated array " + (arrayIndex + 1) + ": " + Arrays.toString(numbers));

                // Використовуємо CopyOnWriteArraySet для збереження результатів
                Set<Double> results = new CopyOnWriteArraySet<>();
                List<Future<Set<Double>>> futures = new ArrayList<>();
                int chunkSize = 10; // Ділимо масив на частини по 10 елементів

                for (int i = 0; i < arraySize; i += chunkSize) {
                    int end = Math.min(i + chunkSize, arraySize);
                    double[] chunk = new double[end - i];
                    System.arraycopy(numbers, i, chunk, 0, end - i);

                    // Створюємо Callable для обробки частини масиву
                    int chunkStartIndex = i; // зберігаємо початковий індекс
                    int finalArrayIndex = arrayIndex;
                    Callable<Set<Double>> task = () -> {
                        Set<Double> chunkResult = new CopyOnWriteArraySet<>();
                        long threadId = Thread.currentThread().getId(); // Отримуємо ідентифікатор потоку
                        System.out.println("Thread " + threadId + " is processing chunk starting at index " + chunkStartIndex + " of array " + (finalArrayIndex + 1));

                        for (double number : chunk) {
                            chunkResult.add(number * number);
                        }
                        return chunkResult;
                    };

                    Future<Set<Double>> future = executor.submit(task);
                    futures.add(future);
                }

                // Обробка результатів Future та перевірка станів
                for (Future<Set<Double>> future : futures) {
                    try {
                        while (!future.isDone()) {
                            // Дочікуємося завершення завдання
                        }
                        results.addAll(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        if (!future.isCancelled()) {
                            System.out.println("Error processing task: " + e.getMessage());
                        }
                    }
                }

                System.out.println("Squared numbers for array " + (arrayIndex + 1) + ": " + results);
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                System.out.println("Executor was interrupted");
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Total execution time for all arrays: " + (endTime - startTime) + " ms");

        } else {
            System.out.println("Неправильно задані діапазони значень завдання:");
            System.out.println("Діапазон значень чисел у масиві може бути від 0.5 до 99.5 включно.");
            System.out.println("Кількість чисел у масиві може бути від 40 до 60 включно.");
        }
    }
}
