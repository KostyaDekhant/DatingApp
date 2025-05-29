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
    @GET("api/users/{userId}/images")
    Call<List<Object[]>> getUserImages(@Path("userId") int userId, @Query("limit") int limit);

    @POST("api/users/{userId}/images/upload")
    Call<Integer> uploadImage(@Path("userId") int userId, @Body PictureDTO picture);

    @DELETE("api/images/{imageId}/delete")
    Call<Void> deleteImage(@Path("imageId") int imageId);
}
