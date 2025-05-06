package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ProfileCardData;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.recyclerViews.ProfileCardAdapter;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.google.gson.JsonObject;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    /* === CONSTANTS === */
    private static final String ARG_CLIENT_ID = "client_id";

    /* === Repository === */
    private final ImageRepository imageRepository;

    /* === Android Objects === */
    private View activityView;

    /* === Other === */
    private int userId;         // ID юзера приложения
    private int prevUserId;     // ID предыдущего юзера (для запроса)
    private int currentUserId ; // ID юзера текущей анкеты

    private final List<ProfileCardData> profiles = new ArrayList<>(); // Массив карточек юзеров (заполняется по ходу работы)
    private ProfileCardAdapter adapter;

    /* === Methods === */
    public SearchFragment() {
        // Required empty public constructor
        imageRepository = new ImageRepository();
    }

    public static SearchFragment newInstance(int userId) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLIENT_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_search, container, false);

        // Извлекаем clientId из аргументов из формы
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_CLIENT_ID);
        }

        // Загружаем первую анкету при создании фрагмента
        getNextForm();
        // Задаем параметры карточки анкеты
        setupCardStackView();

        return activityView;
    }

    // Метод загрузки следующей анкеты
    private void getNextForm() {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getForms(userId, prevUserId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    Object[] profile = response.body().get(0);

                    // Проверяем наличие значений
                    if (profile[0] != null) {
                        currentUserId = ((Double) profile[0]).intValue();
                        prevUserId = currentUserId; // Обновляем prevUserId на текущую анкету
                    }

                    Log.d("SearchFragment", "" + currentUserId);
                    String name = profile[1] != null ? (String) profile[1] : "Unknown";
                    String birthday = profile[2] != null ? profile[2].toString() : "Unknown";
                    String description = profile[5] != null ? (String) profile[5] : "";

                    int age = DateUtils.dateToAge(birthday);

                    // Загружаем изображение, если ID пользователя не равен 0
                    if (currentUserId != 0) {
                        loadUserImagesFromForm(currentUserId, (images) -> {
                            profiles.add(new ProfileCardData(name, age, description, images));
                            adapter.notifyItemInserted(profiles.size() - 1);
                        });
                    }
                } else {
                    Log.d("SearchFragment", "No profiles available");
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable t) {
                Log.e("SearchFragment", "Error loading profile", t);
            }
        });
    }

    // Колбэк ответа получения изображений
    interface ImageCallback {
        void onLoaded(List<UserImage> images);
    }

    // Метод загрузка изображений для юзера из анкеты
    private void loadUserImagesFromForm(int userId, ImageCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER IMAGES (FORMS)";
        imageRepository.fetchUserImages(userId, new ImageRepository.ImagesCallback() {
            @Override
            public void onSuccess(List<Object[]> images) {
                Log.i(logTag, "For userId = " + userId + " - Count images: " + images.size());
                callback.onLoaded(ImageUtils.objectListToUserImageList(images));
            }

            @Override
            public void onEmpty(String message) {
                Log.i(logTag, message + " userId :" + userId);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    // Метод отправки лайка на сервер
    private void sendLike(int liker, int poster) {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("liker", liker);
        jsonObject.addProperty("poster", poster);

        serverAPI.sendLike(jsonObject).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int likeId = response.body();
                    if (likeId != -1) {
                        Log.d("SearchFragment", "Like successfully sent. ID: " + likeId);
                    } else {
                        Log.d("SearchFragment", "Failed to save like. Invalid data.");
                    }
                } else {
                    Log.d("SearchFragment", "Failed to send like. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("SearchFragment", "Failed to send like. Error: " + t.getMessage());
            }
        });
    }

    // Тут можно задать параметры самого свайпа
    private void setupCardStackView() {
        CardStackView cardStackView = activityView.findViewById(R.id.profile_image);

        CardStackLayoutManager layoutManager = new CardStackLayoutManager(activityView.getContext(), setupCardStackListener());

        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollVertical(false);

        cardStackView.setLayoutManager(layoutManager);
        adapter = new ProfileCardAdapter(profiles);
        cardStackView.setAdapter(adapter);
    }

    // Тут можно настроить все действия при свайпе
    private CardStackListener setupCardStackListener() {
        return new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override public void onCardSwiped(Direction direction) {
                getNextForm();
                if (direction == Direction.Right) {
                    sendLike(userId, currentUserId);
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        };
    }
}
