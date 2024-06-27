package server.requests_processor;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import server.app.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


//TODO replave items with categories
public class CategoriesHandler implements HttpHandler {
    private final Map<String, JSONObject> goods = new HashMap<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst(HttpServer.getHeaderString());

        if (token == null) {
            System.out.println("No token provided");
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        if (!isTokenValid(token)) {
            System.out.println("Invalid token: " + token);
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String id = path.substring(path.lastIndexOf('/') + 1);

        switch (method) {
            case "GET":
                System.out.println("GET " + id);
                handleGet(exchange, id);
                break;
            case "PUT":
                System.out.println("PUT " + id);
                handlePut(exchange);
                break;
            case "POST":
                System.out.println("POST " + id);
                handlePost(exchange, id);
                break;
            case "DELETE":
                System.out.println("DELETE " + id);
                handleDelete(exchange, id);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleGet(HttpExchange exchange, String id) throws IOException {

        //TODO change to call to database
        if (goods.containsKey(id)) {
            JSONObject response = goods.get(id);
            byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } else {
            exchange.sendResponseHeaders(404, -1); // 404 Not Found
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        if (json.has("id") && json.has("name") && json.has("manufacturer") && json.has("price_per_unit")) {
            int price = json.getInt("price");
            if (price < 0) {
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            String id = String.valueOf(json.getInt("id"));
            goods.put(id, json);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, 0); // 201 Created
            exchange.getResponseBody().close();
        } else {
            exchange.sendResponseHeaders(409, -1); // Conflict
        }
    }

    private void handlePost(HttpExchange exchange, String id) throws IOException {
        if (!goods.containsKey(id)) {
            exchange.sendResponseHeaders(404, -1); // 404 Not Found
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        if (json.has("price") && json.getInt("price") < 0) {
            exchange.sendResponseHeaders(409, -1); // Conflict
            return;
        }

        JSONObject existingGood = goods.get(id);
        for (String key : json.keySet()) {
            existingGood.put(key, json.get(key));
        }

        exchange.sendResponseHeaders(204, -1); // 204 No Content
    }

    private void handleDelete(HttpExchange exchange, String id) throws IOException {
        if (goods.remove(id) != null) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content
        } else {
            exchange.sendResponseHeaders(404, -1); // 404 Not Found
        }
    }

    private boolean isTokenValid(String token) {
        try {
            System.out.println("ItemsHandler Hey: " + HttpServer.getSecretKey());
            Jwts.parserBuilder()
                    .setSigningKey(Base64.getDecoder().decode(HttpServer.getSecretKey()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
