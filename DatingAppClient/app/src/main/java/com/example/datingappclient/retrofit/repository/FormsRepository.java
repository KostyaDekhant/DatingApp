package com.example.datingappclient.retrofit.repository;

import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.FormsAPI;

public class FormsRepository {
    private final FormsAPI formsAPI;

    public FormsRepository() {
        formsAPI = RetrofitClient.getClient().create(FormsAPI.class);
    }

}
