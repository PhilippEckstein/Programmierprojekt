import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

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
     * State represents an entry for a given node that also contains the distance to the source node.
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
     * Calculates the cost of an edge based on the weight given.
     * @param lengthCm Edge length in cm
     * @param heightCm Edge height difference in cm (= 0 if the actual height difference is negative)
     * @param weight Ranges from 0 to 1 and determines the weighting of length vs. height
     *               1.0: only length counts. 0.0: only height counts.
     * @return Returns an int that contains the edge costs according to the specified weight.
     */
    private static int edgeCostCm(int lengthCm, int heightCm, double weight) {
        double sum = weight * lengthCm + (1 - weight) * heightCm;
        return (int) sum;
    }

    /**
     * Calculates the fastest way from one node to another one.
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

}
