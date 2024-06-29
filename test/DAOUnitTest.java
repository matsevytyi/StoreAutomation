import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import server.database_access.DAO;

public class DAOUnitTest {

    @Test
    public void testValidateUser() {
        String expected = "SELECT 1 FROM public.lgin_data WHERE username = 'testUser' and hashed_password = 'testPassword'";
        String actual = DAO.validateUser("testUser", "testPassword");
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteItem() {
        String expected = "DELETE FROM public.items WHERE id = '1'";
        String actual = DAO.deleteItem("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchItem() {
        String expected = "SELECT id, name, group_id FROM public.items WHERE 1=1 AND group_id = '2' AND name LIKE '%item%' AND price_per_unit BETWEEN '10.0' AND '100.0'";
        String actual = DAO.searchItem("item", 10.0, 100.0, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateItem() {
        String expected = "INSERT INTO public.items (name, description, manufacturer, price_per_unit, group_id) VALUES ('itemName', 'itemDescription', 'itemManufacturer', '20.0', '3');";
        String actual = DAO.createItem("itemName", "itemDescription", "itemManufacturer", 20.0, 3);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetItem() {
        String expected = "SELECT * FROM public.items WHERE id = '1'";
        String actual = DAO.getItem("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateItem() {
        String expected = "UPDATE public.items SET name = 'itemName', description = 'itemDescription', manufacturer = 'itemManufacturer', price_per_unit = '25.0' WHERE id = '1'";
        String actual = DAO.updateItem("1", "itemName", "itemDescription", "itemManufacturer", 25.0);
        assertEquals(expected, actual);
    }

    @Test
    public void testLockItem() {
        String expected = "SELECT quantity_in_stock FROM items WHERE id = '1' FOR UPDATE";
        String actual = DAO.lockItem("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testAlterQuantity() {
        String expected = "UPDATE public.items SET quantity_in_stock = quantity_in_stock + 5 WHERE id = '1'";
        String actual = DAO.alterQuantity("1", 5);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetCategoriesList() {
        String expected = "SELECT * FROM public.categories";
        String actual = DAO.getCategoriesList();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetCategory() {
        String expected = "SELECT * FROM public.categories WHERE id = '1'";
        String actual = DAO.getCategory("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateCategory() {
        String expected = "UPDATE public.categories SET name = 'categoryName', description = 'categoryDescription' WHERE id = '1'";
        String actual = DAO.updateCategory("1", "categoryName", "categoryDescription");
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateCategory() {
        String expected = "INSERT INTO public.categories (name, description) VALUES ('categoryName', 'categoryDescription');";
        String actual = DAO.createCategory("categoryName", "categoryDescription");
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteCategory() {
        String expected = "DELETE FROM public.categories WHERE id = '1'";
        String actual = DAO.deleteCategory("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFullStatistics() {
        String expected = "SELECT c.name AS name, SUM(i.quantity_in_stock) AS total_items_amount, SUM(i.price_per_unit * i.quantity_in_stock) AS total_price FROM categories c LEFT JOIN items i ON c.id = i.group_id GROUP BY c.name;";
        String actual = DAO.getFullStatistics();
        assertEquals(expected, actual);
    }
}
