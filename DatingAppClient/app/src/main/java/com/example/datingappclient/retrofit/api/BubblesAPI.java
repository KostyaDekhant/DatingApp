package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.CategoryDTO;
import com.example.datingappclient.model.InterestDTO;
import com.example.datingappclient.model.UserInterestDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BubblesAPI {
    @GET("/api/interests")
    public Call<List<InterestDTO>> getListInterests();

    @GET("/api/categories")
    public Call<List<CategoryDTO>> getListCategories();

    @GET("/api/interests/{categoryId}")
    Call<List<InterestDTO>> getListInterestsByCategory(@Path("categoryId") int categoryId);

    @GET("/api/users/{userId}/categories/{categoryId}/interests")
    Call<List<UserInterestDTO>> getListUserInterestsByCategory(@Path("userId") int userId, @Path("categoryId") int categoryId);
}
