package tests;

import org.junit.jupiter.api.Test;
import structures.DynamicArray;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {
    @Test
    void testAddAndGet() {
        DynamicArray da = new DynamicArray(null);
        da.add(10);
        da.add(20);
        assertEquals(10, da.get(0));
        assertEquals(20, da.get(1));
    }

    @Test
    void testInsertAndRemove() {
        DynamicArray da = new DynamicArray(null);
        da.add(1);
        da.add(3);
        da.add(1, 2);
        assertEquals(2, da.get(1));
        assertEquals(3, da.size());
        assertEquals(2, da.remove(1));
        assertEquals(2, da.size());
    }

    @Test
    void testExceptions() {
        DynamicArray da = new DynamicArray(null);
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> da.remove(0));
    }
}