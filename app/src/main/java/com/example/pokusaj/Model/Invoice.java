package com.example.pokusaj.Model;

import com.example.pokusaj.Database.CartItem;

import java.util.List;

public class Invoice {
    private String labId, labName, labAddress;
    private String doktorId,doktorName;
    private String customerName,customerPhone;
    private String imageUrl;
    private List<CartItem> shoppingItemList;
    private List<DoktorServices> doktorServices;
    private double finalPrice;

    public Invoice() {
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLabAddress() {
        return labAddress;
    }

    public void setLabAddress(String labAddress) {
        this.labAddress = labAddress;
    }


    public String getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(String doktorId) {
        this.doktorId = doktorId;
    }

    public String getDoktorName() {
        return doktorName;
    }

    public void setDoktorName(String doktorName) {
        this.doktorName = doktorName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CartItem> getShoppingItemList() {
        return shoppingItemList;
    }

    public void setShoppingItemList(List<CartItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
    }

    public List<DoktorServices> getDoktorServices() {
        return doktorServices;
    }

    public void setDoktorServices(List<DoktorServices> doktorServices) {
        this.doktorServices = doktorServices;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
