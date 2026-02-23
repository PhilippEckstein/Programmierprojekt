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

        double markerALat = jsonObject.get("aLat").getAsDouble();
        double markerALon = jsonObject.get("aLon").getAsDouble();
        int markerAId = jsonObject.get("aId").getAsInt();
        double markerBLat = jsonObject.get("bLat").getAsDouble();;
        double markerBLon = jsonObject.get("bLon").getAsDouble();
        int markerBId = jsonObject.get("bId").getAsInt();
        double sliderWeight = jsonObject.get("sliderWeight").getAsDouble();

        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.DijkstraPath path = dijkstra.oneToOnePath(markerAId, markerBId, sliderWeight);
        JsonObject geo = GeoJsonBuilder.buildRouteGeoJson(
                graph,
                path,
                markerAId, markerALat, markerALon,
                markerBId, markerBLat, markerBLon,
                sliderWeight
        );

        String geoJson= gson.toJson(geo);


        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = geoJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
