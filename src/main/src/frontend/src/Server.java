package frontend.src;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;


public class Server {

    private HttpServer server;

    public Server(int port) throws IOException {

        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/nearest", new NearestHandler());
        server.createContext("/api/route", new RouteHandler());
        server.createContext("/", new StaticFileHandler("src/main/src/frontend/web"));

        server.start();
        System.out.println("Server läuft auf http://localhost:8080");
    }
    public static void main(String[] args) throws IOException {
        int port = 8080; // default, optional per args
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Server(port);
    }
}