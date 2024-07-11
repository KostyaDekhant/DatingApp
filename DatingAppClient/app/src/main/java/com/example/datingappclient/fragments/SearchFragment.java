package com.example.datingappclient.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.searchlogic.OnSwipeTouchListener;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private ImageButton dislikeButton;
    private ImageButton likeButton;
    private TextView userNameAgeLabel;
    private TextView descLabel;
    private FrameLayout profileContainer;
    private View swipeOverlay;
    private ImageView profileImage;

    private int clientId = 24; // ID нашего клиента
    private int prevUserId = 0; // Для первой анкеты
    private int currentUserId = 0; // Текущая анкета

    private List<UserImage> userImages;
    private int currentImageIndex = 0;

    private static final String ARG_CLIENT_ID = "client_id";

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(int clientId) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Извлекаем clientId из аргументов
        if (getArguments() != null) {
            clientId = getArguments().getInt(ARG_CLIENT_ID);
        }

        dislikeButton = view.findViewById(R.id.dislike_button);
        likeButton = view.findViewById(R.id.like_button);
        userNameAgeLabel = view.findViewById(R.id.userNameAge_label);
        descLabel = view.findViewById(R.id.description_label);
        profileContainer = view.findViewById(R.id.profile_container);
        swipeOverlay = view.findViewById(R.id.swipe_overlay);
        profileImage = view.findViewById(R.id.profile_image);

        dislikeButton.setOnClickListener(v -> {
            showSwipeOverlay(false); // Показать красную вуаль
            loadNextProfile();
        });

        likeButton.setOnClickListener(v -> {
            showSwipeOverlay(true); // Показать зеленую вуаль
            sendLike(clientId, currentUserId);
            loadNextProfile();
        });

        profileImage.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    int width = profileImage.getWidth();
                    if (x < width / 2) {
                        showPreviousImage();
                    } else {
                        showNextImage();
                    }
                    return true;
                }
                return super.onTouch(v, event);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                showNextImage();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                showPreviousImage();
            }
        });

        // Загружаем первую анкету при создании фрагмента
        loadNextProfile();

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

    private void loadNextProfile() {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        userNameAgeLabel.setText("");
        descLabel.setText("");
        profileImage.setImageResource(R.drawable.images);
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);

        serverAPI.getForms(clientId, prevUserId).enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    Object[] profile = response.body().get(0);

                    // Проверяем наличие значений
                    if (profile[0] != null) {
                        currentUserId = ((Double) profile[0]).intValue();
                        prevUserId = currentUserId; // Обновляем prevUserId на текущую анкету
                    }

                    String name = profile[1] != null ? (String) profile[1] : "Unknown";
                    String age = profile[2] != null ? profile[2].toString() : "Unknown";
                    String description = profile[5] != null ? (String) profile[5] : "";

                    userNameAgeLabel.setText(name + ", " + DateUtils.dateToAge(age));
                    descLabel.setText(description);

                    // Загружаем изображение, если ID пользователя не равен 0
                    if (currentUserId != 0) {
                        loadImage(currentUserId);
                    }
                    likeButton.setEnabled(true);
                    dislikeButton.setEnabled(true);
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

    private void loadImage(int userId) {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getUserImages(userId).enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    userImages = ImageUtils.objectListToUserImageList(response.body());
                    currentImageIndex = 0;
                    showCurrentImage();
                } else {
                    Log.d("SearchFragment", "No images found for user");
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable t) {
                Log.e("SearchFragment", "Error loading images", t);
            }
        });
    }

    private void showCurrentImage() {
        if (userImages != null && !userImages.isEmpty() && currentImageIndex >= 0 && currentImageIndex < userImages.size()) {
            Bitmap profileBitmap = userImages.get(currentImageIndex).getImage();
            profileImage.setImageBitmap(profileBitmap);
        }
    }

    private void showPreviousImage() {
        if (userImages != null && !userImages.isEmpty()) {
            currentImageIndex = (currentImageIndex - 1 + userImages.size()) % userImages.size();
            showCurrentImage();
        }
    }

    private void showNextImage() {
        if (userImages != null && !userImages.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % userImages.size();
            showCurrentImage();
        }
    }

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
}
