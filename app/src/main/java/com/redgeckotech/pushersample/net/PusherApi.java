package com.redgeckotech.pushersample.net;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PusherApi {

    @POST("sendEvent.php")
    Call<Void> sendEvent(@Body Event chatEvent);
}


