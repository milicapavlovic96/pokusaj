package com.example.pokusaj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.R;

import java.util.ArrayList;
import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> {


    Context context;
    List<Laboratory> laboratoryList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(Context context, List<Laboratory> laboratoryList) {
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
            for(CardView cardView:cardViewList)
                cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            holder.card_salon.setCardBackgroundColor(context.getResources()
            .getColor(android.R.color.holo_orange_dark));

                    //send broadcast to tell booking activity enable button next
                    Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_SALON_STORE,laboratoryList.get(pos));
                    intent.putExtra(Common.KEY_STEP,1);
                    localBroadcastManager.sendBroadcast(intent);

        }
    });

    }

    @Override
    public int getItemCount() {
        return laboratoryList.size();
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
