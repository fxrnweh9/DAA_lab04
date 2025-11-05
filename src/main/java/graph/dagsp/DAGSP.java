package graph.dagsp;

import graph.graph.Edge;
import graph.graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

/**
 * Implements single-source shortest and longest path algorithms in a Directed Acyclic Graph (DAG).
 * The algorithm assumes that all nodes are processed in topological order.
 * Edge weights are treated as durations (positive integers).
 */
public class DAGSP {
    private final Graph dag;
    private final Metrics metrics;

    /**
     * Constructor for DAGShortestPath.
     *
     * @param dag     a directed acyclic graph
     * @param metrics metrics tracker for algorithm performance
     */
    public DAGSP(Graph dag, Metrics metrics) {
        this.dag = dag;
        this.metrics = metrics;
    }

    /**
     * Computes the shortest path distances from a single source vertex.
     *
     * @param source    the starting vertex
     * @param topoOrder precomputed topological order of the DAG
     * @return an array of shortest path distances
     */
    public int[] shortestPaths(int source, List<Integer> topoOrder) {
        metrics.start();

        int n = dag.getN();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Process vertices in topological order
        for (int u : topoOrder) {
            if (dist[u] == Integer.MAX_VALUE) continue;

            for (Edge edge : dag.getNeighbors(u)) {
                int v = edge.getTo();
                int w = edge.getWeight();
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    metrics.incrementCounter("Relaxations");
                }
            }
        }

        metrics.stop();
        return dist;
    }

    /**
     * Computes the longest path distances from a single source vertex.
     *
     * @param source    the starting vertex
     * @param topoOrder precomputed topological order of the DAG
     * @return an array of longest path distances
     */
    public int[] longestPaths(int source, List<Integer> topoOrder) {
        metrics.start();

        int n = dag.getN();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        dist[source] = 0;

        for (int u : topoOrder) {
            if (dist[u] == Integer.MIN_VALUE) continue;

            for (Edge edge : dag.getNeighbors(u)) {
                int v = edge.getTo();
                int w = edge.getWeight();
                if (dist[v] < dist[u] + w) {
                    dist[v] = dist[u] + w;
                    metrics.incrementCounter("Relaxations");
                }
            }
        }

        metrics.stop();
        return dist;
    }

    /**
     * Reconstructs one optimal path from source to target.
     *
     * @param source     starting vertex
     * @param target     target vertex
     * @param topoOrder  topological order of the DAG
     * @param useLongest if true, reconstructs the longest path; otherwise shortest
     * @return list of vertices representing the optimal path
     */
    public List<Integer> reconstructPath(int source, int target, List<Integer> topoOrder, boolean useLongest) {
        int n = dag.getN();
        int[] dist = useLongest ? longestPaths(source, topoOrder) : shortestPaths(source, topoOrder);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        for (int u : topoOrder) {
            for (Edge edge : dag.getNeighbors(u)) {
                int v = edge.getTo();
                int w = edge.getWeight();

                if (dist[u] != Integer.MAX_VALUE && dist[v] == dist[u] + w) {
                    parent[v] = u;
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        for (int curr = target; curr != -1; curr = parent[curr]) {
            path.add(curr);
        }
        Collections.reverse(path);

        if (path.size() == 1 && path.get(0) != source) return Collections.emptyList();
        return path;
    }

    /**
     * Finds and returns the length of the critical path (longest path in the DAG).
     *
     * @param source    starting vertex
     * @param topoOrder topological order of the DAG
     * @return the length of the critical path
     */
    public int getCriticalPathLength(int source, List<Integer> topoOrder) {
        int[] longest = longestPaths(source, topoOrder);
        int max = Integer.MIN_VALUE;
        for (int d : longest) {
            if (d > max) max = d;
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }
}
