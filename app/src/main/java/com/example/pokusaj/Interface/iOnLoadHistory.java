package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.Model.Laboratory;
import com.example.pokusaj.Model.UserBookingLoadEvent;

import java.util.List;

public interface iOnLoadHistory {
    void onLoadHistorySuccess(List<BookingInformation> historyList);
    void onLoadHistoryFailed(String message);
}
