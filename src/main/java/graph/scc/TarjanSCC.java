package graph.scc;

import graph.graph.Edge;
import graph.graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

public class TarjanSCC {

    private final Graph graph;
    private final Metrics metrics;

    private int time;
    private final int[] disc;       // Discovery times
    private final int[] low;        // Low-link values
    private final boolean[] onStack;
    private final Stack<Integer> stack;
    private final int[] sccMap;     // Maps each vertex -> its SCC index
    private final List<List<Integer>> sccs;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        int n = graph.getN();
        this.disc = new int[n];
        this.low = new int[n];
        this.onStack = new boolean[n];
        this.stack = new Stack<>();
        this.sccMap = new int[n];
        this.sccs = new ArrayList<>();
        Arrays.fill(sccMap, -1);
    }

    /**
     * Finds all strongly connected components and builds condensation DAG.
     *
     * @return result object containing SCCs, map, count, condensation DAG, and metrics
     */
    public Result findSCCs() {
        metrics.start();
        time = 0;

        for (int i = 0; i < graph.getN(); i++) {
            if (disc[i] == 0) {
                dfs(i);
            }
        }

        Graph condensation = buildCondensationGraph();
        metrics.stop();

        return new Result(sccs, sccMap, sccs.size(), condensation, metrics);
    }

    private void dfs(int u) {
        metrics.incrementCounter("DFS Visits");
        disc[u] = low[u] = ++time;
        stack.push(u);
        onStack[u] = true;

        for (Edge edge : graph.getAdj().get(u)) {
            metrics.incrementCounter("DFS Edges");
            int v = edge.getV();

            if (disc[v] == 0) { // Not visited
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) { // Back-edge
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // If u is a root of an SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                onStack[node] = false;
                sccMap[node] = sccs.size();
                component.add(node);
                if (node == u) break;
            }
            sccs.add(component);
        }
    }

    /**
     * Builds a condensation graph (DAG) where each node represents one SCC.
     */
    private Graph buildCondensationGraph() {
        Graph condensation = new Graph(sccs.size());
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < graph.getN(); u++) {
            int sccU = sccMap[u];
            for (Edge edge : graph.getAdj().get(u)) {
                int v = edge.getV();
                int sccV = sccMap[v];
                if (sccU != sccV) {
                    String key = sccU + "->" + sccV;
                    if (!addedEdges.contains(key)) {
                        condensation.addEdge(sccU, sccV, 1);
                        addedEdges.add(key);
                    }
                }
            }
        }

        return condensation;
    }

    /**
     * Holds the results of SCC analysis and condensation DAG.
     */
    public static class Result {
        public final List<List<Integer>> sccs;
        public final int[] sccMap;
        public final int sccCount;
        public final Graph condensation;
        public final Metrics metrics;

        public Result(List<List<Integer>> sccs, int[] sccMap, int sccCount,
                      Graph condensation, Metrics metrics) {
            this.sccs = sccs;
            this.sccMap = sccMap;
            this.sccCount = sccCount;
            this.condensation = condensation;
            this.metrics = metrics;
        }

        public void printSummary() {
            System.out.println("Strongly Connected Components (Tarjan):");
            for (int i = 0; i < sccs.size(); i++) {
                System.out.printf("SCC #%d: %s (size=%d)%n",
                        i, sccs.get(i).toString(), sccs.get(i).size());
            }
            System.out.println("\nCondensation Graph has " +
                    condensation.getN() + " nodes.");
        }
    }
}
