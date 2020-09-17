package com.example.textme.Fragments;

import com.example.textme.Notifications.MyResponse;
import com.example.textme.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:Key=AAAAmVNnPS4:APA91bHOVyE_4PemQwrHYfSzHfjlHni-oPuJWi6PJA8G7Dg0qKENtyp_elhaFae_ZA9fFEt-8mpscNxi-NrMb9u1rBm9q32yEIsM_eaUwK4CCkSXo1NtMsFeJP8CEET0eM2LsQLZeSdj"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
