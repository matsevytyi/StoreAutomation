package client.entities;

//Entity class for item
public class Item {

    private int id;
    private String name;
    private String description;

    private String manufacturer;

    private int group_id;

    private double price_per_unit;

    public Item(int id, String name, String description, String manufacturer, double price_per_unit, int group_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.group_id = group_id;
        this.price_per_unit = price_per_unit;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price_per_unit;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public int getGroup_id() {
        return group_id;
    }

    public long getId() {
        return id;
    }

    public String toString() {
        return "Item: " + name + " " + description + " " + manufacturer + " " + price_per_unit + " " + group_id;
    }
}
