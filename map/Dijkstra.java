/*************************************************************************
 *  Dijkstra's algorithm.
 *
 *************************************************************************/

 import java.awt.Color;

 public class Dijkstra {
     private static final double INFINITY = Double.MAX_VALUE;
     private static final double EPSILON = 0.000001;
 
     private boolean useAStar = false;
     private int targetNode = -1;  // used for A* search
 
     private final EuclideanGraph G;
     private final double[] dist;
     private final int[] pred;
 
     // For resetting only touched vertices per query
     private int[] seen;
     private int queryId = 1;
 
     // to record how many vertices were visited this run
     private int lastVisitedCount;
 
     public Dijkstra(EuclideanGraph G) {
         this.G = G;
         int V  = G.V();
         dist = new double[V];
         pred = new int[V];
         seen = new int[V];  // default 0 = never seen
     }
 
     // enable A* mode by setting the target (goal) node
     public void enableAStar(int destination) {
         useAStar  = true;
         targetNode = destination;
     }
 
     // run the algorithm once (caches pred[])
     public void compute(int s, int d) {
         dijkstra(s, d);
     }
 
     // return number of vertices visited in last compute
     public int visitedCount() {
         return lastVisitedCount;
     }
 
     // return the shortest path distance from s to d
     public double distance(int s, int d) {
         dijkstra(s, d);
         // if d was never touched, treat as unreachable
         return (seen[d] == queryId) ? dist[d] : INFINITY;
     }
 
     // Print the shortest path from s to d.
     public void showPath(int d, int s) {
         // if we never saw d in this query, it’s unreachable
         if (seen[d] != queryId) {
             System.out.println("No path from " + s + " to " + d);
             return;
         }
       
         for (int v = d; v != s; v = pred[v])
             System.out.print(v + "-");
         System.out.println(s);
     }
 
     // draw the shortest path from s to d using Turtle graphics.
     public void drawPath(int s, int d) {
         if (pred == null || pred[d] == -1) return;
         Turtle.setColor(Color.red);
         for (int v = d; v != s; v = pred[v])
             G.point(v).drawTo(G.point(pred[v]));
         Turtle.render();
     }
 
     // optimized Dijkstra's 
     private void dijkstra(int s, int d) {
         queryId++;  // start a new query
         IndexPQ pq = new IndexPQ(G.V());
 
         // initialize only source vertex
         dist[s] = 0.0;
         pred[s] = s;
         seen[s] = queryId;
         pq.insert(s, 0.0);
 
         int visitedCount = 0;     // ← count how many vertices we pop
 
         while (!pq.isEmpty()) {
             int v = pq.delMin();
             visitedCount++;       // ← increment on each visit
 
             if (v == d) break; // stop when hit dest (shortest path found)
 
             for (IntIterator it = G.neighbors(v); it.hasNext(); ) { //LZ reset code
                 int w = it.next();
                 double baseCost = dist[v] + G.distance(v, w);
                 if (useAStar) {
                     // A* heuristic adjustment
                     baseCost += G.distance(w, targetNode)
                               - G.distance(v, targetNode);
                 }
 
                 // if unseen or found shorter path
                 // if seen[w] != queryId, dist[w] and pred[w] may be garbage from an old query,
                // so we enter this block unconditionally the first time:
                 if (seen[w] != queryId || baseCost < dist[w] - EPSILON) {
                    // first touch OR found a better path
                     dist[w] = baseCost;
                     pred[w] = v;
                     if (seen[w] != queryId) {
                         pq.insert(w, baseCost);
                     } else {
                         pq.change(w, baseCost);
                     }
                     seen[w] = queryId;
                 }
             }
         }
 
         lastVisitedCount = visitedCount;
         // reset A* for next call
         useAStar = false;
     }
 }
 