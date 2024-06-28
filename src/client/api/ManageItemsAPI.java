package client.api;

import client.app.Main;
import client.entities.DisplayItem;
import client.entities.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

import static client.api.ConnectionProvider.*;

public class ManageItemsAPI {
    static String url = "http://localhost:8765/api/item";

    public static Item getItem(int id) {

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

            return new Item(
                    resultJson.getInt("id"),
                    resultJson.getString("name"),
                    resultJson.getString("description"),
                    resultJson.getString("manufacturer"),
                    resultJson.getDouble("price_per_unit"),
                    resultJson.getInt("group_id")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<DisplayItem> getItemList(double min_price, double max_price, int category_id) {

        try {

            ArrayList<DisplayItem> items = new ArrayList<>();

            System.out.println(Main.getToken());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body =  "{ \"min_price\": \""+min_price+"\", \"max_price\": \""+max_price+"\", \"category_id\": \""+category_id+"\" }";

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

    public static boolean addItem(Item item) {
        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body = "{ \"name\": \""+item.getName()+"\", \"description\": \""+item.getDescription()+"\", \"manufacturer\": \""+item.getManufacturer()+"\", \"price_per_unit\": \""+item.getPrice()+"\", \"group_id\": \""+item.getGroup_id()+"\" }";

            HttpResponse<String> response = sendPutRequest(url, headers, body);

            if(response.statusCode() == 201)
                return true;

            return false;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteItem(String id) {

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

    public static boolean updateItem(String id, Item newItem) {

        try {
            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", Main.getToken()
            );

            String body = "{ \"name\": \""+ newItem.getName()+"\", \"description\": \""+ newItem.getDescription()+"\", \"manufacturer\": \""+ newItem.getManufacturer()+"\", \"price_per_unit\": \""+ newItem.getPrice()+"\", \"group_id\": \""+ newItem.getGroup_id()+"\" }";

            HttpResponse<String> loginResponse = sendPostRequest(url+"/"+id, headers, body);

            if(loginResponse.statusCode() == 204)
                return true;

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
