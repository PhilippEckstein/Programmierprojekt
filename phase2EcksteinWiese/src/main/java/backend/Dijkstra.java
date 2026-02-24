package backend;

import java.util.*;

public class Dijkstra {
    private final Graph graph;

    /**
     * Creates a new instance of the Dijkstra class.
     * @param graph the graph Dijkstra is performed on.
     */
    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    /**
     * State represents an entry for a given node that contains the distance to the source node and the node ID.
     */
    private static final class State {
        private long distance;
        private int node;
        State(long distance, int node) {
            this.distance = distance;
            this.node = node;
        }
    }

    /**
     * Holds ID, latitude and longitude of a node.
     */
    class Node {
        final public int id;
        final public long lat;
        final public long lon;
        public Node(int id, long lat, long lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
    }

    /**
     * Calculates the cost of an edge based on the weight given.
     * @param lengthCm Edge length in cm
     * @param heightCm Edge height difference in cm (= 0 if the actual height difference is negative)
     * @param weight Ranges from 0 to 1 and determines the weighting of length vs. height
     *               1.0: only distance counts. 0.0: only height difference/elevation counts.
     * @return Returns an int that contains the edge costs according to the specified weight.
     */
    private static int edgeCostCm(int lengthCm, int heightCm, double weight) {
        double sum = weight * lengthCm + (1 - weight) * heightCm;
        return (int) sum;
    }

    /**
     * Calculates the lowest distance from one node to another one.
     * @param source The int id of the node that is the source.
     * @param target The int id of the node that is the target.
     * @return Returns an int that is the shortest distance from the source node to the target node.
     */
    public long oneToOneLong(int source, int target, double weight) {
        if (source == target) return 0;
        long[] dist = new long[graph.getNumberOfNodes()];

        Arrays.fill(dist,Long.MAX_VALUE);
        dist[source] = 0L;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0L, source));

