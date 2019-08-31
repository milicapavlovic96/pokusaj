package com.example.pokusaj.Model;

import com.example.pokusaj.Common.Common;

public class MyToken {
    private String token,userPhone;
    private Common.TOKEN_TYPE token_type;

    public MyToken(String token, String userPhone, Common.TOKEN_TYPE token_type) {
        this.token = token;
        this.userPhone = userPhone;
        this.token_type = token_type;
    }

    public MyToken(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Common.TOKEN_TYPE getToken_type() {
        return token_type;
    }

    public void setToken_type(Common.TOKEN_TYPE token_type) {
        this.token_type = token_type;
    }
}
