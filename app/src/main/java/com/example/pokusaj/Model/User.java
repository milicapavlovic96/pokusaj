package com.example.pokusaj.Model;

public class User {
    private String name,password,phoneNumber;
    public User(){

    }

    public User(String name, String password, String phoneNumber){
        this.name=name;
        this.password=password;
        this.phoneNumber=phoneNumber;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



}
