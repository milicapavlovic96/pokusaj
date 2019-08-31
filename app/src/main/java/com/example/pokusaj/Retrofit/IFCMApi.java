package com.example.pokusaj.Retrofit;

import com.example.pokusaj.Model.FCMResponse;
import com.example.pokusaj.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAVioxVss:APA91bEtPTWdR8wKIuA192VsT85MDOLnl8fyLf2HMjfklqwNitUlsYHeLfxQqadRzpAH4EIrNlgESEm2TIh-wd-9avpvYnG8kW0sEhFpq0-sHQPVTDMJHlLfbNschJpargKOpfRqySYP"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);


}

