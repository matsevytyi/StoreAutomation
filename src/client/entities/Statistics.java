package client.entities;
public class Statistics {
    private String category;
    private double totalPrice;
    private int totalGoods;

    public Statistics(String category, double totalPrice, int totalGoods) {
        this.category = category;
        this.totalPrice = totalPrice;
        this.totalGoods = totalGoods;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalGoods() {
        return totalGoods;
    }

    public void setTotalGoods(int totalGoods) {
        this.totalGoods = totalGoods;
    }
}

