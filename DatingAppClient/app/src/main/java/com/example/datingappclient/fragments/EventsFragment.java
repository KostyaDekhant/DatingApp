package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.EventDTO;
import com.example.datingappclient.model.dto.EventMemberDTO;
import com.example.datingappclient.model.dto.EventsParamsDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.recyclerViews.EventsAdapter;
import com.example.datingappclient.retrofit.repository.EventsRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsFragment extends Fragment {

    private int userId;
    private View activityView;
    private EventsAdapter adapter;
    private EventsRepository eventsRepository;

    private EventsFragment() {}

    public static Fragment newInstance(int userId) {
        EventsFragment fragment = new EventsFragment();
        fragment.userId = userId;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_events, container, false);

        setupRepository();
        setupToolbar();
        initViews();

        return activityView;
    }

    private void setupRepository() {
        eventsRepository = new EventsRepository(requireContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void initViews() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        toolbar.setTitle("Мероприятия");

        RecyclerView recyclerView = activityView.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);

        adapter = new EventsAdapter(event -> {
            // Здесь можно навигацию или Toast
            // Например: Toast.makeText(getContext(), "Вы присоединились к " + event.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        EventsParamsDTO params = getEventsParams();
        loadEvents(params);
        //loadFakeData(); // пока нет API
    }

    private EventsParamsDTO getEventsParams() {
        EventsParamsDTO params = new EventsParamsDTO(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),   // start_time
                LocalDateTime.of(2025, 12, 31, 23, 59, 59), // end_time
                50,     // capacity
                20,     // limit: сколько элементов хотим получить
                0       // offset: с какого по счёту (0 — с начала)
        );
        return params;
    }

    private void loadEvents(EventsParamsDTO params) {
        String logTag  = Constants.GLOBAL_LOG_TAG + "GET EVENTS";
        eventsRepository.fetchEvents(params, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено " + result.data.size() + " мероприятий!");
                    adapter.submitList(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Мероприятий не найдено!");
                    break;
            }
        });
    }

    private void loadFakeData() {
        List<EventDTO> exampleEvents = new ArrayList<>();

        List<EventMemberDTO> membersYoga = Arrays.asList(
                new EventMemberDTO(1, "Anna"),
                new EventMemberDTO(2, "Dmitry")
        );

        List<EventMemberDTO> membersMovie = Arrays.asList(
                new EventMemberDTO(3, "Ivan"),
                new EventMemberDTO(4, "Olga"),
                new EventMemberDTO(5, "Sergey")
        );

        exampleEvents.add(new EventDTO(
                101,
                "Йога на закате",
                "Расслабляющее занятие на крыше с видом на город.",
                Timestamp.valueOf("2025-06-01 18:00:00"),
                Timestamp.valueOf("2025-06-01 19:00:00"),
                "Крыша офиса, этаж 15",
                30,
                11,
                501,
                membersYoga
        ));

        exampleEvents.add(new EventDTO(
                102,
                "Киновечер",
                "Просмотр и обсуждение инди-фильмов. Попкорн прилагается!",
                Timestamp.valueOf("2025-06-03 20:00:00"),
                Timestamp.valueOf("2025-06-03 22:30:00"),
                "Конференц-зал 3",
                50,
                12,
                502,
                membersMovie
        ));

        exampleEvents.add(new EventDTO(
                103,
                "Hackathon Sprint",
                "Командная работа над проектами в течение 48 часов. Победители получат призы!",
                Timestamp.valueOf("2025-06-05 10:00:00"),
                Timestamp.valueOf("2025-06-07 10:00:00"),
                "IT-хаб, 2 этаж",
                100,
                15,
                503,
                new ArrayList<>() // пока без участников
        ));
        adapter.submitList(exampleEvents);
    }
}
