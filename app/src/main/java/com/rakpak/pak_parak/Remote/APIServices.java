package com.rakpak.pak_parak.Remote;

import com.rakpak.pak_parak.Model.Myresponse;
import com.rakpak.pak_parak.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHRfaOnQ:APA91bEjE-MgoNJcgVZHbvPl1mRB9BE6__muItOSJ8ClY_XI93DrVtPTHrMej9jWj-c0ke_AWx1PoEOiG-YUZoJqMDj26y8ejQfnEQio2MJA2xQthJy7kiBgLL31K95XUSs7Me4elB4P"
            }
    )
    @POST("fcm/send")
    Call<Myresponse> sendNotification(@Body Sender body);


    //Call<Myresponce>
}
