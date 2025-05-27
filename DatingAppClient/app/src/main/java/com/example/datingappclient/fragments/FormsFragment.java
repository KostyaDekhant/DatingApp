package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.FormDTO;
import com.example.datingappclient.model.dto.FormsParametersDTO;
import com.example.datingappclient.model.dto.LikeDTO;
import com.example.datingappclient.model.ProfileCardData;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.recyclerViews.ProfileCardAdapter;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
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

public class FormsFragment extends Fragment {

    /* === CONSTANTS === */
    private static final String ARG_CLIENT_ID = "client_id";

    /* === Repository === */
    private ImageRepository imageRepository;
    private FormsRepository formsRepository;
    private LikesRepository likesRepository;
    private BubblesRepository bubblesRepository;

    /* === Android Objects === */
    private View activityView;
    private FrameLayout frameLayout;
    private View swipeOverlay;
    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;
    private ProgressBar progressBar;

    /* === Other === */
    private int userId;         // ID юзера приложения
    private int currentUserId ; // ID юзера текущей анкеты
    private final int LIMIT = 5;

    private final List<ProfileCardData> profiles = new ArrayList<>(); // Массив карточек юзеров (заполняется по ходу работы)
    private ProfileCardAdapter adapter;

    /* === Methods === */
    public FormsFragment() {
        // Required empty public constructor
    }

    public static FormsFragment newInstance(int userId) {
        FormsFragment fragment = new FormsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLIENT_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_search, container, false);

        progressBar = activityView.findViewById(R.id.progressBar);
        progressBar.setVisibility(VISIBLE);

        // Извлекаем clientId из аргументов из формы
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_CLIENT_ID);
        }

        setupRepository();

        frameLayout = activityView.findViewById(R.id.search_frame);
        swipeOverlay = activityView.findViewById(R.id.swipe_overlay);

        adapter = new ProfileCardAdapter(new ArrayList<>(), requireContext());

        setupCardStackView();

        // Загружаем первую анкету при создании фрагмента
        getForms(() -> progressBar.setVisibility(GONE));

        return activityView;
    }

    private void setupRepository() {
        imageRepository = new ImageRepository(requireContext());
        formsRepository = new FormsRepository(requireContext());
        likesRepository = new LikesRepository(requireContext());
        bubblesRepository = new BubblesRepository(requireContext());
    }

    interface LoadInterface { void onLoad(); }
    // TODO: передавать фильтры из отдельной формы
    private int offset = 0;
    private void getForms(LoadInterface callback) {
        int limit = Constants.FORMS_LIMIT;
        String logTag = Constants.GLOBAL_LOG_TAG + "GET FORMS";
        FormsParametersDTO params = new FormsParametersDTO(userId, 0, 100, 0, 200, "Both", limit, offset);
        formsRepository.fetchForms(params, result -> {
            switch (result.status) {
                case SUCCESS:
                    offset += limit;
                    Log.i(logTag, "Успешно получены анкеты " +  result.data.size());

                    List<ProfileCardData> old = adapter.getProfiles();
                    List<ProfileCardData> updated = new ArrayList<>(old);

                    List<ProfileCardData> newForms = parseForms(result.data);

                    // Отложим добавление в конец, чтобы избежать "всплытия"
                    cardStackView.post(() -> {
                        int currentPosition = layoutManager.getTopPosition();

                        updated.addAll(newForms);
                        adapter.setProfiles(updated);

                        layoutManager.scrollToPosition(currentPosition);
                        cardStackView.setVisibility(VISIBLE);
                    });
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Анкет не найдено");
                    renderEmptyLabel();
                    break;
            }
            callback.onLoad();
        });
    }

    private List<ProfileCardData> parseForms(List<FormDTO> data) {
        List<ProfileCardData> newForms = new ArrayList<>();
        for (FormDTO form : data) {
            int age = DateUtils.dateToAge(form.getBirthday());
            ProfileCardData profile = new ProfileCardData(
                    form.getUserId(),
                    form.getName(),
                    age,
                    form.getDescription(),
                    null, null);
            newForms.add(profile);
        }
        return newForms;
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
        cardStackView = activityView.findViewById(R.id.profile_image);
        layoutManager = new CardStackLayoutManager(requireContext(), setupCardStackListener());

        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setScaleInterval(0.6f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(10.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollVertical(false);

        cardStackView.setVisibility(View.INVISIBLE);
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setItemAnimator(null);
        cardStackView.setAdapter(adapter);
    }

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
                // animate
                swipeAnimateFlash(direction);
                frameLayout.setBackgroundColor(Color.TRANSPARENT);

                // send like
                if (direction == Direction.Right) sendLikeToPoster(userId, (int)adapter.getItemId(0));

                // remove card
                removeCard();

                // check count
                checkFormsCount();
            }

            @Override
            public void onCardRewound() {}

            @Override
            public void onCardCanceled() {
                frameLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onCardAppeared(View view, int position) {}

            @Override
            public void onCardDisappeared(View view, int position) {}
        };
    }

    private void removeCard() {
        List<ProfileCardData> newList = new ArrayList<>(adapter.getProfiles());
        newList.remove(0);
        adapter.setProfiles(newList);

        if (newList.isEmpty()) {
            renderEmptyLabel();
        }
    }

    private void checkFormsCount() {
        if (adapter.getItemCount() < LIMIT) {
            getForms(() -> {});
        }
    }

    private void renderEmptyLabel() {
        TextView emptyTextView = activityView.findViewById(R.id.empty_text);
        if (adapter.getItemCount() == 0) {
            cardStackView.setVisibility(GONE);
            emptyTextView.setVisibility(VISIBLE);
        }
        else {
            cardStackView.setVisibility(VISIBLE);
            emptyTextView.setVisibility(GONE);
        }
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
        swipeOverlay.setVisibility(VISIBLE);

        swipeOverlay.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction(() -> {
                    swipeOverlay.setVisibility(GONE);
                    swipeOverlay.setAlpha(1f);
                })
                .start();
    }
}
