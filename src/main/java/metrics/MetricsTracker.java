package metrics;

public class MetricsTracker {
    public long steps = 0;
    public long moves = 0;
    public long comparisons = 0;

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}