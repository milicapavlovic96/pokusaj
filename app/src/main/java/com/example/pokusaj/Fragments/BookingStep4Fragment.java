package com.example.pokusaj.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookingStep4Fragment extends Fragment {

SimpleDateFormat simpleDateFormat;
LocalBroadcastManager localBroadcastManager;
Unbinder unbinder;


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
    BookingInformation bookingInformation=new BookingInformation();
    bookingInformation.setDoktorId(Common.currentDoktor.getDoktorId());
    bookingInformation.setDoktorName(Common.currentDoktor.getName());
    bookingInformation.setCustomerName(Common.currentUser.getName());
    bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
    bookingInformation.setLabId(Common.currentLab.getLabId());
    bookingInformation.setLabAddress(Common.currentLab.getAddress());
    bookingInformation.setLabName(Common.currentLab.getName());
    bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
            .append("at")
            .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
    bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
    DocumentReference bookingDate= FirebaseFirestore.getInstance()
            .collection("AllLaboratories")
            .document(Common.city)
            .collection("Branch")
            .document(Common.currentLab.getLabId())
            .collection("Doktori")
            .document(Common.currentDoktor.getDoktorId())
            .collection(Common.simpleFormatDate.format(Common.currentDate.getTime()))
.document(String.valueOf(Common.currentTimeSlot));//simpleformatdate

    bookingDate.set(bookingInformation)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    getActivity().finish();
                    resetStaticData();
                    Toast.makeText(getContext(),"Success!",Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    });
}

    private void resetStaticData() {
   Common.step=0;
   Common.currentTimeSlot=-1;
   Common.currentLab=null;
   Common.currentDoktor=null;
   Common.currentDate.add(Calendar.DATE,0);

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
        .append(simpleDateFormat.format(Common.currentDate.getTime())));
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
