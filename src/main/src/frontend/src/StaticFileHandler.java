package frontend.src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {
    private final File root;

    public StaticFileHandler(String rootDir){
        this.root = new File(rootDir);
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/map.html";

        if (path.startsWith("/")) path = path.substring(1);

        File file = new File(root, path);

        if (!file.exists()) {

            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
            return;
        }

        byte[] bytes = Files.readAllBytes(file.toPath());

        String mime = "text/html";
        if (path.endsWith(".js")) mime = "application/javascript";
        if (path.endsWith(".css")) mime = "text/css";

        exchange.getResponseHeaders().add("Content-Type", mime);
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.flush();
        os.close();
    }
}
