package com.example.datingappclient.retrofit;

import com.example.datingappclient.model.Picture;
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
    @GET("api/users/{id}")
    Call<JsonObject> getUser(@Path("id") int id);

    // Обновление данных пользователя. Отправляется объект типа ...
    @PATCH("api/users")
    Call<Boolean> updateUser(@Body JsonObject jsonObject);

    @POST("api/login")
    Call<Integer> login(@Body JsonObject jsonObject);

    @POST("api/signup")
    Call<Integer> signup(@Body JsonObject jsonObject);

    // Получние чатов для опр. юзера
    @GET("api/chat_users/{pk_user}")
    Call<List<Object[]>> getChats(@Path("pk_user") int id);

    // Получение фоток юзера по его id
    @GET("api/user_images/{user_id}")
    Call<List<Object[]>> getUserImages(@Path("user_id") int user_id);

    // Загрузка фоток пользователя на сервер
    // Picture содержит id фото и юзера, а также саму фотку
    @POST("api/user_images/upload")
    Call<Integer> uploadImage(@Body Picture picture);
    // Удаление фотографий. Передается image_id
    @DELETE("api/user_images/delete/{image_id}")
    Call<Integer> deleteImage(@Path("image_id") int imageID);
    @GET("api/forms")
    Call<List<Object[]>> getForms(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);

    @POST("api/likes")
    Call<Integer> sendLike(@Body JsonObject jsonObject);

    // Получение лайков, поставленных юзеру
    @GET("api/received_likes/{user_id}")
    Call<List<Object[]>> getLikes(@Path("user_id") int userID);

    // Удаление лайков
    @DELETE("api/likes")
    Call<Integer> deleteLike(@Query("liker") int likerID, @Query("poster") int posterID);

    // Создание чата с юзером
    @POST("api/chats")
    Call<Integer> createChat(@Query("pk_user") int userID, @Query("pk_user1") int likerID);
}
