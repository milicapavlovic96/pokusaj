package com.example.pokusaj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.R;

import java.util.ArrayList;
import java.util.List;

public class MyDoktorAdapter extends RecyclerView.Adapter<MyDoktorAdapter.MyViewHolder> {

    Context context;
    List<Doktor> doktorList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyDoktorAdapter(Context context, List<Doktor> doktorList) {
        this.context = context;
        this.doktorList = doktorList;
        cardViewList=new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyDoktorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem= LayoutInflater.from(context)
                .inflate(R.layout.layout_doktor,parent,false);

        return new MyDoktorAdapter.MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDoktorAdapter.MyViewHolder holder, int position) {
        holder.txt_doktor_name.setText(doktorList.get(position).getName());
        if(doktorList.get(position).getRatingTimes()!=null)
            holder.ratingBar.setRating(doktorList.get(position).getRating().floatValue() / doktorList.get(position).getRatingTimes());
        else
            holder.ratingBar.setRating(0);
        if(!cardViewList.contains(holder.card_doktor))
            cardViewList.add(holder.card_doktor);
//set background
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView : cardViewList) {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }
                holder.card_doktor.setCardBackgroundColor(context
                        .getResources().getColor(android.R.color.holo_orange_dark));
                //send local broadcast to enable button next
                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_DOKTOR_SELECTED,doktorList.get(pos));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);

            }

        });
    }

    @Override
    public int getItemCount() {
        return doktorList.size();
    }

   public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_doktor_name;
        RatingBar ratingBar;
        CardView card_doktor;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;



       public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
           this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
       }

       public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            card_doktor=(CardView)itemView.findViewById(R.id.card_doktor);
            txt_doktor_name=(TextView)itemView.findViewById(R.id.txt_doktor_name);
            ratingBar=(RatingBar)itemView.findViewById(R.id.rtb_doktor);
            itemView.setOnClickListener(this);
        }

       @Override
       public void onClick(View view) {
           iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
       }
   }
}
