package com.example.pokusaj.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.MyTimeSlotAdapter;
import com.example.pokusaj.Adapter.MyTimeSlotAdapter2;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Interface.ITimeSlotLoadListener;
import com.example.pokusaj.Interface.ITimeSlotLoadListener2;
import com.example.pokusaj.Model.TimeSlot;
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingStep3Fragment extends Fragment implements ITimeSlotLoadListener2 {

    DocumentReference doktorDoc;
    ITimeSlotLoadListener2 iTimeSlotLoadListener;
    AlertDialog dialog;

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    Calendar selected_date;

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;

    @BindView(R.id.calendarView)
    HorizontalCalendarView calndarView;

    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date=Calendar.getInstance();
            date.add(Calendar.DATE,0);
            loadAvailableTimeSlotOfDoktor(Common.currentDoktor.getDoktorId(),
            simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlotOfDoktor(String doktorId,String bookDate){
            dialog.show();

            ///AllLaboratories/NoviSad/Branch/HBRJEFF1Onzsb0Vr4xzV/Doktori/DRSA0NwCCbEmH9fviVMh
            doktorDoc= FirebaseFirestore.getInstance()
                    .collection("AllLaboratories")
                    .document(Common.city)
                    .collection("Branch")
                    .document(Common.currentLab.getLabId())
                    .collection("Doktori")
                    .document(Common.currentDoktor.getDoktorId());

            doktorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful())
            {
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists())
                {
                    CollectionReference date=FirebaseFirestore.getInstance()
                            .collection("AllLaboratories")
                            .document(Common.city)
                            .collection("Branch")
                            .document(Common.currentLab.getLabId())
                            .collection("Doktori")
                            .document(Common.currentDoktor.getDoktorId())
                            .collection(bookDate);//simpleformatdate
                    date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                QuerySnapshot querySnapshot=task.getResult();
                                if(querySnapshot.isEmpty())
                                    iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                else{
                                    List<TimeSlot> timeSlot=new ArrayList<>();
                                    for(QueryDocumentSnapshot document:task.getResult())
                                        timeSlot.add(document.toObject(TimeSlot.class));
                                    iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlot);


                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());

                        }
                    });
                }
            }
                }
            });


    }



    static BookingStep3Fragment instance;
    public static BookingStep3Fragment getInstance(){
        if(instance==null)
            instance=new BookingStep3Fragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
    View itemView=inflater.inflate(R.layout.fragment_booking_step_three,container,false);
unbinder= ButterKnife.bind(this,itemView);
init(itemView);


        return itemView;

    }

    private void init(View itemView) {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate=Calendar.getInstance();
        startDate.add(Calendar.DATE,0);

        Calendar endDate=Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar=new HorizontalCalendar.Builder(itemView,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.bookingDate.getTimeInMillis()!=date.getTimeInMillis())
                {
                    Common.bookingDate=date;
                    loadAvailableTimeSlotOfDoktor(Common.currentDoktor.getDoktorId(),
                            simpleDateFormat.format(date.getTime()));

                }
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener=this;
        localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
   simpleDateFormat=new SimpleDateFormat("dd_MM_yyyy");
   dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

selected_date= Calendar.getInstance();
selected_date.add(Calendar.DATE,0);
    }



    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }



    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(getContext(),timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();
    }
}
