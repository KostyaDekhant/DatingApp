package com.example.datingappclient.retrofit.controllers;

import com.example.datingappclient.model.dto.LikeDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikesController {

    @GET("api/users/{userId}/likes")
    Call<List<LikeDTO>> getLikes(@Path("userId") int userId);

    @POST("api/likes")
    Call<Integer> sendLike(@Body LikeDTO likeDTO);

    @HTTP(method = "DELETE", path = "api/likes", hasBody = true)
    Call<Integer> deleteLike(@Body LikeDTO likeDTO);
}
