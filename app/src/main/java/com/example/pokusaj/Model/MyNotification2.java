package com.example.pokusaj.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class MyNotification2 {
    private String uid,title,content;
    private boolean read;
    private Timestamp serverTimestamp;

    public MyNotification2() {
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

    public Timestamp getServerTimeStamp() {
        return serverTimestamp;
    }

    public void setServerTimeStamp(Timestamp serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
