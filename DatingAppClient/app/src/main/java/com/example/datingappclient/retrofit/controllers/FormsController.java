package com.example.datingappclient.retrofit.controllers;

import com.example.datingappclient.model.dto.FormDTO;
import com.example.datingappclient.model.dto.FormsParametersDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FormsController {
    @GET("api/forms")
    Call<FormDTO> getForms__old(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);

    /*
     *limit - количество анкет, которые ты хочешь получить
     *gender - указываешь Male/Female, либо Both
     *offset - начинаешь с нуля, будет соответствовать порядку, если выгружаешь более одной, то нужно и offset соответственно изменять (получил одну анкету  (limit = 1) -> offset затем будет 1, получил две анкеты (limit = 2) -> offset+2 в следующий раз)
     */
    @POST("/api/forms")
    Call<List<FormDTO>> getForms(@Body FormsParametersDTO params);
}
