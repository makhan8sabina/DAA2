package structures;

import metrics.MetricsTracker;

public class MyLinkedList {
    private static class Node {
        int data;
        Node next;
        Node prev;

        Node(int data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final MetricsTracker metrics;

    public MyLinkedList(MetricsTracker metrics) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.metrics = metrics != null ? metrics : new MetricsTracker();
    }

    public int size() {
        return size;
    }

    public void add(int element) {
        Node newNode = new Node(element);
        if (head == null) {
            head = tail = newNode;
            metrics.moves += 2;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            metrics.moves += 3;
        }
        size++;
    }

    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (index == size) {
            add(element);
            return;
        }
        Node newNode = new Node(element);
        if (index == 0) {
            newNode.next = head;
            if (head != null) head.prev = newNode;
            head = newNode;
            metrics.moves += 3;
        } else {
            Node curr = getNode(index);
            Node prevNode = curr.prev;
            newNode.next = curr;
            newNode.prev = prevNode;
            prevNode.next = newNode;
            curr.prev = newNode;
            metrics.moves += 4;
        }
        size++;
    }

    private Node getNode(int index) {
        Node curr;
        if (index < (size >> 1)) {
            curr = head;
            for (int i = 0; i < index; i++) {
                metrics.steps++;
                curr = curr.next;
            }
        } else {
            curr = tail;
            for (int i = size - 1; i > index; i--) {
                metrics.steps++;
                curr = curr.prev;
            }
        }
        metrics.steps++;
        return curr;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        Node target = getNode(index);
        Node prevNode = target.prev;
        Node nextNode = target.next;

        if (prevNode == null) {
            head = nextNode;
            metrics.moves++;
        } else {
            prevNode.next = nextNode;
            metrics.moves++;
        }

        if (nextNode == null) {
            tail = prevNode;
            metrics.moves++;
        } else {
            nextNode.prev = prevNode;
            metrics.moves++;
        }
        size--;
        return target.data;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return getNode(index).data;
    }

    public boolean contains(int element) {
        Node curr = head;
        while (curr != null) {
            metrics.steps++;
            metrics.comparisons++;
            if (curr.data == element) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }
}