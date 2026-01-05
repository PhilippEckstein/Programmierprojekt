import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;


    public Dijkstra(Graph graph) {
        this.graph = graph;
    }
    private static final class State {
        private int distance;
        private int node;
        State(int distance, int node)
        {this.distance = distance;this.node = node;}
    }
    private static int edgeCostCm(int lengthCm, int heightCm, double weight){
        double cost = weight * lengthCm + (1 - weight) * heightCm;
        return (int) Math.round(cost);
    }
    /**
     * Calculates the fastest way from one node to another one.
     * @param source The int id of the node that is the source.
     * @param target The int id of the node that is the target.
     * @return Returns an int that is the shortest distance from the source node to the target node.
     */
    public int oneToOne(int source, int target, double weight) {
        if (source == target) return 0;
        long[] dist = new long[graph.getNumberOfNodes()];

        Arrays.fill(dist,Long.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0, source));

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
                System.out.println(
                        "edge " + node + " -> " + dest +
                                " len=" + edgeLength[i] +
                                " up=" + edgeHeight[i]
                );
                int cost = edgeCostCm(edgeLength[i], edgeHeight[i], weight);
                int totalCost = current.distance + cost;
                if (totalCost < dist[dest]) {
                    dist[dest] = totalCost;
                    queue.add(new State(totalCost, dest));
                }
            }
        }
        return Integer.MAX_VALUE;
    }
    /**
     * Calculates the fastest ways to every node from the source node.
     * @param source The int id of the source node.
     * @param weight The double weight, that decides the relation from distance to heightIncrease.
     * with 1.0 using only distance as the weight.
     *
     * @return Returns an array of Integers that show the shortest weights from the source node, to every other node in the graph.
     */
    public int[] oneToAll(int source,  double weight) {
        int[] dist = new int[graph.getNumberOfNodes()];
        Arrays.fill(dist,Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0, source));

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
                int totalCost = Math.round(current.distance + cost);

                if (totalCost < dist[dest]) {
                    dist[dest] = totalCost;
                    queue.add(new State(totalCost, dest));
                }
            }
        }
        return dist;
    }

}
