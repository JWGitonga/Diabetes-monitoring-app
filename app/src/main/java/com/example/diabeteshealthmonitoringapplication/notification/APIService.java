package com.example.diabeteshealthmonitoringapplication.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqGFAW7M:APA91bGP86tXRM2t-KANHFh-aMFxAdphNon9CaBObcmwmMzgouvFn2mfpxUzOzFHE-pWx3OCzi4TDZ0TQ_0gqTFIAROrmPAWt4FijFzezU54uzcQ-DGvfWpnB2Z5Ca5CVjFEhT-yAW0I"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
