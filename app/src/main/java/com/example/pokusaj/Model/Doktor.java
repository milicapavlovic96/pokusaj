package com.example.pokusaj.Model;

import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

public class Doktor implements Parcelable {
   private String name,username,password,labId,doktorId;
    private Double rating;
    private Long ratingTimes;

    public Doktor(String name,String doktorId) {
        this.name = name;
        this.doktorId=doktorId;
    }

    public String getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(String doktorId) {
        this.doktorId = doktorId;
    }

    public Doktor() {
    }


    public Long getRatingTimes() {
        return ratingTimes;
    }

    public void setRatingTimes(Long ratingTimes) {
        this.ratingTimes = ratingTimes;
    }
    protected Doktor(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        labId = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
    }

    public static final Creator<Doktor> CREATOR = new Creator<Doktor>() {
        @Override
        public Doktor createFromParcel(Parcel in) {
            return new Doktor(in);
        }

        @Override
        public Doktor[] newArray(int size) {
            return new Doktor[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(labId);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(rating);
        }
    }
}
