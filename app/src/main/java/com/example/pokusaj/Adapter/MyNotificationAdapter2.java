package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Common.MyDiffCallBack;
import com.example.pokusaj.Model.MyNotification2;
import com.example.pokusaj.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyNotificationAdapter2 extends RecyclerView.Adapter<MyNotificationAdapter2.MyViewHolder> {
    Context context;
    List<MyNotification2> myNotificationList;

    public MyNotificationAdapter2(Context context, List<MyNotification2> myNotificationList) {
        this.context = context;
        this.myNotificationList = myNotificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.layout_notification_item,parent,false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_notification_title.setText(myNotificationList.get(position).getTitle());

        holder.txt_notification_content.setText(myNotificationList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return myNotificationList.size();
    }

    public void updateList(List<MyNotification2> newList) {
        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(new MyDiffCallBack(this.myNotificationList,newList));
        myNotificationList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_notification_title)
        TextView txt_notification_title;

        @BindView(R.id.txt_notification_content)
        TextView txt_notification_content;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

