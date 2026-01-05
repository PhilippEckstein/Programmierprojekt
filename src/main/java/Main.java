import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: GraphReaderMain <path-to-graph-file>");
            return;
        }
        String path = args[0];
        GraphReader reader = new GraphReader(path);
        Graph graph = reader.readData();
        System.out.println("Done");
        // System.out.println(Arrays.toString(graph.getEdgeHeight()));
        Dijkstra dijkstra = new Dijkstra(graph);
        int distance = dijkstra.oneToOne(0,3, 0.9);
        //int[] distance = dijkstra.oneToAll(0,0.8);
        System.out.println(distance);
    }
}