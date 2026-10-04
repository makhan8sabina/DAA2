import os
import pandas as pd
import matplotlib.pyplot as plt

csv_file = "results/results.csv"
output_dir = "results/plots"
os.makedirs(output_dir, exist_ok=True)

if not os.path.exists(csv_file):
    print("ERROR: results/results.csv was not found.")
    raise SystemExit(1)

df = pd.read_csv(csv_file)
for col in ["n", "time_ms", "steps", "moves", "comparisons"]:
    df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)
df["variant"] = df["variant"].fillna("-").astype(str)
df = df[df["n"] > 0]

markers = {"DynamicArray": "o", "MyLinkedList": "s", "MinHeap": "^"}
variant_styles = {"head": "-", "middle": "--", "-": "-"}
sizes = sorted(df["n"].unique())


def plot_workload(workload, metrics, title, filename, by_variant=False):
    sub = df[df["workload"] == workload]
    if sub.empty:
        print("Warning: no data for", workload)
        return

    fig, axes = plt.subplots(1, len(metrics), figsize=(5.5 * len(metrics), 4.5))
    if len(metrics) == 1:
        axes = [axes]
    fig.suptitle(title, fontsize=14, fontweight="bold")

    keys = ["structure", "variant"] if by_variant else ["structure"]
    for ax, (metric, label) in zip(axes, metrics):
        for key, group in sub.groupby(keys):
            group = group.sort_values("n")
            group = group[group[metric] > 0]
            if group.empty:
                continue
            if by_variant:
                structure, variant = key
                name = f"{structure} ({variant})"
            else:
                structure, variant, name = key, "-", key
            ax.plot(
                group["n"], group[metric],
                marker=markers.get(structure, "o"),
                linestyle=variant_styles.get(variant, "-"),
                linewidth=2, markersize=7, alpha=0.85, label=name,
            )
        ax.set_title(label)
        ax.set_xlabel("Number of elements (n)")
        ax.set_ylabel(label)
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_xticks(sizes)
        ax.set_xticklabels([str(int(s)) for s in sizes])
        ax.grid(True, which="both", linestyle="--", alpha=0.5)
        ax.legend()

    plt.tight_layout()
    path = os.path.join(output_dir, filename)
    plt.savefig(path, dpi=300, bbox_inches="tight")
    plt.close()
    print("Saved:", path)


plot_workload("W1", [("time_ms", "Time (ms)"), ("steps", "Steps (count)")],
              "W1 - Random Access", "01_W1_combined.png")
plot_workload("W2", [("time_ms", "Time (ms)"), ("comparisons", "Comparisons (count)"), ("steps", "Steps (count)")],
              "W2 - Search", "02_W2_combined.png")
plot_workload("W3", [("time_ms", "Time (ms)"), ("moves", "Moves (count)"), ("steps", "Steps (count)")],
              "W3 - Insert & Remove", "03_W3_combined.png", by_variant=True)
plot_workload("W4", [("time_ms", "Time (ms)"), ("steps", "Steps (count)"),
                     ("comparisons", "Comparisons (count)"), ("moves", "Moves (count)")],
              "W4 - MinHeap", "04_W4_combined.png")

print("Done")