package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.BookingInformation;

public interface IBookingInfoLoadListener {

    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation);
    void onBookingInfoLoadFailed(String message);

}
