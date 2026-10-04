package structures;

import metrics.MetricsTracker;

public class DynamicArray implements IntList {
    private int[] data;
    private int size;
    private final MetricsTracker metrics;

    public DynamicArray(MetricsTracker metrics) {
        this.data = new int[10];
        this.size = 0;
        this.metrics = metrics != null ? metrics : new MetricsTracker();
    }

    public int size() {
        return size;
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

    public void add(int element) {
        ensureCapacity();
        data[size] = element;
        metrics.moves++;
        size++;
    }

    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            metrics.steps++;
            metrics.moves++;
            data[i] = data[i - 1];
        }
        data[index] = element;
        metrics.moves++;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        metrics.steps++;
        int removedValue = data[index];
        for (int i = index; i < size - 1; i++) {
            metrics.steps++;
            metrics.moves++;
            data[i] = data[i + 1];
        }
        size--;
        return removedValue;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        metrics.steps++;
        return data[index];
    }

    public boolean contains(int element) {
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            metrics.comparisons++;
            if (data[i] == element) {
                return true;
            }
        }
        return false;
    }
}