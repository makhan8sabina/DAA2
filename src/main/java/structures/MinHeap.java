package structures;

import metrics.MetricsTracker;

public class MinHeap {
    private int[] data;
    private int size;
    private final MetricsTracker metrics;

    public MinHeap(MetricsTracker metrics) {
        this.data = new int[10];
        this.size = 0;
        this.metrics = metrics != null ? metrics : new MetricsTracker();
    }

    public int size() {
        return size;
    }

    public boolean isHeap() {
        for (int i = 1; i < size; i++) {
            if (data[(i - 1) / 2] > data[i]) {
                return false;
            }
        }
        return true;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int[] newData = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                metrics.steps++;
                metrics.moves++;
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    public void insert(int element) {
        ensureCapacity();
        data[size] = element;
        metrics.moves++;
        size++;
        bubbleUp(size - 1);
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;

            metrics.steps += 2;
            metrics.comparisons++;

            if (data[index] < data[parentIndex]) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        metrics.steps++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        metrics.steps++;
        int min = data[0];

        data[0] = data[size - 1];
        metrics.steps++;
        metrics.moves++;

        size--;

        if (size > 0) {
            bubbleDown(0);
        }
        return min;
    }

    private void bubbleDown(int index) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size) {
                metrics.steps += 2;
                metrics.comparisons++;
                if (data[leftChild] < data[smallest]) {
                    smallest = leftChild;
                }
            }

            if (rightChild < size) {
                metrics.steps += 2;
                metrics.comparisons++;
                if (data[rightChild] < data[smallest]) {
                    smallest = rightChild;
                }
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        metrics.steps += 2;
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
        metrics.moves += 2;
    }
}