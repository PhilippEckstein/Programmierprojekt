public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: GraphReaderMain <path-to-graph-file>");
            return;
        }
        String path = args[0];
        GraphReader reader = new GraphReader(path);
        Graph graph = reader.readData();

        System.out.println("Done");

        Dijkstra dijkstra = new Dijkstra(graph);
        long dist = dijkstra.oneToOne(0,1);
        System.out.println(dist);


    }
}