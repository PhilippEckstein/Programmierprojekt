package frontend.src;

import backend.Dijkstra;
import backend.Graph;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class RouteHandler implements HttpHandler {
    final Graph graph;
    public RouteHandler(Graph graph) {
        this.graph = graph;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        InputStream input = exchange.getRequestBody();
        String jsonInput = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Received input for route calculation: " + jsonInput);
        JsonObject jsonObject = gson.fromJson(jsonInput, JsonObject.class);

        double markerALat = Double.parseDouble(jsonObject.get("aLat").toString());
        double markerALon = Double.parseDouble(jsonObject.get("aLon").toString());
        int markerAId = Integer.parseInt(jsonObject.get("aId").toString());
        double markerBLat = Double.parseDouble(jsonObject.get("bLat").toString());
        double markerBLon = Double.parseDouble(jsonObject.get("bLon").toString());
        int markerBId = Integer.parseInt(jsonObject.get("bId").toString());
        double sliderWeight = Double.parseDouble(jsonObject.get("sliderWeight").toString());

        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.DijkstraPath path = dijkstra.oneToOnePath(markerAId, markerBId, sliderWeight);
        


        String geojson = "{ \"type\": \"FeatureCollection\", \"features\": [] }";

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = geojson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
