package com.example.pokusaj;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Adapter.MyStateAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Interface.IOnAllStateLoadListener;
import com.example.pokusaj.Model.City;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.Model.Laboratory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class StaffMainActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference allLabsCollection;
    IOnAllStateLoadListener iOnAllStateLoadListener;

    MyStateAdapter adapter;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StaffMainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful())
                        {
Common.updateToken(StaffMainActivity.this,task.getResult().getToken());
                            Log.d("EDMTTOken",task.getResult().getToken());
                        }
                    }
                });

        Paper.init(this);
        String user=Paper.book().read(Common.LOGGED_KEY);

        if(TextUtils.isEmpty(user))
        {
            setContentView(R.layout.staff_main_activity_layout);
            ButterKnife.bind(this);
            initView();
            init();
            loadAllStateFromFireStore();
        }
    else
        {
            Gson gson=new Gson();
            Common.state_name=Paper.book().read(Common.STATE_KEY);
            Common.selectedLab=gson.fromJson(Paper.book().read(Common.LAB_KEY,""),
                    new TypeToken<Laboratory>(){}.getType());
            Common.currentDoktor=gson.fromJson(Paper.book().read(Common.DOKTOR_KEY,""),
                    new TypeToken<Doktor>(){}.getType());

            Intent intent=new Intent(this,StaffHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        
    }

    private void loadAllStateFromFireStore() {
        dialog.show();

        allLabsCollection.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<City> cities=new ArrayList<>();
                    for(DocumentSnapshot citySnapShot:task.getResult())
                    {
                        City city=citySnapShot.toObject(City.class);
                    cities.add(city);
                    }
                    iOnAllStateLoadListener.onAllStateLoadSucess(cities);
                }
            }
        });
    }

    private void init() {
        allLabsCollection= FirebaseFirestore.getInstance().collection("AllLaboratories");
        iOnAllStateLoadListener=this;
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onAllStateLoadSucess(List<City> cityList) {
        adapter=new MyStateAdapter(this,cityList);
        recycler_state.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();

    }
}
