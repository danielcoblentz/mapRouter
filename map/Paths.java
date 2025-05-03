import java.awt.Color;

public class Paths {

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.err.println("Usage: java Paths <graph-file> < <query-file>");
            System.exit(1);
        }

        // overall timer 
        long overallStart = System.currentTimeMillis();

        // 1) load & draw the graph
        In graphIn = new In(args[0]);
        EuclideanGraph G = new EuclideanGraph(graphIn);
        System.err.println("Done reading the graph " + args[0]);

        Turtle.create(1000, 700);
        G.draw();
        Turtle.render();

        Dijkstra dijkstra = new Dijkstra(G);

        long totalVisited = 0;
        long totalTime = 0;
        int  qCount = 0;
        long totalInsert = 0;
        long totalChange = 0;
        long totalDelMin = 0;
        long totalMaxPQSize = 0;

        while (true) {
            int s, d;
            try {
                s = StdIn.readInt();
            } catch (RuntimeException e) {
                break;
            }
            try {
                d = StdIn.readInt();
            } catch (RuntimeException e) {
                break;
            }

            qCount++;
            dijkstra.enableAStar(d);

            // Reset PQ metrics
            IndexPQ.insertCount = 0;
            IndexPQ.changeCount = 0;
            IndexPQ.delMinCount = 0;
            IndexPQ.maxPQSize = 0;

            long t0 = System.currentTimeMillis();
            dijkstra.compute(s, d);
            long t1 = System.currentTimeMillis();

            int visited = dijkstra.visitedCount();
            long elapsed = t1 - t0;
            totalVisited += visited;
            totalTime += elapsed;
            totalInsert += IndexPQ.insertCount;
            totalChange += IndexPQ.changeCount;
            totalDelMin += IndexPQ.delMinCount;
            totalMaxPQSize += IndexPQ.maxPQSize;

            // per-query output
            System.out.println("Query " + qCount +
                               ": visited " + visited +
                               " nodes, time " + elapsed + " ms");
            System.out.println("PQ Ops: insert=" + IndexPQ.insertCount +
                               ", change=" + IndexPQ.changeCount +
                               ", delMin=" + IndexPQ.delMinCount);
            System.out.println("Max PQ Size: " + IndexPQ.maxPQSize);
            System.out.println("Seen[] memory overhead: " +
                               (dijkstra.seenMemoryBytes()) + " bytes");

            dijkstra.showPath(d, s);
            System.out.println();

            // draw and render the path
            Turtle.setColor(Color.red);
            dijkstra.drawPath(s, d);
            Turtle.render();
            Thread.sleep(5); // optional visual delay
        }

        // summary info 
        if (qCount > 0) {
            System.out.println();
            System.out.println("=== Summary over " + qCount + " queries ===");
            System.out.println("Total nodes visited: " + totalVisited);
            System.out.println("Total time         : " + totalTime + " ms");
            System.out.printf("Avg nodes visited  : %.1f%n",
                               (double)totalVisited / qCount);
            System.out.printf("Avg time per query : %.1f ms%n",
                               (double)totalTime    / qCount);
            System.out.printf("Avg inserts        : %.1f%n",
                               (double)totalInsert / qCount);
            System.out.printf("Avg changes        : %.1f%n",
                               (double)totalChange / qCount);
            System.out.printf("Avg delMins        : %.1f%n",
                               (double)totalDelMin / qCount);
            System.out.printf("Avg max PQ size    : %.1f%n",
                               (double)totalMaxPQSize / qCount);
            System.out.println("Seen[] memory per query: " +
                               dijkstra.seenMemoryBytes() + " bytes");
        }

        // overall end & print
        long overallEnd = System.currentTimeMillis();
        System.out.printf("%nTotal program runtime: %.3f s%n",
                          (overallEnd - overallStart) / 1000.0);
    }
}
