package com.example.datingappclient.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.EventDTO;
import com.example.datingappclient.model.dto.EventMemberDTO;
import com.example.datingappclient.model.dto.EventsParamsDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.recyclerViews.EventsAdapter;
import com.example.datingappclient.retrofit.repository.EventsRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class EventsFragment extends Fragment {

    private UserDTO user;
    private View activityView;
    private EventsAdapter adapter;
    private EventsRepository eventsRepository;

    private EventsFragment() {}

    public static Fragment newInstance(UserDTO user) {
        EventsFragment fragment = new EventsFragment();
        fragment.user = user;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_events, container, false);

        setupRepository();
        setupToolbar();
        setupCreateEventButton();
        initViews();

        return activityView;
    }

    private void setupCreateEventButton() {
        FloatingActionButton createEventButton = activityView.findViewById(R.id.createEvent);
        createEventButton.setOnClickListener(v -> {
            openCreateEventDialog();
        });
    }

    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private void openCreateEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создание мероприятия");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_event, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.inputTitle);
        EditText descriptionInput = dialogView.findViewById(R.id.inputDescription);
        EditText locationInput = dialogView.findViewById(R.id.inputLocation);
        EditText capacityInput = dialogView.findViewById(R.id.inputCapacity);
        EditText startTimeInput = dialogView.findViewById(R.id.inputStartTime);
        EditText endTimeInput = dialogView.findViewById(R.id.inputEndTime);


        TextInputLayout layoutTitle = dialogView.findViewById(R.id.layoutTitle);
        TextInputLayout layoutStartTime = dialogView.findViewById(R.id.layoutStartTime);

        clearErrorOnInput(layoutTitle, titleInput);
        clearErrorOnInput(layoutStartTime, startTimeInput);

        // Кнопки сброса дат
        ImageButton clearStartTime = dialogView.findViewById(R.id.clearStartTime);
        ImageButton clearEndTime = dialogView.findViewById(R.id.clearEndTime);

        // Сброс времени начала
        clearStartTime.setOnClickListener(v -> {
            startTimeInput.setText("");
            startTimestamp = null;
        });

        // Сброс времени окончания
        clearEndTime.setOnClickListener(v -> {
            endTimeInput.setText("");
            endTimestamp = null;
        });

        // Обработчики взятия времени
        startTimeInput.setOnClickListener(v -> showDateTimePicker(startTimeInput, null, ts -> startTimestamp = ts));
        endTimeInput.setOnClickListener(v -> {
            if (startTimestamp == null) {
                Toast.makeText(requireContext(), "Сначала выберите дату и время начала", Toast.LENGTH_SHORT).show();
                return;
            }
            showDateTimePicker(endTimeInput, startTimestamp, ts -> endTimestamp = ts);
        });

        // Кнопки
        builder.setPositiveButton("Создать", null);
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // ✅ Обработка кнопки создания
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            boolean hasError = false;

            String title = titleInput.getText().toString().trim();
            String startStr = startTimeInput.getText().toString().trim();

            if (title.isEmpty()) {
                hasError = true;
                layoutTitle.setError("Обязательное поле");       // показывает красную подсказку и рамку
                layoutTitle.setErrorEnabled(true);               // (необязательно, для совместимости)
            }

            if (startStr.isEmpty()) {
                hasError = true;
                layoutStartTime.setError("Обязательное поле");       // показывает красную подсказку и рамку
                layoutStartTime.setErrorEnabled(true);               // (необязательно, для совместимости)
            }

            if (hasError) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return; // диалог остаётся открытым
            }

            // Всё ок — создаём событие
            String description = descriptionInput.getText().toString();
            String location = locationInput.getText().toString();
            String capasityString = capacityInput.getText().toString();
            Integer capacity = capasityString.isEmpty() ? null : Integer.parseInt(capasityString);
            Timestamp startTime = Timestamp.valueOf(startStr);
            Timestamp endTime = endTimestamp;

            EventDTO newEvent = new EventDTO(
                    null, title, description, startTime, endTime, location, capacity,
                    user.getId(), -1, new ArrayList<>()
            );

            createEvent(newEvent);
            dialog.dismiss(); // теперь можно закрыть вручную
        });

    }

    private void clearErrorOnInput(TextInputLayout layout, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
                layout.setErrorEnabled(false);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void showDateTimePicker(EditText target, @Nullable Timestamp minTimestamp, @NonNull Consumer<Timestamp> onTimestampSelected) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            TimePickerDialog timePicker = new TimePickerDialog(requireContext(), (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                Timestamp selectedTimestamp = new Timestamp(calendar.getTimeInMillis());

                // ⚠ Проверка, если minTimestamp задан и дата совпадает
                if (minTimestamp != null) {
                    Calendar minCal = Calendar.getInstance();
                    minCal.setTimeInMillis(minTimestamp.getTime());

                    boolean sameDay = calendar.get(Calendar.YEAR) == minCal.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == minCal.get(Calendar.DAY_OF_YEAR);

                    if (sameDay && selectedTimestamp.before(minTimestamp)) {
                        Toast.makeText(requireContext(), "Время не может быть раньше времени начала", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Всё ок — выводим
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                target.setText(sdf.format(selectedTimestamp));
                onTimestampSelected.accept(selectedTimestamp);

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (minTimestamp != null) {
            datePicker.getDatePicker().setMinDate(minTimestamp.getTime());
        }

        datePicker.show();
    }

    private void createEvent(EventDTO newEvent) {
        String logTag = Constants.GLOBAL_LOG_TAG + "CREATE EVENT";
        eventsRepository.createEvent(newEvent, result -> {
            switch (result.status) {
                case SUCCESS:
                    List<EventDTO> currentList = new ArrayList<>(adapter.getCurrentList());
                    currentList.add(newEvent);
                    adapter.submitList(currentList);
                    Log.d(logTag, "Успешно создано мероприятие");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
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

        adapter = new EventsAdapter(new EventsAdapter.OnJoinClickListener() {
            @Override
            public void onJoinClick(EventDTO event, EventsAdapter.OnClickResult callback) {
                joinToEvent(event, callback);
            }

            @Override
            public void onLeaveClick(EventDTO event, EventsAdapter.OnClickResult callback) {
                leaveFromEvent(event, callback);
            }
        }, user.getId());

        recyclerView.setAdapter(adapter);

        EventsParamsDTO params = getEventsParams();
        loadEvents(params);
        //loadFakeData(); // пока нет API
    }

    private void leaveFromEvent(EventDTO event, EventsAdapter.OnClickResult callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "LEAVE EVENT";

        List<Integer> member = new ArrayList<>();
        member.add(user.getId());

        eventsRepository.removeMembersFromEvent(event.getId(), member, event.getOrganizerId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Успешно покинуто мероприятие");
                    Toast.makeText(getContext(), "Вы покинули мероприятие " + event.getTitle(), Toast.LENGTH_SHORT).show();
                    event.leave(user.getId());
                    callback.onClick(false);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    Toast.makeText(getContext(), "Ошибка выхода их мероприятия " + event.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void joinToEvent(EventDTO event, EventsAdapter.OnClickResult callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "JOIN TO EVENT";

        List<Integer> member = new ArrayList<>();
        member.add(user.getId());

        eventsRepository.addMembersToEvent(event.getId(), member, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Успешно присоедено к мероприятию");
                    Toast.makeText(getContext(), "Вы присоединились к " + event.getTitle(), Toast.LENGTH_SHORT).show();
                    event.join(user.getId(), user.getName());
                    callback.onClick(true);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    Toast.makeText(getContext(), "Ошибка присоеденения к мероприятию " + event.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private EventsParamsDTO getEventsParams() {
        EventsParamsDTO params = new EventsParamsDTO(
                Timestamp.valueOf("2025-01-01 00:00:00"),   // start_time
                Timestamp.valueOf("2025-12-31 23:59:59"), // end_time
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
