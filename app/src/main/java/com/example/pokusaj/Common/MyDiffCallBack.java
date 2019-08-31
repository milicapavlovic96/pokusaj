package com.example.pokusaj.Common;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.pokusaj.Model.MyNotification;
import com.example.pokusaj.Model.MyNotification2;

import java.util.List;

public class MyDiffCallBack extends DiffUtil.Callback {

    List<MyNotification2> oldList;
    List<MyNotification2> newList;

    public MyDiffCallBack(List<MyNotification2> oldList, List<MyNotification2> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }


    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int il) {
        return oldList.get(i).getUid()==newList.get(il).getUid();
    }

    @Override
    public boolean areContentsTheSame(int i, int il) {
        return oldList.get(i)==newList.get(il);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);

    }
}
