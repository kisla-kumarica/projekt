package com.example.gregor.movielist;

public class Film {
    private String naslov;
    private int leto;
    private double rating;
    private String id;
    private String imglink;

    public Film(String naslov, int leto, double rating, String id, String imglink) {
        this.naslov = naslov;
        this.leto = leto;
        this.rating = rating;
        this.id = id;
        this.imglink = imglink;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public int getLeto() {
        return leto;
    }

    public void setLeto(int leto) {
        this.leto = leto;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }
}
