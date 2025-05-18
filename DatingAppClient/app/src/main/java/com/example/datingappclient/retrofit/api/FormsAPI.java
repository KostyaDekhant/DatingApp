package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.dto.FormDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FormsAPI {
    @GET("api/forms")
    Call<FormDTO> getForms__old(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);

    /*
     *limit - количество анкет, которые ты хочешь получить
     *gender - указываешь Male/Female, либо Both
     *offset - начинаешь с нуля, будет соответствовать порядку, если выгружаешь более одной, то нужно и offset соответственно изменять (получил одну анкету  (limit = 1) -> offset затем будет 1, получил две анкеты (limit = 2) -> offset+2 в следующий раз)
     */
    @GET("/api/forms")
    Call<List<FormDTO>> getForms(
            @Query("userId") int userId,
            @Query("age_min") int ageMin,
            @Query("age_max") int ageMax,
            @Query("height_min") int heightMin,
            @Query("height_max") int heightMax,
            @Query("gender") String gender,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
