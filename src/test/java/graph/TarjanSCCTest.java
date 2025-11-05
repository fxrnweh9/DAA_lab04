package graph;

import graph.graph.Graph;
import graph.metrics.Metrics;
import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TarjanSCCTest {


    @Test
    public void testSingleSCC() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1); // cycle

        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        TarjanSCC.Result result = scc.findSCCs();

        assertEquals(1, result.sccCount, "All nodes form a single SCC");
        assertEquals(3, result.sccs.get(0).size(), "SCC should contain all nodes");
    }

    @Test
    public void testMultipleSCCs() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);
        g.addEdge(4, 4, 1);

        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        TarjanSCC.Result result = scc.findSCCs();

        assertEquals(3, result.sccCount, "Graph should have three SCCs");
    }

    @Test
    public void testCondensationGraph() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        TarjanSCC.Result result = scc.findSCCs();

        assertEquals(3, result.condensation.getN(), "Condensation graph nodes = number of SCCs");
    }


    @Test
    public void testEmptyGraphSCC() {
        Graph g = new Graph(0);
        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        TarjanSCC.Result result = scc.findSCCs();
        assertEquals(0, result.sccCount, "Empty graph has 0 SCCs");
        assertTrue(result.sccs.isEmpty(), "SCC list should be empty");
    }

    @Test
    public void testSingleVertexSCC() {
        Graph g = new Graph(1);
        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        TarjanSCC.Result result = scc.findSCCs();
        assertEquals(1, result.sccCount, "Graph with one vertex has 1 SCC");
        assertEquals(1, result.sccs.get(0).size(), "SCC should contain the single vertex");
    }

}
