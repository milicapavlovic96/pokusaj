package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.Laboratory;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Laboratory> laboratoryList);
    void onBranchLoadFailed(String message);
}
