/*************************************************************************
 *  Dijkstra's algorithm.
 *
 *************************************************************************/

 import java.awt.Color;

 public class Dijkstra {
     private static double INFINITY = Double.MAX_VALUE;
     private static double EPSILON  = 0.000001;
     
     private boolean useAStar = false;
     private int targetNode = -1;  // only used for A* search (change in shortestpath.java file)
     
     private EuclideanGraph G;
     private double[] dist;
     private int[] pred;
     
     public Dijkstra(EuclideanGraph G) {
         this.G = G;
     }
     
     // enable A* mode by setting the target (goal) node
     public void enableAStar(int destination) {
         useAStar = true;
         targetNode = destination;
     }
     
     // compile the algorithm once and store the result
     public void compute(int s, int d) {
         dijkstra(s, d);
     }
     
     // return the shortest path distance from s to d.
     public double distance(int s, int d) {
         dijkstra(s, d);
         return dist[d];
     }
     
     // Print the shortest path from s to d.
     // (Note that we assume compute(s, d) was already called and pred[] is up to date
     public void showPath(int d, int s) {
         if (pred == null || pred[d] == -1) {
             System.out.println(d + " is unreachable from " + s);
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
     
     // plain Dijkstra's algorithm with optional A* modification (changeable in shortestpath.java)
     private void dijkstra(int s, int d) {
         int V = G.V();
         dist = new double[V];
         pred = new int[V];
         for (int v = 0; v < V; v++) {
             dist[v] = INFINITY;
             pred[v] = -1;
         }
         
         IndexPQ pq = new IndexPQ(V);
         for (int v = 0; v < V; v++) {
             pq.insert(v, dist[v]);
         }
         
         dist[s] = 0.0;
         pred[s] = s;
         pq.change(s, dist[s]);
         
         while (!pq.isEmpty()) {
             int v = pq.delMin();
             // stop when we reach the destination node.
             if (v == d) break;
             
             IntIterator it = G.neighbors(v);
             while (it.hasNext()) {
                 int w = it.next();
                 double baseCost = dist[v] + G.distance(v, w);
                 double fCost;
                 if (useAStar) {
                     // standard A* evaluation: f(n) = g(n) + h(n)
                     fCost = baseCost + G.distance(w, targetNode);
                 } else {
                     fCost = baseCost;
                 }
                 
                 if (baseCost < dist[w] - EPSILON) {
                     dist[w] = baseCost;
                     pred[w] = v;
                     pq.change(w, fCost);
                 }
             }
         }
     }
 }
 