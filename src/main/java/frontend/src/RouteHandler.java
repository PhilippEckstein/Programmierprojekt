package frontend.src;

import backend.Graph;
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
        InputStream input = exchange.getRequestBody();
        String jsonInput = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Received input for route calculation: " + jsonInput);

        String geojson = "{ \"type\": \"FeatureCollection\", \"features\": [] }";

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = geojson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
