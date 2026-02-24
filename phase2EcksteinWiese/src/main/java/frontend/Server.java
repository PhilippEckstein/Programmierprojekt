package frontend;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

import backend.*;


public class Server {
    final private HttpServer server;
    final Graph graph;
    public Server(int port) throws IOException {

        server = HttpServer.create(new InetSocketAddress(port), 0);

        final GraphReader graphReader = new GraphReader(getGraphDirectory());
        System.out.println("Initiating graph loading. Server will start once the graph is loaded. This might take a short while.");
        graph = graphReader.readData();
        System.out.println("Graph loading completed. Server is starting.");
        server.start();
        server.createContext("/api/nearest", new NearestHandler(graph));
        server.createContext("/api/route", new RouteHandler(graph));
        server.createContext("/", new StaticFileHandler("src/main/resources/web"));
        System.out.println("Server now running under http://localhost:8080.");
    }

    public static void main(String[] args) throws IOException {
        int port = 8080; // default, optional per args
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Server(port);
    }

    public static String getGraphDirectory() throws IOException {
        InputStream is = Server.class.getClassLoader().getResourceAsStream("config.txt");
        if (is == null) {
            throw new RuntimeException("config.txt not found");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        line = reader.readLine();
        line = reader.readLine();
        if (!line.startsWith("graphFilePath =")) {
            throw new IOException("config.txt is corrupted. Second line must start with 'graphFilePath ='.");
        } else {
            String path = line.substring(15).trim();
            if (path != null && path.length() >= 2 &&
                    path.startsWith("\"") && path.endsWith("\"")) {
                path = path.substring(1, path.length() - 1);
            }
            System.out.println("Determined directory of graph file from config.txt: "+path);
            return path;
        }
    }
}

