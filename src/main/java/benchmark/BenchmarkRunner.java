package benchmark;

import metrics.MetricsTracker;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class BenchmarkRunner {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final String[] STRUCTURES = {"DynamicArray", "MyLinkedList"};
    private static final int RUNS = 5;
    private static final int WARMUP_RUNS = 2;
    private static final int W1_QUERIES = 10000;
    private static final int W2_QUERIES = 1000;
    private static final int W3_OPS = 1000;
    private static volatile long sink;

    public static void main(String[] args) throws IOException {
        File dir = new File("results");
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter discard = new PrintWriter(Writer.nullWriter())) {
            for (int i = 0; i < 3; i++) {
                runAll(discard, SIZES[i]);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (int n : SIZES) {
                runAll(writer, n);
            }
        }
        System.out.println("Benchmark completed successfully. File saved to results/results.csv");
    }

    private static void runAll(PrintWriter w, int n) {
        runW1(w, n);
        runW2(w, n);
        runW3(w, n);
        runW4(w, n);
    }

    private static IntList create(String name, MetricsTracker m) {
        return name.equals("DynamicArray") ? new DynamicArray(m) : new MyLinkedList(m);
    }

    private static void runW1(PrintWriter w, int n) {
        for (String name : STRUCTURES) {
            BenchmarkResult r = runWithMedian(() -> {
                Random rnd = new Random(42);
                MetricsTracker m = new MetricsTracker();
                IntList list = create(name, m);
                for (int i = 0; i < n; i++) list.add(rnd.nextInt());

                m.reset();
                long acc = 0;
                long start = System.nanoTime();
                for (int i = 0; i < W1_QUERIES; i++) acc += list.get(rnd.nextInt(n));
                long time = System.nanoTime() - start;
                sink += acc;
                return new BenchmarkResult(time, m);
            });
            writeRow(w, "W1", "-", name, n, r);
        }
    }

    private static void runW2(PrintWriter w, int n) {
        Random rndSetup = new Random(42);
        int[] dataset = new int[n];
        for (int i = 0; i < n; i++) dataset[i] = rndSetup.nextInt(n * 10);

        int[] queries = new int[W2_QUERIES];
        for (int i = 0; i < W2_QUERIES / 2; i++) queries[i] = dataset[rndSetup.nextInt(n)];
        for (int i = W2_QUERIES / 2; i < W2_QUERIES; i++) queries[i] = -1 - i;

        for (String name : STRUCTURES) {
            BenchmarkResult r = runWithMedian(() -> {
                MetricsTracker m = new MetricsTracker();
                IntList list = create(name, m);
                for (int v : dataset) list.add(v);

                m.reset();
                long found = 0;
                long start = System.nanoTime();
                for (int q : queries) {
                    if (list.contains(q)) found++;
                }
                long time = System.nanoTime() - start;
                sink += found;
                return new BenchmarkResult(time, m);
            });
            writeRow(w, "W2", "-", name, n, r);
        }
    }

    private static void runW3(PrintWriter w, int n) {
        String[] variants = {"head", "middle"};
        for (String variant : variants) {
            int idx = variant.equals("head") ? 0 : n / 2;
            for (String name : STRUCTURES) {
                BenchmarkResult r = runWithMedian(() -> {
                    Random rnd = new Random(42);
                    MetricsTracker m = new MetricsTracker();
                    IntList list = create(name, m);
                    for (int i = 0; i < n; i++) list.add(rnd.nextInt());

                    m.reset();
                    long acc = 0;
                    long start = System.nanoTime();
                    for (int i = 0; i < W3_OPS; i++) list.add(idx, rnd.nextInt());
                    for (int i = 0; i < W3_OPS; i++) acc += list.remove(idx);
                    long time = System.nanoTime() - start;
                    sink += acc;
                    return new BenchmarkResult(time, m);
                });
                writeRow(w, "W3", variant, name, n, r);
            }
        }
    }

    private static void runW4(PrintWriter w, int n) {
        Random rnd = new Random(42);
        int[] values = new int[n];
        for (int i = 0; i < n; i++) values[i] = rnd.nextInt();

        BenchmarkResult r = runWithMedian(() -> {
            MetricsTracker m = new MetricsTracker();
            MinHeap heap = new MinHeap(m);
            int[] out = new int[n];

            long start = System.nanoTime();
            for (int i = 0; i < n; i++) heap.insert(values[i]);
            for (int i = 0; i < n; i++) out[i] = heap.extractMin();
            long time = System.nanoTime() - start;

            for (int i = 1; i < n; i++) {
                if (out[i] < out[i - 1]) {
                    throw new IllegalStateException("extractMin order violated at index " + i);
                }
            }
            sink += out[n - 1];
            return new BenchmarkResult(time, m);
        });
        writeRow(w, "W4", "-", "MinHeap", n, r);
    }

    private static BenchmarkResult runWithMedian(Supplier<BenchmarkResult> task) {
        for (int i = 0; i < WARMUP_RUNS; i++) task.get();

        BenchmarkResult[] results = new BenchmarkResult[RUNS];
        for (int i = 0; i < RUNS; i++) {
            results[i] = task.get();
        }
        Arrays.sort(results, (a, b) -> Long.compare(a.timeNs, b.timeNs));
        return results[RUNS / 2];
    }

    private static void writeRow(PrintWriter w, String workload, String variant, String struct, int n, BenchmarkResult r) {
        double timeMs = r.timeNs / 1_000_000.0;
        w.println(String.format(Locale.US, "%s,%s,%s,%d,%.6f,%d,%d,%d",
                workload, variant, struct, n, timeMs, r.steps, r.moves, r.comparisons));
    }

    private static class BenchmarkResult {
        final long timeNs;
        final long steps;
        final long moves;
        final long comparisons;

        BenchmarkResult(long timeNs, MetricsTracker m) {
            this.timeNs = timeNs;
            this.steps = m.steps;
            this.moves = m.moves;
            this.comparisons = m.comparisons;
        }
    }
}