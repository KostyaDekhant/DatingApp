package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.searchlogic.OnSwipeTouchListener;

public class SearchFragment extends Fragment {

    private ImageButton dislikeButton;
    private ImageButton likeButton;
    private TextView profileInfo;
    private FrameLayout profileContainer;
    private View swipeOverlay;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dislikeButton = view.findViewById(R.id.dislike_button);
        likeButton = view.findViewById(R.id.like_button);
        profileInfo = view.findViewById(R.id.profile_info);
        profileContainer = view.findViewById(R.id.profile_container);
        swipeOverlay = view.findViewById(R.id.swipe_overlay);

        dislikeButton.setOnClickListener(v -> {
            Log.d("SearchFragment", "Dislike button clicked");
            // Обработка клика на дислайк
            // Запрос следующей анкеты на сервер
        });

        likeButton.setOnClickListener(v -> {
            Log.d("SearchFragment", "Like button clicked");
            // Обработка клика на лайк
            // Запрос следующей анкеты на сервер
        });

        profileContainer.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Log.d("SearchFragment", "Swiped Right in fragment");
                showSwipeOverlay(true); // Показать красную вуаль
                // Действие на свайп вправо (дислайк)
                // Запрос следующей анкеты на сервер
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d("SearchFragment", "Swiped Left in fragment");
                showSwipeOverlay(false); // Показать зеленую вуаль
                // Действие на свайп влево (лайк)
                // Запрос следующей анкеты на сервер
            }
        });

        return view;
    }

    private void showSwipeOverlay(boolean isRightSwipe) {
        if (isRightSwipe) {
            swipeOverlay.setBackgroundColor(0x8800FF00); // Полупрозрачный зеленый
        } else {
            swipeOverlay.setBackgroundColor(0x88FF0000); // Полупрозрачный красный
        }
        swipeOverlay.setVisibility(View.VISIBLE);
        swipeOverlay.postDelayed(() -> swipeOverlay.setVisibility(View.GONE), 300); // Скрыть через 300 мс
    }
}
