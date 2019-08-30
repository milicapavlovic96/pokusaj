package com.example.pokusaj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.LabListActivity;
import com.example.pokusaj.Model.City;
import com.example.pokusaj.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyStateAdapter extends RecyclerView.Adapter<MyStateAdapter.MyViewHolder> {

    //posle interfejsa,modela adaptera fetchujemo svaki state u nas all salon collection
    Context context;
    List<City> cityList;
    int lastPosition=-1;

    public MyStateAdapter(Context context, List<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView= LayoutInflater.from(context)
               .inflate(R.layout.layout_state,parent,false);
       return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.txt_state_name.setText(cityList.get(position).getName());

        setAnimation(holder.itemView,position);

holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
    @Override
    public void onItemSelectedListener(View view, int pos) {
        Common.state_name=cityList.get(position).getName();
        context.startActivity(new Intent(context, LabListActivity.class));
    }
});
    }

    private void setAnimation(View itemView,int position) {
        if(position>lastPosition)
        {
            Animation animation= AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
            lastPosition=position;
        }

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.txt_state_name)
        TextView txt_state_name;

    IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
