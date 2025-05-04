package com.example.datingappclient.retrofit;

import com.example.datingappclient.model.PictureDTO;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerAPI {

    // Получние чатов для опр. юзера
    // TODO: Переделать
    @GET("api/chats/{pk_user}")
    Call<List<Object[]>> getChats(@Path("pk_user") int id);

    @GET("api/forms")
    Call<List<Object[]>> getForms(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);

    @POST("api/likes")
    Call<Integer> sendLike(@Body JsonObject jsonObject);

    // Получение лайков, поставленных юзеру
    @GET("api/likes/{user_id}")
    Call<List<Object[]>> getLikes(@Path("user_id") int userID);

    // Удаление лайков
    @DELETE("api/likes")
    Call<Integer> deleteLike(@Query("liker") int likerID, @Query("poster") int posterID);

    // Создание чата с юзером
    @POST("api/chats")
    Call<Integer> createChat(@Query("pk_user") int userID, @Query("pk_user1") int likerID);
}
