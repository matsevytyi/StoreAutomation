package client.api;

import client.entities.DisplayItem;
import client.entities.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

import static client.api.ConnectionProvider.*;
import static client.app.App.getToken;

public class ManageItemsAPI {
    static String url = "http://localhost:8765/api/item";

    public static Item getItem(int id) {

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

    public static ArrayList<DisplayItem> getItemList(String name, double min_price, double max_price, int category_id) throws Exception {

            ArrayList<DisplayItem> items = new ArrayList<>();

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body =  "{ \"name_part\": \""+name+"\", " + "\"min_price\": \""+min_price+"\", \"max_price\": \""+max_price+"\", " + "\"category_id\": \""+category_id+"\" }";

            HttpResponse<String> response = sendPostRequest(url+"s", headers, body);

            if(response.statusCode() == 404){
                throw new RuntimeException("No items found");
            }
            else {

                System.out.println(response.body());

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
            }
    }

    public static int addItem(Item item) {
        try {

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body = "{ \"name\": \""+item.getName()+"\", \"description\": \""+item.getDescription()+"\", \"manufacturer\": \""+item.getManufacturer()+"\", \"price_per_unit\": \""+item.getPrice()+"\", \"group_id\": \""+item.getGroup_id()+"\" }";

            HttpResponse<String> response = sendPutRequest(url, headers, body);

            return response.statusCode();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int deleteItem(String id) {

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

    public static int updateItem(String id, Item newItem) {

        try {
            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body = "{ \"name\": \""+ newItem.getName()+"\", \"description\": \""+ newItem.getDescription()+"\", \"manufacturer\": \""+ newItem.getManufacturer()+"\", \"price_per_unit\": \""+ newItem.getPrice()+"\", \"group_id\": \""+ newItem.getGroup_id()+"\" }";

            HttpResponse<String> loginResponse = sendPostRequest(url+"/"+id, headers, body);

            return loginResponse.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int alterQuantity(String id, int delta_quantity) {

        try {
            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "Authorization", getToken()
            );

            String body = "{ \"delta_quantity\": \""+ delta_quantity+"\" }";

            HttpResponse<String> loginResponse = sendPostRequest(url+"/altquantity/"+id, headers, body);

            return loginResponse.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
