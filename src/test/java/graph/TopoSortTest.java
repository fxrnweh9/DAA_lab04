package graph;

import graph.graph.Graph;
import graph.metrics.Metrics;
import graph.topo.TopoSort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopoSortTest {

    @Test
    public void testSimpleDAG() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);

        TopoSort topo = new TopoSort(g, new Metrics());
        List<Integer> order = topo.sort();

        assertEquals(4, order.size(), "All nodes should be in topological order");
        assertTrue(order.indexOf(0) < order.indexOf(3), "0 must appear before 3");
    }

    @Test
    public void testCycleDetection() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1); // cycle

        TopoSort topo = new TopoSort(g, new Metrics());
        List<Integer> order = topo.sort();

        assertTrue(order.isEmpty(), "Graph with cycle should return empty list");
    }

    @Test
    public void testDisconnectedDAG() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);

        TopoSort topo = new TopoSort(g, new Metrics());
        List<Integer> order = topo.sort();

        assertEquals(3, order.size(), "All nodes must appear in topological order");
        assertTrue(order.indexOf(0) < order.indexOf(1));
    }



    @Test
    public void testEmptyGraph() {
        Graph g = new Graph(0);
        TopoSort topo = new TopoSort(g, new Metrics());
        List<Integer> order = topo.sort();
        assertTrue(order.isEmpty(), "Empty graph should return empty topological order");
    }

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1);
        TopoSort topo = new TopoSort(g, new Metrics());
        List<Integer> order = topo.sort();
        assertEquals(1, order.size(), "Single vertex graph should return list with one element");
        assertEquals(0, order.get(0), "Vertex 0 should be in the order");
    }

}
