package com.example.pokusaj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Adapter.MyTimeSlotAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Interface.INotificationCountListener;
import com.example.pokusaj.Interface.ITimeSlotLoadListener;
import com.example.pokusaj.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Formatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static com.example.pokusaj.Common.Common.simpleFormatDate;

public class StaffHomeActivity extends AppCompatActivity implements ITimeSlotLoadListener, INotificationCountListener {

    TextView txt_doktor_name;

    @Nullable @BindView(R.id.activity_staff_home)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    ActionBarDrawerToggle actionBarDrawerToggle;



    DocumentReference doktorDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    android.app.AlertDialog alertDialog;

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    TextView txt_notification_badge;
    CollectionReference notificationCollection;
CollectionReference currentBookDateCollection;

TextView notification_badge;

    EventListener<QuerySnapshot> notificationEvent;
    EventListener<QuerySnapshot> bookingEvent;


    ListenerRegistration notificationListener;
    ListenerRegistration bookingRealtimeListener;



    INotificationCountListener iNotificationCountListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);

        ButterKnife.bind(this);
        init();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        if(item.getItemId()==R.id.action_new_notification)
        {
            startActivity(new Intent(StaffHomeActivity.this,NotificationActivity.class));
            txt_notification_badge.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,
                R.string.open,
                R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if(menuItem.getItemId()==R.id.menu_exit)
                logOut();
                return true;
        }
    });

    View headerView=navigationView.getHeaderView(0);
    txt_doktor_name=(TextView)headerView.findViewById(R.id.txt_doktor_name);
    txt_doktor_name.setText(Common.currentDoktor.getName());


alertDialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        Calendar date=Calendar.getInstance();
        date.add(Calendar.DATE,0);
        loadAvailableTimeSlotOfDoktor(Common.currentDoktor.getDoktorId(),
                Common.simpleFormatDate.format(date.getTime()));
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recycler_time_slot.setLayoutManager(layoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate=Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate=Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar=new HorizontalCalendar.Builder(this,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .configure()
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.bookingDate.getTimeInMillis()!=date.getTimeInMillis())
                {
                    Common.bookingDate=date;
                    loadAvailableTimeSlotOfDoktor(Common.currentDoktor.getDoktorId(),
                            simpleFormatDate.format(date.getTime()));

                }
            }
        });



    }

    private void logOut() {
        Paper.init(this);
        Paper.book().delete(Common.LAB_KEY);
        Paper.book().delete(Common.DOKTOR_KEY);
        Paper.book().delete(Common.STATE_KEY);
        Paper.book().delete(Common.LOGGED_KEY);

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent mainActivity=new Intent(StaffHomeActivity.this,StaffMainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    private void loadAvailableTimeSlotOfDoktor(String doktorId, String bookDate) {

        alertDialog.show();

        ///AllLaboratories/NoviSad/Branch/HBRJEFF1Onzsb0Vr4xzV/Doktori/DRSA0NwCCbEmH9fviVMh


        doktorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        CollectionReference date = FirebaseFirestore.getInstance()
                                .collection("AllLaboratories")
                                .document(Common.state_name)
                                .collection("Branch")
                                .document(Common.selectedLab.getLabId())
                                .collection("Doktori")
                                .document(doktorId)
                                .collection(bookDate);//simpleformatdate
                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    else {
                                        List<TimeSlot> timeSlot = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult())
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


    private void init() {
        iTimeSlotLoadListener=this;
        iNotificationCountListener=this;
        initNotificationRealTimeUpdate();
        initBookingRealtimeUpdate();
        
    }

    private void initBookingRealtimeUpdate() {
        doktorDoc = FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Doktori")
                .document(Common.currentDoktor.getDoktorId());

        final Calendar date=Calendar.getInstance();
        date.add(Calendar.DATE,0);
        bookingEvent=new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                loadAvailableTimeSlotOfDoktor(Common.currentDoktor.getDoktorId(),
                        Common.simpleFormatDate.format(date.getTime()));
            }
        };
        currentBookDateCollection=doktorDoc.collection(Common.simpleFormatDate.format(date.getTime()));
        bookingRealtimeListener=currentBookDateCollection.addSnapshotListener(bookingEvent);


    }

    private void initNotificationRealTimeUpdate() {
        notificationCollection=FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Doktori")
                .document(Common.currentDoktor.getDoktorId())
                .collection("Notifications");

        notificationEvent=new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() > 0)
                    loadNotification();

            }
            };

notificationListener=notificationCollection.whereEqualTo("read",false)
        .addSnapshotListener(notificationEvent);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(StaffHomeActivity.this,"Fake function exit",Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(this,timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(this,""+message,Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_home_menu,menu);
        final MenuItem menuItem=menu.findItem(R.id.action_new_notification);
        txt_notification_badge=(TextView)menuItem.getActionView()
                .findViewById(R.id.notification_badge);

        loadNotification();

        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    private void loadNotification() {
        notificationCollection.whereEqualTo("read",false)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
Toast.makeText(StaffHomeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    iNotificationCountListener.onNotificationCountSuccess(task.getResult().size());
                }
            }
        });
    }

    @Override
    public void onNotificationCountSuccess(int count) {
if(count==0)
    txt_notification_badge.setVisibility(View.INVISIBLE);
else
{
    txt_notification_badge.setVisibility(View.VISIBLE);
    if(count<=9)
        txt_notification_badge.setText(String.valueOf(count));
    else
        txt_notification_badge.setText("9+");
}
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBookingRealtimeUpdate();
        initNotificationRealTimeUpdate();
    }

    @Override
    protected void onStop() {
        if(notificationListener!=null)
            notificationListener.remove();
        if(bookingRealtimeListener!=null)
            bookingRealtimeListener.remove();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(notificationListener!=null)
            notificationListener.remove();
        if(bookingRealtimeListener!=null)
            bookingRealtimeListener.remove();
        super.onDestroy();
    }
}
