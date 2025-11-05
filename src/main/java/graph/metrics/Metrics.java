package graph.metrics;

public class Metrics {
    private long startTime;
    private long endTime;
    private int counter;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public void incrementCounter(String name) {
        counter++;
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public int getCounter() {
        return counter;
    }
}
