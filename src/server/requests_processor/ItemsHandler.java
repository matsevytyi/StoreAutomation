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

public class ItemsHandler implements HttpHandler {

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
                //handle get items list
                if(id.equals("items")){
                    handleGetList(exchange);
                    System.out.println("GET " + id);
                    break;
                }
                //handle get particular item
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
                if(path.contains("altquantity"))
                    handleAlterQuantity(exchange, id);
                else
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

        try {

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);
            String query;

            if(!json.has("category_id")){
                exchange.sendResponseHeaders(400, -1); // 400 Bad Request
                return;
            }

            if(json.has("name_part"))
                query = DAO.searchItem(json.getString("name_part"), json.getInt("category_id"));
            else if (json.has("min_price") && json.has("max_price"))

                query = DAO.filterByPrice(json.getDouble("min_price"), json.getDouble("max_price"), json.getInt("category_id"));
            else {
                exchange.sendResponseHeaders(400, -1); // 400 Bad Request
                return;
            }

            ResultSet resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(query)
                    .executeQuery();

            JSONArray response = DAO.unpackItemList(resultSet);

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
            String query = DAO.getItem(id);

            ResultSet resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(query)
                    .executeQuery();

            JSONObject response = DAO.unpackItem(resultSet);

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

        if (json.has("name") && json.has("description") && json.has("manufacturer") && json.has("price_per_unit") && json.has("group_id")) {

            double price = json.getDouble("price_per_unit");

            if (price < 0) {
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            try {


                String query = DAO.createItem(
                        json.getString("name"),
                        json.getString("description"),
                        json.getString("manufacturer"),
                        price,
                        json.getInt("group_id")
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
                //TODO change to separate response to handle item already exists
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

            if(!json.has("name") || !json.has("description") || !json.has("manufacturer") || !json.has("price_per_unit")){
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            // Validate the price
            if (json.getDouble("price_per_unit") < 0) {
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            // Update the existing record in the database
            String updateQuery = DAO.updateItem(
                    id,
                    json.getString("name"),
                    json.getString("description"),
                    json.getString("manufacturer"),
                    json.getDouble("price_per_unit")
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

    private void handleAlterQuantity(HttpExchange exchange, String id) throws IOException {
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet lockResult = null;

        try {
            // Read and parse the request body
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);

            if (!json.has("delta_quantity")) {
                exchange.sendResponseHeaders(409, -1); // Conflict
                return;
            }

            int delta_quantity = json.getInt("delta_quantity");

            // Update the existing record in the database
            String lockQuery = DAO.lockItem(id);
            String updateQuery = DAO.alterQuantity(id, delta_quantity);

            conn = DoConnection.getConnection();

            // Lock the row for update
            lockStmt = conn.prepareStatement(lockQuery);
            lockResult = lockStmt.executeQuery();

            // Test for concurrent requests
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

            if (lockResult.next()) {
                // Check if the current quantity allows the operation
                int currentQuantity = lockResult.getInt("quantity_in_stock");
                if (currentQuantity >= -delta_quantity) {
                    // Update the quantity
                    updateStmt = conn.prepareStatement(updateQuery);

                    try {
                        updateStmt.executeUpdate();
                    } catch (SQLException e) {
                        if(e.getMessage().contains("violates check constraint")){
                            exchange.sendResponseHeaders(409, -1); // Conflict
                        }
                        return;
                    }

                    exchange.sendResponseHeaders(202, -1); // 202 Accepted
                } else {
                    // Not enough items in stock
                    exchange.sendResponseHeaders(409, -1); // 409 Conflict
                }
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

            String query = DAO.deleteItem(id);

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
