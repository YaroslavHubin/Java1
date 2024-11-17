import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MatrixColumnSumWorkStealing {
    static class ColumnSumTask extends RecursiveTask<Integer> {
        private final int[][] matrix;
        private final int col;
        private final int startRow;
        private final int endRow;

        public ColumnSumTask(int[][] matrix, int col, int startRow, int endRow) {
            this.matrix = matrix;
            this.col = col;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected Integer compute() {
            if (endRow - startRow <= 10) { // Розбиття на підзадачі, коли кількість рядків більша 10
                int sum = 0;
                for (int i = startRow; i < endRow; i++) {
                    sum += matrix[i][col];
                }
                return sum;
            } else { // Якщо рядків менше ніж 10, то просто ділимо масив на дві частини
                int mid = (startRow + endRow) / 2;
                ColumnSumTask task1 = new ColumnSumTask(matrix, col, startRow, mid);
                ColumnSumTask task2 = new ColumnSumTask(matrix, col, mid, endRow);
                task1.fork();
                return task2.compute() + task1.join();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int rows = 0, cols = 0, min = 0, max = 0;
        try {
            System.out.println("Enter number of rows: ");
            rows = scanner.nextInt();

            System.out.println("Enter number of columns: ");
            cols = scanner.nextInt();

            System.out.println("Enter minimum value: ");
            min = scanner.nextInt();

            System.out.println("Enter maximum value: ");
            max = scanner.nextInt();

        } catch (InputMismatchException e) {
            System.out.println("Неправильний ввід даних. Дані можуть бути лише типу Integer.");
            // Якщо ввід некоректний, завершуємо роботу
            return;
        }

        // Перевірка, чи min менше max
        if (min > max) {
            System.out.println("Мінімальне значення не може бути більше максимального, уважно вводьте дані.");
            return;
        }

        int[][] matrix = generateMatrix(rows, cols, min, max);
        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.nanoTime();
        int[] columnSums = new int[cols];
        for (int col = 0; col < cols; col++) {
            columnSums[col] = pool.invoke(new ColumnSumTask(matrix, col, 0, rows)); // Передача ColumnSumTask у пул потоків і виклик метода compute, при використанні pool.invoke
        }
        long endTime = System.nanoTime();

        printMatrix(matrix);
        System.out.println("Column sums (Work Stealing):");
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
}
