package server.database_access;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DAO {

    public static String deleteItem(String id) {
        return "DELETE FROM public.items WHERE id = '" + id + "'";
    }

    public static String searchItem(String name, int category_id) {
        return "SELECT id, name, group_id FROM public.items WHERE name LIKE '%" + name + "%' and group_id = '" + category_id + "'";
    }

    public static String createItem(String name, String description, String manufacturer, double price_per_unit, int group_id) {
        System.out.println("DAO");
        return "INSERT INTO public.items (name, description, manufacturer, price_per_unit, group_id) VALUES ('" + name + "', '" + description + "', '" + manufacturer + "', '" + price_per_unit + "', '" + group_id + "');";
    }

    public static String getItem(String id) {
        return "SELECT * FROM public.items WHERE id = '" + id + "'";
    }

    public static String updateItem(String id, String name, String description, String manufacturer, double price_per_unit) {
        return "UPDATE public.items SET name = '" + name + "', description = '" + description + "', manufacturer = '" + manufacturer + "', price_per_unit = '" + price_per_unit + "' WHERE id = '" + id + "'";
    }

    public static String filterByPrice(double minPrice, double maxPrice, int group_id) {
        return "SELECT id, name, group_id FROM public.items WHERE price_per_unit > '" + minPrice + "' AND price_per_unit < '" + maxPrice + "' AND group_id = '" + group_id + "'";
    }

    static String validateUser(String username, String hashed_password) {
        return "SELECT 1 FROM public.lgin_data WHERE username = '" + username + "' and hashed_password = '" + hashed_password + "'";
    }

    static JSONObject unpackItem(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("id", resultSet.getInt(1));
            itemJson.put("name", resultSet.getString(2));
            itemJson.put("description", resultSet.getString(3));
            itemJson.put("manufacturer", resultSet.getString(4));
            itemJson.put("quantity_in_stock", resultSet.getInt(6));
            itemJson.put("price_per_unit", resultSet.getInt(6));
            itemJson.put("group_id", resultSet.getInt(7));
            return itemJson;
        } else {
            return null; // No item found
        }
    }

    public static JSONArray unpackItemList(ResultSet resultSet) {

        JSONArray jsonArray = new JSONArray();

        try {
            while (resultSet.next()) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("id", resultSet.getInt("id"));
                jsonItem.put("group_id", resultSet.getInt("group_id"));
                jsonItem.put("name", resultSet.getString("name"));

                jsonArray.put(jsonItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
