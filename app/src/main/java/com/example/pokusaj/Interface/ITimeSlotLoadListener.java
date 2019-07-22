package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
void onTimeSlotLoadFailed(String message);
void onTimeSlotLoadEmpty();


}

