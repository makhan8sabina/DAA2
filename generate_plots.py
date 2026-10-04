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
df = df.loc[:, ~df.columns.astype(str).str.contains("^Unnamed")]

numeric_columns = ["n", "time_ms", "steps", "moves", "comparisons"]
for col in numeric_columns:
    df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)

df["variant"] = df["variant"].fillna("-").astype(str)
df = df.dropna(subset=["workload", "structure", "n"])
df = df[df["n"] > 0]

style_map = {
    "head": {"linestyle": "-", "alpha": 0.85},
    "middle": {"linestyle": "--", "alpha": 0.85}
}

def plot_workload_combined(workload_name, metrics, title, filename, group_by_variant=False):
    sub = df[df["workload"] == workload_name]
    if sub.empty:
        print(f"Warning: No data for workload {workload_name}")
        return

    num_metrics = len(metrics)
    fig, axes = plt.subplots(1, num_metrics, figsize=(5.5 * num_metrics, 4.5))
    if num_metrics == 1:
        axes = [axes]

    fig.suptitle(title, fontsize=14, fontweight="bold")

    for idx, (metric, label) in enumerate(metrics):
        ax = axes[idx]

        if group_by_variant:
            grouped = sub.groupby(["structure", "variant"])
            for (structure, variant), group in grouped:
                group = group.sort_values("n")
                style = style_map.get(variant, {"linestyle": "-", "alpha": 1.0})
                ax.plot(
                    group["n"],
                    group[metric],
                    marker="o",
                    linewidth=2,
                    markersize=6,
                    linestyle=style["linestyle"],
                    alpha=style["alpha"],
                    label=f"{structure} ({variant})"
                )
        else:
            grouped = sub.groupby("structure")
            for structure, group in grouped:
                group = group.sort_values("n")
                ax.plot(
                    group["n"],
                    group[metric],
                    marker="o",
                    linewidth=2,
                    markersize=6,
                    label=structure
                )

        ax.set_title(label, fontsize=11)
        ax.set_xlabel("Number of Elements (n)")
        ax.set_ylabel(label)
        ax.set_xscale("log")

        has_zeros = (sub[metric] <= 0).any()
        if has_zeros or metric == "time_ms":
            min_val = sub[sub[metric] > 0][metric].min() if (sub[metric] > 0).any() else 0.001
            ax.set_yscale("log")
            ax.set_ylim(bottom=max(0.0001, min_val * 0.5))
        else:
            ax.set_yscale("log")

        ax.grid(True, which="both", linestyle="--", alpha=0.5)
        ax.legend()

    plt.tight_layout()
    path = os.path.join(output_dir, filename)
    plt.savefig(path, dpi=300, bbox_inches="tight")
    plt.close()
    print("Saved:", path)

plot_workload_combined(
    "W1",
    [("time_ms", "Execution Time (ms)"), ("steps", "Steps")],
    "W1 - Random Access Metrics",
    "01_W1_combined.png"
)

plot_workload_combined(
    "W2",
    [("time_ms", "Execution Time (ms)"), ("comparisons", "Comparisons"), ("steps", "Steps")],
    "W2 - Search Metrics",
    "02_W2_combined.png"
)

plot_workload_combined(
    "W3",
    [("time_ms", "Execution Time (ms)"), ("moves", "Moves"), ("steps", "Steps")],
    "W3 - Insert & Remove Metrics",
    "03_W3_combined.png",
    group_by_variant=True
)

plot_workload_combined(
    "W4",
    [("time_ms", "Execution Time (ms)"), ("steps", "Steps"), ("comparisons", "Comparisons"), ("moves", "Moves")],
    "W4 - MinHeap Metrics",
    "04_W4_combined.png"
)

print("\nALL 4 COMBINED PLOTS CREATED SUCCESSFULLY!")