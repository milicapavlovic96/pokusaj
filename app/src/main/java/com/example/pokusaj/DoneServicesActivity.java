package com.example.pokusaj;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Interface.IDoktorServicesLoadListener;
import com.example.pokusaj.Model.DoktorServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class DoneServicesActivity extends AppCompatActivity implements IDoktorServicesLoadListener {

    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;

    @BindView(R.id.txt_customer_phone)
    TextView txt_customer_phone;

    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_services;

    @BindView(R.id.chip_group_shopping)
    ChipGroup chip_group_shopping;

    @BindView(R.id.edt_services)
    AppCompatAutoCompleteTextView edt_services;

    @BindView(R.id.img_customer_hair)
    ImageView img_customer_hair;

    @BindView(R.id.btn_shopping)
    Button btn_shopping;

    @BindView(R.id.btn_finish)
   Button btn_finish;

AlertDialog dialog;
IDoktorServicesLoadListener iDoktorServicesLoadListener;

HashSet<DoktorServices> serviceAdded=new HashSet<>();

LayoutInflater inflater;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_services);

        ButterKnife.bind(this);
        init();
        
        setCustomerInformation();

        loadDoktorServices();
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();

        inflater=LayoutInflater.from(this);
        iDoktorServicesLoadListener=this;
    }

    private void loadDoktorServices() {
        dialog.show();
        FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Services")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iDoktorServicesLoadListener.onDoktorServicesLoadFailed(e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<DoktorServices> doktorServices=new ArrayList<>();
                    for(DocumentSnapshot doktorSnapshot:task.getResult())
                    {
                        DoktorServices services=doktorSnapshot.toObject(DoktorServices.class);
                    doktorServices.add(services);
                    }
                    iDoktorServicesLoadListener.onDoktorServicesLoadSuccess(doktorServices);
                }
            }
        });
    }

    private void setCustomerInformation() {
    txt_customer_name.setText(Common.currentBookingInformation.getCustomerName());
    txt_customer_phone.setText(Common.currentBookingInformation.getCustomerPhone());
    }

    @Override
    public void onDoktorServicesLoadSuccess(List<DoktorServices> doktorServicesList) {
        List<String> nameServices=new ArrayList<>();
        Collections.sort(doktorServicesList, new Comparator<DoktorServices>() {
            @Override
            public int compare(DoktorServices doktorServices, DoktorServices t1) {
                return doktorServices.getName().compareTo(t1.getName());

            }
        });

        for(DoktorServices doktorServices:doktorServicesList)
            nameServices.add(doktorServices.getName());

        ArrayAdapter<String> adapter =new ArrayAdapter<>(this, android.R.layout.select_dialog_item,nameServices);
        edt_services.setThreshold(1);
        edt_services.setAdapter(adapter);

edt_services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int index = nameServices.indexOf(edt_services.getText().toString().trim());

        if (!serviceAdded.contains(doktorServicesList.get(index))) {
            serviceAdded.add(doktorServicesList.get(index));
            Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
            item.setText(edt_services.getText().toString());
            item.setTag(i);
            edt_services.setText("");

            item.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chip_group_services.removeView(view);
                    serviceAdded.remove((int) item.getTag());
                }
            });

            chip_group_services.addView(item);
        } else {
            edt_services.setText("");
        }
    }
});
        dialog.dismiss();
    }

    @Override
    public void onDoktorServicesLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    dialog.dismiss();
    }
}
