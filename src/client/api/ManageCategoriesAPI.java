package client.api;

import client.app.Main;
import client.entities.Category;
import client.entities.DisplayItem;
import client.entities.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

import static client.api.ConnectionProvider.*;

public class ManageCategoriesAPI {
    static String url = "http://localhost:8765/api/category";

    public static Category getCategory(String id) {

        try {

            System.out.println(Main.getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
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

            System.out.println(Main.getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
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

    public static ArrayList<DisplayItem> getItemList(String name, int category_id) {
        try {

            ArrayList<DisplayItem> items = new ArrayList<>();

            System.out.println(Main.getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body =  "{ \"name_part\": \""+name+"\", \"category_id\": \""+category_id+"\" }";

            HttpResponse<String> response = sendGetRequestWithBody(url+"s", headers, body);

            if(response.statusCode() == 404){
                return null;
            }

            JSONArray resultJson = new JSONArray(response.body());

            for (int i = 0; i < resultJson.length(); i++) {
                JSONObject item = resultJson.getJSONObject(i);
                items.add(new DisplayItem(
                        item.getInt("id"),
                        item.getInt("group_id"),
                        item.getString("name")
                ));
            }

            return items;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addCategory(Category category) {
        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body = "{ \"name\": \""+ category.getName()+"\", \"description\": \""+ category.getDescription()+"\" }";

            HttpResponse<String> response = sendPutRequest(url, headers, body);

            if(response.statusCode() == 201)
                return true;

            return false;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteCategory(String id) {

        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            HttpResponse<String> response = sendDeleteRequest(url + "/" + id, headers);

            if(response.statusCode() == 204)
                return true;

            return false;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateCategory(String id, Category newCategory) {

        try {
            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body = "{ \"name\": \""+ newCategory.getName()+"\", \"description\": \""+ newCategory.getDescription()+"\" }";

            HttpResponse<String> loginResponse = sendPostRequest(url+"/"+id, headers, body);

            if(loginResponse.statusCode() == 204)
                return true;

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
