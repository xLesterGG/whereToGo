package com.example.lesgo.wheretogo;

import android.graphics.Bitmap;

import org.json.JSONArray;

/**
 * Created by lesgo on 11/13/2016.
 */
public class Place {
    String name,address,longi,lat,desc,category,imgstring,id;
    Bitmap img;
    JSONArray comments;

    public Place(String name, String address, String longi, String lat, String desc,Bitmap image,String cat,String imagestring,JSONArray com, String idee) {
        this.name = name;
        this.address = address;
        this.longi = longi;
        this.lat = lat;
        this.desc = desc;
        this.img = image;
        this.category = cat;
        this.imgstring = imagestring;
        this.comments = com;
        this.id = idee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONArray getComments() {
        return comments;
    }

    public void setComments(JSONArray comments) {
        this.comments = comments;
    }

    public String getImgstring() {
        return imgstring;
    }

    public void setImgstring(String imgstring) {
        this.imgstring = imgstring;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
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
