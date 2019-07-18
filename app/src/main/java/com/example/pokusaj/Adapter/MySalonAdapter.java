package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.R;

import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> {


    Context context;
    List<Laboratory> laboratoryList;

    public MySalonAdapter(Context context, List<Laboratory> laboratoryList) {
        this.context = context;
        this.laboratoryList = laboratoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem= LayoutInflater.from(context)
                .inflate(R.layout.layout_salon,parent,false);

        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_laboratory_name.setText(laboratoryList.get(position).getName());
        holder.txt_laboratory_address.setText(laboratoryList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return laboratoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_laboratory_name,txt_laboratory_address;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            txt_laboratory_address=(TextView) itemView.findViewById(R.id.txt_laboratory_address);
        txt_laboratory_name=(TextView)itemView.findViewById(R.id.txt_laboratory_name);
        }
    }
}
