package com.example.lesgo.wheretogo;

/**
 * Created by lesgo on 11/13/2016.
 */
public class Place {
    String name,address,longi,lat,desc;



    public Place(String name, String address, String longi, String lat, String desc) {
        this.name = name;
        this.address = address;
        this.longi = longi;
        this.lat = lat;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