        int[] edgesTo = graph.getEdgeTo();
        int[] edgeLength = graph.getEdgeLength();
        int[] edgeHeight = graph.getEdgeHeight();

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.distance != dist[current.node]) continue;
            if (current.node == target) {return current.distance;}
            int node = current.node;

            for (int i = graph.firstOut(node); i < graph.firstOut(node+1); i++) {
                int dest = edgesTo[i];
                long cost = edgeCostCm(edgeLength[i], edgeHeight[i], weight);
                long totalCost = current.distance + cost;
                if (totalCost < dist[dest]) {
                    dist[dest] = totalCost;
                    queue.add(new State(totalCost, dest));

                }
            }
        }
        return Long.MAX_VALUE;
    }

    /**
     * Returns the int max value if the parameter long is above Integer.MAX_VALUE. If not, returns the
     * actual value.
     * @param x long value that is supposed to be reduced if it is above Integer.MAX_VALUE
     * @return Returns an int with the resulting value not exceeding Integer.MAX_VALUE.
     */
    private static int toIntDistance (long x) {
        return (x >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) x;
    }

    /**
     * Determines the costs for the Dijkstra algorithm when going from source to target node, taking weight
     * into account.
     * @param source The source node ID.
     * @param target The target node ID.
     * @param weight Ranges from 0 to 1 and determines the weighting of length vs. height
     *               1.0: only length counts. 0.0: only height counts.
     * @return The resulting costs as int.
     */
    public int oneToOne(int source, int target, double weight) {
        long res = oneToOneLong(source, target, weight);
        return toIntDistance(res);
    }

    /**
     * Calculates the fastest ways to every node from the source node.
     * @param source The int id of the source node.
     * @param weight Ranges from 0 to 1 and determines the weighting of length vs. height
     *               1.0: only length counts. 0.0: only height counts.
     *
     * @return Returns an array of Integer that show the shortest weights from the source node, to every other node in the graph.
     */
    public long[] oneToAllLong(int source,  double weight) {
        long[] dist = new long[graph.getNumberOfNodes()];
        Arrays.fill(dist,Long.MAX_VALUE);
        dist[source] = 0L;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0L, source));

        int[] edgesTo = graph.getEdgeTo();
        int[] edgeLength = graph.getEdgeLength();
        int[] edgeHeight = graph.getEdgeHeight();

        while(!queue.isEmpty()){
            State current = queue.poll();
            int node = current.node;

            if (current.distance != dist[node]) continue;

            for (int i = graph.firstOut(node); i < graph.firstOut(node+1); i++) {
                int dest = edgesTo[i];
                long cost = edgeCostCm(edgeLength[i], edgeHeight[i], weight);
                long totalCost = current.distance + cost;

                if (totalCost < dist[dest]) {
                    dist[dest] = totalCost;
                    queue.add(new State(totalCost, dest));
                }
            }
        }
        return dist;
    }

    /**
     * Calculates oneToAll Dijkstra distances from source node to all other reachable nodes in the graph.
     * @param source The source node ID.
     * @param weight Ranges from 0 to 1 and determines the weighting of length vs. height
     *               1.0: only length counts. 0.0: only height counts.
     * @return Returns an int[] array that contains the distances from the source node for each node of
     * the graph.
     */
    public int[] oneToAll(int source, double weight) {
        long[] distLong = oneToAllLong(source, weight);
        int[] dist = new int[distLong.length];
        for (int i = 0; i < dist.length; i++) {
            dist[i] = toIntDistance(distLong[i]);
        }
        return dist;
    }


    /**
     * Calculates the fastest path from one node to another one.
     * @param source The int id of the node that is the source.
     * @param target The int id of the node that is the target.
     * @return Returns an int that is the shortest distance from the source node to the target node.
     */
    public DijkstraPath oneToOnePath(int source, int target, double weight) {
        DijkstraPath path = new DijkstraPath();
        path.setSourceNode(source);
        path.setDestinationNode(target);
        path.setWeight(weight);


        if (source == target) return path;
        long[] nodeCost = new long[graph.getNumberOfNodes()];
        int[] lastVisited = new int[graph.getNumberOfNodes()]; //Each node that has been visited stores the node in here that is the ancestor in the shortest path to it.
        lastVisited[source] = source;


        Arrays.fill(nodeCost,Long.MAX_VALUE);
        nodeCost[source] = 0L;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0L, source));

        int[] edgesTo = graph.getEdgeTo();
        int[] edgeLength = graph.getEdgeLength();
        int[] edgeHeight = graph.getEdgeHeight();

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.distance != nodeCost[current.node]) continue;
            if (current.node == target) {
                path.setTotalCost(current.distance);
                return getDijkstraPath(source, target, lastVisited, path);
            }

            int node = current.node;

            for (int i = graph.firstOut(node); i < graph.firstOut(node+1); i++) { //Goes through all edges going out of current node
                int dest = edgesTo[i]; // Destination of edge
                long cost = edgeCostCm(edgeLength[i], edgeHeight[i], weight); //Cost of edge
                long totalCost = current.distance + cost;
                if (totalCost < nodeCost[dest]) { //If costs of current node + edge are lower than current cost of destination
                    nodeCost[dest] = totalCost; //Replace costs (/distance) of destination with new value
                    lastVisited[dest] = node; //Sets last visited (= cheapest predecessor) of dest to current node.
                    queue.add(new State(totalCost, dest)); //Add destination to queue as it has now been visited.
                }
            }
        }
        return null;
    }

    private DijkstraPath getDijkstraPath(int source, int target, int[] lastVisited, DijkstraPath path) {
        int currentNode = target;

        Stack<Integer> reversePath = new Stack<Integer>();
        while (currentNode != source) {
            reversePath.add(currentNode);
//                    System.out.println("Adding node "+currentNode+" to stack.");
            currentNode = lastVisited[currentNode];
        }
        reversePath.add(currentNode);
//                System.out.println("Adding node "+currentNode+" to stack.");

        while (!reversePath.isEmpty()) {
            currentNode = reversePath.pop();
            path.addNextNodeToPathFromAToB(currentNode);
//            System.out.println("Removing node "+currentNode+" from stack and adding to reversePath.");
        }

//        for (int i = 0; i < path.pathFromAToB.size(); i++) {
//            System.out.println(path.pathFromAToB.get(i));
//        }

        int[] edgeTo = graph.getEdgeTo();
        int[] edgeLength = graph.getEdgeLength();
        int[] edgeElevation = graph.getEdgeHeight();

        for (int i = 0; i < path.pathFromAToB.size() - 1; i++) {
            int currentNodeId = path.pathFromAToB.get(i);
            int nextNodeId = path.pathFromAToB.get(i+1);
            int j = 0;
            while (edgeTo[graph.firstOut(currentNodeId) + j] != nextNodeId) j++;
            path.setTotalElevationCm(path.getTotalElevationCm() + edgeElevation[graph.firstOut(currentNodeId) + j]);
            path.setTotalDistanceCm(path.getTotalDistanceCm() + edgeLength[graph.firstOut(currentNodeId) + j]);
        }

        System.out.println("Calculation complete. Total costs (taking slider weight into account): " + path.getTotalCost() + ", Total elevation (cm): "  + path.getTotalElevationCm() + ", Total distance (cm): " + path.getTotalDistanceCm());
        return path;
    }

    public class DijkstraPath {
        private LinkedList<Integer> pathFromAToB;
        private int sourceNode;
        private int destinationNode;
        private long totalCost;
        private long totalElevationCm;
        private long totalDistanceCm;
        private double weight;

        public DijkstraPath() {
            pathFromAToB = new LinkedList<>();
        }

        public LinkedList<Integer> getPathFromAToB() {
            return pathFromAToB;
        }

        public void setPathFromAToB(LinkedList<Integer> pathFromAToB) {
            this.pathFromAToB = pathFromAToB;
        }

        public void addNextNodeToPathFromAToB(int nextNode) {
            pathFromAToB.add(nextNode);
        }

        public long getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(long totalCost) {
            this.totalCost = totalCost;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getSourceNode() {
            return sourceNode;
        }

        public void setSourceNode(int sourceNode) {
            this.sourceNode = sourceNode;
        }

        public long getTotalElevationCm() {
            return totalElevationCm;
        }

        public void setTotalElevationCm(long totalElevationCm) {
            this.totalElevationCm = totalElevationCm;
        }

        public int getDestinationNode() {
            return destinationNode;
        }

        public void setDestinationNode(int destinationNode) {
            this.destinationNode = destinationNode;
        }

        public long getTotalDistanceCm() {
            return totalDistanceCm;
        }

        public void setTotalDistanceCm(long totalDistanceCm) {
            this.totalDistanceCm = totalDistanceCm;
        }
    }
}