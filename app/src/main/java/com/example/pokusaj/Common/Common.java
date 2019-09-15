package com.example.pokusaj.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.NotificationCompat;

import com.example.pokusaj.HomeActivity;
import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.Model.Doktor2;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.Model.MyToken;
import com.example.pokusaj.Model.User;
import com.example.pokusaj.R;
import com.example.pokusaj.Service.MyFCMService;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Common {

    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_DOKTOR_LOAD_DONE = "DOKTOR_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP ="STEP";
    public static final String KEY_DOKTOR_SELECTED ="DOKTOR_SELECTED";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final String DISABLE_TAG ="DISABLE" ;
    public static final String KEY_TIME_SLOT ="TIME_SLOT" ;
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String LOGGED_KEY = "LOGGED";
    public static final String LOGGED_KEY2 = "UserLogged";

    public static final String STATE_KEY ="STATE" ;
    public static final String LAB_KEY = "LAB";
    public static final String DOKTOR_KEY = "DOKTOR";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final int MAX_NOTIFICATION_PER_LOAD = 10;
    public static final String SERVICES_ADDED = "SERVICES_ADDED";
    public static final double DEFAULT_PRICE = 30;
    public static final String MONEY_SIGN = " din ";
    public static final String SHOPPING_LIST = "SHOPPING_LIST_ITEMS";
    public static final String IMAGE_DOWNLOADABLE_URL = "DOWNLOADABLE_URL";
    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_LAB_ID = "RATING_LAB_ID";
    public static final String RATING_LAB_NAME = "RATING_LAB_NAME";
    public static final String RATING_DOKTOR_ID = "RATING_DOKTOR_ID";
    public static final String RATING_INFORMATION_KEY = "RATING_INFORMATION";
    public static final String USER_KEY ="";
    public static Laboratory selectedLab;
    public static String IS_LOGIN="IsLogin";
    public static User currentUser;
    public static Laboratory currentLab;
    public static int step=0;
    public static String state_name="";

    public static String city="";
    public static Doktor currentDoktor;
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate=Calendar.getInstance();
    public static SimpleDateFormat simpleFormatDate=new SimpleDateFormat("dd_MM_yyyy");
    public static BookingInformation currentBooking;
    public static String currentBookingId="";
    public static BookingInformation currentBookingInformation;
    public static String phonenum="";

    public static String convertTimeSlotToString(int position) {
        switch (position){
            case 0:
                return "9:00-9:30";
            case 1:
                return "9:30-10:00";
            case 2:
                return "10:00-10:30";
            case 3:
                return "10:30-11:00";
            case 4:
                return "11:00-11:30";
            case 5:
                return "11:30-12:00";
            case 6:
                return "12:00-12:30";
            case 7:
                return "12:30-13:00";
            case 8:
                return "13:00-13:30";
            case 9:
                return "13:30-14:00";
            case 10:
                return "14:00-14:30";
            case 11:
                return "14:30-15:00";
            case 12:
                return "15:00-15:30";
            case 13:
                return "15:30-16:00";
            case 14:
                return "16:00-16:30";
            case 15:
                return "16:30-17:00";
            case 16:
                return "17:00-17:30";
            case 17:
                return "17:30-18:00";
            case 18:
                return "18:00-18:30";
            case 19:
                return "18:30-19:00";
            default:
                return "Closed";

  }
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {
    Date date=timestamp.toDate();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd_MM_yyyy");
    return simpleDateFormat.format(date);
    }

    public static String formatShoppingItemName(String name) {
    return name.length()>13 ? new StringBuilder(name.substring(0,10)).append(". . .").toString():name;

    }

    public static void showNotification(Context context, int notification_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent=null;
        if(intent!=null)
            pendingIntent=PendingIntent.getActivity(context,
                    notification_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID="edmt_doktor_booking_channel_01";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"DOKTOR BOOKING APP",NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Doktor app");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));

        if(pendingIntent!=null)
            builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(notification_id,notification);
    }

    public static void showNotification2(Context context, int notification_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent=null;
        if(intent!=null)
            pendingIntent=PendingIntent.getActivity(context,
                    notification_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID="edmt_client_booking_app";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"DOKTOR BOOKING APP",NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Doktor app");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));

        if(pendingIntent!=null)
            builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(notification_id,notification);
    }

    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            } finally {
                cursor.close();
            }

        }
        if (result == null)
        {
            result = fileUri.getPath();
            int cut=result.lastIndexOf('/');
            if(cut!= -1)
                result=result.substring(cut+1);
        }
        return result;
    }

    public static void showRatingDialog(Context context, String stateName, String labID, String labName, String doktorId) {
        DocumentReference doktorNeedRateRef=FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(stateName)
                .collection("Branch")
                .document(labID)
                .collection("Doktori")
                .document(doktorId);

        doktorNeedRateRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
            Doktor doktorRate=task.getResult().toObject(Doktor.class);
                doktorRate.setDoktorId(task.getResult().getId());

                View view= LayoutInflater.from(context)
                        .inflate(R.layout.layout_rating_dialog,null);

                    TextView txt_lab_name=(TextView)view.findViewById(R.id.txt_laboratory_name);
                    TextView txt_doktor_name=(TextView)view.findViewById(R.id.txt_doktor_name);

                    AppCompatRatingBar  ratingBar=(AppCompatRatingBar)view.findViewById(R.id.rating);


                    //set info

                    txt_doktor_name.setText(doktorRate.getName());
                    txt_doktor_name.setText(labName);

                    AlertDialog.Builder builder=new AlertDialog.Builder(context)
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        Double original_rating=doktorRate.getRating();
                                        Long ratingTimes=doktorRate.getRatingTimes();
                                        float userRating=ratingBar.getRating();

                                        Double finalRating=(original_rating+userRating);
                                        Map<String,Object> data_update=new HashMap<>();
                                        data_update.put("rating",finalRating);
                                        data_update.put("ratingTimes",++ratingTimes);

                                        doktorNeedRateRef.update(data_update)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(context,"Hvala na oceni!",Toast.LENGTH_SHORT).show();
                                                Paper.init(context);
                                                Paper.book().delete(Common.RATING_INFORMATION_KEY);

                                                }
                                            }
                                        });
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        Paper.init(context);
                                        Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                }
                            });

                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });


    }

    public static enum TOKEN_TYPE{
        CLIENT,
        DOKTOR,
        MANAGER
    }

    public static void updateToken(Context context,String token)
    {
        Paper.init(context);
        String user=Paper.book().read(Common.LOGGED_KEY);

        if(user!=null)
        {
            if(!TextUtils.isEmpty(user))
            {
               MyToken myToken=new MyToken();
                myToken.setToken(token);
                myToken.setToken_type(TOKEN_TYPE.DOKTOR);
                myToken.setUserPhone(user);


                FirebaseFirestore.getInstance()
                        .collection("Tokens")
                        .document(user)
                        .set(myToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                            }
                        });
            }
        }
    }
    public static void updateToken2(Context context,final String s)
    {




        AccessToken accessToken= AccountKit.getCurrentAccessToken();

        if(accessToken!=null)
        {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setToken_type(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(account.getPhoneNumber().toString());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(account.getPhoneNumber().toString())
                    .set(myToken)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

                @Override
                public void onError(AccountKitError accountKitError) {


                }
            });
        }
        else
        {
            Paper.init(context);
            String user=Paper.book().read(Common.LOGGED_KEY2);
            if(user!=null)
            {
                if(!TextUtils.isEmpty(user))
                {
                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setToken_type(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(user);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user)
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
            }
        }
        }
    }
