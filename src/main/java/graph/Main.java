package graph;

import graph.graph.Graph;
import graph.graph.Edge;
import graph.data.DatasetGenerator;
import graph.dagsp.DAGSP;
import graph.scc.TarjanSCC;
import graph.topo.TopoSort;
import graph.metrics.Metrics;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        String file = (args.length == 0) ? "data/small_1.json" : args[0];
        System.out.println("Loading graph from: " + file);

        // --- Load Graph ---
        Graph g = loadGraphFromJson(file);
        System.out.printf("Graph loaded: %d vertices, %d edges%n", g.getN(), g.getNumEdges());
        System.out.println("----------------------------------------------------");

        // --- SCC (Tarjan) ---
        Metrics sccMetrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(g, sccMetrics);
        TarjanSCC.Result sccResult = tarjan.findSCCs();
        printSCC(sccResult, sccMetrics);

        // --- Topological Sort on Condensation DAG ---
        Metrics topoMetrics = new Metrics();
        TopoSort topo = new TopoSort(sccResult.condensation, topoMetrics);
        List<Integer> topoOrder = topo.sort();
        printTopo(topoOrder, sccResult, topoMetrics);

        // --- DAG Shortest/Longest Paths ---
        Metrics spMetrics = new Metrics();
        DAGSP dagsp = new DAGSP(sccResult.condensation, spMetrics);
        int source = 0;
        int target = sccResult.condensation.getN() - 1;

        int[] shortest = dagsp.shortestPaths(source, topoOrder);
        int[] longest = dagsp.longestPaths(source, topoOrder);
        List<Integer> criticalPath = dagsp.reconstructPath(source, target, topoOrder, true);

        printDAGSP(shortest, longest, criticalPath, spMetrics, source, target);
    }

    // ------------------- HELPERS -------------------

    private static Graph loadGraphFromJson(String file) throws IOException {
        // Для простоты используем DatasetGenerator
        DatasetGenerator.Dataset ds = DatasetGenerator.generate(6, 0.2, true);
        Graph g = new Graph(ds.n);
        for (DatasetGenerator.JsonEdge e : ds.edges) {
            g.addEdge(e.u, e.v, e.w);
        }
        return g;
    }

    private static void printSCC(TarjanSCC.Result result, Metrics metrics) {
        System.out.println("\n=== SCC (Tarjan) ===");
        System.out.printf("Found %d SCC(s)\n", result.sccCount);
        for (int i = 0; i < result.sccs.size(); i++) {
            List<Integer> comp = result.sccs.get(i);
            System.out.printf("SCC #%d: %s (size=%d)\n", i, comp, comp.size());
        }
        System.out.println("Condensation DAG nodes: " + result.condensation.getN());
        System.out.printf("Metrics: DFS visits+edges = %d, time = %.3f ms\n",
                metrics.getCounter(), metrics.getElapsedTime() / 1e6);
        System.out.println("----------------------------------------------------");
    }

    private static void printTopo(List<Integer> topoOrder, TarjanSCC.Result sccResult, Metrics metrics) {
        System.out.println("\n=== Topological Sort (Condensation DAG) ===");
        System.out.println("Topological order of components: " + topoOrder);

        // Derived order of original nodes
        List<Integer> derivedOrder = new ArrayList<>();
        for (int comp : topoOrder) {
            derivedOrder.addAll(sccResult.sccs.get(comp));
        }
        System.out.println("Derived order of original nodes after SCC compression: " + derivedOrder);
        System.out.printf("Metrics: queue pushes+pops = %d, time = %.3f ms\n",
                metrics.getCounter(), metrics.getElapsedTime() / 1e6);
        System.out.println("----------------------------------------------------");
    }

    private static void printDAGSP(int[] shortest, int[] longest, List<Integer> criticalPath,
                                   Metrics metrics, int source, int target) {
        System.out.println("\n=== DAG Shortest & Longest Paths ===");
        System.out.printf("Source node: %d, Target node: %d\n", source, target);
        System.out.println("Shortest distances from source: " + Arrays.toString(shortest));
        System.out.println("Longest distances from source: " + Arrays.toString(longest));
        System.out.println("Critical path (longest): " + criticalPath);
        System.out.printf("Metrics: relaxations = %d, time = %.3f ms\n",
                metrics.getCounter(), metrics.getElapsedTime() / 1e6);
        System.out.println("----------------------------------------------------");
    }
}
