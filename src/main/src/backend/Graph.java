
public class Graph {
    private final int numberOfNodes;
    private final int numberOfEdges;

    private final double[] lat;
    private final double[] lon;
    private final int[] heightCm;

    private final int[] offset;
    private final int[] edgeTo;

    private final int[] edgeLength;
    private final int[] edgeHeight;


    /**
     * Creates a graph object which stores all relevant data to the graph.
     *
     * @param numberOfNodes number of vertices of the graph
     * @param numberOfEdges of edges of the graph
     */
    public Graph(int numberOfNodes, int numberOfEdges) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
        //System.out.println("New graph created with " + numberOfNodes + " nodes and " + numberOfEdges + " edges.");
        this.lat = new double[numberOfNodes];
        this.lon = new double[numberOfNodes];
        this.heightCm = new int[numberOfNodes];
        this.offset = new int[numberOfNodes + 1];
        this.edgeTo = new int[numberOfEdges];
        this.edgeLength = new int[numberOfEdges];
        this.edgeHeight = new int[numberOfEdges];
    }

    /**
     * Finds the graph's node that is closest to the coordinates given.
     * @param lon The longitude.
     * @param lat The latitude.
     * @return A double[] array with length 2 that has one entry for longitude and one entry for latitude of
     * the node in question.
     */
    public double[] findClosestNode(double lon, double lat) {
        int best = -1;
        double bestApprox = Double.POSITIVE_INFINITY;
        double[] coords = {0.0,0.0};
        double[] gLat = getLat();
        double[] gLon = getLon();
        for (int i = 0; i < numberOfNodes; i++) {
            double dx = gLat[i] - lat;
            double dy = gLon[i] - lon;
            double d2 = Math.sqrt(dx * dx + dy * dy);
            if (d2 < bestApprox) {
                bestApprox = d2;
                best = i;
            }
        }
        coords[0] = gLon[best];
        coords[1] = gLat[best];
        return coords;
    }

    //Getters and setters
    public int getNumberOfNodes() { return numberOfNodes; }
    public int getNumberOfEdges() { return numberOfEdges; }
    public double[] getLat() { return lat; }
    public double[] getLon() { return lon; }
    public int[] getHeightCm() { return heightCm; }

    public int[] getOffset() { return offset; }
    public int[] getEdgeTo() { return edgeTo; }
    public int[] getEdgeLength() { return edgeLength; }
    public int[] getEdgeHeight() { return edgeHeight; }
    public int firstOut(int e) { return offset[e]; }
    public int lastOut(int e) { return offset[e + 1]; }
}
