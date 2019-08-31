package com.example.pokusaj.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.Model.MyNotification;
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class BookingStep4Fragment extends Fragment {

SimpleDateFormat simpleDateFormat;
LocalBroadcastManager localBroadcastManager;
Unbinder unbinder;

AlertDialog dialog;


@BindView(R.id.txt_booking_doktor)
    TextView txt_booking_doktor;
@BindView(R.id.txt_booking_time_text)
TextView txt_booking_time_text;
@BindView(R.id.txt_laboratory_address)
TextView txt_laboratory_address;
@BindView(R.id.txt_laboratory_name)
TextView txt_laboratory_name;
@BindView(R.id.txt_lab_open_hours)
TextView txt_lab_open_hours;
@BindView(R.id.txt_lab_phone)
TextView txt_lab_phone;
@BindView(R.id.txt_lab_website)
TextView txt_lab_website;

@OnClick(R.id.btn_confirm)
        void confirmBooking(){

    dialog.show();
    String startTime=Common.convertTimeSlotToString(Common.currentTimeSlot);
    String[] convertTime=startTime.split("-");

    String[] startTimeConvert=convertTime[0].split(":");
    int startHourInt=Integer.parseInt(startTimeConvert[0].trim());//get 9
    int startMinInt=Integer.parseInt(startTimeConvert[1].trim());//get 00

    Calendar bookingDateWithourHouse=Calendar.getInstance();
    bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
    bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
    bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

    Timestamp timestamp=new Timestamp(bookingDateWithourHouse.getTime());


    final BookingInformation bookingInformation=new BookingInformation();

    bookingInformation.setCityBook(Common.city);
    bookingInformation.setTimestamp(timestamp);
    bookingInformation.setDone(false);
    bookingInformation.setDoktorId(Common.currentDoktor.getDoktorId());
    bookingInformation.setDoktorName(Common.currentDoktor.getName());
    bookingInformation.setCustomerName(Common.currentUser.getName());
    bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
    bookingInformation.setLabId(Common.currentLab.getLabId());
    bookingInformation.setLabAddress(Common.currentLab.getAddress());
    bookingInformation.setLabName(Common.currentLab.getName());
    bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
            .append("at")
            .append(simpleDateFormat.format(bookingDateWithourHouse.getTime())).toString());
    bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
    DocumentReference bookingDate= FirebaseFirestore.getInstance()
            .collection("AllLaboratories")
            .document(Common.city)
            .collection("Branch")
            .document(Common.currentLab.getLabId())
            .collection("Doktori")
            .document(Common.currentDoktor.getDoktorId())
            .collection(Common.simpleFormatDate.format(Common.bookingDate.getTime()))
.document(String.valueOf(Common.currentTimeSlot));//simpleformatdate

    bookingDate.set(bookingInformation)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    addToUserBooking(bookingInformation);

                }


            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    });
}

    private void addToUserBooking(BookingInformation bookingInformation) {

        final CollectionReference userBooking=FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp toDayTimeStamp=new Timestamp(calendar.getTime());
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().isEmpty())
                    {
                    userBooking.document()
                            .set(bookingInformation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    MyNotification myNotification=new MyNotification();
                                    myNotification.setUid(UUID.randomUUID().toString());
                                    myNotification.setTitle("New Booking");
                                    myNotification.setContent("You have a new appoiment for customer health!");
                                myNotification.setRead(false);

                                FirebaseFirestore.getInstance()
                                        .collection("AllLaboratories")
                                        .document(Common.city)
                                        .collection("Branch")
                                        .document(Common.currentLab.getLabId())
                                        .collection("Doktori")
                                        .document(Common.currentDoktor.getDoktorId())
                                        .collection("Notifications")
                                        .document(myNotification.getUid())
                                        .set(myNotification)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if(dialog.isShowing())
                                                    dialog.dismiss();
                                                addToCalendar(Common.bookingDate,
                                                        Common.convertTimeSlotToString(Common.currentTimeSlot));
                                                resetStaticData();
                                                getActivity().finish();
                                                Toast.makeText(getContext(),"Success!",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(dialog.isShowing())
                                dialog.dismiss();
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    }else
                    {
                        if(dialog.isShowing())
                            dialog.dismiss();
                        resetStaticData();
                        getActivity().finish();
                        Toast.makeText(getContext(),"Success!",Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime=Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime=startTime.split("-");

        String[] startTimeConvert=convertTime[0].split(":");
        int startHourInt=Integer.parseInt(startTimeConvert[0].trim());//get 9
        int startMinInt=Integer.parseInt(startTimeConvert[1].trim());//get 00

        String[] endTimeConvert=convertTime[1].split(":");
        int endHourInt=Integer.parseInt(endTimeConvert[0].trim());//get 9
        int endMinInt=Integer.parseInt(endTimeConvert[1].trim());//get 00

        Calendar startEvent=Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY,startHourInt);
        startEvent.set(Calendar.MINUTE,startMinInt);

        Calendar endEvent=Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY,endHourInt);
        endEvent.set(Calendar.MINUTE,endMinInt);

        SimpleDateFormat calendarDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime=calendarDateFormat.format(startEvent.getTime());
        String endEventTime=calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime,endEventTime,"Zakazan pregled",
                new StringBuilder("Pregled od ")
        .append(startTime)
                .append(" kod ")
                .append(Common.currentDoktor.getName())
                .append(" u ")
                .append(Common.currentLab.getName()).toString(),
                new StringBuilder("Adresa: ").append(Common.currentLab.getAddress()).toString());



    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {
    SimpleDateFormat calendarDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");

    try{
        Date start=calendarDateFormat.parse(startEventTime);
        Date end=calendarDateFormat.parse(endEventTime);

        ContentValues event=new ContentValues();
        //put
        event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
        event.put(CalendarContract.Events.TITLE,title);
        event.put(CalendarContract.Events.DESCRIPTION,description);
        event.put(CalendarContract.Events.EVENT_LOCATION,location);

        //time
        event.put(CalendarContract.Events.DTSTART,start.getTime());
        event.put(CalendarContract.Events.DTEND,end.getTime());
        event.put(CalendarContract.Events.ALL_DAY,0);
        event.put(CalendarContract.Events.HAS_ALARM,1);

        String timeZone= TimeZone.getDefault().getID();
        event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

        Uri calendars;
            if(Build.VERSION.SDK_INT>=8)
                calendars=Uri.parse("content://com.android.calendar/events");
            else
                calendars=Uri.parse("content://calendar/events");

        Uri uri_save=getActivity().getContentResolver().insert(calendars,event);

        Paper.init(getActivity());
        Paper.book().write(Common.EVENT_URI_CACHE,uri_save.toString());



    }catch(ParseException e)
    {
        e.printStackTrace();
    }

}

    private String getCalendar(Context context) {
            String gmailIdCalendar="";
            String projection[]={"_id","calendar_displayName"};
        Uri calendars=Uri.parse("content://com.android.calendar/calendars");
        ContentResolver contentResolver=context.getContentResolver();
        Cursor managedCursor =contentResolver.query(calendars,projection,null,null, null);

        if(managedCursor.moveToFirst())
        {
            String calName;
            int nameCol=managedCursor.getColumnIndex(projection[1]);
            int idCol=managedCursor.getColumnIndex(projection[0]);
            do{
                calName=managedCursor.getString(nameCol);
                if(calName.contains("@gmail.com"))
                {
                    gmailIdCalendar=managedCursor.getString(idCol);
                    break;
                }
            }while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return  gmailIdCalendar;
    }

    private void resetStaticData() {
   Common.step=0;
   Common.currentTimeSlot=-1;
   Common.currentLab=null;
   Common.currentDoktor=null;
   Common.bookingDate.add(Calendar.DATE,0);

    }

    BroadcastReceiver confirmBookingReciever=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        setData();
        
        
    }
};

    private void setData() {
        txt_booking_doktor.setText(Common.currentDoktor.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append("at")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));
        txt_laboratory_address.setText(Common.currentLab.getAddress());
        txt_lab_website.setText(Common.currentLab.getWebsite());
        txt_lab_open_hours.setText(Common.currentLab.getOpenHours());
        txt_laboratory_name.setText(Common.currentLab.getName());


    }


    static BookingStep4Fragment instance;
    public static BookingStep4Fragment getInstance(){
        if(instance==null)
            instance=new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
    localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
localBroadcastManager.registerReceiver(confirmBookingReciever,new IntentFilter(Common.KEY_CONFIRM_BOOKING));

    dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
            .build();

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReciever);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView= inflater.inflate(R.layout.fragment_booking_step_four,container,false);
    unbinder= ButterKnife.bind(this,itemView);
        return itemView;
    }
}
