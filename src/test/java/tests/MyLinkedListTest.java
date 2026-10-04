package tests;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import structures.MyLinkedList;
public class MyLinkedListTest {
    @Test
    void testListOperations() {
        MyLinkedList list = new MyLinkedList(null);
        list.add(10);
        list.add(30);
        list.add(1, 20);

        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
        assertTrue(list.contains(20));
        assertFalse(list.contains(99));

        assertEquals(20, list.remove(1));
        assertEquals(2, list.size());
    }

    @Test
    void testInvalidIndex() {
        MyLinkedList list = new MyLinkedList(null);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }
}
