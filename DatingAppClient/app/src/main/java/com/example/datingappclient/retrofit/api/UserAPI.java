package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.TokenRefreshRequest;
import com.example.datingappclient.model.dto.CompanyInfoDTO;
import com.example.datingappclient.model.dto.AuthDTO;
import com.example.datingappclient.model.dto.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*

    Методы API для работы с сущностями пользователя
    -- описание --

*/
public interface UserAPI {
    @GET("api/users/{userId}")
    Call<UserDTO> getUser(@Path("userId") int userId);

    @PATCH("api/users/{userId}")
    Call<Void> updateUser(@Path("userId") int userId, @Body UserDTO userDTO);

    @GET("api/users/{userId}/company_info")
    Call<CompanyInfoDTO> getUserCompany(@Path("userId") int userId);

    @PATCH("api/users/{userId}/company_info")
    Call<Void> updateUserCompany(@Path("userId") int userId, @Body CompanyInfoDTO companyInfo);

    @GET("api/users/online")
    Call<List<Integer>> getOnlineUsers();

    // TODO: перенести в контроллер Auth
    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthDTO authDTO);

    @POST("auth/signup")
    Call<AuthResponse> signup(@Body AuthDTO authDTO);
}
