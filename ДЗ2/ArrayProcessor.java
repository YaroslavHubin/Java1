import java.util.concurrent.ThreadLocalRandom;

public class ArrayProcessor {

    public static double[] generateRandomArray(int size, double min, double max) {
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = ThreadLocalRandom.current().nextDouble(min, max + 1e-14);
        }
        return array;
    }
}
