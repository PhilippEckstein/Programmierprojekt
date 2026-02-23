package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class GraphReader {
    private final File file;

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
            //System.out.println("File found at: " + f.getAbsolutePath());
            //System.out.println("Graph read initialized");
        }
    }



    /**
     * Uses a BufferedReader to read the next line of a file and returns it if it is relevant.
     *
     * @param br The buffered reader.
     * @return The next relevant line as String. Excludes empty lines and lines starting with #.
     * @throws IOException for I/O issues.
     */
    private static String nextDataLineWithExtraChecks(BufferedReader br) throws IOException {
        while (true) {
            String line = br.readLine();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            return line;
        }
    }

    /**
     * Reads the data from the file and inserts them into a new Graph object.
     *
     * @return Returns a Graph object with all data inserted.
     */
    public Graph readData() {
//        long t0 = System.nanoTime();
        Path graphPath = file.toPath().toAbsolutePath().normalize();
        Path baseDir = graphPath.getParent();
        Path srtmDir = baseDir.resolve("srtm");
        CordToTile cordToTile = new CordToTile(srtmDir);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Node and edge count
            String nodeCount = nextDataLineWithExtraChecks(br);
            int numberOfNodes = Integer.parseInt(nodeCount);
            String edgeCount = nextDataLineWithExtraChecks(br);
            int numberOfEdges = Integer.parseInt(edgeCount);


            // Create graph object
            Graph graph = new Graph(numberOfNodes, numberOfEdges);

//            double t1 = (System.nanoTime() - t0) / 1_000_000_000.0;
//            System.out.println("Finished reading node/edge amount and creating graph. Time elapsed: " + t1 + " s");

            // Inserts latitude and longitude for each node. Calculates height for each node using cordToTile.
            double[] lat = graph.getLat();
            double[] lon = graph.getLon();
            int[] heightCm = graph.getHeightCm();

            for (int i = 0; i < numberOfNodes; i++) {
                String line = br.readLine();

                int p2 = line.indexOf(' ', line.indexOf(' ') + 1);
                int p3 = line.indexOf(' ', p2 + 1);
                int p4 = line.indexOf(' ', p3 + 1);

                lat[i] = Double.parseDouble(line.substring(p2 + 1, p3));
                lon[i] = Double.parseDouble(line.substring(p3 + 1, p4));
                heightCm[i] = cordToTile.heightCmAt(lat[i], lon[i]);
            }


//            double t2 = (System.nanoTime() - t0) / 1_000_000_000.0;
//            System.out.println("Finished reading lon and lat for each node and calculating height. Time elapsed: " + t2 + " s");

            // Edges
            int[] edgeTo = graph.getEdgeTo();
            int[] offset = graph.getOffset();
            int[] edgeHeight = graph.getEdgeHeight();
            int[] edgeLength = graph.getEdgeLength();

            offset[0] = 0;
            int currentOffsetSourceNode = 0;

            for (int e = 0; e < numberOfEdges; e++) {
                String line = br.readLine();

                int p1 = line.indexOf(' ');
                int p2 = line.indexOf(' ', p1 + 1);
                int p3 = line.indexOf(' ', p2 + 1);
                // no need to find p4

                int sourceNode = Integer.parseInt(line, 0, p1, 10);
                int to = Integer.parseInt(line, p1 + 1, p2, 10);
                int length = Integer.parseInt(line, p2 + 1, p3, 10);

                edgeTo[e] = to;
                edgeLength[e] = length;
                edgeHeight[e] = Math.max(heightCm[to] - heightCm[sourceNode], 0);

                while (currentOffsetSourceNode < sourceNode) {
                    currentOffsetSourceNode++;
                    offset[currentOffsetSourceNode] = e;
                }
            }

            while (currentOffsetSourceNode < numberOfNodes) {
                currentOffsetSourceNode++;
                offset[currentOffsetSourceNode] = numberOfEdges;
            }

//            double t3 = (System.nanoTime() - t0) / 1_000_000_000.0;
//            System.out.println("Finished reading edges. Time elapsed: " + t3 + " s");

//            double sec = (System.nanoTime() - t0) / 1_000_000_000.0;
//            System.out.println("Finished reading graph. Time elapsed: " + sec + " s");
            return graph;
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return null;
    }
}