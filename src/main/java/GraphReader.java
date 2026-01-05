import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class GraphReader  {
    private File file;
    private int numberOfNodes;
    private int numberOfEdges;

    /**
     * Constructor of GraphReader
     *
     * @param name The path of the file that should be read.
     */

    public GraphReader(String name) {
        File f = new File(name);
        this.file = f;
        if (!f.isFile()) {
            System.err.println("File not found");
        } else {
            System.out.println("File found at: " + f.getAbsolutePath());
            System.out.println("Graph read initialized");
        }
    }

    private static String nextDataLine(BufferedReader br) throws IOException {
        while(true) {
            String line = br.readLine();
            if (line == null) {return null;}
            line = line.trim();
            if (line.isEmpty()) {continue;}
            if (line.startsWith("#")) {continue;}
            return line;
        }
    }
    /**
     * Reads the data of the file from the given past.
     */

    public Graph readData(){
        long t0 = System.nanoTime();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String nodeCount = nextDataLine(br);
            if (nodeCount == null) throw new IOException("Missing node count");
            numberOfNodes = Integer.parseInt(nodeCount);
            String edgeCount = nextDataLine(br);
            if (edgeCount == null) {
                throw new IOException("Missing edge count");
            }
            numberOfEdges = Integer.parseInt(edgeCount);
            Graph graph = new Graph(numberOfNodes, numberOfEdges);
            double[] lat =graph.getLat();
            double[] lon = graph.getLon();
            for (int i = 0; i < numberOfNodes; i++) {
                String line = nextDataLine(br);
                if (line == null) throw new IOException("Error no line to read");
                String[] tokens = line.split("\\s+");
                lat[i] = Double.parseDouble(tokens[2]);
                lon[i] = Double.parseDouble(tokens[3]);
            }

            int[] tempSource = new int[numberOfEdges];
            int[] tempDestination = new int[numberOfEdges];
            int[] tempDistance = new int[numberOfEdges];
            int[] outDeg = new int[numberOfNodes];
            for (int e = 0; e < numberOfEdges; e++) {
                String line = nextDataLine(br);
                if (line == null)  throw new IOException("Error no line to read");
                String[] tokens = line.split("\\s+");
                int sourceNode = Integer.parseInt(tokens[0]);
                tempSource[e] = sourceNode;
                outDeg[sourceNode]++;
                tempDestination[e] = Integer.parseInt(tokens[1]);
                tempDistance[e] = Integer.parseInt(tokens[2]);
            }

            int[] offset = graph.getOffset();
            offset[0] = 0;
            for (int i = 0; i < numberOfNodes; i++) {
                offset[i+1] = offset[i] + outDeg[i];
            }
            int[] cursor = Arrays.copyOf(graph.getOffset(), numberOfNodes);
            int[] edgeTo = graph.getEdgeTo();
            int[] edgeLength = graph.getEdgeLength();

            for (int e = 0; e < numberOfEdges; e++) {
                int u = tempSource[e];
                int idx = cursor[u]++;
                edgeTo[idx] = tempDestination[e];
                edgeLength[idx] = tempDistance[e];
            }
            double sec = (System.nanoTime() - t0) / 1_000_000_000.0;
            System.out.println("Finished reading. Time elapsed: " + sec + " s");
            return graph;
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
        }
        return null;
    }

}
