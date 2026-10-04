package tests;

import org.junit.jupiter.api.Test;
import structures.MinHeap;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {
    @Test
    void emptyHeapThrows() {
        MinHeap heap = new MinHeap(null);
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void singleElement() {
        MinHeap heap = new MinHeap(null);
        heap.insert(42);
        assertEquals(42, heap.peekMin());
        assertEquals(42, heap.extractMin());
        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void peekMinDoesNotRemove() {
        MinHeap heap = new MinHeap(null);
        heap.insert(5);
        heap.insert(2);
        heap.insert(9);
        assertEquals(2, heap.peekMin());
        assertEquals(2, heap.peekMin());
        assertEquals(3, heap.size());
    }

    @Test
    void duplicateValues() {
        MinHeap heap = new MinHeap(null);
        for (int i = 0; i < 20; i++) heap.insert(3);
        heap.insert(1);
        assertEquals(1, heap.extractMin());
        for (int i = 0; i < 20; i++) assertEquals(3, heap.extractMin());
        assertEquals(0, heap.size());
    }

    @Test
    void heapPropertyAfterEveryOperation() {
        Random rnd = new Random(3);
        MinHeap heap = new MinHeap(null);
        for (int i = 0; i < 500; i++) {
            heap.insert(rnd.nextInt(100));
            assertTrue(heap.isHeap());
        }
        while (heap.size() > 0) {
            heap.extractMin();
            assertTrue(heap.isHeap());
        }
    }

    @Test
    void sortedOutput() {
        Random rnd = new Random(42);
        int n = 10000;
        int[] data = new int[n];
        MinHeap heap = new MinHeap(null);
        for (int i = 0; i < n; i++) {
            data[i] = rnd.nextInt();
            heap.insert(data[i]);
        }
        int[] expected = data.clone();
        Arrays.sort(expected);
        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int v = heap.extractMin();
            assertTrue(v >= prev);
            assertEquals(expected[i], v);
            prev = v;
        }
    }

    @Test
    void mixedOperationsMatchPriorityQueue() {
        Random rnd = new Random(5);
        MinHeap heap = new MinHeap(null);
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        for (int step = 0; step < 5000; step++) {
            if (rnd.nextInt(3) != 0 || expected.isEmpty()) {
                int x = rnd.nextInt(100);
                heap.insert(x);
                expected.add(x);
            } else {
                assertEquals(expected.poll().intValue(), heap.extractMin());
            }
            assertEquals(expected.size(), heap.size());
            if (!expected.isEmpty()) assertEquals(expected.peek().intValue(), heap.peekMin());
            assertTrue(heap.isHeap());
        }
    }
}