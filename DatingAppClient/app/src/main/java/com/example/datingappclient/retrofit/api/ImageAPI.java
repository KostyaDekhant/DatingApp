package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.dto.PictureDTO;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ImageAPI {
    @GET("api/user_images")
    Call<List<Object[]>> getUserImages(@Query("user_id") int userId, @Query("limit") int limit);

    @POST("api/user_images/upload")
    Call<Integer> uploadImage(@Body PictureDTO picture);

    @DELETE("api/user_images/delete/{image_id}")
    Call<Void> deleteImage(@Path("image_id") int imageID);
}
