package com.example.pokusaj.Service;


import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokusaj.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.sql.DriverPropertyInfo;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class MyFCMClientService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Common.updateToken2(this,s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().get("update_done") != null) {
                updateLastBooking();
                Map<String,String> dataRecieved =remoteMessage.getData();
                Paper.init(this);
                Paper.book().write(Common.RATING_INFORMATION_KEY,new Gson().toJson(dataRecieved));


            }
            if (remoteMessage.getData().get(Common.TITLE_KEY) != null &&
                    remoteMessage.getData().get(Common.CONTENT_KEY) != null)
            {
                Common.showNotification2(this, new Random().nextInt(),
                        remoteMessage.getData().get(Common.TITLE_KEY),
                        remoteMessage.getData().get(Common.CONTENT_KEY), null
                );

            }
        }
    }


    private void updateLastBooking() {

        CollectionReference userBooking;
        if (Common.currentUser != null) {
            userBooking = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");
        } else {
            //if app not runing
            Paper.init(this);
            String user = Paper.book().read(Common.LOGGED_KEY2);

            userBooking = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(user)
                    .collection("Booking");
        }

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.add(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.MINUTE,0);

        Timestamp timestamp=new Timestamp(calendar.getTimeInMillis());
        userBooking.whereGreaterThanOrEqualTo("timestamp",timestamp)//get only today booking info
        .whereEqualTo("done",false)//done field is false
        .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyFCMClientService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().size()>0)
                    {
                        DocumentReference userBookingCurrentDocument=null;
                        for(DocumentSnapshot documentSnapshot:task.getResult())
                        {
                            userBookingCurrentDocument= userBooking.document(documentSnapshot.getId());

                        }
                        if(userBookingCurrentDocument!=null)
                        {
                            Map<String,Object> dataUpdate=new HashMap<>();
                            dataUpdate.put("done",true);
                            userBookingCurrentDocument.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyFCMClientService.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }
}
