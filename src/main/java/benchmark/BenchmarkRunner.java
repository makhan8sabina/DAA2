package benchmark;

import metrics.MetricsTracker;
import structures.DynamicArray;
import structures.MinHeap;
import structures.MyLinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class BenchmarkRunner {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int RUNS = 5;
    private static final int WARMUP_RUNS = 2;

    public static void main(String[] args) throws IOException {
        File dir = new File("results");
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            // Ровно 8 столбцов без запятой на конце!
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                runW1(writer, n);
                runW2(writer, n);
                runW3(writer, n);
                runW4(writer, n);
            }
        }
        System.out.println("Benchmark completed successfully. File saved to results/results.csv");
    }

    private static void runW1(PrintWriter w, int n) {
        // DynamicArray
        BenchmarkResult rDA = runWithMedian(() -> {
            Random rnd = new Random(42);
            MetricsTracker m = new MetricsTracker();
            DynamicArray da = new DynamicArray(m);
            for (int i = 0; i < n; i++) da.add(rnd.nextInt());

            m.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 10000; i++) da.get(rnd.nextInt(n));
            long timeUs = (System.nanoTime() - start) / 1000;
            return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
        });
        writeRow(w, "W1", "-", "DynamicArray", n, rDA);

        // MyLinkedList
        BenchmarkResult rLL = runWithMedian(() -> {
            Random rnd = new Random(42);
            MetricsTracker m = new MetricsTracker();
            MyLinkedList list = new MyLinkedList(m);
            for (int i = 0; i < n; i++) list.add(rnd.nextInt());

            m.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 10000; i++) list.get(rnd.nextInt(n));
            long timeUs = (System.nanoTime() - start) / 1000;
            return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
        });
        writeRow(w, "W1", "-", "MyLinkedList", n, rLL);
    }

    private static void runW2(PrintWriter w, int n) {
        int[] dataset = new int[n];
        Random rndSetup = new Random(42);
        for (int i = 0; i < n; i++) dataset[i] = rndSetup.nextInt(n * 10);

        int[] queries = new int[1000];
        for (int i = 0; i < 500; i++) queries[i] = dataset[rndSetup.nextInt(n)];
        for (int i = 500; i < 1000; i++) queries[i] = -1 - i;

        // DynamicArray
        BenchmarkResult rDA = runWithMedian(() -> {
            MetricsTracker m = new MetricsTracker();
            DynamicArray da = new DynamicArray(m);
            for (int v : dataset) da.add(v);

            m.reset();
            long start = System.nanoTime();
            for (int q : queries) da.contains(q);
            long timeUs = (System.nanoTime() - start) / 1000;
            return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
        });
        writeRow(w, "W2", "-", "DynamicArray", n, rDA);

        // MyLinkedList
        BenchmarkResult rLL = runWithMedian(() -> {
            MetricsTracker m = new MetricsTracker();
            MyLinkedList list = new MyLinkedList(m);
            for (int v : dataset) list.add(v);

            m.reset();
            long start = System.nanoTime();
            for (int q : queries) list.contains(q);
            long timeUs = (System.nanoTime() - start) / 1000;
            return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
        });
        writeRow(w, "W2", "-", "MyLinkedList", n, rLL);
    }

    private static void runW3(PrintWriter w, int n) {
        String[] variants = {"head", "middle"};

        for (String var : variants) {
            // DynamicArray
            BenchmarkResult rDA = runWithMedian(() -> {
                Random rnd = new Random(42);
                MetricsTracker m = new MetricsTracker();
                DynamicArray da = new DynamicArray(m);
                for (int i = 0; i < n; i++) da.add(rnd.nextInt());

                m.reset();
                long start = System.nanoTime();
                for (int i = 0; i < 1000; i++) {
                    int idx = var.equals("head") ? 0 : da.size() / 2;
                    da.add(idx, rnd.nextInt());
                }
                for (int i = 0; i < 1000; i++) {
                    int idx = var.equals("head") ? 0 : da.size() / 2;
                    da.remove(idx);
                }
                long timeUs = (System.nanoTime() - start) / 1000;
                return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
            });
            writeRow(w, "W3", var, "DynamicArray", n, rDA);

            // MyLinkedList
            BenchmarkResult rLL = runWithMedian(() -> {
                Random rnd = new Random(42);
                MetricsTracker m = new MetricsTracker();
                MyLinkedList list = new MyLinkedList(m);
                for (int i = 0; i < n; i++) list.add(rnd.nextInt());

                m.reset();
                long start = System.nanoTime();
                for (int i = 0; i < 1000; i++) {
                    int idx = var.equals("head") ? 0 : list.size() / 2;
                    list.add(idx, rnd.nextInt());
                }
                for (int i = 0; i < 1000; i++) {
                    int idx = var.equals("head") ? 0 : list.size() / 2;
                    list.remove(idx);
                }
                long timeUs = (System.nanoTime() - start) / 1000;
                return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
            });
            writeRow(w, "W3", var, "MyLinkedList", n, rLL);
        }
    }

    private static void runW4(PrintWriter w, int n) {
        BenchmarkResult rHeap = runWithMedian(() -> {
            Random rnd = new Random(42);
            MetricsTracker m = new MetricsTracker();
            MinHeap heap = new MinHeap(m);

            long start = System.nanoTime();
            for (int i = 0; i < n; i++) heap.insert(rnd.nextInt());
            for (int i = 0; i < n; i++) heap.extractMin();
            long timeUs = (System.nanoTime() - start) / 1000;

            return new BenchmarkResult(timeUs, m.steps, m.moves, m.comparisons);
        });
        writeRow(w, "W4", "-", "MinHeap", n, rHeap);
    }

    private static BenchmarkResult runWithMedian(BenchmarkRunnable task) {
        for (int i = 0; i < WARMUP_RUNS; i++) task.run();

        BenchmarkResult[] results = new BenchmarkResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = task.run();
        }
        Arrays.sort(results, (a, b) -> Long.compare(a.timeUs, b.timeUs));
        return results[RUNS / 2];
    }

    private static void writeRow(PrintWriter w, String workload, String variant, String struct, int n, BenchmarkResult r) {
        double timeMs = r.timeUs / 1000.0;
        // Строго 8 значений через запятую с англ. точкой в числах
        w.println(String.format(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d",
                workload, variant, struct, n, timeMs, r.steps, r.moves, r.comparisons));
    }

    @FunctionalInterface
    interface BenchmarkRunnable {
        BenchmarkResult run();
    }

    static class BenchmarkResult {
        long timeUs, steps, moves, comparisons;
        BenchmarkResult(long timeUs, long steps, long moves, long comparisons) {
            this.timeUs = timeUs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }
    }
}