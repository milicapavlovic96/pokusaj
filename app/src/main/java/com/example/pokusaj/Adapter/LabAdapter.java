package com.example.pokusaj.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.CustomLoginDialog;
import com.example.pokusaj.Interface.IDialogClickListener;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.R;
import com.example.pokusaj.StaffHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.MyViewHolder> implements IDialogClickListener {


    Context context;
    List<Laboratory> laboratoryList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public LabAdapter(Context context, List<Laboratory> laboratoryList) {
        this.context = context;
        this.laboratoryList = laboratoryList;
        cardViewList=new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem= LayoutInflater.from(context)
                .inflate(R.layout.layout_lab,parent,false);

        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_laboratory_name.setText(laboratoryList.get(position).getName());
        holder.txt_laboratory_address.setText(laboratoryList.get(position).getAddress());
        if(!cardViewList.contains(holder.card_salon))
            cardViewList.add(holder.card_salon);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
              Common.selectedLab=laboratoryList.get(pos);
              showLoginDialog();


                /*for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                holder.card_salon.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_orange_dark));

                //send broadcast to tell booking activity enable button next
                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE,laboratoryList.get(pos));
                intent.putExtra(Common.KEY_STEP,1);
                localBroadcastManager.sendBroadcast(intent);*/

            }
        });

    }

    private void showLoginDialog() {
        CustomLoginDialog.getInstance()
                .showLoginDialog("STAFF LOGIN",
                "LOGIN",
        "CANCEL",
        context,
        this);
    }

    @Override
    public int getItemCount() {
        return laboratoryList.size();
    }

    @Override
    public void onClickPositiveButton(DialogInterface dialogInterface, String userName, String password) {
        AlertDialog loading = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();

        loading.show();

        FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Doktori")
                .whereEqualTo("username", userName)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                dialogInterface.dismiss();
                                loading.dismiss();
                                Intent staffHome = new Intent(context, StaffHomeActivity.class);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(staffHome);
                            } else {
                                loading.dismiss();
                                Toast.makeText(context, "Wrong username / password or wrong salon", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_laboratory_name,txt_laboratory_address;
        CardView card_salon;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            card_salon=(CardView)itemView.findViewById(R.id.card_salon);
            txt_laboratory_address=(TextView) itemView.findViewById(R.id.txt_laboratory_address);
            txt_laboratory_name=(TextView)itemView.findViewById(R.id.txt_laboratory_name);

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());

        }
    }
}
