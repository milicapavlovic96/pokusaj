package com.example.pokusaj.Model;

public class BookingInformation {
String customerName,customerPhone,time,doktorId,doktorName,labId,labName,labAddress;
private Long slot;

    public BookingInformation() {
    }

    public BookingInformation(String customerName, String customerPhone, String time, String doktorId, String doktorName, String labId, String labName, String labAddress, Long slot) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.doktorId = doktorId;
        this.doktorName = doktorName;
        this.labId = labId;
        this.labName = labName;
        this.labAddress = labAddress;
        this.slot = slot;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }
}
