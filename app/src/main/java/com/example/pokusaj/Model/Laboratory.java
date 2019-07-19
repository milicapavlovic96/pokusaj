package com.example.pokusaj.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Laboratory implements Parcelable {
    private String name,address,labId;

    public Laboratory(String name, String address, String labId) {
        this.name = name;
        this.address = address;
        this.labId = labId;
    }

    public Laboratory() {
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

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    protected Laboratory(Parcel in) {
        name = in.readString();
        address = in.readString();
        labId = in.readString();
    }

    public static final Creator<Laboratory> CREATOR = new Creator<Laboratory>() {
        @Override
        public Laboratory createFromParcel(Parcel in) {
            return new Laboratory(in);
        }

        @Override
        public Laboratory[] newArray(int size) {
            return new Laboratory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(labId);
    }
}
