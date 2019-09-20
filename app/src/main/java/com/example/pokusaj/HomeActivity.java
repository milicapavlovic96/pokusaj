package com.example.pokusaj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Fragments.HomeFragment;
import com.example.pokusaj.Fragments.ShoppingFragment;
import com.example.pokusaj.Model.User;
import com.example.pokusaj.R;
import com.facebook.accountkit.AccessToken;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;
    CollectionReference userRef;

    AlertDialog dialog;

    @Nullable @BindView(R.id.activity_home)
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
            Common.showRatingDialog(HomeActivity.this,
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



        ButterKnife.bind(HomeActivity.this);

        userRef = FirebaseFirestore.getInstance().collection("User");

        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        if (account != null) {

                            Paper.init(HomeActivity.this);
                            Paper.book().write(Common.LOGGED_KEY2,account.getPhoneNumber().toString());

                            DocumentReference currentUser = userRef.document(account.getPhoneNumber().toString());
                            currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot userSnapshot = task.getResult();
                                        if (!userSnapshot.exists())
                                        {

                                            showUpdateDialog(account.getPhoneNumber().toString());

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


                    @Override
                    public void onError(AccountKitError accountKitError) {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            initView();
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

                        Intent mainActivity=new Intent(HomeActivity.this,MainActivity.class);
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


    private void showUpdateDialog(String phoneNumber) {



        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("Još jedan korak!");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView=getLayoutInflater().inflate(R.layout.layout_update_inflater,null);


        Button btn_update=(Button)sheetView.findViewById(R.id.btn_update);
        final TextInputEditText edt_name=(TextInputEditText)sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edt_password=(TextInputEditText)sheetView.findViewById(R.id.edt_password);
        btn_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!dialog.isShowing())
                    dialog.show();
                User user = new User(edt_name.getText().toString(),
                        edt_password.getText().toString(),
                        phoneNumber);


                userRef.document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                if(dialog.isShowing())
                                    dialog.dismiss();

                                Common.currentUser=user;

                                Paper.init(HomeActivity.this);

                                Paper.book().write(Common.LOGGED_KEY, phoneNumber);

                                FirebaseInstanceId.getInstance()
                                        .getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (task.isSuccessful()) {
                                                    Common.updateToken3(getBaseContext(), task.getResult().getToken());
                                                }
                                            }
                                                               }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });




                                                    bottomNavigationView.setSelectedItemId(R.id.action_home);


                                Toast.makeText(HomeActivity.this, "Thank you", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        if(dialog.isShowing())
                            dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}








