package tests;
import structures.MinHeap;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {
    @Test
    void testHeapOrderAndExtraction() {
        MinHeap heap = new MinHeap(null);
        int[] data = {5, 3, 8, 1, 2, 7};
        for (int x : data) heap.insert(x);

        int[] sorted = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            sorted[i] = heap.extractMin();
        }

        int[] expected = data.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, sorted);
    }

    @Test
    void testEmptyHeapException() {
        MinHeap heap = new MinHeap(null);
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }
}
