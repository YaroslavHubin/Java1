import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutionException;

public class SoftwareSelection {

    // Метод для отримання рейтингу програмного забезпечення за ціною
    private static CompletableFuture<Integer> getPriceRating(String software) {
        return CompletableFuture.supplyAsync(() -> {
            int rating = ThreadLocalRandom.current().nextInt(1, 10);
            System.out.println("Ціна " + software + ": " + rating);
            return rating;
        });
    }

    // Метод для отримання рейтингу програмного забезпечення за функціональністю
    private static CompletableFuture<Integer> getFunctionalityRating(String software) {
        return CompletableFuture.supplyAsync(() -> {
            int rating = ThreadLocalRandom.current().nextInt(1, 10);
            System.out.println("Функціональність " + software + ": " + rating);
            return rating;
        });
    }

    // Метод для отримання рейтингу програмного забезпечення за підтримкою
    private static CompletableFuture<Integer> getSupportRating(String software) {
        return CompletableFuture.supplyAsync(() -> {
            int rating = ThreadLocalRandom.current().nextInt(1, 10);
            System.out.println("Підтримка " + software + ": " + rating);
            return rating;
        });
    }

    // Метод для обчислення загального рейтингу
    private static CompletableFuture<Integer> calculateTotalRating(String software) {
        // Паралельне обчислення для ціни, функціональності та підтримки
        CompletableFuture<Integer> priceRating = getPriceRating(software);
        CompletableFuture<Integer> functionalityRating = getFunctionalityRating(software);
        CompletableFuture<Integer> supportRating = getSupportRating(software);

        // Використовуємо allOf() для того, щоб дочекатися завершення всіх обчислень
        return CompletableFuture.allOf(priceRating, functionalityRating, supportRating)
                .thenApply(v -> priceRating.join() + functionalityRating.join() + supportRating.join());
    }

    public static void main(String[] args) {
        String software1 = "Software A";
        String software2 = "Software B";

        // Вибір найкращого варіанту на основі числових рейтингів
        CompletableFuture<String> bestSoftware = calculateTotalRating(software1).thenCombine(calculateTotalRating(software2), (r1, r2) -> {
            return r1 > r2 ? software1 : software2;
        });

        // Виведення результатів
        try {
            System.out.println("Найкраще програмне забезпечення: " + bestSoftware.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
