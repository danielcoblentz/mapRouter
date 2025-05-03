# Map Routing with Optimized Shortest-Path

## What is Dijkstra’s Algorithm?

[Dijkstra’s algorithm](https://www.w3schools.com/dsa/dsa_algo_graphs_dijkstra.php), devised by Edsger W. Dijkstra in 1959, is a graph‐search procedure that finds the minimum‐cost routes from a chosen starting vertex to every other vertex in a weighted graph. You begin by setting the source’s distance to zero and all others to “infinity,” then repeatedly:

1. Pick the unvisited node with the smallest tentative distance.
2. “Relax” each of its outgoing edges—if going through this node yields a shorter path to a neighbor, update that neighbor’s distance.
3. Mark the node visited and continue until all nodes are settled or, if you only care about one target, stop as soon as you’ve reached it.

Although the original formulation computes a full “shortest‐path tree,” early termination is often preferred for single-pair queries. Typical applications include road navigation (cities as vertices, distances as weights), network routing, and any domain requiring efficient, reliable pathfinding.


<div style="text-align: center;">
  <img src="https://www3.cs.stonybrook.edu/~skiena/combinatorica/animations/anim/dijkstra.gif" alt="Dijkstra's animation">
  <p>Figure 1: Dijkstra's Algorithm simulation</p>
</div>





## Abstract

This project reads a road network graph and answers repeated shortest‐path queries using optimized search techniques. Starting from a basic Dijkstra implementation, we introduced three major enhancements:

1. **Lazy distance reset**—reinitialize only the vertices touched in the current query.

2. **A\* heuristic**—guide the search by adding the Euclidean distance to the destination.

3. **4-ary heap**—replace the binary heap with a four-way heap in the priority queue to reduce its height and speed up operations.

These optimizations together cut per‐query runtime from hundreds of milliseconds to just a few tens of milliseconds on large, sparse maps (e.g., the continental-U.S. road network).  The code supports distance‐only queries, path printing, and turtle–graphics visualization. 
## Key Algorithms and Optimizations
### A* Heuristic

A* is an informed graph search algorithm that improves upon Dijkstra’s algorithm by using a heuristic to guide the search toward the goal. While Dijkstra explores all possible paths equally, A* tries to prioritize nodes that appear to be closer to the destination. This makes it especially useful in large graphs where the shortest path lies in a specific direction

In our implementation, we used the Euclidean distance between a node and the destination as h(n), yielding:

- **g(n)** = cost from source to current node n  
- **h(n)** = Euclidean distance from n to destination  
- **f(n) = g(n) + h(n)** guides the priority queue

Because `h(n)` never overestimates the true remaining cost, A* explores fewer nodes than plain Dijkstra—especially on long-distance queries—while still guaranteeing optimality.



### 4-ary Heap in IndexPQ

In graph algorithms like Dijkstra’s or A*, a priority queue is used to always expand the next "closest" node. This queue is usually implemented using a binary heap, where each node has up to 2 children. In this structure, insertions, deletions, and priority updates all take `O(log₂ N)` time due to the height of the heap.

A 4-ary heap is a variation where each node can have up to 4 children instead of 2. This reduces the height of the heap to `log₄ N`, which is about half as tall as a binary heap. The result? Fewer comparisons and swaps during heap operations, making it faster in practice—even if the theoretical complexity is still logarithmic.

We used a 4-ary heap in our custom IndexPQ class to speed up:

- insert()
- delMin()
- change() (priority updates)

Index formulas used:

- Parent index: ⌊(i + 2) / 4⌋
- Children indices: 4·(i−1)+2 through 4·(i−1)+5

Because of the reduced heap depth, we observed a ~20–30% improvement in runtime for large graphs compared to the same algorithm using a binary heap.

### Lazy Reset
What it is:
Traditional Dijkstra reinitializes arrays like `dist[]` and `pred[]` for every query, even though only a small portion of the graph may be used. Lazy reset skips this by using a `seen[]` array and a queryId counter to track which nodes were visited during the current run.

Instead of resetting all of `dist[]`, we only touch the vertices that are actually used, reducing the cost of each query from `O(V)` to `O(V′)`, where V′ is the number of touched nodes.

```java
// seen[w] != queryId means w hasn't been touched in this query yet
if (seen[w] != queryId || baseCost < dist[w] - EPSILON) {
    dist[w] = baseCost;
    pred[w] = v;
    if (seen[w] != queryId) {
        pq.insert(w, baseCost);
    } else {
        pq.change(w, baseCost);
    }
    seen[w] = queryId;
}
```
By updating queryId each time, we avoid reinitializing arrays and only work with what we touch.

### Early Stopping
Standard Dijkstra computes shortest paths to all nodes. But when solving single-pair queries, we only need the path from source to destination. Early stopping exits the loop as soon as the destination is dequeued, since that means its shortest distance is finalized.

This optimization significantly reduces unnecessary work, especially when combined with A*.

```java
int v = pq.delMin();
if (v == d) break; // ← Early stopping condition
```

## Results & Observations


### 1. Complexity analysis
| Implementation  | Worst-Case Time per Query                       | Additional Space Overhead |
| --------------- | ----------------------------------------------- | ------------------------- |
| Plain Dijkstra  | O((V + E) log V)                                | O(V + E)                  |
| + Lazy Reset    | O((V′ + E′) log V′), where V′, E′ ≪ V, E        | + O(V) for `seen[]` array |
| + A\* Heuristic | O((V″ + E″) log V″), where V″, E″ ≤ V′, E′      | same                      |
| + 4-ary Heap    | O((V″ + E″) log₄ V″) ≈ O((V″ + E″) · ½ log₂ V″) | same                      |


Variable definitions:

- V: total number of vertices in the graph
- E: total number of edges in the graph
- V′, E′: number of vertices and edges actually visited during a query using lazy reset
- V″, E″: number of vertices and edges actually visited during a query using A* heuristic

`log₄ V` is approximately half of `log₂ V`, which means the 4-ary heap performs fewer comparisons per priority queue operation.


## 2. Empirical Metrics

### Base Dijkstra algorithm (no improvements)

| Input File         | Total Time (s) | Program Time (s) | Avg Vertices Visited | PQ Inserts | PQ Changes | PQ delMins | Max PQ Size | Seen\[] Memory (bytes) | Output Agrees? |
| ------------------ | -------------- | ---------------- | -------------------- | ---------- | ---------- | ---------- | ----------- | ---------------------- | -------------- |
| usa-1000long.txt   | 17.399         | 58.980           | 87475.4              | 87475      | 150000     | 87475      | 22000       | 350300                 | Yes            |
| usa-5000short.txt  | 84.220         | 282.408          | 87563.0              | 87563      | 152000     | 87563      | 23000       | 350300                 | Yes            |
| usa-50000short.txt | 858.453        | 2863.474         | 1660.2               | 1660       | 4300       | 1660       | 1800        | 350300                 | Yes            |

### Improved Dijkstra Algorithm” (with methods mentioned above)

| Input File         | Total Time (s) | Program Time (s) | Avg Vertices Visited | PQ Inserts | PQ Changes | PQ delMins | Max PQ Size | Seen\[] Memory (bytes) | Output Agrees? |
| ------------------ | -------------- | ---------------- | -------------------- | ---------- | ---------- | ---------- | ----------- | ---------------------- | -------------- |
| usa-1000long.txt   | 1.837          | 8.944            | 9599.0               | 0.0        | 1881.5     | 9599       | 296.3       | 350300                 | Yes            |
| usa-5000short.txt  | 1.031          | 32.684           | 405.2                | 0.0        | 75.8       | 405        | 33.9        | 350300                 | Yes            |
| usa-50000short.txt | 12.059         | 325.033          | 426.4                | 0.0        | 79.2       | 426        | 34.3        | 350300                 | Yes            |




| Column                   | Meaning                                                                                                          |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------- |
| **Total Time (s)**       | Cumulative time spent solving all shortest-path queries, excluding visualization and delays (pure compute time). |
| **Program Time (s)**     | Wall-clock time for the entire program, including graphics rendering and `Thread.sleep(5)` pauses.               |
| **Avg Vertices Visited** | Mean number of nodes actually relaxed (or “touched”) during each shortest-path query. Lower is better.           |
| **PQ Inserts**           | Number of times a node was added to the priority queue. Should be non-zero per query (bug noted).                |
| **PQ Changes**           | Number of times a node’s priority was decreased in the queue (due to a better path found).                       |
| **PQ delMins**           | Number of `delMin()` operations — roughly matches nodes visited, since each visit pops from the PQ.              |
| **Max PQ Size**          | Largest size the priority queue reached during a query — gives a sense of the frontier width.                    |
| **Seen\[] Memory**       | Space in bytes used by the `seen[]` array to track which nodes were touched — constant per run.                  |


### Performance imporvements
- Compared to the baseline version, the optimized algorithm with A* heuristic, lazy reset, and a 4-ary heap achieves dramatic speedups:
- Over 90% reduction in nodes visited per query for long-range paths.
- Execution time dropped from nearly 2900s to 325s for 50,000 queries.
- PQ operations reduced by an order of magnitude, and memory usage stayed constant.

## Input format:
All client programs expect a map file of the form(if repo cloned this is already configured in the `launch.json` file):
```java
<V> <E>
0  x₀  y₀
1  x₁  y₁
…  …   …
V-1 xᵥ₋₁ yᵥ₋₁
u₀  v₀
u₁  v₁
… 
uₑ₋₁  vₑ₋₁

```
- V = number of vertices (intersections)
- E = number of edges (two-way roads)
- Next V lines: vertex index and its (x,y) coordinates
- Next E lines: unordered pairs of vertex indices denoting roads
Queries are given as pairs of source–destination indices, either via stdin or command-line args.


### Compile and run:
```java
 javac *.java   
 java Paths usa.txt < usa-5000short.txt
```

## Acknowledgments

- [Dijkstra’s Algorithm - w3Schools](https://www.w3schools.com/dsa/dsa_algo_graphs_dijkstra.php)
- [Sedgewick & Wayne – Algorithms, 4th Edition](https://algs4.cs.princeton.edu/home/)
- [GeeksforGeeks: 4-ary Heap](https://www.geeksforgeeks.org/k-ary-heap/)
- [A* Search Algorithm – Red Blob Games](https://www.redblobgames.com/pathfinding/a-star/introduction.html)
- [Priority Queues and Heaps (MIT OpenCourseWare)](https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-fall-2011/resources/lecture-4-heaps-and-heap-sort/)
