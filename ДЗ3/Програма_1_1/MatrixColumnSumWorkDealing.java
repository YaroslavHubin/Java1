import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixColumnSumWorkDealing {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        int rows = getIntInput(scanner, "Enter number of rows: ");
        int cols = getIntInput(scanner, "Enter number of columns: ");
        int min = getIntInput(scanner, "Enter minimum value: ");
        int max = getIntInput(scanner, "Enter maximum value: ");

        // Перевірка, чи min менше max
        if (min > max) {
            System.out.println("Мінімальне значення не може бути більше максимального. Спробуйте ще раз.");
            return;
        }

        int[][] matrix = generateMatrix(rows, cols, min, max);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Створення групи потоків з наявною кількістю вільного ресурсу для обчислень

        long startTime = System.nanoTime();
        Future<Integer>[] results = new Future[cols];
        int[] columnSums = new int[cols];

        // Кожен потік підраховує суму в колонках та зберігає її в Future
        for (int col = 0; col < cols; col++) {
            int finalCol = col;
            results[col] = executor.submit(() -> {
                int sum = 0;
                for (int i = 0; i < rows; i++) {
                    sum += matrix[i][finalCol];
                }
                return sum;
            });
        }
        // Повернення результату з Future
        for (int col = 0; col < cols; col++) {
            columnSums[col] = results[col].get();
        }
        long endTime = System.nanoTime();

        executor.shutdown();
        printMatrix(matrix);
        System.out.println("Column sums (Work Dealing):");
        printArray(columnSums);
        System.out.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }

    private static int[][] generateMatrix(int rows, int cols, int min, int max) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(max - min + 1) + min;
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + "\t");
            }
            System.out.println();
        }
    }

    private static void printArray(int[] array) {
        for (int val : array) {
            System.out.print(val + "\t");
        }
        System.out.println();
    }

    // Метод для вводу з перевіркою
    private static int getIntInput(Scanner scanner, String prompt) {
        int value = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            try {
                value = scanner.nextInt();
                valid = true; // Якщо ввід коректний
            } catch (InputMismatchException e) {
                System.out.println("Помилка: Введіть ціле число.");
                scanner.next(); // Очищення некоректного вводу
            }
        }
        return value;
    }
}
