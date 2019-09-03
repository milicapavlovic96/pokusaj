package com.example.pokusaj;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.pokusaj.Common.Common;
import com.example.pokusaj.Fragments.ShoppingFragment2;
import com.example.pokusaj.Interface.IDoktorServicesLoadListener;
import com.example.pokusaj.Interface.IOnShoppingItemSelected;
import com.example.pokusaj.Model.DoktorServices;
import com.example.pokusaj.Model.ShoppingItem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class DoneServicesActivity extends AppCompatActivity implements IDoktorServicesLoadListener, IOnShoppingItemSelected {

    private static final int MY_CAMERA_REQUEST_CODE = 1000;
    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;

    @BindView(R.id.txt_customer_phone)
    TextView txt_customer_phone;

    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_services;

    @BindView(R.id.chip_group_shopping)
    ChipGroup chip_group_shopping;

    @BindView(R.id.edt_services)
    AppCompatAutoCompleteTextView edt_services;

    @BindView(R.id.img_customer_hair)
    ImageView img_customer_hair;

    @BindView(R.id.add_shopping)
    ImageView add_shopping;

    @BindView(R.id.btn_finish)
   Button btn_finish;

Uri fileUri;

AlertDialog dialog;
IDoktorServicesLoadListener iDoktorServicesLoadListener;
    List<ShoppingItem> shoppingItems=new ArrayList<>();


HashSet<DoktorServices> serviceAdded=new HashSet<>();

LayoutInflater inflater;

StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_services);

        ButterKnife.bind(this);
        init();

        initView();

        setCustomerInformation();

        loadDoktorServices();
    }

    private void initView() {


    getSupportActionBar().setTitle("Checkout");

    btn_finish.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            uploadPicture(fileUri);
        }
    });

    img_customer_hair.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            fileUri=getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
        }
    });

        add_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingFragment2 shoppingFragment2= ShoppingFragment2.getInstance(DoneServicesActivity.this);
            shoppingFragment2.show(getSupportFragmentManager(),"Shopping");
            }
        });



    }

    private void uploadPicture(Uri fileUri) {
        if(fileUri!=null)
        {
            dialog.show();
            String fileName=Common.getFileName(getContentResolver(),fileUri);
            String path=new StringBuilder("Customer_Pictures/")
                    .append(fileName)
                    .toString();
            storageReference= FirebaseStorage.getInstance().getReference(path);

            UploadTask uploadTask=storageReference.putFile(fileUri);

            Task<Uri> task=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        Toast.makeText(DoneServicesActivity.this,"Failed to upload",Toast.LENGTH_SHORT);

                        return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        String url=task.getResult().toString().substring(0,task.getResult().toString().indexOf("&token"));
                        Log.d("DOWNLOADABLE_LINK",url);
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(DoneServicesActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this,"Image is empty",Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {
        File  mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"pokusaj");
        if(!mediaStorageDir.exists())
        {
            if(!mediaStorageDir.mkdir())
                return null;
        }
           String time_stamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+time_stamp+"_"+new Random().nextInt()+".jpg");
            return mediaFile;
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();

        inflater=LayoutInflater.from(this);
        iDoktorServicesLoadListener=this;
    }

    private void loadDoktorServices() {
        dialog.show();
        FirebaseFirestore.getInstance()
                .collection("AllLaboratories")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedLab.getLabId())
                .collection("Services")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iDoktorServicesLoadListener.onDoktorServicesLoadFailed(e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<DoktorServices> doktorServices=new ArrayList<>();
                    for(DocumentSnapshot doktorSnapshot:task.getResult())
                    {
                        DoktorServices services=doktorSnapshot.toObject(DoktorServices.class);
                    doktorServices.add(services);
                    }
                    iDoktorServicesLoadListener.onDoktorServicesLoadSuccess(doktorServices);
                }
            }
        });
    }

    private void setCustomerInformation() {
    txt_customer_name.setText(Common.currentBookingInformation.getCustomerName());
    txt_customer_phone.setText(Common.currentBookingInformation.getCustomerPhone());
    }

    @Override
    public void onDoktorServicesLoadSuccess(List<DoktorServices> doktorServicesList) {
        List<String> nameServices=new ArrayList<>();
        Collections.sort(doktorServicesList, new Comparator<DoktorServices>() {
            @Override
            public int compare(DoktorServices doktorServices, DoktorServices t1) {
                return doktorServices.getName().compareTo(t1.getName());

            }
        });

        for(DoktorServices doktorServices:doktorServicesList)
            nameServices.add(doktorServices.getName());

        ArrayAdapter<String> adapter =new ArrayAdapter<>(this, android.R.layout.select_dialog_item,nameServices);
        edt_services.setThreshold(1);
        edt_services.setAdapter(adapter);

edt_services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int index = nameServices.indexOf(edt_services.getText().toString().trim());

        if (!serviceAdded.contains(doktorServicesList.get(index))) {
            serviceAdded.add(doktorServicesList.get(index));
            Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
            item.setText(edt_services.getText().toString());
            item.setTag(i);
            edt_services.setText("");

            item.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chip_group_services.removeView(view);
                    serviceAdded.remove((int) item.getTag());
                }
            });

            chip_group_services.addView(item);
        } else {
            edt_services.setText("");
        }
    }
});
        dialog.dismiss();
    }

    @Override
    public void onDoktorServicesLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    dialog.dismiss();
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
        shoppingItems.add(shoppingItem);
        Log.d("ShoppingItem",""+shoppingItems.size());


        Chip item = (Chip) inflater.inflate(R.layout.chip_item, null);
        item.setText(shoppingItem.getName());
        item.setTag(shoppingItems.indexOf(shoppingItem));
        edt_services.setText("");

        item.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip_group_shopping.removeView(view);
                shoppingItems.remove((int) item.getTag());
            }
        });

        chip_group_shopping.addView(item);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MY_CAMERA_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Bitmap bitmap=null;
                ExifInterface ei=null;
                try{
                    bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),fileUri);
                ei = new ExifInterface(getContentResolver().openInputStream(fileUri));

                int orientation=ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotateBitmap=null;
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                       rotateBitmap=rotateImage(bitmap,90);
                    break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotateBitmap=rotateImage(bitmap,180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotateBitmap=rotateImage(bitmap,270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotateBitmap=bitmap;
                        break;
                }
                    img_customer_hair.setImageBitmap(rotateBitmap);
                btn_finish.setEnabled(true);
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, int i) {
        Matrix matrix=new Matrix();
        matrix.postRotate(i);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }
}
