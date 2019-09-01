package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.DoktorServices;

import java.util.List;

public interface IDoktorServicesLoadListener {
    void onDoktorServicesLoadSuccess(List<DoktorServices> doktorServicesList);
    void onDoktorServicesLoadFailed(String message);
}
