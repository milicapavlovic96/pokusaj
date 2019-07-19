package com.example.pokusaj.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.MySalonAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Interface.IAllSalonLoadListener;
import com.example.pokusaj.Interface.IBranchLoadListener;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IAllSalonLoadListener, IBranchLoadListener {


    CollectionReference allLabRef;
    CollectionReference branchRef;

    IAllSalonLoadListener iAllSalonLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    Unbinder unbinder;
    AlertDialog dialog;


    static BookingStep1Fragment instance;

    public static  BookingStep1Fragment getInstance()
    {
        if(instance==null)
            instance=new BookingStep1Fragment();
        return instance;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       allLabRef= FirebaseFirestore.getInstance().collection("AllLaboratories");

        iAllSalonLoadListener=this;
        iBranchLoadListener=this;

        dialog=new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView= inflater.inflate(R.layout.fragment_booking_step_one,container,false);
    unbinder= ButterKnife.bind(this,itemView);
    
    initView();
    loadAllSalon();
    
    
    return itemView;

    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllSalon() {
        allLabRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<String> list=new ArrayList<>();
                    list.add("Please choose city");
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                        list.add(documentSnapshot.getId());
                    iAllSalonLoadListener.onAllSalonLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllSalonLoadListener.onAllSalonLoadFailed(e.getMessage());
            }
        });


    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
    spinner.setItems(areaNameList);
    spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
            if(position>0)
            {
                loadBranchOfCity(item.toString());
            }
        }
    });
    }

    private void loadBranchOfCity(String cityName) {
        dialog.show();
        Common.city=cityName;
        branchRef=FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(cityName)
                .collection("Branch");

    branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            List<Laboratory> list=new ArrayList<>();
            if(task.isSuccessful())
            {
                for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                {
                   Laboratory laboratory=documentSnapshot.toObject(Laboratory.class);
                   laboratory.setLabId(documentSnapshot.getId());
                    list.add(laboratory);

                }
                iBranchLoadListener.onBranchLoadSuccess(list);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            iBranchLoadListener.onBranchLoadFailed(e.getMessage());
        }
    });
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Laboratory> laboratoryList) {
        MySalonAdapter adapter=new MySalonAdapter(getActivity(),laboratoryList);
        recycler_salon.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
dialog.dismiss();
    }
}
