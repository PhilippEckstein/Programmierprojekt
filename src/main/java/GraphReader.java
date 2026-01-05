import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        CordToTile cordToTile = new CordToTile(Paths.get("C:\\Users\\phili\\Documents\\Programmierprojekt\\data\\srtm"));


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

            double[] lat = graph.getLat();
            double[] lon = graph.getLon();
            int[] heightCm = graph.getHeightCm();

            for (int i = 0; i < numberOfNodes; i++) {
                String line = nextDataLine(br);
                if (line == null) throw new IOException("Error no line to read");
                String[] tokens = line.split("\\s+");
                double currentLat = Double.parseDouble(tokens[2]);
                double currentLon = Double.parseDouble(tokens[3]);
                lat[i] = currentLat;
                lon[i] = currentLon;
                heightCm[i] = cordToTile.heightCmAt(lat[i], lon[i]);
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
            int[] edgeHeight = graph.getEdgeHeight();

            for (int e = 0; e < numberOfEdges; e++) {
                int nodeSource = tempSource[e];
                int idx = cursor[nodeSource]++;
                int nodeDest = tempDestination[e];
                edgeTo[idx] = nodeDest;
                edgeLength[idx] = tempDistance[e];
                int increase = heightCm[nodeDest] - heightCm[nodeSource] ;
                edgeHeight[idx] = Math.max(increase, 0);
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
