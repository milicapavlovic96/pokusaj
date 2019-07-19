package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.R;

import java.util.List;

public class MyDoktorAdapter extends RecyclerView.Adapter<MyDoktorAdapter.MyViewHolder> {

    Context context;
    List<Doktor> doktorList;

    public MyDoktorAdapter(Context context, List<Doktor> doktorList) {
        this.context = context;
        this.doktorList = doktorList;
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
        holder.ratingBar.setRating((float)doktorList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return doktorList.size();
    }

   public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView txt_doktor_name;
        RatingBar ratingBar;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            txt_doktor_name=(TextView)itemView.findViewById(R.id.txt_doktor_name);
            ratingBar=(RatingBar)itemView.findViewById(R.id.rtb_doktor);

        }
    }
}
