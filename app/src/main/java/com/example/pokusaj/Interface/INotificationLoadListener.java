package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.MyNotification2;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface INotificationLoadListener {
    void onNotificationLoadSuccess(List<MyNotification2> myNotificationList, DocumentSnapshot lastDocument);
    void onNotificationLoadFailed(String message);
}
