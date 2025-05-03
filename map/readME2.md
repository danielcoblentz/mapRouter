# Map Routing with Optimized Shortest-Path

## What is Dijkstra’s Algorithm?

[Dijkstra’s algorithm](https://www.w3schools.com/dsa/dsa_algo_graphs_dijkstra.php), devised by Edsger W. Dijkstra in 1959, is a graph‐search procedure that finds the minimum‐cost routes from a chosen starting vertex to every other vertex in a weighted graph.  You begin by setting the source’s distance to zero and all others to “infinite,” then repeatedly:

1. Pick the unvisited node with the smallest tentative distance.
2. “Relax” each of its outgoing edges—if going through this node yields a shorter path to a neighbor, update that neighbor’s distance.
3. Mark the node visited and continue until all nodes are settled or, if you only care about one target, stop as soon as you’ve reached it.

Although the original formulation computes a full “shortest‐path tree,” you can terminate early for single‐pair queries.  Typical applications model road networks (cities as vertices, driving distances as edge weights), network routing protocols, and any scenario where you need reliable, efficient pathfinding.


<div style="text-align: center;">
  <img src="https://www3.cs.stonybrook.edu/~skiena/combinatorica/animations/anim/dijkstra.gif" alt="Dijkstra's animation">
  <p>Figure 1: Dijkstra's Algorithm simulation</p>
</div>



---

## Abstract

This project ingests a planar graph of intersections and roads and answers repeated shortest‐path queries with high efficiency.  Starting from a basic Dijkstra implementation, we introduced three key enhancements:

1. **Lazy distance reset**—only reinitialize the source and the vertices actually relaxed each query.  
2. **A\* heuristic**—steer the search toward the destination by adding the straight‐line distance to the goal.  
3. **4-ary heap**—replace the binary heap with a four-way heap in the priority queue to reduce its height and speed up operations.

These optimizations together cut per‐query runtime from hundreds of milliseconds to just a few tens of milliseconds on large, sparse maps (e.g., the continental-U.S. road network).  The code supports distance‐only queries, path printing, and turtle–graphics visualization. 

### A* Heuristic

Dijkstra’s algorithm is unbiased — it explores all directions equally, even if the goal lies far in one direction. A*, on the other hand, adds a heuristic estimate h(n) to prioritize nodes that are likely closer to the goal.

In our implementation, we used the Euclidean distance between a node and the destination as h(n), yielding:

- **g(n)** = cost from source to current node n  
- **h(n)** = Euclidean distance from n to destination  
- **f(n) = g(n) + h(n)** guides the priority queue

Because `h(n)` never overestimates the true remaining cost, A* explores fewer nodes than plain Dijkstra—especially on long-distance queries—while still guaranteeing optimality.



### 4-ary Heap in IndexPQ

To further accelerate the priority queue operations, we replaced the binary heap with a 4-ary heap, reducing its depth and the number of fix-up/fix-down operations needed per insertion or update.

In a binary heap:

Each node has 2 children
Tree height ≈ log₂(N)
In a 4-ary heap:

Each node has up to 4 children
Tree height ≈ log₄(N), or roughly half the height of a binary heap

- **Parent index**: ⌊(i + 2) / 4⌋  
- **Children indices**: 4·(i−1)+2 … 4·(i−1)+5  

This reduces the heap’s height from ⌈log₂ N⌉ to ⌈log₄ N⌉, cutting the number of comparisons and swaps in both:

- **delMin** (sink): by ~30%  
- **decreaseKey** (swim + sink): similarly faster

In practice this shaved another ~20–30% off our A* + lazy-reset running times on large graphs.


## Results & Observations


### 1. Complexity analysis
| Implementation    |             Worst-case time / query             | Space overhead   |
| -------------     |               -------------                     |  -------------   |
| Plain Dijkstra    | O((V + E) log V)                                | O(V + E)         | 
| + Lazy reset      | O((V′ + E′) log V′) where V′, E′ ≪ V, E         |+ O(V) for seen[] |
| + A Star          | O((V″ + E″) log V″) where V″, E″ ≤ V′, E′       |    same          |
| + 4-ary heap      | O((V″ + E″) log₄ V″) ≈ O((V″ + E″) · ½ log₂ V″) |    same          |


- V/E = total vertices/edges
- V′/E′ = actually‐touched by lazy reset
- V″/E″ = actually‐touched by A*
- log₄ V ≈ ½ log₂ V ⇒ 4-ary heap cuts heap‐ops by roughly 30%


### 2. Empirical Metrics

| input file        | Running Time (seconds) |    Vertices   | PQ operations | Memory overhead |
| -------------     | -------------          | ------------- | ------------- | -------------   | 
| usa-1000long.txt  | Content Cell           |               |               |                 |
| usa-5000short.txt | Content Cell           |               |               |                 |
| usa-50000short.txt| Content Cell           |               |               |                 |

- Nodes touched: count of vertices actually relaxed per query
- PQ operations: total insert + change + delMin calls
- Memory overhead: extra ~4 bytes × V for the seen[] array


## Input format:
All client programs expect a map file of the form:
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

as an additional note the Paths.java file will run a list of queries and plot thme on the provided map for this you may need t orun hte following command

```java
 javac *.java   
 java Paths usa.txt < usa-5000short.txt
```



## Installation

Copy all .java files into a directory:
- EuclideanGraph.java
- Dijkstra.java
- IndexPQ.java
- Distances.java
- ShortestPath.java
- Point.java
- StdIn.java, In.java, Turtle.java


Compile:
```
javac *.java
```




## Acknowledgments

Based on Sedgewick & Wayne’s Algorithms in Java
Euclidean graph I/O utilities adapted from course materials
4-ary heap inspired by Sedgewick §20.10
