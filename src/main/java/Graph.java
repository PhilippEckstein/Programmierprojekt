
public class Graph {
    private final int numberOfNodes;
    private final int numberOfEdges;

    private final double[] lat;
    private final double[] lon;
    private final int[] height;

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
        System.out.println("New graph created with " + numberOfNodes + " nodes and " + numberOfEdges + " edges.");
        this.lat = new double[numberOfNodes];
        this.lon = new double[numberOfNodes];
        this.height = new int[numberOfNodes];
        this.offset = new int[numberOfNodes + 1];
        this.edgeTo = new int[numberOfEdges];
        this.edgeLength = new int[numberOfEdges];
        this.edgeHeight = new int[numberOfEdges];
    }

    public int getNumberOfNodes(){return numberOfNodes;}
    public int getNumberOfEdges(){return numberOfEdges;}
    public double[] getLat() { return lat; }
    public double[] getLon() { return lon; }
    public int[] getHeight() { return height; }

    public int[] getOffset() { return offset; }
    public int[] getEdgeTo() { return edgeTo; }
    public int[] getEdgeLength() { return edgeLength; }
    public int[] getEdgeHeight() { return edgeHeight; }
    public int firstOut(int e){return offset[e];}
    public int lastOut(int e){return offset[e + 1];}
}
