package com.example.pokusaj;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Fragments.HomeFragment;
import com.example.pokusaj.Fragments.ShoppingFragment;
import com.example.pokusaj.Model.User;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class HomeActivity2 extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;
    CollectionReference userRef;

    AlertDialog dialog;

    @Nullable
    @BindView(R.id.activity_home)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onResume() {
        super.onResume();
        checkRatingDialog();
    }

    private void checkRatingDialog() {
        Paper.init(this);
        String dataSerialized=Paper.book().read(Common.RATING_INFORMATION_KEY,"");
        if(!TextUtils.isEmpty(dataSerialized))
        {
            Map<String,String> dataRecieved=new Gson()
                    .fromJson(dataSerialized,new TypeToken<Map<String,String>>(){}.getType());

            if(dataRecieved!=null){
                Common.showRatingDialog(HomeActivity2.this,
                        dataRecieved.get(Common.RATING_STATE_KEY),
                        dataRecieved.get(Common.RATING_LAB_ID),
                        dataRecieved.get(Common.RATING_LAB_NAME),
                        dataRecieved.get(Common.RATING_DOKTOR_ID));




            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity2.this);

        userRef = FirebaseFirestore.getInstance().collection("User");
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();

                            Paper.init(HomeActivity2.this);
                            Paper.book().write(Common.LOGGED_KEY2,Common.currentUser.getPhoneNumber());

                            DocumentReference currentUser = userRef.document(Common.currentUser.getPhoneNumber());
                            currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (!userSnapshot.exists())
                                        {


                                        }
                                        else
                                        {
                                            //if user already available in our system
                                            Common.currentUser=userSnapshot.toObject(User.class);
                                            bottomNavigationView.setSelectedItemId(R.id.action_home);

                                        }
                                        if(dialog.isShowing())
                                            dialog.dismiss();


                                    }

                                }
                            });
                        }
                    }



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment=null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()== R.id.action_home)
                    fragment=new HomeFragment();
                else if(menuItem.getItemId()==R.id.action_shopping)
                    fragment=new ShoppingFragment();
                return loadFragment(fragment);
            }
        });


        initView();
    }

    private void initView() {
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,
                R.string.open,
                R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.menu_exit)
                    logOut();
                return true;
            }
        });
    }

    private void logOut() {
        Paper.init(this);
        Paper.book().delete(Common.USER_KEY);
        Paper.book().delete(Common.LOGGED_KEY);
        Paper.book().delete(Common.LOGGED_KEY2);
        Paper.book().delete(Common.IS_LOGIN);
        Paper.book().delete(Common.STATE_KEY);


        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AccountKit.logOut();

                        Intent mainActivity=new Intent(HomeActivity2.this,MainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
    private boolean loadFragment(Fragment fragment) {
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
        return false;

    }



}


