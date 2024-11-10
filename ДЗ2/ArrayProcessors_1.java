import java.util.concurrent.ThreadLocalRandom;

public class ArrayProcessors_1 {
    public static double[] generateRandomArray(int size, double min, double max) {
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            // Додаємо epsilon для включення верхньої межі max
            array[i] = ThreadLocalRandom.current().nextDouble(min, max + 1e-14);
        }
        return array;
    }

    public static void main(String[] args) {
        int arraySize = 50; // Наприклад, розмір масиву 50 елементів
        double minRange = 99.4;
        double maxRange = 99.5;

        double[] randomArray = generateRandomArray(arraySize, minRange, maxRange);

        // Вивід для перевірки
        for (double num : randomArray) {
            System.out.println(num + " ");
        }
    }
}

