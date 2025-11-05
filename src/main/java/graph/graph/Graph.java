package graph.graph;

import java.util.*;

/**
 * Represents a directed weighted graph using adjacency sets.
 * Ensures no duplicate edges and allows efficient iteration.
 *
 * Supports algorithms:
 *  - Strongly Connected Components (Kosaraju/Tarjan)
 *  - Topological Sorting (Kahn/DFS)
 *  - Shortest and Longest Paths in DAG
 */
public class Graph {

    private final int numVertices;
    private int numEdges;
    private final List<Set<Edge>> adj;

    /**
     * Constructs a directed graph with a given number of vertices.
     *
     * @param numVertices number of vertices in the graph
     */
    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.numEdges = 0;
        this.adj = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adj.add(new HashSet<>());
        }
    }

    public int getNumVertices() {
        return numVertices;
    }

    public int getNumEdges() {
        return numEdges;
    }

    /**
     * Adds a directed edge (from -> to) with a given weight.
     * Prevents duplicate edges automatically.
     *
     * @param from    source vertex
     * @param to      destination vertex
     * @param weight  edge weight
     */
    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= numVertices || to < 0 || to >= numVertices)
            throw new IllegalArgumentException("Invalid vertex index");

        if (adj.get(from).add(new Edge(from, to, weight))) {
            numEdges++;
        }
    }

    /**
     * Returns all outgoing edges from a given vertex.
     *
     * @param v vertex index
     * @return set of outgoing edges
     */
    public Set<Edge> getNeighbors(int v) {
        if (v < 0 || v >= numVertices)
            throw new IllegalArgumentException("Invalid vertex index");
        return adj.get(v);
    }

    /**
     * Builds the transpose of this graph (reversed edge directions).
     * Used for Kosaraju's SCC algorithm.
     *
     * @return transposed graph
     */
    public Graph getTranspose() {
        Graph transpose = new Graph(numVertices);
        for (int u = 0; u < numVertices; u++) {
            for (Edge e : adj.get(u)) {
                transpose.addEdge(e.getTo(), u, e.getWeight());
            }
        }
        return transpose;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph adjacency list:\n");
        for (int i = 0; i < numVertices; i++) {
            sb.append(i).append(": ");
            for (Edge e : adj.get(i)) {
                sb.append(e).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getN() {
        return numVertices;
    }

    public List<Set<Edge>> getAdj() {
        return adj;
    }

}

