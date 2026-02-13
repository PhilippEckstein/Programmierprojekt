package frontend.src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RouteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String geojson = "{ \"type\": \"FeatureCollection\", \"features\": [] }";

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = geojson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
