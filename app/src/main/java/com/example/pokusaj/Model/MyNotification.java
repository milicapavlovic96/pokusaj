package com.example.pokusaj.Model;

import com.google.firebase.firestore.FieldValue;

import java.lang.reflect.Field;

public class MyNotification {

    private String uid,title,content;
    private boolean read;
private FieldValue serverTimeStamp;

    public FieldValue getServerTimeStamp() {
        return serverTimeStamp;
    }

    public void setServerTimeStamp(FieldValue serverTimeStamp) {
        this.serverTimeStamp = serverTimeStamp;
    }

    public MyNotification() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
