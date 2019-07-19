package com.example.pokusaj.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.MyDoktorAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_doktor)
    RecyclerView recycler_doktor;

    private BroadcastReceiver doktorDoneReciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Doktor> doktorArrayList=intent.getParcelableArrayListExtra(Common.KEY_DOKTOR_LOAD_DONE);
            MyDoktorAdapter adapter=new MyDoktorAdapter(getContext(),doktorArrayList);
            recycler_doktor.setAdapter(adapter);
        }
    };

    static BookingStep2Fragment instance;
    public static BookingStep2Fragment getInstance(){
        if(instance==null)
            instance=new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(doktorDoneReciever,new IntentFilter(Common.KEY_DOKTOR_LOAD_DONE));

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(doktorDoneReciever);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View itemView=inflater.inflate(R.layout.fragment_booking_step_two,container,false);
        unbinder= ButterKnife.bind(this,itemView);
        initView();
        
        return itemView;

    }

    private void initView() {
        recycler_doktor.setHasFixedSize(true);
        recycler_doktor.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_doktor.addItemDecoration(new SpacesItemDecoration(4));
    }
}
