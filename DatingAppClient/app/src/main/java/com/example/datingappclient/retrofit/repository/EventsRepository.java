package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.EventDTO;
import com.example.datingappclient.model.dto.EventsParamsDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.EventsController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRepository {

    private final EventsController eventsController;

    public EventsRepository (Context context) {
        eventsController = RetrofitClient.getClient(context).create(EventsController.class);
    }

    public void fetchEvents(EventsParamsDTO params, ResultCallback<List<EventDTO>> callback) {
        eventsController.getEvents(params).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                } else {
                    callback.onResult(Result.error("Ошибка получения мероприятий: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void createEvent(EventDTO event, ResultCallback<Void> callback) {
        eventsController.createEvent(event).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка создания мероприятия: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети: " + throwable.getMessage()));
            }
        });
    }

    public void addMembersToEvent(int eventId, List<Integer> memberIds, ResultCallback<Void> callback) {
        eventsController.addMembersToEvent(eventId, memberIds).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка при добавлении участников: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети: " + throwable.getMessage()));
            }
        });
    }

    public void removeMembersFromEvent(int eventId, List<Integer> memberIds, int organizerId, ResultCallback<Void> callback) {
        eventsController.removeMembersFromEvent(eventId, memberIds, organizerId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка при удалении участников: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети: " + throwable.getMessage()));
            }
        });
    }

}
