package com.example.pokusaj;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.LabAdapter;
import com.example.pokusaj.Adapter.MySalonAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Interface.IBranchLoadListener;
import com.example.pokusaj.Interface.IGetDoktorListener;
import com.example.pokusaj.Interface.IOnLoadCountSalon;
import com.example.pokusaj.Interface.IUserLoginRememberListener;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.Model.Laboratory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LabListActivity extends AppCompatActivity implements IOnLoadCountSalon, IBranchLoadListener, IGetDoktorListener, IUserLoginRememberListener {
   @BindView(R.id.txt_salon_count)
    TextView txt_salon_count;

   @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

   IOnLoadCountSalon iOnLoadCountSalon;
   IBranchLoadListener iBranchLoadListener;

AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_list);

        ButterKnife.bind(this);
    initView();
    
    init();
    loadSalonBaseOnCity(Common.state_name);
    }

    private void loadSalonBaseOnCity(String name) {
    dialog.show();
        FirebaseFirestore.getInstance().collection("AllLaboratories")
                .document(name)
            .collection("Branch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if(task.isSuccessful())
                        {
                            List<Laboratory> labs=new ArrayList<>();
                           iOnLoadCountSalon.onLoadCountSalonSuccess(task.getResult().size());
                       for(DocumentSnapshot salonSnapShot:task.getResult())
                       {
                           Laboratory lab=salonSnapShot.toObject(Laboratory.class);
                      lab.setLabId(salonSnapShot.getId());
                           labs.add(lab);
                       }
                       iBranchLoadListener.onBranchLoadSuccess(labs);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();
        iOnLoadCountSalon=this;
    iBranchLoadListener=this;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(this,2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onLoadCountSalonSuccess(int count) {
        txt_salon_count.setText(new StringBuilder("Spisak klinika (")
        .append(count)
        .append(")"));

    }

    @Override
    public void onBranchLoadSuccess(List<Laboratory> laboratoryList) {
        LabAdapter salonAdapter=new LabAdapter(this,laboratoryList,this,this);
        recycler_salon.setAdapter(salonAdapter);
        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {

    }

    @Override
    public void onGetDoktorSuccess(Doktor doktor) {
    Common.currentDoktor=doktor;
    Paper.book().write(Common.DOKTOR_KEY,new Gson().toJson(doktor));
    }

    @Override
    public void onUserLoginSuccess(String user) {
        Paper.init(this);
        Paper.book().write(Common.LOGGED_KEY,user);
        Paper.book().write(Common.STATE_KEY,Common.state_name);
        Paper.book().write(Common.LAB_KEY,new Gson().toJson(Common.selectedLab));

    }
}
