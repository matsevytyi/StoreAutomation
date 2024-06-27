package server.app;

import server.requests_processor.CategoriesHandler;
import server.requests_processor.ItemsHandler;
import server.requests_processor.LoginHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class HttpServer {

    private static SecretKeyGenerator generator;

    public static String getSecretKey() {
        return generator.getSecretKey();
    }

    public static String getHeaderString() {
        return generator.getHeaderString();
    }

    public static void main(String[] args) throws Exception {

        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8765), 0);

        server.createContext("/login", new LoginHandler());
        server.createContext("/api/item", new ItemsHandler());
        server.createContext("/api/items", new ItemsHandler());
        server.createContext("/api/item/", new ItemsHandler());
        server.createContext("/api/category", new CategoriesHandler());
        server.createContext("/api/category/", new CategoriesHandler());


        ExecutorService executorService = new ThreadPoolExecutor(
                10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );

        generator = new SecretKeyGenerator();

        server.setExecutor(executorService);
        server.start();

        System.out.println("Server key: " + getSecretKey());
    }

}
