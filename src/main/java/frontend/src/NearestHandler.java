package frontend.src;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import backend.*;




public class NearestHandler implements HttpHandler {
    final Graph graph;
    public NearestHandler(Graph graph) {
        this.graph = graph;
    }
    class Location {
        double lat;
        double lon;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        InputStream input = exchange.getRequestBody();
        String jsonInput = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        //System.out.println(jsonInput);
        Location location = gson.fromJson(jsonInput, Location.class);
        System.out.println("Searching closest node to: Lat " + location.lat + ", Lon " + location.lon);

        double[] nearestPoint = graph.findClosestNode(location.lon, location.lat);
        System.out.println("Found closest node at: Lat "+nearestPoint[0]+ ", Lon "+nearestPoint[1]);

        String jsonOutput = "{\"id\": "+(int) nearestPoint[2]+", \"lat\": "+nearestPoint[0]+",\"lon\": "+nearestPoint[1]+"}";
        //System.out.println(jsonOutput);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = jsonOutput.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
