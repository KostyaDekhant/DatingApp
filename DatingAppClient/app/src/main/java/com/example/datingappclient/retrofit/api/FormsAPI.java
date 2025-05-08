package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.FormDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FormsAPI {
    @GET("api/forms")
    Call<FormDTO> getForms(@Query("user_id") int userId, @Query("prev_user_id") int prevUserId);
}
