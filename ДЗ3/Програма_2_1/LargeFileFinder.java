import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Scanner;

public class LargeFileFinder {

    // Клас для пошуку файлів у директоріях за допомогою Fork/Join
    static class FileSearchTask extends RecursiveTask<Long> {
        private final File directory;
        private final long minSize;

        public FileSearchTask(File directory, long minSize) {
            this.directory = directory;
            this.minSize = minSize;
        }

        @Override
        protected Long compute() {
            long count = 0;
            File[] files = directory.listFiles();

            if (files == null) {
                return 0L;                              // Повертаємо 0, якщо директорія недоступна
            }

            // Створюємо підзадачі для вкладених директорій
            for (File file : files) {
                if (file.isDirectory()) {
                    FileSearchTask subTask = new FileSearchTask(file, minSize);
                    subTask.fork();                     // Запускаємо підзадачу
                    count += subTask.join();            // Чекаємо завершення підзадачі
                } else if (file.isFile() && file.length() > minSize) {
                    count++;                            // Якщо файл задовольняє умову, збільшуємо лічильник
                }
            }

            return count;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Введення директорії
        System.out.println("Enter directory path: ");
        String directoryPath = scanner.nextLine();

        // Введення мінімального розміру файлу
        System.out.println("Enter minimum file size in bytes: ");
        long minSize = 0;
        try {
            minSize = scanner.nextLong();
        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a valid number for file size.");
            return;
        }

        // Перевірка валідності введеної директорії
        File rootDir = new File(directoryPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Invalid directory path!");
            return;
        }

        // Використання ForkJoinPool для виконання задачі
        ForkJoinPool pool = new ForkJoinPool();
        long startTime = System.nanoTime();

        FileSearchTask task = new FileSearchTask(rootDir, minSize);
        long fileCount = pool.invoke(task);

        long endTime = System.nanoTime();

        // Виведення результатів
        System.out.println("Number of files larger than " + minSize + " bytes: " + fileCount);
        System.out.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
