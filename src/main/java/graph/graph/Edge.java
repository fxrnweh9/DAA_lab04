package graph.graph;

import java.util.Objects;

/**
 * Represents a directed weighted edge in a graph.
 * Each edge goes from one vertex (from) to another vertex (to)
 * and has an associated integer weight.
 *
 * Used by algorithms: SCC, Topological Sort, and Shortest/Longest Paths.
 */
public class Edge {

    private final int from;
    private final int to;
    private final int weight;


    public Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int getTo() {
        return to;
    }

    public int getV() {
        return to;
    }

    public int getFrom() {
        return from;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("->%d(w=%d)", to, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge e = (Edge) obj;
        return to == e.to && weight == e.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, weight);
    }
}