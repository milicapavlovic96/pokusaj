package com.example.pokusaj.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.MyConfrimShoppingItemAdapter;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Database.CartItem;
import com.example.pokusaj.Interface.IBottomSheetDialogOnDismissListener;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.Model.DoktorServices;
import com.example.pokusaj.Model.FCMResponse;
import com.example.pokusaj.Model.FCMSendData;
import com.example.pokusaj.Model.Invoice;
import com.example.pokusaj.Model.MyToken;
import com.example.pokusaj.Model.ShoppingItem;
import com.example.pokusaj.Model.TokenUser;
import com.example.pokusaj.R;
import com.example.pokusaj.Retrofit.IFCMService;
import com.example.pokusaj.Retrofit.RetroFitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TotalPriceFragment extends BottomSheetDialogFragment {
    Unbinder unbinder;

    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_services;

    @BindView(R.id.recycler_view_shopping)
    RecyclerView recycler_view_shopping;

//    @BindView(R.id.txt_salon_name)
//    TextView txt_salon_name;

    @BindView(R.id.txt_doktor_name)
    TextView txt_doktor_name;

    @BindView(R.id.txt_time)
    TextView txt_time;

    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;

    @BindView(R.id.txt_customer_phone)
    TextView txt_customer_phone;

    @BindView(R.id.txt_total_price)
    TextView txt_total_price;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    HashSet<DoktorServices> servicesAdded;

    //List<ShoppingItem> shoppingItemList;

    IFCMService ifcmService;

    AlertDialog dialog;

    String image_url;

    IBottomSheetDialogOnDismissListener iBottomSheetDialogOnDismissListener;

    private static  TotalPriceFragment instance;

    public TotalPriceFragment(IBottomSheetDialogOnDismissListener iBottomSheetDialogOnDismissListener) {
        this.iBottomSheetDialogOnDismissListener=iBottomSheetDialogOnDismissListener;
    }

    public static  TotalPriceFragment getInstance(IBottomSheetDialogOnDismissListener iBottomSheetDialogOnDismissListener)
    {
        return instance==null?new TotalPriceFragment(iBottomSheetDialogOnDismissListener):instance;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView=inflater.inflate(R.layout.fragment_total_price,container,false);

        unbinder= ButterKnife.bind(this,itemView);
        
        init();
        
        initView();

        getBundle(getArguments());

        setInformation();

        return itemView;


    }

    private void setInformation() {
        //txt_salon_name.setText(Common.selectedLab.getName());
        //txt_doktor_name.setText(Common.currentDoktor.getName());
        txt_time.setText(Common.convertTimeSlotToString(Common.currentBookingInformation.getSlot().intValue()));
        txt_customer_name.setText(Common.currentBookingInformation.getCustomerName());
        txt_customer_phone.setText(Common.currentBookingInformation.getCustomerPhone());

        if(servicesAdded.size()>0){
            int i=0;
            for(DoktorServices services:servicesAdded)
            {
                Chip chip=(Chip) getLayoutInflater().inflate(R.layout.chip_item,null);
                chip.setText(services.getName());
                chip.setTag(i);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        servicesAdded.remove((int)view.getTag());
                        chip_group_services.removeView(view);

                        calculatePrice();
                    }
                });
                chip_group_services.addView(chip);

                i++;


            }
        }
        if(Common.currentBookingInformation.getCartItemList()!=null)
        {
            if(Common.currentBookingInformation.getCartItemList().size()>0)
            {
                MyConfrimShoppingItemAdapter adapter=new MyConfrimShoppingItemAdapter(getContext(),Common.currentBookingInformation.getCartItemList());
                recycler_view_shopping.setAdapter(adapter);
            }
            calculatePrice();
        }
    }

    private double calculatePrice() {

        double price=Common.DEFAULT_PRICE;
        for(DoktorServices services: servicesAdded)
            price+=services.getPrice();
        if(Common.currentBookingInformation.getCartItemList()!=null)
        {
            for(CartItem cartItem: Common.currentBookingInformation.getCartItemList())
                price+=(cartItem.getProductPrice()*cartItem.getProductQuantity());
        }

        txt_total_price.setText(new StringBuilder(Common.MONEY_SIGN).append(price));

        return price;
    }


    private void getBundle(Bundle arguments) {

   this.servicesAdded=new Gson()
           .fromJson(arguments.getString(Common.SERVICES_ADDED),
                   new TypeToken<HashSet<DoktorServices>>(){}.getType());

//        this.shoppingItemList=new Gson()
//                .fromJson(arguments.getString(Common.SHOPPING_LIST),
//                        new TypeToken<List<ShoppingItem>>(){}.getType());


        image_url=arguments.getString(Common.IMAGE_DOWNLOADABLE_URL);
    }

    private void initView() {
        recycler_view_shopping.setHasFixedSize(true);
        recycler_view_shopping.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                DocumentReference bookingSet= FirebaseFirestore.getInstance()
                        .collection("AllLaboratories")
                        .document(Common.state_name)
                        .collection("Branch")
                        .document(Common.selectedLab.getLabId())
                        .collection("Doktori")
                        .document(Common.currentDoktor.getDoktorId())
                        .collection(Common.simpleFormatDate.format(Common.bookingDate.getTime()))
                        .document(Common.currentBookingInformation.getBookingId());

            bookingSet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().exists())
                        {
                            Map<String,Object> dataUpdate=new HashMap<>();
                            dataUpdate.put("done",true);
                            bookingSet.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        createInvoice();
                                    }
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    private void createInvoice() {
        CollectionReference invoiceRef=FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Invoices");

        Invoice invoice=new Invoice();

        invoice.setDoktorId(Common.currentDoktor.getDoktorId());
        invoice.setDoktorName(Common.currentDoktor.getName());

        invoice.setLabId(Common.selectedLab.getLabId());
        invoice.setLabName(Common.selectedLab.getName());
        invoice.setLabAddress(Common.selectedLab.getAddress());

        invoice.setCustomerName(Common.currentBookingInformation.getCustomerName());
        invoice.setCustomerPhone(Common.currentBookingInformation.getCustomerPhone());

        invoice.setImageUrl(image_url);

        invoice.setDoktorServices(new ArrayList<DoktorServices>(servicesAdded));
        invoice.setShoppingItemList(Common.currentBookingInformation.getCartItemList());
        invoice.setFinalPrice(calculatePrice());


        invoiceRef.document()
                .set(invoice)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendNotificationUpdateToUser(Common.currentBookingInformation.getCustomerPhone());

                }
            }
        });


    }

    private void sendNotificationUpdateToUser(String customerPhone) {
        FirebaseFirestore.getInstance().collection("Tokens")
                .whereEqualTo("userPhone",customerPhone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().size()>0)
                        {

                            TokenUser tk=new TokenUser();
                            MyToken myToken=new MyToken();
                            for(DocumentSnapshot tokenSnapShot:task.getResult())
                                tk=tokenSnapShot.toObject(TokenUser.class);
                                myToken.setToken(tk.getToken());
                                myToken.setToken_type(tk.getToken_type());
                                myToken.setUserPhone(tk.getUserPhone());

                            FCMSendData fcmSendData=new FCMSendData();
                            Map<String,String> dataSend=new HashMap<>();
                            dataSend.put("update_done","true");

                            dataSend.put(Common.RATING_STATE_KEY,Common.state_name);
                            dataSend.put(Common.RATING_LAB_ID,Common.selectedLab.getLabId());
                            dataSend.put(Common.RATING_LAB_NAME,Common.selectedLab.getName());
                            dataSend.put(Common.RATING_DOKTOR_ID,Common.currentDoktor.getDoktorId());



                            fcmSendData.setTo(myToken.getToken());
                            fcmSendData.setData(dataSend);

                            ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            dialog.dismiss();
                                            dismiss();
                                            iBottomSheetDialogOnDismissListener.onDismissBottomSheetDialog(true);

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT);
                                        }

                        });


                        }
                    }
                });
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setContext(getContext())
                .setCancelable(false).build();
        ifcmService= RetroFitClient.getInstance().create(IFCMService.class);

    }
}
