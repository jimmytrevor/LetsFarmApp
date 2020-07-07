package com.example.eyitapp;

public class Cart_Objects {
    private int ID;
    private String Image;
    private String Name;
    private  int Price;
    private int Quantity;

    public Cart_Objects() {
    }

    public Cart_Objects(int ID, String image, String name, int price, int quantity) {
        this.ID = ID;
        Image = image;
        Name = name;
        Price = price;
        Quantity = quantity;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
