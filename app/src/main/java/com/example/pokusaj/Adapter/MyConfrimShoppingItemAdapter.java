package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Database.CartItem;
import com.example.pokusaj.Model.ShoppingItem;
import com.example.pokusaj.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyConfrimShoppingItemAdapter extends RecyclerView.Adapter<MyConfrimShoppingItemAdapter.MyViewHolder> {
   Context context;
   List<CartItem> shoppingItemList;

    public MyConfrimShoppingItemAdapter(Context context, List<CartItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
    }

    @NonNull
    @Override
    public MyConfrimShoppingItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_confirm_shopping,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyConfrimShoppingItemAdapter.MyViewHolder holder, int position) {
        Picasso.get().load(shoppingItemList.get(position).getProductImage())
                .into(holder.item_image);
        holder.txt_name.setText(new StringBuilder(shoppingItemList.get(position).getProductName())
        .append(" x")
        .append(shoppingItemList.get(position).getProductQuantity()));
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image)
        ImageView item_image;
        @BindView(R.id.txt_name)
        TextView txt_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
