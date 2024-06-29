package server.database_access;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DAO {

    //User authentication
    static String validateUser(String username, String hashed_password) {
        return "SELECT 1 FROM public.lgin_data WHERE username = '" + username + "' and hashed_password = '" + hashed_password + "'";
    }

    //Items operations
    public static String deleteItem(String id) {
        return "DELETE FROM public.items WHERE id = '" + id + "'";
    }

    public static String searchItem(String name, double min_price, double max_price, Integer category_id) {
        String query = "SELECT id, name, group_id FROM public.items WHERE 1=1";

        if(category_id != -1) {
            query += " AND group_id = '" + category_id + "'";
        }

        if(name != "-1") {
            query += " AND name LIKE '%" + name + "%'";
        }

        if (min_price > 0 && max_price > 0) {
            query += " AND price_per_unit BETWEEN '" + min_price + "' AND '" + max_price + "'";
        }

        System.out.println(query);




        return query.toString();
    }

    public static String createItem(String name, String description, String manufacturer, double price_per_unit, int group_id) {
        return "INSERT INTO public.items (name, description, manufacturer, price_per_unit, group_id) VALUES ('" + name + "', '" + description + "', '" + manufacturer + "', '" + price_per_unit + "', '" + group_id + "');";
    }

    public static String getItem(String id) {
        return "SELECT * FROM public.items WHERE id = '" + id + "'";
    }

    public static String updateItem(String id, String name, String description, String manufacturer, double price_per_unit) {
        return "UPDATE public.items SET name = '" + name + "', description = '" + description + "', manufacturer = '" + manufacturer + "', price_per_unit = '" + price_per_unit + "' WHERE id = '" + id + "'";
    }

    static String lockItem(String id) {
        return "SELECT quantity_in_stock FROM items WHERE id = '" + id + "' FOR UPDATE";
    }

    static String alterQuantity(String id, int quantity) {
        return "UPDATE public.items SET quantity_in_stock = quantity_in_stock + " + quantity + " WHERE id = '" + id + "'";
    }

    //Categories operations
    public static String getCategoriesList() {
        return "SELECT * FROM public.categories";
    }

    public static String getCategory(String id) {
        return "SELECT * FROM public.categories WHERE id = '" + id + "'";
    }

    public static String updateCategory(String id, String name, String description) {
        return "UPDATE public.categories SET name = '" + name + "', description = '" + description + "' WHERE id = '" + id + "'";
    }

    public static String createCategory(String name, String description) {
        return "INSERT INTO public.categories (name, description) VALUES ('" + name + "', '" + description + "');";
    }


    public static String deleteCategory(String id) {
        return "DELETE FROM public.categories WHERE id = '" + id + "'";
    }

    public static String getFullStatistics() {
        return "SELECT c.name AS name, SUM(i.quantity_in_stock) AS total_items_amount, SUM(i.price_per_unit * i.quantity_in_stock) AS total_price FROM categories c LEFT JOIN items i ON c.id = i.group_id GROUP BY c.name;";
    }


    // unpack items operations
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

    //unpack categories operations

    static JSONObject unpackCategory(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            JSONObject categoryJson = new JSONObject();
            categoryJson.put("id", resultSet.getInt(1));
            categoryJson.put("name", resultSet.getString(2));
            categoryJson.put("description", resultSet.getString(3));
            return categoryJson;
        } else {
            return null; // No category found
        }
    }

    public static JSONArray unpackCategoryList(ResultSet resultSet) {
        JSONArray jsonArray = new JSONArray();

        try {
            while (resultSet.next()) {
                JSONObject jsonCategory = new JSONObject();
                jsonCategory.put("id", resultSet.getInt("id"));
                jsonCategory.put("name", resultSet.getString("name"));
                jsonCategory.put("description", resultSet.getString("description"));
                jsonArray.put(jsonCategory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //Statistics


    public static JSONArray unpackStatisticsList(ResultSet resultSet) {
        JSONArray jsonArray = new JSONArray();

        try {
            while (resultSet.next()) {
                JSONObject jsonCategory = new JSONObject();
                jsonCategory.put("name", resultSet.getString("name"));
                jsonCategory.put("total_price", resultSet.getDouble("total_price"));
                jsonCategory.put("total_items_amount", resultSet.getInt("total_items_amount"));
                jsonArray.put(jsonCategory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(jsonArray);
        return jsonArray;
    }

}
