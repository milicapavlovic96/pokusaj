package com.example.pokusaj.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Laboratory implements Parcelable {
    private String name,address,website,phone,openHours,labId;

    public Laboratory() {
    }

    public Laboratory(String labId) {
        this.labId = labId;
    }

    protected Laboratory(Parcel in) {
        name = in.readString();
        address = in.readString();
        website = in.readString();
        phone = in.readString();
        openHours = in.readString();
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
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
        parcel.writeString(address);
        parcel.writeString(website);
        parcel.writeString(phone);
        parcel.writeString(openHours);
        parcel.writeString(labId);
    }
}
