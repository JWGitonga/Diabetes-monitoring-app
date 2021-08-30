package com.example.diabeteshealthmonitoringapplication.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvGJBBx8:APA91bGFpezUUm5M0X9FocLxfDCF1d9wSUC2-MQUryscy_XDd0yk4YvVUwBd6VB1BEKkmr07p8d1uW4ZT2r47z1_i_CZZJVRux3DnoTD4eLJ5AnzGLiKI0lWGF0xJWfoaEmAgZBPACgP"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
