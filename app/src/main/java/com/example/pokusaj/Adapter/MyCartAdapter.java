package com.example.pokusaj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Database.CartDatabase;
import com.example.pokusaj.Database.CartItem;
import com.example.pokusaj.Database.DatabaseUtils;
import com.example.pokusaj.Interface.ICartItemUpdateListener;
import com.example.pokusaj.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {



    Context context;
    List<CartItem> cartItemList;

    CartDatabase cartDatabase;
    ICartItemUpdateListener iCartItemUpdateListener;



    public MyCartAdapter(Context context, List<CartItem> cartItemList,ICartItemUpdateListener iCartItemUpdateListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.iCartItemUpdateListener=iCartItemUpdateListener;
        this.cartDatabase=CartDatabase.getInstance(context);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    interface IImageButtonListener {
        void onImageButtonClick(View view, int pos, boolean isDecrease);
    }

    @NonNull
    @Override
    public MyCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView= LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item,parent,false


        );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.MyViewHolder holder, int position) {
        Picasso.get().load(cartItemList.get(position).getProductImage()).into(holder.img_product);
        holder.txt_cart_price.setText(new StringBuilder("Ukupno: ").append(cartItemList.get(position).getProductPrice()));
        holder.txt_cart_name.setText(new StringBuilder(cartItemList.get(position).getProductName()));
        holder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(position).getProductQuantity())));
        holder.setListener(new IImageButtonListener() {
            @Override
            public void onImageButtonClick(View view, int pos, boolean isDecrease) {
                if(isDecrease)
                {
                    if(cartItemList.get(pos).getProductQuantity()>0)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                .get(pos)
                                .getProductQuantity()-1);
                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                        holder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(position).getProductQuantity())));

                    }
                    else if(cartItemList.get(pos).getProductQuantity()==0)
                    {
                        DatabaseUtils.deleteCart(cartDatabase,cartItemList.get(pos));
                        cartItemList.remove(pos);
                        notifyItemRemoved(pos);
                    }

                }
                else
                {
                    if(cartItemList.get(pos).getProductQuantity()<99)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                        .get(pos)
                                        .getProductQuantity()+1);
                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                        holder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(position).getProductQuantity())));

                    }
                }
                iCartItemUpdateListener.onCartItemUpdateListener();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_cart_name,txt_cart_price,txt_quantity;
        ImageView img_decrease,img_increase,img_product;
        IImageButtonListener listener;


        public void setListener(IImageButtonListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cart_name=(TextView)itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price=(TextView)itemView.findViewById(R.id.txt_cart_price);
            txt_quantity=(TextView)itemView.findViewById(R.id.txt_cart_quantity);

            img_decrease=(ImageView)itemView.findViewById(R.id.img_decrease);
            img_increase=(ImageView)itemView.findViewById(R.id.img_increase);
            img_product=(ImageView)itemView.findViewById(R.id.cart_img);


        //event
            img_decrease.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view,getAdapterPosition(),true);
                }
            });
            img_increase.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view,getAdapterPosition(),false);
                }


            });
        }

    }

}
