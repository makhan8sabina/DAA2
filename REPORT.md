# Assignment 2 Report: In-Memory Workload Engine Performance Analysis

## 1. Theoretical Complexity Analysis

| Structure | Operation | Best Case | Average Case | Worst Case | Space (Auxiliary) | Justification |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| DynamicArray | `get(i)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $O(1)$ | Direct index-based memory address computation. |
| DynamicArray | `add(x)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(n)$ | $O(1)$ | Amortized $O(1)$; worst case occurs when array reallocation (2x) happens. |
| DynamicArray | `add(i, x)` | $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Inserting at tail is $O(1)$; head/middle insertions require shifting elements right. |
| DynamicArray | `remove(i)` | $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Removing last element is $O(1)$; otherwise requires shifting remaining elements left. |
| DynamicArray | `contains(x)`| $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Sequential linear search from index 0 to $size - 1$. |
| MyLinkedList | `get(i)` | $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Requires sequential pointer traversal from head or tail. |
| MyLinkedList | `add(x)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $O(1)$ | Appending to doubly linked list tail via `tail` pointer. |
| MyLinkedList | `add(i, x)` | $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Traversal to index takes $\Theta(n)$; node pointer rewiring takes $O(1)$. |
| MyLinkedList | `remove(i)` | $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Traversal to index takes $\Theta(n)$; node unlinking takes $O(1)$. |
| MyLinkedList | `contains(x)`| $O(1)$ | $\Theta(n)$ | $\Theta(n)$ | $O(1)$ | Sequential node pointer traversal. |
| MinHeap | `insert(x)` | $O(1)$ | $O(\log n)$ | $O(\log n)$ | $O(1)$ | Bubble-up operation along tree height $\log n$. |
| MinHeap | `peekMin()` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $O(1)$ | Direct access to root node at array index 0. |
| MinHeap | `extractMin()`| $O(1)$ | $\Theta(\log n)$ | $\Theta(\log n)$ | $O(1)$ | Root removal followed by bubble-down operation along tree height $\log n$. |

---

## 2. Loop Invariant Proofs

### Proof 1: `DynamicArray.contains(int element)`

* **Loop Statement:** `for (int i = 0; i < size; i++)`
* **Invariant:** At the start of iteration $k$, the target element is not present in the slice `data[0...k-1]`.
* **Initialization:** Before the first iteration ($i = 0$), the slice `data[0...-1]` is empty. The invariant holds vacuously.
* **Maintenance:** Assume the invariant holds at step $i$. If `data[i] != element`, control proceeds to iteration $i + 1$. Since `element` was not in `data[0...i-1]` and `data[i] != element`, it is not in `data[0...i]`. The invariant is preserved.
* **Termination:** The loop terminates either when `data[i] == element` (returning `true`) or when $i = \text{size}$. In the latter case, by the invariant, `element` is not in `data[0...size-1]`, proving returning `false` is correct.
* **Conclusion:** This proves that `contains` correctly returns `true` if and only if the element is present in the dynamic array.

### Proof 2: `MinHeap.bubbleDown(int index)`

* **Loop Statement:** `while (true)` (terminates when heap property holds or leaf reached)
* **Invariant:** For all nodes in the subtree rooted at `index`, the min-heap property ($A[\text{parent}] \le A[\text{child}]$) holds, except possibly at `index` relative to its immediate children.
* **Initialization:** Prior to the first iteration, only the root element placed at `index` (swapped from the last element) may violate the min-heap order relative to its children. All other subtrees strictly maintain the heap property.
* **Maintenance:** At step $k$, the smallest value among `index`, `leftChild`, and `rightChild` is identified. If `index` is not the smallest, it is swapped with the smallest child. This restores the min-heap property at `index`, while pushing any potential violation down to the child position. Thus, the invariant holds for the next iteration.
* **Termination:** The loop terminates when `index` is smaller than or equal to both children, or when `index` becomes a leaf node. At this point, no violations remain in the subtree.
* **Conclusion:** This proves that `extractMin()` successfully restores the binary min-heap invariant throughout the structure in $O(\log n)$ operations.

---

## 3. Workload Performance Discussion

### CPU Cache Locality & Spatial Locality
`DynamicArray` stores data in contiguous memory blocks. Modern CPUs take advantage of **spatial locality** by fetching entire cache lines (64 bytes) into L1/L2/L3 caches. When accessing index `i`, subsequent adjacent elements are already pre-fetched into the cache, resulting in minimal memory access latency.

### Pointer Chasing & Garbage Collection
`MyLinkedList` allocates nodes dynamically on the heap. Accessing linked elements requires following memory addresses (**pointer chasing**), leading to frequent CPU cache misses. Additionally, each node incurs an object header overhead (12–16 bytes per node) and increases GC (Garbage Collector) management overhead, making linked lists noticeably slower even for workloads with comparable operation counts (`steps`).

### Structural Trade-offs
* **`DynamicArray`** is superior for random access (`get(i)`) and general read-heavy workloads.
* **`MyLinkedList`** outperforms arrays when frequent $O(1)$ insertions/removals occur strictly at the boundaries (`head`), as no elements need to be shifted in memory.
* **`MinHeap`** is optimal for priority-based scheduling, allowing $O(1)$ access to the minimal item and $O(\log n)$ insertion/extraction.