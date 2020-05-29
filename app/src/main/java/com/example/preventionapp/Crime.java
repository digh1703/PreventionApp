package com.example.preventionapp;

public class Crime {
    public double lat;
    public double lng;
    public String name;
    public String murder;
    public String robbery;
    public String rape;
    public String larceny;
    public String violence;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMurder() {
        return murder;
    }

    public void setMurder(String murder) {
        this.murder = murder;
    }

    public String getRobbery() {
        return robbery;
    }

    public void setRobbery(String robbery) {
        this.robbery = robbery;
    }

    public String getRape() {
        return rape;
    }

    public void setRape(String rape) {
        this.rape = rape;
    }

    public String getLarceny() {
        return larceny;
    }

    public void setLarceny(String larceny) {
        this.larceny = larceny;
    }

    public String getViolence() {
        return violence;
    }

    public void setViolence(String violence) {
        this.violence = violence;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
