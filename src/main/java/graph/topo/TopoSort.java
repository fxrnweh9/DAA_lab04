package graph.topo;


import graph.graph.Edge;
import graph.graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

/**
 * Implements Kahn's algorithm for Topological Sorting of a Directed Acyclic Graph (DAG).
 *
 * - Works only on DAGs (Directed Acyclic Graphs).
 * - Detects cycles: if a cycle exists, the algorithm stops and returns an empty list.
 * - Measures performance using the Metrics class (time and operation count).
 */
public class TopoSort {

    private final Graph graph;
    private final Metrics metrics;

    /**
     * Constructs a TopologicalSort object with a given graph and metrics tracker.
     *
     * @param graph   Directed Acyclic Graph (DAG) to sort
     * @param metrics Performance metrics tracker
     */
    public TopoSort(Graph graph, Metrics metrics) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Executes Kahn’s algorithm to produce a valid topological ordering of vertices.
     *
     * @return list of vertices in topological order, or an empty list if a cycle is detected
     */
    public List<Integer> sort() {
        metrics.start();

        int n = graph.getN();
        int[] inDegree = new int[n];

        // Step 1: Compute in-degree (number of incoming edges) for each vertex.
        for (int u = 0; u < n; u++) {
            for (Edge edge : graph.getNeighbors(u)) {
                inDegree[edge.getTo()]++;
            }
        }

        // Step 2: Initialize queue with vertices that have in-degree = 0.
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                metrics.incrementCounter("Pushes");
            }
        }

        // Step 3: Process vertices in queue.
        List<Integer> topOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("Pops");
            topOrder.add(u);

            // For each outgoing edge (u -> v), reduce in-degree of v by 1.
            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.getTo();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                    metrics.incrementCounter("Pushes");
                }
            }
        }

        metrics.stop();

        // Step 4: If not all vertices are processed, the graph contains a cycle.
        if (topOrder.size() != n) {
            System.out.println("Cycle detected! Topological sort not possible.");
            return new ArrayList<>();
        }

        return topOrder;
    }
}