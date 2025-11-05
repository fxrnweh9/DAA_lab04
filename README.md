DAA Assignment 4 
Olzhas Omerzak
SE - 2403

---

### Run instruction

### Clone the repository

git clone <repository-url>
cd 

Run the application
Open the project in your IDE and run Main.java

---


# Graph Algorithms Report

## 1. Data Summary

| Dataset       | Vertices (n) | Edges | Weight Model | Cycles / SCCs   |
| ------------- | ------------ | ----- | ------------ | --------------- |
| small_1.json  | 6            | 9     | edge (1–9)   | acyclic         |
| small_2.json  | 7–10         |       | edge (1–9)   | 1 cycle         |
| small_3.json  | 6–10         |       | edge (1–9)   | 2 cycles        |
| medium_1.json | 10–14        |       | edge (1–9)   | acyclic         |
| medium_2.json | 12–18        |       | edge (1–9)   | several SCCs    |
| medium_3.json | 14–20        |       | edge (1–9)   | multi-SCC       |
| large_1.json  | 20–30        |       | edge (1–9)   | sparse, acyclic |
| large_2.json  | 25–40        |       | edge (1–9)   | medium density  |
| large_3.json  | 30–50        |       | edge (1–9)   | dense-ish       |

> Weight model: positive integer edge weights (1–9).

---

## 2. Results

### SCC (Tarjan)


### Topological Sort (Kahn)

| Dataset | Topo Order Length | Queue Push+Pop | Time (ms) |
| ------- | ----------------- | -------------- | --------- |
| small_1 | 6                 | 8              | 0.256     |

### DAG Shortest & Longest Paths

| Dataset | Source | Target | Shortest Path Length | Longest Path Length | Relaxations | Time (ms) |
| ------- | ------ | ------ | -------------------- | ------------------- | ----------- | --------- |
| small_1 | 0      | 3      | INF                  | -INF                | 0           | 0.003     |


> Note: INF = unreachable, -INF = no path for longest in DAG.

---

## 3. Analysis

### SCC / Topo / DAG-SP

- **Bottlenecks**:
  - SCC: DFS traversal dominates; large, dense graphs with many cycles increase DFS visits.
  - TopoSort: Queue operations minor, but cycle detection requires full scan.
  - DAG-SP: Relaxations depend on edge count; sparse graphs compute quickly.

- **Effect of structure**:
  - Higher density → more SCCs merged, longer DFS per component.
  - Larger SCCs → TopoSort runs on condensation graph, reducing node count.
  - Sparse DAGs → shortest/longest paths computed quickly; dense DAGs → more relaxations.

---

## 4. Conclusions

- **SCC (Tarjan)**:  
  - Use for detecting strongly connected components in directed graphs.  
  - Efficient for sparse and moderately dense graphs.  

- **Topological Sort (Kahn)**:  
  - Use after SCC condensation to order DAG components.  
  - Detects cycles efficiently; queue operations minimal cost.  

- **DAG Shortest & Longest Paths**:  
  - Use on DAGs only, preferably preprocessed via TopoSort.  
  - Shortest path: project scheduling, dependency analysis.  
  - Longest path (critical path): scheduling, project duration estimation.  

**Practical Recommendations**:

1. First run SCC to compress cycles into DAG (condensation graph).  
2. Apply TopoSort on condensation graph.  
3. Compute DAG shortest/longest paths for scheduling or reachability.  
4. For large graphs, track metrics to identify performance hotspots (DFS visits, relaxations).  
