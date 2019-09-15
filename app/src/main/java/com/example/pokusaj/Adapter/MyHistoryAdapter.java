package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyViewHolder> {

Context context;
List<BookingInformation> bookingInformationList;

    public MyHistoryAdapter(Context context, List<BookingInformation> bookingInformationList) {
        this.context = context;
        this.bookingInformationList = bookingInformationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        Unbinder unbinder;

        @BindView(R.id.txt_laboratory_name)
        TextView txt_laboratory_name;

        @BindView(R.id.txt_laboratory_address)
        TextView txt_laboratory_adress;

        @BindView(R.id.txt_booking_time_text)
        TextView txt_booking_time_text;

        @BindView(R.id.txt_booking_doktor_text)
        TextView txt_booking_doktor_text;
        @BindView(R.id.txt_booking_date)
        TextView txt_booking_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        unbinder= ButterKnife.bind(this,itemView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_history,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.txt_booking_date.setText(bookingInformationList.get(position).getTime());
        holder.txt_booking_doktor_text.setText(bookingInformationList.get(position).getDoktorName());

    holder.txt_booking_time_text.setText(bookingInformationList.get(position).getTime());

    holder.txt_laboratory_adress.setText(bookingInformationList.get(position).getLabAddress());
    holder.txt_laboratory_name.setText(bookingInformationList.get(position).getLabName());

    }

    @Override
    public int getItemCount() {
        return bookingInformationList.size();
    }


}
