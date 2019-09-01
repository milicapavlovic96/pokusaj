package com.example.pokusaj.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokusaj.Adapter.MyShoppingItemAdapter;
import com.example.pokusaj.Adapter.MyShoppingItemAdapter2;
import com.example.pokusaj.Common.SpacesItemDecoration;
import com.example.pokusaj.DoneServicesActivity;
import com.example.pokusaj.Interface.IOnShoppingItemSelected;
import com.example.pokusaj.Interface.IShoppingDataLoadListener;
import com.example.pokusaj.Model.ShoppingItem;
import com.example.pokusaj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingFragment2 extends BottomSheetDialogFragment implements IShoppingDataLoadListener, IOnShoppingItemSelected {
IOnShoppingItemSelected callBackToActivity;

Unbinder unbinder;

@BindView(R.id.chip_group)
    ChipGroup chipGroup;

@BindView(R.id.chip_bebe)
Chip chip_bebe;
@OnClick(R.id.chip_bebe)
void bebeLoadClick(){
    setSelectedChip(chip_bebe);
    loadShoppingItem("Bebe");

}

    @BindView(R.id.chip_lice)
    Chip chip_lice;
    @OnClick(R.id.chip_lice)
    void liceLoadClick(){
        setSelectedChip(chip_lice);
        loadShoppingItem("Lice");

    }
    @BindView(R.id.chip_telo)
    Chip chip_telo;
    @OnClick(R.id.chip_telo)
    void teloLoadClick(){
        setSelectedChip(chip_telo);
        loadShoppingItem("Telo");

    }

    @BindView(R.id.chip_vitamini)
    Chip chip_vitamini;
    @OnClick(R.id.chip_vitamini)
    void vitaminiLoadClick(){
        setSelectedChip(chip_vitamini);
        loadShoppingItem("Vitamini");

    }

    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;

    private static ShoppingFragment2 instance;

    public static  ShoppingFragment2 getInstance(IOnShoppingItemSelected iOnShoppingItemSelected){
        return instance==null?new ShoppingFragment2(iOnShoppingItemSelected):instance;
    }

    IShoppingDataLoadListener iShoppingDataLoadListener;
    CollectionReference shoppingItemReference;

    public ShoppingFragment2(IOnShoppingItemSelected callBackToActivity){
        this.callBackToActivity=callBackToActivity;
    }

    private void setSelectedChip(Chip chip_bebe) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipItem = (Chip) chipGroup.getChildAt(i);
            if (chipItem.getId() != chip_bebe.getId()) {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));

            } else {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));

            }
        }
    }

    private void loadShoppingItem(String itemMenu) {
        shoppingItemReference= FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("items");

        shoppingItemReference.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iShoppingDataLoadListener.onShoppingDataLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<ShoppingItem> shoppingItems=new ArrayList<>();
                    for(DocumentSnapshot itemSnapShot:task.getResult()){
                        ShoppingItem shoppingItem=itemSnapShot.toObject(ShoppingItem.class);
                        shoppingItem.setId(itemSnapShot.getId());
                        shoppingItems.add(shoppingItem);
                    }
                    iShoppingDataLoadListener.onShoppingLoadSuccess(shoppingItems);

                }
            }
        });
    }

    public ShoppingFragment2() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View itemView= inflater.inflate(R.layout.fragment_shopping, container, false);

        unbinder= ButterKnife.bind(this,itemView);

        loadShoppingItem("Bebe");

        initView();
        init();
        return itemView;
    }

    private void init() {
        iShoppingDataLoadListener=this;

    }

    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(),2));
        recycler_items.addItemDecoration(new SpacesItemDecoration(8));

    }

    @Override
    public void onShoppingLoadSuccess(List<ShoppingItem> shoppingItemList) {
        MyShoppingItemAdapter2 adapter=new MyShoppingItemAdapter2(getContext(),shoppingItemList,this);
        recycler_items.setAdapter(adapter);

    }

    @Override
    public void onShoppingDataLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
callBackToActivity.onShoppingItemSelected(shoppingItem);
    }


}
