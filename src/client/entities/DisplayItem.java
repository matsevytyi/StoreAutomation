package client.entities;

//used to display list of items and enhance UX by shortening glitter (user will not need all items fully)
public class DisplayItem {
    private int id;

    private int group_id;

    private String name;

    public DisplayItem(int id, int category_id, String name) {
        this.id = id;
        this.group_id = category_id;
        this.name = name;
    }

    public String toString() {
        return "Displayable Item: " + name + " " + group_id;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return group_id;
    }

    public String getName() {
        return name;
    }

}
