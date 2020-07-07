package com.example.eyitapp;

public class Home_Objects {
    private int Id;
    private String Name;
    private int Price;
    private String Description;
    private String Category;

    public Home_Objects() {
    }

    public Home_Objects(int id, String name, int price, String description, String category) {
        Id = id;
        Name = name;
        Price = price;
        Description = description;
        Category = category;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
