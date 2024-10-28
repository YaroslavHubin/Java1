import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) throws Exception {
        int[] array1 = createRandomArray();
        int[] array2 = createRandomArray();
        int[] array3 = createRandomArray();

        System.out.println("Initial data:");
        System.out.println(Arrays.toString(array1));
        System.out.println(Arrays.toString(array2));
        System.out.println(Arrays.toString(array3));

        writeArrayToFile(array1, "array1.txt");
        writeArrayToFile(array2, "array2.txt");
        writeArrayToFile(array3, "array3.txt");

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        int[] processedArray1 = forkJoinPool.invoke(new ProcessArrayTask(array1, TaskType.MULTIPLY_BY_3));
        int[] processedArray2 = forkJoinPool.invoke(new ProcessArrayTask(array2, TaskType.EVEN_ONLY));
        int[] processedArray3 = forkJoinPool.invoke(new ProcessArrayTask(array3, TaskType.RANGE_10_TO_175));

        System.out.println("Result arrays:");
        System.out.println(Arrays.toString(processedArray1));
        System.out.println(Arrays.toString(processedArray2));
        System.out.println(Arrays.toString(processedArray3));

        List<Integer> mergedArray = mergeArrays(processedArray1, processedArray2, processedArray3);

        System.out.println("Merged and sorted array: " + mergedArray);

        forkJoinPool.shutdown();
    }

    private static int[] createRandomArray() {
        Random random = new Random();
        int size = 15 + random.nextInt(11);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1001);
        }
        return array;
    }

    private static void writeArrayToFile(int[] array, String filename) throws IOException {
        Files.write(Paths.get(filename), Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.toList()));
    }

    private static List<Integer> mergeArrays(int[] array1, int[] array2, int[] array3) {
        Set<Integer> set1 = Arrays.stream(array1).boxed().collect(Collectors.toSet());
        Set<Integer> set2 = Arrays.stream(array2).boxed().collect(Collectors.toSet());

        return Arrays.stream(array3)
                .boxed()
                .filter(x -> !set1.contains(x) && !set2.contains(x))
                .sorted()
                .collect(Collectors.toList());
    }

    enum TaskType {
        MULTIPLY_BY_3,
        EVEN_ONLY,
        RANGE_10_TO_175
    }

    static class ProcessArrayTask extends RecursiveTask<int[]> {
        private final int[] array;
        private final TaskType taskType;

        public ProcessArrayTask(int[] array, TaskType taskType) {
            this.array = array;
            this.taskType = taskType;
        }

        @Override
        protected int[] compute() {
            if (array.length <= 2) {
                return processArray(array);
            }

            int mid = array.length / 2;
            ProcessArrayTask firstHalfTask = new ProcessArrayTask(Arrays.copyOfRange(array, 0, mid), taskType);
            ProcessArrayTask secondHalfTask = new ProcessArrayTask(Arrays.copyOfRange(array, mid, array.length), taskType);

            firstHalfTask.fork();
            secondHalfTask.fork();
            int[] firstHalfResult = firstHalfTask.join();
            int[] secondHalfResult = secondHalfTask.join();

            return mergeResults(firstHalfResult, secondHalfResult);
        }

        private int[] processArray(int[] array) {
            switch (taskType) {
                case MULTIPLY_BY_3:
                    return Arrays.stream(array).map(x -> x * 3).toArray();
                case EVEN_ONLY:
                    return Arrays.stream(array).filter(x -> x % 2 == 0).toArray();
                case RANGE_10_TO_175:
                    return Arrays.stream(array).filter(x -> x >= 10 && x <= 175).toArray();
                default:
                    return new int[0];
            }
        }

        private int[] mergeResults(int[] first, int[] second) {
            int[] result = new int[first.length + second.length];
            System.arraycopy(first, 0, result, 0, first.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }
}