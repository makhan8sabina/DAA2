package tests;

import metrics.MetricsTracker;
import org.junit.jupiter.api.Test;
import structures.MyLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {
    @Test
    void emptyList() {
        MyLinkedList list = new MyLinkedList(null);
        assertEquals(0, list.size());
        assertFalse(list.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    void singleElement() {
        MyLinkedList list = new MyLinkedList(null);
        list.add(7);
        assertEquals(7, list.get(0));
        assertTrue(list.contains(7));
        assertEquals(7, list.remove(0));
        assertEquals(0, list.size());
        list.add(8);
        assertEquals(8, list.get(0));
    }

    @Test
    void duplicateValues() {
        MyLinkedList list = new MyLinkedList(null);
        list.add(5);
        list.add(5);
        list.add(5);
        assertEquals(5, list.remove(1));
        assertEquals(2, list.size());
        assertTrue(list.contains(5));
        list.remove(0);
        list.remove(0);
        assertFalse(list.contains(5));
    }

    @Test
    void firstAndLastIndex() {
        MyLinkedList list = new MyLinkedList(null);
        for (int i = 0; i < 5; i++) list.add(i);
        list.add(0, 100);
        list.add(list.size(), 200);
        assertEquals(100, list.get(0));
        assertEquals(200, list.get(list.size() - 1));
        assertEquals(100, list.remove(0));
        assertEquals(200, list.remove(list.size() - 1));
        assertEquals(5, list.size());
        assertEquals(0, list.get(0));
        assertEquals(4, list.get(4));
    }

    @Test
    void invalidIndices() {
        MyLinkedList list = new MyLinkedList(null);
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

    @Test
    void randomOperationsMatchArrayList() {
        Random rnd = new Random(2);
        MyLinkedList list = new MyLinkedList(null);
        List<Integer> expected = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int op = rnd.nextInt(4);
            if (op == 0) {
                int x = rnd.nextInt(50);
                list.add(x);
                expected.add(x);
            } else if (op == 1) {
                int idx = rnd.nextInt(expected.size() + 1);
                int x = rnd.nextInt(50);
                list.add(idx, x);
                expected.add(idx, x);
            } else if (op == 2 && !expected.isEmpty()) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.remove(idx).intValue(), list.remove(idx));
            } else {
                int x = rnd.nextInt(50);
                assertEquals(expected.contains(x), list.contains(x));
            }
            assertEquals(expected.size(), list.size());
        }
        for (int i = 0; i < expected.size(); i++) assertEquals(expected.get(i).intValue(), list.get(i));
    }

    @Test
    void countersAreIncremented() {
        MetricsTracker m = new MetricsTracker();
        MyLinkedList list = new MyLinkedList(m);
        for (int i = 0; i < 10; i++) list.add(i);
        m.reset();
        list.add(0, 99);
        assertEquals(3, m.moves);
        m.reset();
        list.remove(0);
        assertTrue(m.steps > 0);
        assertEquals(2, m.moves);
        m.reset();
        list.contains(1000);
        assertEquals(10, m.comparisons);
        assertEquals(10, m.steps);
    }
}