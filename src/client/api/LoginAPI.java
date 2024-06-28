package client.api;

import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.Map;

import static client.api.ConnectionProvider.sendPostRequest;

public class LoginAPI {

    static String url = "http://localhost:8765/login";
    public static String execute(String username, String password) throws Exception {
        Map<String, String> loginHeaders = Map.of(
                "Content-Type", "application/json"
        );

        String loginBody = "{ \"login\": " + "\"" + username + "\", \"password\": " + "\"" + password + "\" }";

        HttpResponse<String> loginResponse = sendPostRequest(url, loginHeaders,loginBody);

        if(loginResponse.statusCode() == 401){
            return "bad login";
        }

        JSONObject resultJson = new JSONObject(loginResponse.body());

        System.out.println("auth token obtained: " + resultJson.getString("token"));

        return resultJson.getString("token");
    }
}
