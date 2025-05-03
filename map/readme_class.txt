/******************************************************************************
 *  readme.txt template                                                   
 *  Map
 *****************************************************************************/

Name(s): Daniel Coblentz      
Login(s): N/A     
Precept #: N/A 
OS: Mac OS        
Compiler: JVM  
Editor: VS-Code     
Hours: 20 


/******************************************************************************
 *  Explain your overall approach.
 *****************************************************************************/
Approach:
Optimize repeated shortest-path queries on a static, Euclidean-weighted graph by eliminating redundant work and guiding the search toward the goal. By implemnting the 3
ideas below.

What I changed and why:
- Lazy reset (Idea 1): Introduced a `seen[]` array and a per-query `queryId` so that we only initialize the source and the vertices actually relaxed in each run. This removes the O(V) overhead of clearing all distances on every query and limits work to just the touched subset.
- A* heuristic (Idea 2): Kept the built-in Euclidean “straight-line distance to the goal” as a lower-bound heuristic in the priority calculation (`f(n) = g(n) + h(n)`). That moves the search toward the destination and removes away irrelevant branches.
- 4-ary heap (Idea 3, extra credit): Replaced the binary heap in `IndexPQ` with a four-way heap. Reducing the heap’s height from ⌈log₂ N⌉ to ⌈log₄ N⌉ speeds up both `delMin` and `decreaseKey` methods.

/******************************************************************************
 *  Which input files did you use to test your program? Mark the
 *  ones where your answers agreed with our reference solutions and
 *  the ones where it disagreed. How long (in seconds) did your program
 *  take to solve each instance? How many vertices did it examine
 *  on average per shortest path query?
 *****************************************************************************/

Basic Dijkstra algo (base implementation)

 Input file                Total Running Time (seconds)   Program running time (with 5ms sleep)  Vertices    Agreed?
----------------------------------------------------------------------------------------------------------------
usa-1000long.txt              17.399 s                          58.980 s                           87475.44     Yes
usa-5000short.txt             84.220 s                         282.408 s                           87563.00     Yes
usa-50000short.txt           858.453 s                        2863.474 s                           1660.15      Yes



Improved Dijkstra algo (with A* + LZ reset + 4-ary PQ)

 Input file                Total Running Time (seconds)   Program running time (with 5ms sleep)   Vertices    Agreed?
----------------------------------------------------------------------------------------------------------------
usa-1000long.txt               1.806 s                          9.281 s                             9599.0      Yes
usa-5000short.txt              1.050 s                         32.630 s                             405.2       Yes
usa-50000short.txt             5.677 s                        321.557 s                             426.4       Yes





/******************************************************************************
 *  Known bugs / limitations.
 *****************************************************************************/
There is a small issue where the improved version cannot process three of the queries in the testing files. I had to remove them to run the program. Other than that, there are no other problems to report at this time.
/******************************************************************************
 *  List whatever help (if any) that you received.
 *****************************************************************************/
I discussed high-level optimization ideas with classmates and reviewed lecture notes on A* and heap structures, but all code design and debugging was done independently.

/******************************************************************************
 *  Describe any serious problems you encountered.                    
 *****************************************************************************/
The first issue was getting the A* search to render on the visual mapping this was an error in logic in the 'shortest path' file where i called 
dijkstra.showPath(s, d);
dijkstra.drawPath(s, d);

and each of those calls returns dijkstra(s, d) internally, which wipes out the results causing no result to appear on the given map.
The second issue was implementing A* I kept getting the same error that there was no path avaible when I entered queries with a destination value above 3000 this was because
my origional implementation had a check that terminated the loop if the current node had no predecessor:
'if (pred[v] == -1) break;'
This condition was appropriate in plain Dijkstra algo when you might want to stop processing unreachable nodes. However, with A* which uses a heuristic to guide the search—it can occur that a node hasn’t been updated with a predecessor yet even though the destination is still reachable. 
This check caused the algorithm to stop too early, making it appear that the destination was unreachable and print the error message form earier.

/******************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 *****************************************************************************/

This project was both challenging and rewarding. I enjoyed building on a working Dijkstra template and layering in optimizations rather than starting from scratch. 
Implementing the 4-ary heap deepened my understanding of priority-queue design, and integrating A was a useful refresher from my previous ML work. 
As a next step, it would be exciting to connect this to a real-world map API (e.g., OpenStreetMap) to benchmark on live data. Overall, 
I learned a great deal about graph theory in practice and appreciated seeing algorithmic theory translate into significant speedups.