package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.Picture;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ImageAPI {
    @GET("api/user_images/{user_id}")
    Call<List<Object[]>> getUserImages(@Path("user_id") int user_id);

    @POST("api/user_images/upload")
    Call<Integer> uploadImage(@Body Picture picture);

    @DELETE("api/user_images/delete/{image_id}")
    Call<Integer> deleteImage(@Path("image_id") int imageID);
}
