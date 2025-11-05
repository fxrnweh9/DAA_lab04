package graph.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Generates directed weighted graph datasets in required JSON format.
 * Produces 9 datasets: 3 small, 3 medium, 3 large (files are placed under ./data/).
 * Weight model: "edge" (positive integer weights).
 */
public class DatasetGenerator {

    private static final Random rnd = new Random(42);
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static class JsonEdge {
        public int u, v, w;
        public JsonEdge(int u, int v, int w){ this.u=u; this.v=v; this.w=w; }
    }
    public static class Dataset {
        public boolean directed = true;
        public int n;
        public List<JsonEdge> edges;
        public Integer source;
        public String weight_model = "edge";
        public Dataset(int n){ this.n=n; edges = new ArrayList<>(); source = 0; }
    }

    public static void main(String[] args) throws Exception {
        // small
        makeAndSave("data/small_1.json", generate(nRandom(6,8), 0.12, false));
        makeAndSave("data/small_2.json", generate(nRandom(7,10), 0.18, true)); // one cycle
        makeAndSave("data/small_3.json", generate(nRandom(6,10), 0.28, true)); // two cycles

        // medium
        makeAndSave("data/medium_1.json", generate(nRandom(10,14), 0.08, false));
        makeAndSave("data/medium_2.json", generate(nRandom(12,18), 0.12, true)); // several SCCs
        makeAndSave("data/medium_3.json", generate(nRandom(14,20), 0.20, true)); // denser + multi-SCC

        // large
        makeAndSave("data/large_1.json", generate(nRandom(20,30), 0.04, false)); // sparse
        makeAndSave("data/large_2.json", generate(nRandom(25,40), 0.08, true));  // medium density
        makeAndSave("data/large_3.json", generate(nRandom(30,50), 0.16, true));  // dense-ish
    }

    private static int nRandom(int a, int b){ return a + rnd.nextInt(b - a + 1); }

    /**
     * Generate directed graph dataset.
     * @param n number of vertices
     * @param density target edge probability (0..1) for possible edges
     * @param allowCycles if true, will seed cycles and SCCs
     */
    public static Dataset generate(int n, double density, boolean allowCycles){
        Dataset ds = new Dataset(n);
        ds.source = 0;

        // Create random edges by probability
        for (int u = 0; u < n; u++){
            for (int v = 0; v < n; v++){
                if (u==v) continue;
                if (rnd.nextDouble() < density){
                    int w = rnd.nextInt(9) + 1; // weights 1..9
                    ds.edges.add(new JsonEdge(u,v,w));
                }
            }
        }

        // If we need cycles, force some: create small cycles / strongly connected groups
        if (allowCycles){
            // create between 1 and min(3,n/4) cycles / SCCs seeds
            int groups = Math.min(3, Math.max(1, n/6));
            for (int g = 0; g < groups; g++){
                int size = Math.min(4, Math.max(2, rnd.nextInt(Math.max(2, n/6)) + 2));
                int base = rnd.nextInt(Math.max(1, n - size + 1));
                // connect cycle base..base+size-1 fully in one direction to make SCC
                for (int i = 0; i < size; i++){
                    int a = (base + i) % n;
                    int b = (base + (i+1) % size) % n;
                    // ensure edge exists (if absent, add)
                    if (!edgeExists(ds, a, b)) ds.edges.add(new JsonEdge(a,b, rnd.nextInt(9)+1));
                    // add back edges to create strongly connectedness
                    if (!edgeExists(ds, b, a)) ds.edges.add(new JsonEdge(b,a, rnd.nextInt(9)+1));
                }
            }
        }

        // ensure graph has at least one outgoing edge from source if possible
        if (ds.edges.stream().noneMatch(e -> e.u == ds.source)){
            int v = Math.min(1, n-1);
            ds.edges.add(new JsonEdge(ds.source, v, rnd.nextInt(9)+1));
        }

        // remove duplicate edges (same u,v) keeping first
        List<JsonEdge> unique = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (JsonEdge e : ds.edges){
            long key = ((long)e.u<<32) | (e.v & 0xffffffffL);
            if (!seen.contains(key)){
                unique.add(e); seen.add(key);
            }
        }
        ds.edges = unique;
        return ds;
    }

    private static boolean edgeExists(Dataset ds, int u, int v){
        return ds.edges.stream().anyMatch(e -> e.u == u && e.v == v);
    }

    private static void makeAndSave(String path, Dataset ds) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            G.toJson(ds, fw);
        }
        System.out.println("Wrote " + path + " (n=" + ds.n + ", edges=" + ds.edges.size() + ")");
    }
}
