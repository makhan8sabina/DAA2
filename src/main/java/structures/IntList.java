package structures;

public interface IntList {
    int size();
    void add(int element);
    void add(int index, int element);
    int remove(int index);
    int get(int index);
    boolean contains(int element);
}
