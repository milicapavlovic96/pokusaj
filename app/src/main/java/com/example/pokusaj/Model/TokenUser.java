package com.example.pokusaj.Model;

import com.example.pokusaj.Common.Common;

public class TokenUser {
    private String name,password,userPhone,token,state_name,labId,doktorId;
    private Common.TOKEN_TYPE token_type;

    public TokenUser(){

    }

    public TokenUser(String name, String password, String phoneNumber){
        this.name=name;
        this.password=password;
        this.userPhone=phoneNumber;

    }

    public TokenUser(String name, String password, String userPhone, String token, String state_name, String labId, String doktorId, Common.TOKEN_TYPE token_type) {
        this.name = name;
        this.password = password;
        this.userPhone = userPhone;
        this.token = token;
        this.state_name = state_name;
        this.labId = labId;
        this.doktorId = doktorId;
        this.token_type = token_type;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    public String getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(String doktorId) {
        this.doktorId = doktorId;
    }

    public TokenUser(String name, String password, String phoneNumber, String token, Common.TOKEN_TYPE token_type) {
        this.name = name;
        this.password = password;
        this.userPhone = phoneNumber;
        this.token = token;
        this.token_type = token_type;
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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Common.TOKEN_TYPE getToken_type() {
        return token_type;
    }

    public void setToken_type(Common.TOKEN_TYPE token_type) {
        this.token_type = token_type;
    }
}
