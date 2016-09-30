package com.example.omer.imagetest;

/**
 * Created by Omer on 24/09/2016.
 */

public class City {
    private String name;
    private String url;

    public City(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public City(String name, String url) {

        this.name = name;
        this.url = url;
    }
}
