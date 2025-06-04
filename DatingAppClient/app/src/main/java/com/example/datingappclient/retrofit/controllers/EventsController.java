package com.example.datingappclient.retrofit.controllers;

import com.example.datingappclient.model.dto.EventDTO;
import com.example.datingappclient.model.dto.EventsParamsDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventsController {
    @POST("/api/event")
    Call<List<EventDTO>> getEvents(@Body EventsParamsDTO params);

    @POST("/api/event/create")
    Call<Integer> createEvent(@Body EventDTO event);

    @POST("/api/event/{eventId}/members")
    Call<Void> addMembersToEvent(@Path("eventId") int userId, @Query("memberIds") List<Integer> memberIds);

    @DELETE("/api/event/{eventId}/members")
    Call<Void> removeMembersFromEvent(@Path("eventId") int userId, @Query("memberIds") List<Integer> memberIds, @Query("organizerId") int organizerId);
}
