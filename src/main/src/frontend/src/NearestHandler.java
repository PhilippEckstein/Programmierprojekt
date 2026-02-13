package frontend.src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class NearestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String json = "{\"id\":0,\"lat\":47.270,\"lon\"55.058";

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);

        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
