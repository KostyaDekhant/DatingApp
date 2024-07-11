package com.example.datingappclient.retrofit;

import com.example.datingappclient.model.Picture;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerAPI {

    @GET("api/user")
    Call<JsonObject> getUser(@Query("id") int id);

    @POST("api/user")
    Call<Boolean> updateUser(@Body JsonObject jsonObject);

    @POST("api/login")
    Call<Integer> login(@Body JsonObject jsonObject);

    @POST("api/signup")
    Call<Integer> signup(@Body JsonObject jsonObject);

    @GET("api/chat_users")
    Call<List<Object[]>> getChats(@Query("pk_user") int id);

    @GET("api/user_images")
    Call<List<Object[]>> getUserImages(@Query("pk_user") int id);

    @POST("api/upload_image")
    Call<Integer> uploadImage(@Body Picture picture);

    @POST("api/delete_image")
    Call<Integer> deleteImage(@Query("image_id") int imageID);
    @GET("api/forms")
    Call<List<Object[]>> getForms(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);

    @POST("api/likes")
    Call<Integer> sendLike(@Body JsonObject jsonObject);

    @GET("api/received_likes")
    Call<List<Object[]>> getLikes(@Query("user_id") int userID);

    @GET("api/delete_like")
    Call<Integer> deleteLike(@Query("liker") int likerID, @Query("poster") int posterID);

    @POST("api/chats")
    Call<Integer> createChat(@Query("pk_user") int userID, @Query("pk_user1") int likerID);
}
