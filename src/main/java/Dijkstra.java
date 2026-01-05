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

    /**
     * Calculates the fastest way from one node to another one.
     * @param source The int id of the node that is the source.
     * @param target The int id of the node that is the target.
     * @return Returns a long
     */
    public long oneToOne(int source, int target) {
        if (source == target) return 0;
        long[] dist = new long[graph.getNumberOfNodes()];

        Arrays.fill(dist,Long.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.distance));
        queue.add(new State(0L, source));
        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.distance != dist[current.node]) continue;
            if (current.node == target) {return current.distance;}
            int node = current.node;
            int[] edgesTo = graph.getEdgeTo();
            for (int i = graph.firstOut(node); i < graph.firstOut(node+1); i++) {
                int dest = edgesTo[i];
                int distCm = graph.getEdgeLength()[i];
                long currentDist = current.distance + (long) distCm;
                if (currentDist < dist[dest]) {
                    dist[dest] = currentDist;
                    queue.add(new State(currentDist, dest));
                }
            }
        }
        return Long.MAX_VALUE;
    }
    public Graph getGraph() {
        return graph;
    }

}
