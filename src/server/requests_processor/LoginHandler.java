package server.requests_processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import server.app.HttpServer;
import server.database_access.DAO;
import server.database_access.DoConnection;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);

            String login = json.getString("login");
            String password = json.getString("password"); // Assume password is already MD5 encoded

            if (authenticate(login, password)) {
                String token = createJWT(login, 3600000); // 1 hour expiration
                JSONObject responseJson = new JSONObject();
                responseJson.put("token", token);
                byte[] responseBytes = responseJson.toString().getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

                System.out.println("Login successful: " + login + " " + password);

            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private boolean authenticate(String login, String password) {

        //for integration tests, 5f4dcc3b5aa765d61d8327deb882cf99 = "password" MD5

        try {
            ResultSet resultSet = DoConnection
                    .getConnection()
                    .prepareStatement(DAO.validateUser(login, password))
                    .executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getSQLState());
            return false;
        }
    }

    private String createJWT(String subject, long ttl_ms) {
        long now_ms = System.currentTimeMillis();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        System.out.println("Login key: " + HttpServer.getSecretKey());

        byte[] apiKeySecretBytes = Base64.getDecoder().decode(HttpServer.getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setIssuedAt(new Date(now_ms)).setSubject(subject).signWith(signingKey, signatureAlgorithm);

        if (ttl_ms >= 0) {
            long expMillis = now_ms + ttl_ms;

            builder.setExpiration(new Date(expMillis));
        }

        return builder.compact();
    }
}
