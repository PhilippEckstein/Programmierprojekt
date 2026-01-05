
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Benchmark {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: GraphReaderMain <path-to-graph-file>");
			return;
		}
		// read parameters (parameters are expected in exactly this order)
		String graphPath = args[1];
		double lon = Double.parseDouble(args[3]);
		double lat = Double.parseDouble(args[5]);
		String quePath = args[7];
		int sourceNodeId = Integer.parseInt(args[9]);

		// run benchmarks
		System.out.println("Reading graph file and creating graph data structure (" + graphPath + ")");
		long graphReadStart = System.currentTimeMillis();
		GraphReader reader = new GraphReader(graphPath);
		Graph graph = reader.readData();
		long graphReadEnd = System.currentTimeMillis();
		System.out.println("\tgraph read took " + (graphReadEnd - graphReadStart) + "ms");

		System.out.println("Finding closest node to coordinates " + lon + " " + lat);
		long nodeFindStart = System.currentTimeMillis();
		double[] coords = {0.0, 0.0};




		// TODO: find closest node here and write coordinates into coords
		graph.findClosestNode(coords[0], coords[1]);


		long nodeFindEnd = System.currentTimeMillis();
		System.out.println("\tfinding node took " + (nodeFindEnd - nodeFindStart) + "ms: " + coords[0] + ", " + coords[1]);

		Dijkstra dijkstra = new Dijkstra(graph);
		System.out.println("Running one-to-one Dijkstras for queries in .que file " + quePath);
		long queStart = System.currentTimeMillis();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(quePath))) {
			String currLine;
			while ((currLine = bufferedReader.readLine()) != null) {
				int oneToOneSourceNodeId = Integer.parseInt(currLine.substring(0, currLine.indexOf(" ")));
				int oneToOneTargetNodeId = Integer.parseInt(currLine.substring(currLine.indexOf(" ") + 1, currLine.indexOf(" ", currLine.indexOf(" ") + 1)));
				double oneToOneWeight = Double.parseDouble(currLine.substring(currLine.indexOf(" ", currLine.indexOf(" ") + 1) + 1));
				int oneToOneDistance = (int) dijkstra.oneToOne(oneToOneSourceNodeId, oneToOneTargetNodeId, oneToOneWeight);
				System.out.println(oneToOneDistance);
			}
		} catch (Exception e) {
			System.out.println("Exception...");
			e.printStackTrace();
		}
		long queEnd = System.currentTimeMillis();
		System.out.println("\tprocessing .que file took " + (queEnd - queStart) + "ms");

		System.out.println("Computing one-to-all Dijkstra from node id " + sourceNodeId);
		long oneToAllStart = System.currentTimeMillis();
		// TODO: run one-to-all Dijkstra with weight 1.0 here

		long oneToAllEnd = System.currentTimeMillis();
		System.out.println("\tone-to-all Dijkstra took " + (oneToAllEnd - oneToAllStart) + "ms");

		// ask user for a target node id
		System.out.print("Enter target node id... ");
		int targetNodeId = (new Scanner(System.in)).nextInt();
		int oneToAllDistance = -42;
		// TODO set oneToAllDistance to the distance from sourceNodeId to
		// targetNodeId as computed by the one-to-all Dijkstra
		System.out.println("Distance from " + sourceNodeId + " to " + targetNodeId + " is " + oneToAllDistance);
	}

}
