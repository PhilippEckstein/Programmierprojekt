import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;


    public Dijkstra(Graph graph) {
        this.graph = graph;
    }
    private static final class State {
        private long distance;
        private int node;
        State(long distance, int node)
        {this.distance = distance;this.node = node;}
    }
    private static long edgeCostCm(int lengthCm, int heightCm, double weight){
        double cost = weight * lengthCm + (1 - weight) * heightCm;
        return Math.round(cost);
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
    private static int toIntDistance(long x) {
        return (x >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) x;
    }
    public int oneToOne(int source, int target,double weight) {
        long res = oneToOneLong(source, target, weight);
        return toIntDistance(res);
    }

    /**
     * Calculates the fastest ways to every node from the source node.
     * @param source The int id of the source node.
     * @param weight The double weight, that decides the relation from distance to heightIncrease.
     * with 1.0 using only distance as the weight.
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
    public int[] oneToAll(int source, double weight) {
        long[] distLong = oneToAllLong(source, weight);
        int[] dist = new int[distLong.length];
        for (int i = 0; i < dist.length; i++) {
            dist[i] = toIntDistance(distLong[i]);
        }
        return dist;
    }

}
