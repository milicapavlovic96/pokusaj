package com.example.pokusaj.Model;

import java.util.List;

public class UserBookingLoadEvent {
    private boolean success;
    private String message;
    private List<BookingInformation> bookingInformationList;


    public UserBookingLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public UserBookingLoadEvent(boolean success, List<BookingInformation> bookingInformationList) {
        this.success = success;
        this.bookingInformationList = bookingInformationList;
    }

    public boolean isSuccess() {
        return success;
    }


}
