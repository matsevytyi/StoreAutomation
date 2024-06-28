package server.requests_processor;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.json.JSONArray;
import org.json.JSONObject;
import server.app.HttpServer;
import server.database_access.DAO;
import server.database_access.DoConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;

public class CategoriesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst(HttpServer.getHeaderString());

        if (token == null) {
            System.out.println("No token provided");
            exchange.sendResponseHeaders(401, -1);
            return;
        }

        if (!isTokenValid(token)) {
            System.out.println("Invalid token: " + token);
            exchange.sendResponseHeaders(401, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String id = path.substring(path.lastIndexOf('/') + 1);

        switch (method) {
            case "GET":
                //handle get categories list
                if(id.equals("list")){
                    handleGetList(exchange);
                    System.out.println("GET " + id);
                    break;
                }
                //handle get particular categories
                else {
                    System.out.println("GET " + id);
                    handleGetItem(exchange, id);
                    break;
                }
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

    private void handleGetList(HttpExchange exchange) throws IOException {
        try
        {
            String query = DAO.getCategoriesList();

            ResultSet resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(query)
                    .executeQuery();

            JSONArray response = DAO.unpackCategoryList(resultSet);

            if (response != null) {

                byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(404, -1); // 404 Not Found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
        }
    }

    //Get item(s)
    //DONE we need separate functionality to obtain filtered itemslist
    // that contains only items names and id's (to enhance UX, user will not click on all items, so he need just names)
    private void handleGetItem(HttpExchange exchange, String id) throws IOException {
        try {
            String query = DAO.getCategory(id);

            ResultSet resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(query)
                    .executeQuery();

            JSONObject response = DAO.unpackCategory(resultSet);

            if (response != null) {

                byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(404, -1); // 404 Not Found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
        }
    }

    // Add item
    private void handlePut(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        if (json.has("name") && json.has("description")) {
            try {


                String query = DAO.createCategory(
                        json.getString("name"),
                        json.getString("description")
                );

                int resultSet = DoConnection
                        .getConnection()
                        .prepareStatement(query)
                        .executeUpdate();

                if (resultSet > 0) {
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(201, 0); // 201 Created
                    exchange.getResponseBody().close();

                } else {
                    exchange.sendResponseHeaders(409, -1); // Conflict

                }

            } catch (SQLException e) {
                e.printStackTrace();
                //TODO change to separate response to handle category already exists
                exchange.sendResponseHeaders(409, -1); // Conflict
            }


        } else {
            exchange.sendResponseHeaders(409, -1); // Conflict
        }
    }

    // Update item
    private void handlePost(HttpExchange exchange, String id) throws IOException {
        try {

            // Read and parse the request body
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);

            if(!json.has("name") || !json.has("description")){
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            // Update the existing record in the database
            String updateQuery = DAO.updateCategory(
                    id,
                    json.getString("name"),
                    json.getString("description")
            );

            int resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(updateQuery)
                    .executeUpdate();


            if (resultSet > 0) {
                exchange.sendResponseHeaders(204, -1); // 204 No Content
            } else {
                exchange.sendResponseHeaders(404, -1); // 404 Not Found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
        }
    }



    // Delete item
    private void handleDelete(HttpExchange exchange, String id) throws IOException {
        try {

            String query = DAO.deleteCategory(id);

            int resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(query)
                    .executeUpdate();


            if (resultSet > 0) {
                exchange.sendResponseHeaders(204, -1); // 204 No Content
            } else {
                exchange.sendResponseHeaders(404, -1); // 404 Not Found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
        }
    }

    private boolean isTokenValid(String token) {
        try {
            System.out.println("CategoriesHandler Hey: " + HttpServer.getSecretKey());
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
