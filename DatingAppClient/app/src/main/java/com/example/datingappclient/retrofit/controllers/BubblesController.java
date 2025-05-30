package com.example.datingappclient.retrofit.controllers;

import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.model.dto.UserInterestDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BubblesController {
    @GET("/api/interests")
    Call<List<InterestDTO>> getListInterests();

    @GET("/api/categories")
    Call<List<CategoryDTO>> getListCategories();

    @GET("/api/categories/{categoryId}/interests")
    Call<List<InterestDTO>> getListInterestsByCategory(@Path("categoryId") int categoryId);

    @GET("/api/users/{userId}/interests")
    Call<List<UserInterestDTO>> getListUserInterests(@Path("userId") int userId);

    @GET("/api/users/{userId}/categories/{categoryId}/interests")
    Call<List<UserInterestDTO>> getListUserInterestsByCategory(@Path("userId") int userId, @Path("categoryId") int categoryId);

    @POST("/api/users/{userId}/interests")
    Call<Void> addUserInterests(@Path("userId") int userId, @Body List<UserInterestDTO> payload);

    @POST("/api/users/{userId}/interests/remove")
    Call<Void> deleteUserInterests(@Path("userId") int userId, @Body List<UserInterestDTO> payload);
}

