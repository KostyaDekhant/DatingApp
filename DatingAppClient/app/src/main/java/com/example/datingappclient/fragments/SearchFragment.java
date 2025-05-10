package com.example.datingappclient.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.FormDTO;
import com.example.datingappclient.model.LikeDTO;
import com.example.datingappclient.model.ProfileCardData;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.recyclerViews.ProfileCardAdapter;
import com.example.datingappclient.retrofit.repository.FormsRepository;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.LikesRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    /* === CONSTANTS === */
    private static final String ARG_CLIENT_ID = "client_id";

    /* === Repository === */
    private ImageRepository imageRepository;
    private FormsRepository formsRepository;
    private LikesRepository likesRepository;

    /* === Android Objects === */
    private View activityView;
    private FrameLayout frameLayout;
    private View swipeOverlay;

    /* === Other === */
    private int userId;         // ID юзера приложения
    private int currentUserId ; // ID юзера текущей анкеты

    private final List<ProfileCardData> profiles = new ArrayList<>(); // Массив карточек юзеров (заполняется по ходу работы)
    private ProfileCardAdapter adapter;

    /* === Methods === */
    public SearchFragment() {
        // Required empty public constructor
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
        
        setupRepository();
        
        frameLayout = activityView.findViewById(R.id.search_frame);
        swipeOverlay = activityView.findViewById(R.id.swipe_overlay);

        // Загружаем первую анкету при создании фрагмента
        getForm();
        // Задаем параметры карточки анкеты
        setupCardStackView();

        return activityView;
    }

    private void setupRepository() {
        imageRepository = new ImageRepository(requireContext());
        formsRepository = new FormsRepository(requireContext());
        likesRepository = new LikesRepository(requireContext());
    }

    // Метод загрузки следующей анкеты
    private void getForm() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET FORM";
        formsRepository.fetchForm(userId, currentUserId, result -> {
            switch (result.status) {
                case SUCCESS:
                    FormDTO form = result.data;
                    Log.i(logTag, "Успешно получена анкета юзера: " +  form.getUserId());
                    currentUserId = form.getUserId();
                    getUserImages(currentUserId, (images) -> {
                        int age = DateUtils.dateToAge(form.getBirthday());
                        profiles.add(new ProfileCardData(form.getName(), age, form.getDescription(), images));
                        adapter.notifyItemInserted(profiles.size() - 1);
                    });
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Анкет для пользователя " + userId + " нет!");
                    TextView emptyTextView = activityView.findViewById(R.id.empty_text);
                    emptyTextView.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    // Колбэк ответа получения изображений
    interface ImageCallback {
        void onLoaded(List<UserImage> images);
    }

    // Метод загрузка изображений для юзера из анкеты
    private void getUserImages(int userId, ImageCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER IMAGES (FORMS)";
        imageRepository.fetchUserImages(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "For userId = " + userId + " - Count images: " + result.data.size());
                    callback.onLoaded(ImageUtils.objectListToUserImageList(result.data));
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Для пользователя " + userId + " не найдено изображений!");
                    break;
            }
        });
    }

    // Метод отправки лайка на сервер
    private void sendLikeToPoster(int likerId, int posterId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "SEND LIKE";
        likesRepository.sendLike(new LikeDTO(likerId, posterId), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Успешно поставлен лайк: " + result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
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
                int alpha = Math.min((int) (ratio * 150), 150);
                int color;

                if (direction == Direction.Right) {
                    color = Color.argb(alpha, 0, 255, 0); // зелёный
                } else if (direction == Direction.Left) {
                    color = Color.argb(alpha, 255, 0, 0); // красный
                } else {
                    color = Color.TRANSPARENT;
                }

                frameLayout.setBackgroundColor(color);
            }

            @Override public void onCardSwiped(Direction direction) {
                swipeAnimateFlash(direction);
                getForm();
                if (direction == Direction.Right) {
                    sendLikeToPoster(userId, currentUserId);
                }
                frameLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {
                frameLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        };
    }

    // Анимация вспышки при свайпе в сторону
    private void swipeAnimateFlash(Direction direction) {
        int flashColor;
        if (direction == Direction.Right) {
            flashColor = Color.GREEN;
        } else if (direction == Direction.Left) {
            flashColor = Color.RED;
        } else {
            flashColor = Color.GRAY;
        }

        swipeOverlay.setBackgroundColor(flashColor);
        swipeOverlay.setAlpha(1f);
        swipeOverlay.setVisibility(View.VISIBLE);

        swipeOverlay.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction(() -> {
                    swipeOverlay.setVisibility(View.GONE);
                    swipeOverlay.setAlpha(1f);
                })
                .start();
    }
}
