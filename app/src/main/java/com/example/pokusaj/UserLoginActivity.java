package com.example.pokusaj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Common.CustomLoginDialog;
import com.example.pokusaj.Model.City;
import com.example.pokusaj.Model.Doktor;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.Model.MyToken;
import com.example.pokusaj.Model.TokenUser;
import com.example.pokusaj.Model.User;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class UserLoginActivity extends AppCompatActivity {
    @BindView(R.id.edt_user)
    TextInputEditText edt_user;

    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    AlertDialog dialog;


    @BindView(R.id.btn_login)
    Button btn_login;

    Context context=this;

    public UserLoginActivity activity;

    public void onAttach(UserLoginActivity activity){
        this.activity = activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        ButterKnife.bind(this);


    }

@OnClick(R.id.btn_login)
    void login(){

    Paper.init(this);


        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .whereEqualTo("name",edt_user.getText().toString())
                .whereEqualTo("password",edt_password.getText().toString())
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {



                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() > 0) {


                                TokenUser user = new TokenUser();

                                for (DocumentSnapshot userSnapShot : task.getResult()) {
                                    user = userSnapShot.toObject(TokenUser.class);

                                }

                                if (user.getToken_type() == Common.TOKEN_TYPE.CLIENT) {
                                        User pomocni = new User();

                                        Paper.init(context);
                                    Paper.book().write(Common.LOGGED_KEY, user.getName());

                                        pomocni.setPassword(user.getPassword());
                                        pomocni.setName(user.getName());
                                        pomocni.setPhoneNumber(user.getUserPhone());

                                        Common.currentUser = pomocni;

                                        Paper.book().write(Common.USER_KEY, new Gson().toJson(pomocni));


                                    FirebaseInstanceId.getInstance()
                                            .getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Common.updateToken3(getBaseContext(),task.getResult().getToken());

                                                        Log.d("EDMTToken", task.getResult().getToken());

                                                        Intent home = new Intent(UserLoginActivity.this, HomeActivity2.class);
                                                        home.putExtra(Common.IS_LOGIN, true);
                                                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(home);
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                }
                                else if(user.getToken_type()== Common.TOKEN_TYPE.DOKTOR)
                                {

                                    Paper.init(context);
                                    Paper.book().write(Common.LOGGED_KEY, user.getName());
                                    Doktor pomocni= new Doktor(user.getName(),user.getDoktorId());

                                    Common.currentDoktor=pomocni;
                                    Common.currentDoktor.setPassword(user.getPassword());
                                    Common.selectedLab=new Laboratory(user.getLabId());
                                    Common.state_name=user.getState_name();



                                    FirebaseInstanceId.getInstance()
                                            .getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Common.updateToken(getBaseContext(),task.getResult().getToken());

                                                        Log.d("EDMTToken", task.getResult().getToken());

                                                        Intent intent=new Intent(UserLoginActivity.this,StaffHomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });



                                }
                            }
                            } else {
                                Toast.makeText(context, "Wrong username / password or wrong salon", Toast.LENGTH_SHORT).show();
                            }

                        }


                });
    }


    }

