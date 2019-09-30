package com.example.pokusaj;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Adapter.LabAdapter;
import com.example.pokusaj.Adapter.MyHistoryAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Interface.IonLoadCountHistory;
import com.example.pokusaj.Interface.iOnLoadHistory;
import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.Model.UserBookingLoadEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class HistoryActivity extends AppCompatActivity implements iOnLoadHistory, IonLoadCountHistory{

    CollectionReference userBooking;
    IonLoadCountHistory ionLoadCountHistory;
    com.example.pokusaj.Interface.iOnLoadHistory iOnLoadHistory;
    @BindView(R.id.recycler_history)
    RecyclerView recycler_history;
    @BindView(R.id.txt_history)
    TextView txt_history;

    AlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ionLoadCountHistory=this;
        iOnLoadHistory=this;
        ButterKnife.bind(this);

        init();
        initView();
        
        loadUserBookingInformation();
    }

    private void loadUserBookingInformation() {
        CollectionReference userBooking=FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");


        //select booking info from firebase with done=false and timestamp greater today
        userBooking
                .whereEqualTo("done",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            List<BookingInformation> bookingInformations=new ArrayList<>();

                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation=queryDocumentSnapshot.toObject(BookingInformation.class);
                                    bookingInformation.setBookingId(queryDocumentSnapshot.getId());
                                    bookingInformations.add(bookingInformation);

                                }
                                ionLoadCountHistory.onLoadCountSuccess(task.getResult().size());
                                iOnLoadHistory.onLoadHistorySuccess(bookingInformations);
                            }
                            else
                            {
                                //
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iOnLoadHistory.onLoadHistoryFailed(e.getMessage());
            }
        });


        /*dialog.show();

        userBooking= FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");



                userBooking.whereEqualTo("done",true)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnLoadHistory.onLoadHistoryFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<BookingInformation> bookingInformations=new ArrayList<>();
//                            ionLoadCountHistory.onLoadCountSuccess(task.getResult().size());
                            for(DocumentSnapshot historySnapShot:task.getResult())
                            {
                                BookingInformation bookingInformation=historySnapShot.toObject(BookingInformation.class);
                                bookingInformation.setLabId(historySnapShot.getId());
                                bookingInformations.add(bookingInformation);
                            }
//                            iOnLoadHistory.onLoadHistorySuccess(bookingInformations);
                        }

                    }
                });


                //userBooking
                //        .whereEqualTo("done",true)
                //.orderBy("timestamp", Query.Direction.DESCENDING)


*/
    }

    private void initView() {
        recycler_history.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recycler_history.setLayoutManager(layoutManager);
        recycler_history.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

    }

    private void init() {
    dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

    }


    @Override
    public void onLoadHistorySuccess(List<BookingInformation> historyList) {
        MyHistoryAdapter histAdapter=new MyHistoryAdapter(this,historyList);
        recycler_history.setAdapter(histAdapter);
        dialog.dismiss();
    }

    @Override
    public void onLoadHistoryFailed(String message) {

    }

    @Override
    public void onLoadCountSuccess(int count) {
        txt_history.setText(new StringBuilder("Istorija pregleda (")
        .append(count)
        .append(")"));
    }
}
