package com.example.eyitapp;

public class Single_Object {
    private int Product_ID;
    private int Quantity;
    private int Single_Price;
    private String Name;

    public Single_Object() {
    }

    public Single_Object(int product_ID, int quantity, int single_Price, String name) {
        Product_ID = product_ID;
        Quantity = quantity;
        Single_Price = single_Price;
        Name = name;
    }

    public int getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(int product_ID) {
        Product_ID = product_ID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getSingle_Price() {
        return Single_Price;
    }

    public void setSingle_Price(int single_Price) {
        Single_Price = single_Price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
