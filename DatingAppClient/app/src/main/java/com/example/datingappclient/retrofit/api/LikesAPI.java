package com.example.datingappclient.retrofit.api;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LikesAPI {
    @POST("api/likes")
    Call<Integer> sendLike(@Body JsonObject jsonObject);

    @GET("api/received_likes/{user_id}")
    Call<List<Object[]>> getLikes(@Path("user_id") int userID);

    @DELETE("api/likes")
    Call<Integer> deleteLike(@Query("liker") int likerID, @Query("poster") int posterID);
}
