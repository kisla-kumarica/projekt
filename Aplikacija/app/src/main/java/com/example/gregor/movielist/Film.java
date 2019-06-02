package com.example.gregor.movielist;

import android.os.Parcel;
import android.os.Parcelable;

public class Film implements Parcelable{
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        public Film[] newArray(int size) {
            return new Film[size];
        }
    };
    public Film(Parcel in) {
        this.naslov=in.readString();
        this.id=in.readString();
        this.imglink=in.readString();
        this.rating=in.readDouble();
        this.leto=in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.naslov);
        dest.writeString(this.id);
        dest.writeString(this.imglink);
        dest.writeDouble(this.rating);
        dest.writeInt(this.leto);
    }
}
