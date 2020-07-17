package com.example.eyitapp;

public class Note_Objects {

    private int ID;
    private  String Tag,Body,Visibility,Destination,Date;

    public Note_Objects() {
    }

    public Note_Objects(int ID, String tag, String body, String visibility, String destination, String date) {
        this.ID = ID;
        Tag = tag;
        Body = body;
        Visibility = visibility;
        Destination = destination;
        Date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getVisibility() {
        return Visibility;
    }

    public void setVisibility(String visibility) {
        Visibility = visibility;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
