package com.example.pokusaj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.pokusaj.Adapter.MyViewPagerAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.NonSwipeViewPager;
import com.example.pokusaj.Model.Doktor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference labRef;


    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

//view pager vrsi navigaciju izmedju fragmenata u zavisnosti od step-a
    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if(Common.step==3||Common.step>0){
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if(Common.step<3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick() {
//jos pri izboru laboratorije na step 1 odmah se ucitavaju doktori na osnovu id-a
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) {
                if (Common.currentLab != null)
                    loadDoktorByLab(Common.currentLab.getLabId());
            }
            //viewPager.setCurrentItem(Common.step);
         else if (Common.step == 2)
        {
            if (Common.currentDoktor != null)
                loadTimeSlotDoktor(Common.currentDoktor.getDoktorId());
        }
            else if (Common.step == 3)
            {
                if (Common.currentTimeSlot != -1)
                    confirmBooking();
            }
        viewPager.setCurrentItem(Common.step);

    }
    }
//intent se salje local broadcast manageru i na osnovu key-a fragmenti ucitavaju deo intenta koji im je potreban
    private void confirmBooking() {
       //send broadcast to fragment step four
        Intent intent=new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
            }

    private void loadTimeSlotDoktor(String doktorId) {
        Intent intent=new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadDoktorByLab(String labId) {
        dialog.show();
        if (!TextUtils.isEmpty(Common.city)) {
            labRef = FirebaseFirestore.getInstance()
                    .collection("AllLaboratories")
                    .document(Common.city)
                    .collection("Branch")
                    .document(labId)
                    .collection("Doktori");
            labRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    ArrayList<Doktor> doktori=new ArrayList<Doktor>();
                    for(QueryDocumentSnapshot doktorSnapShot:task.getResult())
                    {
                        Doktor doktor=doktorSnapShot.toObject(Doktor.class);
                        doktor.setPassword("");
                        doktor.setDoktorId(doktorSnapShot.getId());
                        doktori.add(doktor);
                    }
                    Intent intent=new Intent(Common.KEY_DOKTOR_LOAD_DONE);
                    intent.putParcelableArrayListExtra(Common.KEY_DOKTOR_LOAD_DONE,doktori);
                    localBroadcastManager.sendBroadcast(intent);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

               dialog.dismiss();
                }
            });
        }
    }

    private BroadcastReceiver buttonNextReciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step=intent.getIntExtra(Common.KEY_STEP,0);
            if(step==1)
                Common.currentLab=intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if(step==2)
                Common.currentDoktor=intent.getParcelableExtra(Common.KEY_DOKTOR_SELECTED);
            else if(step==3)
                Common.currentTimeSlot=intent.getIntExtra(Common.KEY_TIME_SLOT,-1);



            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReciever);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ButterKnife.bind(BookingActivity.this);

        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReciever,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));


        setupStepView();
        setColorButton();
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
       viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //show step
                stepView.go(position,true);
                if(position==0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
    if(btn_next_step.isEnabled())
    {
        btn_next_step.setBackgroundResource(R.color.colorButton);

    }
    else
    {
        btn_next_step.setBackgroundResource(android.R.color.darker_gray);
    }
    if(btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);

        }
    else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
    List<String> stepList=new ArrayList<>();
    stepList.add("Laboratorija");
    stepList.add("Doktor");
    stepList.add("Vreme");
    stepList.add("Potvrdi");
    stepView.setSteps(stepList);

    }
}
