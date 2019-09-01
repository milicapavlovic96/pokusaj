package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.BookingInformation;
import com.example.pokusaj.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
void onTimeSlotLoadSuccess(List<BookingInformation> timeSlotList);
void onTimeSlotLoadFailed(String message);
void onTimeSlotLoadEmpty();


}

