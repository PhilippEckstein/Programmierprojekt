import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        System.out.println(Arrays.toString(graph.getEdgeHeight()));
        Dijkstra dijkstra = new Dijkstra(graph);
        long distance = dijkstra.oneToOne(0,3, 0.9);
        System.out.println(distance);
    }
}