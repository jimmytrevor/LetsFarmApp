package com.example.eyitapp;

public class History_Objects {
    private int ID;
    private int Price;
    private int TotalProducts;
    private int Order_status_ID;
    private  String Customer_ID;
    private  String DateMade;

    public History_Objects() {
    }

    public History_Objects(int ID, int price, int totalProducts, int order_status_ID, String customer_ID, String dateMade) {
        this.ID = ID;
        Price = price;
        TotalProducts = totalProducts;
        Order_status_ID = order_status_ID;
        Customer_ID = customer_ID;
        DateMade = dateMade;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getTotalProducts() {
        return TotalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        TotalProducts = totalProducts;
    }

    public int getOrder_status_ID() {
        return Order_status_ID;
    }

    public void setOrder_status_ID(int order_status_ID) {
        Order_status_ID = order_status_ID;
    }

    public String getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(String customer_ID) {
        Customer_ID = customer_ID;
    }

    public String getDateMade() {
        return DateMade;
    }

    public void setDateMade(String dateMade) {
        DateMade = dateMade;
    }
}
