import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncFileProcessing {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> filePaths = List.of("file1.txt", "file2.txt", "file3.txt");

        long startTime = System.currentTimeMillis();

        // Асинхронно читаємо текст із файлів з використанням supplyAsync
        CompletableFuture<List<String>> readTask = CompletableFuture.supplyAsync(() -> {
            long taskStartTime = System.currentTimeMillis();
            List<String> sentences = new ArrayList<>();
            for (String filePath : filePaths) {
                try {
                    sentences.add(Files.readString(Paths.get(filePath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Read task completed in " + (System.currentTimeMillis() - taskStartTime) + " ms");
            return sentences;
        });

        // Обробка тексту: видалення літер з використанням thenApplyAsync
        CompletableFuture<List<String>> processTask = readTask.thenApplyAsync(sentences -> {
            long taskStartTime = System.currentTimeMillis();
            List<String> processed = new ArrayList<>();
            for (String sentence : sentences) {
                processed.add(sentence.replaceAll("[a-zA-Z]", ""));
            }
            System.out.println("Processing task completed in " + (System.currentTimeMillis() - taskStartTime) + " ms");
            return processed;
        });

        // Виведення початкових речень та результату після завершення всіх операцій з thenCombineAsync
        CompletableFuture<Void> displayTask = processTask.thenCombineAsync(readTask, (processed, original) -> {
            long taskStartTime = System.currentTimeMillis();
            System.out.println("\n--- Final Output ---");

            // Вивід початкових речень
            System.out.println("Original sentences:");
            for (String sentence : original) {
                System.out.println(sentence);
            }

            // Вивід оброблених речень
            System.out.println("\nProcessed sentences:");
            for (String result : processed) {
                System.out.println(result);
            }

            System.out.println("Display task completed in " + (System.currentTimeMillis() - taskStartTime) + " ms");
            return null;
        });

        // Очікуємо завершення виводу
        displayTask.get();

        System.out.println("\nTotal execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
