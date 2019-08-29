package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.City;

import java.util.List;

public interface IOnAllStateLoadListener {

    void onAllStateLoadSucess(List<City> cityList);
    void onAllStateLoadFailed(String message);
}
