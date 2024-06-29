package client.api;

import client.entities.Category;
import client.entities.DisplayItem;
import client.entities.Statistics;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

import static client.api.ConnectionProvider.*;
import static client.app.App.getToken;

public class ManageCategoriesAPI {
    static String url = "http://localhost:8765/api/category";

    public static Category getCategory(String id) {

        try {

            System.out.println(getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            HttpResponse<String> response = sendGetRequest(url+"/"+id, headers);

            if(response.statusCode() == 404){
                return null;
            }

            JSONObject resultJson = new JSONObject(response.body());

            return new Category(
                    resultJson.getInt("id"),
                    resultJson.getString("name"),
                    resultJson.getString("description")
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<Category> getCategoriesList() {

        try {

            ArrayList<Category> categories = new ArrayList<>();

            System.out.println(getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            HttpResponse<String> response = sendGetRequest(url+"/"+"list", headers);

            if(response.statusCode() == 404){
                return null;
            }

            JSONArray resultJson = new JSONArray(response.body());

            for (int i = 0; i < resultJson.length(); i++) {
                JSONObject item = resultJson.getJSONObject(i);
                categories.add(new Category(
                        item.getInt("id"),
                        item.getString("name"),
                        item.getString("description")
                ));
            }

            return categories;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int addCategory(Category category) {
        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body = "{ \"name\": \""+ category.getName()+"\", \"description\": \""+ category.getDescription()+"\" }";

            HttpResponse<String> response = sendPutRequest(url, headers, body);

            return response.statusCode();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int deleteCategory(String id) {

        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            HttpResponse<String> response = sendDeleteRequest(url + "/" + id, headers);

            return response.statusCode();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int updateCategory(String id, Category newCategory) {

        try {
            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body = "{ \"name\": \""+ newCategory.getName()+"\", \"description\": \""+ newCategory.getDescription()+"\" }";

            HttpResponse<String> loginResponse = sendPostRequest(url+"/"+id, headers, body);

            return loginResponse.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Statistics> getStatisticsList() {
        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            HttpResponse<String> response = sendGetRequest(url+"/statistics", headers);

            if(response.statusCode() == 404){
                return new ArrayList<>();
            }

            ArrayList<Statistics> statistics = new ArrayList<>();

            JSONArray resultJson = new JSONArray(response.body());

            for (int i = 0; i < resultJson.length(); i++) {
                JSONObject item = resultJson.getJSONObject(i);
                statistics.add(new Statistics(
                        item.getString("name"),
                        item.getDouble("total_price"),
                        item.getInt("total_items_amount")
                ));
            }

            return statistics;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
