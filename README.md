# Performance Analysis of Linear Data Structures and Priority Queue

This project provides an empirical performance analysis comparing `DynamicArray`, `MyLinkedList`, and `MinHeap` across four distinct workloads. Execution time and structural metrics (`steps`, `moves`, `comparisons`) were recorded using a custom Java benchmark suite and visualized via Matplotlib.

## Project Structure

```text
.
├── src/
│   ├── main/java/
│   │   ├── benchmark/     # Benchmark runner & workload configurations
│   │   ├── metrics/       # Operation counters (Steps, Moves, Comparisons)
│   │   └── structures/    # Custom data structures implementations
│   └── test/java/         # Unit tests
├── results/
│   ├── plots/             # Generated performance plots (.png)
│   └── results.csv        # Raw measurement data
├── generate_plots.py      # Python script for automatic plot generation
├── pom.xml                # Maven configuration
├── README.md              # Setup and execution guide
└── REPORT.md              # Detailed empirical analysis report