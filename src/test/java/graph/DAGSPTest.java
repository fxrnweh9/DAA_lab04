package graph;

import graph.dagsp.DAGSP;
import graph.graph.Graph;
import graph.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DAGSPTest {


    @Test
    public void testShortestPaths() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 4);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 7);
        g.addEdge(2, 4, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4);
        DAGSP dagsp = new DAGSP(g, new Metrics());
        int[] dist = dagsp.shortestPaths(0, topoOrder);

        assertEquals(0, dist[0]);
        assertEquals(2, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(9, dist[3]);
        assertEquals(6, dist[4]);
    }

    @Test
    public void testLongestPaths() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 4);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 7);
        g.addEdge(2, 4, 3);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4);
        DAGSP dagsp = new DAGSP(g, new Metrics());
        int[] dist = dagsp.longestPaths(0, topoOrder);

        assertEquals(0, dist[0]);
        assertEquals(2, dist[1]);
        assertEquals(4, dist[2]);
        assertEquals(9, dist[3]);
        assertEquals(7, dist[4]);
    }

    @Test
    public void testReconstructPath() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 3, 4);
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGSP dagsp = new DAGSP(g, new Metrics());
        List<Integer> path = dagsp.reconstructPath(0, 2, topoOrder, false);

        assertEquals(Arrays.asList(0, 1, 2), path, "Shortest path reconstructed correctly");
    }


    @Test
    public void testSingleVertexDAGSP() {
        Graph g = new Graph(1);
        List<Integer> topoOrder = List.of(0);
        DAGSP dagsp = new DAGSP(g, new Metrics());
        int[] distShortest = dagsp.shortestPaths(0, topoOrder);
        int[] distLongest = dagsp.longestPaths(0, topoOrder);

        assertEquals(0, distShortest[0], "Shortest distance to self is 0");
        assertEquals(0, distLongest[0], "Longest distance to self is 0");
    }

    @Test
    public void testEmptyGraphDAGSP() {
        Graph g = new Graph(0);
        DAGSP dagsp = new DAGSP(g, new Metrics());
        List<Integer> topoOrder = List.of();
        int[] distShortest = dagsp.shortestPaths(0, topoOrder);
        int[] distLongest = dagsp.longestPaths(0, topoOrder);

        assertEquals(0, distShortest.length, "Shortest paths array should be empty");
        assertEquals(0, distLongest.length, "Longest paths array should be empty");
    }

}
