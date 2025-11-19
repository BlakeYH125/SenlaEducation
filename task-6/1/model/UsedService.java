package model;

import java.util.Date;
import java.text.SimpleDateFormat;

public class UsedService {
    private String id;
    private String name;
    private double price;
    private Date date;

    public UsedService(String id, String name, double price, Date date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return "Услуга [" + id + "] " + name + ", стоимость " + price + ", использована " + sdf.format(date);
    }
}
