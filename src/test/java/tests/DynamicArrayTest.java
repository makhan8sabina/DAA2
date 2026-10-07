package tests;

import metrics.MetricsTracker;
import org.junit.jupiter.api.Test;
import structures.DynamicArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {
    @Test
    void emptyArray() {
        DynamicArray da = new DynamicArray(null);
        assertEquals(0, da.size());
        assertFalse(da.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> da.remove(0));
    }

    @Test
    void singleElement() {
        DynamicArray da = new DynamicArray(null);
        da.add(7);
        assertEquals(1, da.size());
        assertEquals(7, da.get(0));
        assertTrue(da.contains(7));
        assertEquals(7, da.remove(0));
        assertEquals(0, da.size());
        assertFalse(da.contains(7));
    }

    @Test
    void duplicateValues() {
        DynamicArray da = new DynamicArray(null);
        da.add(5);
        da.add(5);
        da.add(5);
        assertTrue(da.contains(5));
        assertEquals(5, da.remove(1));
        assertEquals(2, da.size());
        assertTrue(da.contains(5));
        da.remove(0);
        da.remove(0);
        assertFalse(da.contains(5));
    }

    @Test
    void firstAndLastIndex() {
        DynamicArray da = new DynamicArray(null);
        for (int i = 0; i < 5; i++) da.add(i);
        da.add(0, 100);
        da.add(da.size(), 200);
        assertEquals(100, da.get(0));
        assertEquals(200, da.get(da.size() - 1));
        assertEquals(100, da.remove(0));
        assertEquals(200, da.remove(da.size() - 1));
        assertEquals(5, da.size());
        assertEquals(0, da.get(0));
        assertEquals(4, da.get(4));
    }

    @Test
    void invalidIndices() {
        DynamicArray da = new DynamicArray(null);
        da.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> da.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> da.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.remove(1));
    }

    @Test
    void growthKeepsValues() {
        DynamicArray da = new DynamicArray(null);
        for (int i = 0; i < 1000; i++) da.add(i);
        assertEquals(1000, da.size());
        for (int i = 0; i < 1000; i++) assertEquals(i, da.get(i));
    }

    @Test
    void randomOperationsMatchArrayList() {
        Random rnd = new Random(1);
        DynamicArray da = new DynamicArray(null);
        List<Integer> expected = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int op = rnd.nextInt(4);
            if (op == 0) {
                int x = rnd.nextInt(50);
                da.add(x);
                expected.add(x);
            } else if (op == 1) {
                int idx = rnd.nextInt(expected.size() + 1);
                int x = rnd.nextInt(50);
                da.add(idx, x);
                expected.add(idx, x);
            } else if (op == 2 && !expected.isEmpty()) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.remove(idx).intValue(), da.remove(idx));
            } else {
                int x = rnd.nextInt(50);
                assertEquals(expected.contains(x), da.contains(x));
            }
            assertEquals(expected.size(), da.size());
        }
        for (int i = 0; i < expected.size(); i++) assertEquals(expected.get(i).intValue(), da.get(i));
    }

    @Test
    void countersAreIncremented() {
        MetricsTracker m = new MetricsTracker();
        DynamicArray da = new DynamicArray(m);
        da.add(1);
        da.add(2);
        da.add(3);
        m.reset();
        da.get(1);
        assertEquals(1, m.steps);
        m.reset();
        da.add(0, 9);
        assertEquals(3, m.steps);
        assertEquals(4, m.moves);
        m.reset();
        da.contains(100);
        assertEquals(4, m.comparisons);
    }
}