package com.example.pokusaj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.pokusaj.Adapter.MyCartAdapter;
import com.example.pokusaj.Database.CartDatabase;
import com.example.pokusaj.Database.CartItem;
import com.example.pokusaj.Database.DatabaseUtils;
import com.example.pokusaj.Interface.ICartItemLoadListener;
import com.example.pokusaj.Interface.ICartItemUpdateListener;
import com.example.pokusaj.Interface.ISumCartListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements  ICartItemUpdateListener, ISumCartListener, ICartItemLoadListener {


    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.btn_submit_cart)
    Button btn_submit_cart;
    CartDatabase cartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(CartActivity.this);

        cartDatabase= CartDatabase.getInstance(this);
        DatabaseUtils.getAllCart(cartDatabase,this);
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation()));


    }



    @Override
    public void onCartItemUpdateListener() {
        DatabaseUtils.sumCart(cartDatabase,this);
    }

    @Override
    public void onSumCartSuccess(Long value) {
        txt_total_price.setText(new StringBuilder("$").append(value));

    }

    @Override
    public void onGetAllItemFromCartSuccess(List<CartItem> cartItemList) {
        MyCartAdapter adapter=new MyCartAdapter(this,cartItemList,this);
        recycler_cart.setAdapter(adapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
