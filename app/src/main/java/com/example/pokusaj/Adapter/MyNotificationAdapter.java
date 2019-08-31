package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Model.MyNotification;
import com.example.pokusaj.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyViewHolder> {
    Context context;
    List<MyNotification> myNotificationList;

    public MyNotificationAdapter(Context context, List<MyNotification> myNotificationList) {
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
