package com.example.pokusaj.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokusaj.Adapter.HomeSliderAdapter;
import com.example.pokusaj.Adapter.LookbookAdapter;
import com.example.pokusaj.BookingActivity;
import com.example.pokusaj.CartActivity;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Database.CartDatabase;
import com.example.pokusaj.Database.DatabaseUtils;
import com.example.pokusaj.HistoryActivity;
import com.example.pokusaj.Interface.IBannerLoadListener;
import com.example.pokusaj.Interface.IBookingInfoLoadListener;
import com.example.pokusaj.Interface.IBookingInformationChangeListener;
import com.example.pokusaj.Interface.ICountItemInCartListener;
import com.example.pokusaj.Interface.ILookbookLoadListener;
import com.example.pokusaj.Model.Banner;
import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.R;
import com.example.pokusaj.Service.PicassoImageLoadingService;
import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IBannerLoadListener, ILookbookLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener, ICountItemInCartListener {

    private Unbinder unbinder;

    CartDatabase cartDatabase;

    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_information;

    @BindView(R.id.txt_user_name)
    TextView txt_user_name;

    @BindView(R.id.banner_slider)
    Slider banner_slider;

    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_lab_address)
    TextView txt_lab_address;
    @BindView(R.id.txt_salon_doktor)
    TextView txt_salon_doktor;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    AlertDialog dialog;

    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking()
    {
        deleteBookingFromDoktor(false);
    }
    @OnClick(R.id.btn_change_booking)
    void changeBooking(){
        changeBookingFromUser();
    }

    @OnClick(R.id.card_view_history)
    void openHistoryActivity(){

startActivity(new Intent(getActivity(), HistoryActivity.class));    }

    private void changeBookingFromUser() {
        android.app.AlertDialog.Builder confirmDialog=new android.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey!")
                .setMessage("Da li ste sigurni da želite da promenite zakazan pregled?\n Prethodne informacije neće biti sačuvane.\nPotvrdite.")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBookingFromDoktor(true);
                    }
                });
        confirmDialog.show();

    }

    private void deleteBookingFromDoktor(boolean isChange) {
    //prvo brises iz barberove kolekcije, onda od usera, a onda event
        if(Common.currentBooking!=null) {
            dialog.show();

            DocumentReference barberBookingInfo=FirebaseFirestore.getInstance()
                    .collection("AllLaboratories")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getLabId())
                    .collection("Doktori")
                    .document(Common.currentBooking.getDoktorId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

        barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    deleteBookingFromUser(isChange);
            }
        });
        }
        else
        {
            Toast.makeText(getContext(),"Current Booking must not be null",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser(boolean isChange) {
        if (!TextUtils.isEmpty(Common.currentBookingId)) {
            DocumentReference userBookingInfo=FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
            //delete from user i onda from calendar
                    Paper.init(getActivity());
                    if(Paper.book().read(Common.EVENT_URI_CACHE)!= null)
                    {
                        String eventString=Paper.book().read(Common.EVENT_URI_CACHE).toString();
                        Uri eventUri=null;
                        if(eventString!=null&&!TextUtils.isEmpty(eventString))
                            eventUri= Uri.parse(eventString);
                        if(eventUri!=null)
                            getActivity().getContentResolver().delete(eventUri,null,null);

                    }
                    Toast.makeText(getActivity(),"Success delete booking!",Toast.LENGTH_SHORT).show();



                    loadUserBooking();

                    if(isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();
                    dialog.dismiss();

                }
            });
        } else {
            dialog.dismiss();
        Toast.makeText(getContext(),"Booking information ID must not be",Toast.LENGTH_SHORT).show();
        }
    }




    @OnClick(R.id.card_view_booking)
    void booking(){
    startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @OnClick(R.id.card_view_cart)
            void openCartActivity(){
        startActivity(new Intent(getActivity(), CartActivity.class));

    }


    CollectionReference bannerRef,lookbookRef;

    IBannerLoadListener iBannerLoadListener;
    ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    ListenerRegistration userBookingListener=null;
    com.google.firebase.firestore.EventListener<QuerySnapshot> userBookingEvent=null;

    public HomeFragment() {
        bannerRef= FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef=FirebaseFirestore.getInstance().collection("Lookbook");
        // Required empty public constructor

    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }
//Na osnovu danasnjeg datuma vadimo iz baze booking information
    private void
    loadUserBooking() {
        CollectionReference userBooking=FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp toDayTimeStamp=new Timestamp(calendar.getTime());
        //select booking info from firebase with done=false and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation=queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation,queryDocumentSnapshot.getId());
                                    break;
                                }
                            }
                            else
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });

        if(userBookingEvent!=null)
        {
            if(userBookingListener==null)
            {
                userBookingListener=userBooking
                        .addSnapshotListener(userBookingEvent);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
unbinder= ButterKnife.bind(this,view);

        cartDatabase=CartDatabase.getInstance(getContext());


        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener=this;
        iLookbookLoadListener=this;
        iBookingInfoLoadListener=this;
        iBookingInformationChangeListener=this;


        if(AccountKit.getCurrentAccessToken()!=null)
        {
            setUserInformation();
            loadBanner();
            loadLookBook();
            initRealtimeUserBooking();
            loadUserBooking();
            countCartItem();
        }
        else if(Common.currentUser!=null){
            setUserInformation();
            loadBanner();
            loadLookBook();
            initRealtimeUserBooking();
            loadUserBooking();
            countCartItem();
        }


return view;

    }

    private void initRealtimeUserBooking() {
        if(userBookingEvent==null)
        {
            userBookingEvent=new EventListener<QuerySnapshot>() {


                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    loadUserBooking();
                }
            };
        }
    }

    private void countCartItem() {
        DatabaseUtils.countItemInCart(cartDatabase,this);
    }

    private void loadLookBook() {
        lookbookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner> lookbooks=new ArrayList<>();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                    {
                        Banner banner=bannerSnapShot.toObject(Banner.class);
                        lookbooks.add(banner);
                    }
                    iLookbookLoadListener.onLookbookLoadSuccess(lookbooks);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookbookLoadListener.onLookbookLoadFailed(e.getMessage());
            }
        });
    }
    private void loadBanner() {
        bannerRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner>  banners=new ArrayList<>();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                    {
                        Banner banner=bannerSnapShot.toObject(Banner.class);
                        banners.add(banner);
                    }
                    iBannerLoadListener.onBannerLoadSuccess(banners);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
           iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(VISIBLE);
        txt_user_name.setText(Common.currentUser.getName());
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
    banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLookbookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_look_book.setAdapter(new LookbookAdapter(getActivity(),banners));
    }

    @Override
    public void onLookbookLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBookingInfoLoadEmpty() {
            card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String bookingId) {
        Common.currentBooking=bookingInformation;
        Common.currentBookingId=bookingId;

        txt_lab_address.setText(bookingInformation.getLabAddress());
        txt_salon_doktor.setText(bookingInformation.getDoktorName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();
        txt_time_remain.setText(dateRemain);

        card_booking_info.setVisibility(View.VISIBLE);


            dialog.dismiss();


    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBookingInformationChange() {
        startActivity(new Intent(getActivity(),BookingActivity.class));
    }

    @Override
    public void onCartItemCountSuccess(int count) {
        notificationBadge.setText(String.valueOf(count));
    }

    @Override
    public void onDestroy() {
        if(userBookingListener!=null)
            userBookingListener.remove();
        super.onDestroy();
    }
}
