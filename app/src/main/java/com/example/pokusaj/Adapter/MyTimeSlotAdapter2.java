package com.example.pokusaj.Adapter;

import android.content.Context;
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
import com.example.pokusaj.DoneServicesActivity;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter2  extends RecyclerView.Adapter<MyTimeSlotAdapter2.MyViewHolder>{
    Context context;
    List<BookingInformation> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

    public IRecyclerItemSelectedListener getiRecyclerItemSelectedListener() {
        return iRecyclerItemSelectedListener;
    }

    public MyTimeSlotAdapter2(Context context) {
        this.context = context;
        this.timeSlotList=new ArrayList<>();
        this.localBroadcastManager=LocalBroadcastManager.getInstance(context);
        cardViewList=new ArrayList<>();
    }

    public MyTimeSlotAdapter2(Context context, List<BookingInformation> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager=LocalBroadcastManager.getInstance(context);
        cardViewList=new ArrayList<>();
    }


    public class MyViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public IRecyclerItemSelectedListener getiRecyclerItemSelectedListener() {
            return iRecyclerItemSelectedListener;
        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot=(CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot=(TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description=(TextView)itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public MyTimeSlotAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_time_slot,parent,false);


        return new MyTimeSlotAdapter2.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTimeSlotAdapter2.MyViewHolder holder, int position) {

        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        if (timeSlotList.size() == 0) {
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));



            //20 tutorial
            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {

                }
            });
        }
        else {
            for (BookingInformation slotValue : timeSlotList) {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {

                    if (!slotValue.isDone()) {
                        holder.card_time_slot.setTag(Common.DISABLE_TAG);
                        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                        holder.txt_time_slot_description.setText("Full");
                        holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                        holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
//                    holder.card_time_slot.setEnabled(false);
                        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelectedListener(View view, int pos) {
                                //here we get booking info and store to common.current booking info,after that done services activity
                                FirebaseFirestore.getInstance()
                                        .collection("AllLaboratories")
                                        .document(Common.state_name)
                                        .collection("Branch")
                                        .document(Common.selectedLab.getLabId())
                                        .collection("Doktori")
                                        .document(Common.currentDoktor.getDoktorId())
                                        .collection(Common.simpleFormatDate.format(Common.bookingDate.getTime()))
                                        .document(slotValue.getSlot().toString())
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                Common.currentBookingInformation = task.getResult().toObject(BookingInformation.class);
                                                Common.currentBookingInformation.setBookingId(task.getResult().getId());
                                                context.startActivity(new Intent(context, DoneServicesActivity.class));

                                            }
                                        }
                                    }
                                });
                            }
                        });
                    } else
                        {
                            //if service is done
                            holder.card_time_slot.setTag(Common.DISABLE_TAG);
                            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                            holder.txt_time_slot_description.setText("Done");
                            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                                @Override
                                public void onItemSelectedListener(View view, int pos) {

                                }
                            });
                    }
                }
            else {
                    if (holder.getiRecyclerItemSelectedListener()==null) {
                        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelectedListener(View view, int pos) {

                            }
                        });
                    }
                }
            }
        }
        //ADD all card to list
        if(!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);
        //check if time slot is available


    }


    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }


}

