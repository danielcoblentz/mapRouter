/*************************************************************************
 *  Compilation:  javac ShortestPath.java
 *  Execution:    java ShortestPath file < input.txt
 *  Dependencies: EuclideanGraph.java Dijkstra.java In.java StdIn.java Turtle.java
 *
 *  Reads in a map from a file, and two integers s and d from standard input,
 *  and plots the shortest path from s to d using turtle graphics.
 *
 *  % java ShortestPath usa.txt
 * 
 *
 ****************************************************************************/

 public class ShortestPath {

    public static void main(String[] args) {

        Turtle.create(1000, 700);

        // read in the graph from a file
        In graphin = new In(args[0]);
        EuclideanGraph G = new EuclideanGraph(graphin);
        System.err.println("Done reading the graph " + args[0]);
        System.err.println("Enter query pair from stdin");
        G.draw();
    
        // read in the s-d pair from standard input
        Dijkstra dijkstra = new Dijkstra(G);
        int s = StdIn.readInt();
        int d = StdIn.readInt();
        
        // A* test
        long startTime = System.currentTimeMillis();
        dijkstra.enableAStar(d);     // turn on A*
        dijkstra.compute(s, d);      // run the algorithm 
        dijkstra.showPath(d, s);     
        dijkstra.drawPath(s, d);     
        long endTime = System.currentTimeMillis();
        System.out.println("A* runtime: " + (endTime - startTime) + " ms");

        Turtle.render();
    }
}
