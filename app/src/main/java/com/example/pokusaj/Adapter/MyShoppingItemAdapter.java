package com.example.pokusaj.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Database.CartDatabase;
import com.example.pokusaj.Database.CartItem;
import com.example.pokusaj.Database.DatabaseUtils;
import com.example.pokusaj.Interface.IRecyclerItemSelectedListener;
import com.example.pokusaj.Model.ShoppingItem;
import com.example.pokusaj.R;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.List;

public class MyShoppingItemAdapter extends RecyclerView.Adapter<MyShoppingItemAdapter.MyViewHolder> {

    Context context;
    List<ShoppingItem> shoppingItemList;
    CartDatabase cartDatabase;

    public MyShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
        cartDatabase=CartDatabase.getInstance(context);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_shopping_item,parent,false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(shoppingItemList.get(position).getImage()).into(holder.img_shopping_item);
        holder.txt_shopping_item_name.setText(Common.formatShoppingItemName(shoppingItemList.get(position).getName()));
        holder.txt_shopping_item_price.setText(new StringBuilder("din").append(shoppingItemList.get(position).getPrice()));

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                CartItem cartItem=new CartItem();
                cartItem.setProductId(shoppingItemList.get(pos).getId());
                cartItem.setProductName(shoppingItemList.get(pos).getName());
                cartItem.setProductImage(shoppingItemList.get(pos).getImage());
                cartItem.setProductQuantity(1);
                cartItem.setProductPrice(shoppingItemList.get(pos).getPrice());
                cartItem.setUserPhone(Common.currentUser.getPhoneNumber());

                DatabaseUtils.insertToCart(cartDatabase,cartItem);
                Toast.makeText(context,"Added to Cart",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_shopping_item_name,txt_shopping_item_price,txt_add_to_cart;
        ImageView img_shopping_item;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;



        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            img_shopping_item=(ImageView)itemView.findViewById(R.id.img_shopping_item);
            txt_shopping_item_name=(TextView)itemView.findViewById(R.id.txt_name_shopping_item);
            txt_shopping_item_price=(TextView)itemView.findViewById(R.id.txt_price_shopping_item);
            txt_add_to_cart=(TextView)itemView.findViewById(R.id.txt_add_to_cart);
            txt_add_to_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
