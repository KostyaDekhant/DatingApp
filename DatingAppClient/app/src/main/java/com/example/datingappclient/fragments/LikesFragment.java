package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.LikeDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.LikesRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class LikesFragment extends Fragment {

    /* === Repositories === */
    private LikesRepository likesRepository;
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    TextView noLikesView;
    ConstraintLayout mainContent;
    ProgressBar progressBar;

    /* === Other === */
    @Setter
    private UserDTO user;
    private List<LikeDTO> likesArray;

    /* === Methods === */
    public LikesFragment() {

    }

    public static LikesFragment newInstance(UserDTO user) {
        LikesFragment likeFragment = new LikesFragment();
        likeFragment.user = user;
        return likeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_likes, container, false);

        noLikesView = activityView.findViewById(R.id.noLikes_label);
        mainContent = activityView.findViewById(R.id.mainContent);
        progressBar = activityView.findViewById(R.id.progressBar);

        mainContent.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        setupRepository();

        getUserLikes();

        return activityView;
    }

    private void setupRepository() {
        Context context = requireContext();
        likesRepository = new LikesRepository(context);
        chatsRepository = new ChatsRepository(context);
    }

    private void getUserLikes() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET LIKES";
        likesRepository.fetchUserLikes(user.getId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    likesArray = result.data;
                    Log.i(logTag, "Получены лайки: " + likesArray.size());
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка загрузки лайков!", Toast.LENGTH_LONG).show();
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Для user.getId() = " + user.getId() + " нет лайков!");
                    break;
            }
            progressBar.setVisibility(GONE);
            renderLikes();
        });
    }

    private int currentIndex = 0;
    private void renderLikes() {
        if (likesArray == null || likesArray.isEmpty() || currentIndex >= likesArray.size()) {
            // Все лайки показаны
            activityView.findViewById(R.id.cardView).setVisibility(GONE);

            noLikesView.setVisibility(VISIBLE);
            mainContent.setVisibility(GONE);
            Toast.makeText(requireContext(), "Лайки закончились", Toast.LENGTH_SHORT).show();
            return;
        }

        noLikesView.setVisibility(GONE);
        mainContent.setVisibility(VISIBLE);

        LikeDTO like = likesArray.get(currentIndex);

        // Фото
        ImageView imageView = activityView.findViewById(R.id.user_image);
        imageView.setImageBitmap(ImageUtils.convertPrimitiveByteToBitmap(like.getImage()));

        // Имя и возраст
        TextView nameView = activityView.findViewById(R.id.username_label);
        nameView.setText(like.getUserName() + ",");

        TextView ageView = activityView.findViewById(R.id.age_label);
        ageView.setText(String.valueOf(DateUtils.dateToAge(like.getBirthday())));

        /*// Описание
        TextView descriptionView = activityView.findViewById(R.id.description_label);
        descriptionView.setText(like.getDescription());

        // Интересы
        FlexboxLayout interestsContainer = activityView.findViewById(R.id.interests_container);
        interestsContainer.removeAllViews();
        if (like.getInterests() != null) {
            for (String interest : like.getInterests()) {
                TextView chip = new TextView(requireContext());
                chip.setText(interest);
                chip.setTextColor(getResources().getColor(R.color.white));
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                chip.setPadding(24, 12, 24, 12);
                chip.setBackgroundResource(R.drawable.bubble_background);
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 8, 8, 8);
                chip.setLayoutParams(params);
                interestsContainer.addView(chip);
            }
        }*/

        // Обновить кнопки
        FloatingActionButton btnLike = activityView.findViewById(R.id.btnLike);
        FloatingActionButton btnDislike = activityView.findViewById(R.id.btnDislike);
        FloatingActionButton btnSkip = activityView.findViewById(R.id.btnSkip);

        btnLike.setOnClickListener(v -> {
            createGroupChat(v, like);
            currentIndex++;

        });

        btnDislike.setOnClickListener(v -> {
            deleteLike(v, new LikeDTO(like.getLikerId(), user.getId()));
            currentIndex++;
            renderLikes();
        });

        btnSkip.setOnClickListener(v -> {
            currentIndex++;
            renderLikes();
        });
    }

    private void createGroupChat(View view, LikeDTO like) {
        String logTag = Constants.GLOBAL_LOG_TAG + "LIKE (CREATE CHAT)";

        ChatDTO groupChat = getGroupChatFromLike(like);

        mainContent.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        chatsRepository.createChat(groupChat, result -> {
            switch (result.status) {
                case SUCCESS:
                    int chatId = result.data;
                    Log.i(logTag, "Чат успешно создан: " + chatId);
                    chatsRepository.fetchChat(chatId, user.getId(), result1 -> {
                        if (result1.status == Result.Status.SUCCESS) {
                            result1.data.setPartnerId(like.getLikerId());
                            result1.data.setImage(like.getImage());
                            startChatActivity(result1.data);
                        }
                    });
                    deleteLike(view, new LikeDTO(like.getLikerId(), user.getId()));
                    break;
                case ERROR:
                    renderLikes();
                    Log.e(logTag, result.error);
                    break;
                case EXISTS:
                    String message = "Чат уже существует!";
                    Toast.makeText(activityView.getContext(), message, Toast.LENGTH_LONG).show();
                    Log.i(logTag, message);
                    deleteLike(view, new LikeDTO(like.getLikerId(), user.getId()));
                    renderLikes();
                    break;
            }
        });
    }

    private void startChatActivity(ChatDTO data) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        ChatDTO.selectedChat = data;
        startActivity(intent);
        renderLikes();
    }

    private ChatDTO getGroupChatFromLike(LikeDTO like) {
        List<ChatMemberDTO> chatMembers = new ArrayList<>();
        chatMembers.add(new ChatMemberDTO(like.getUserName(), like.getLikerId(), like.getImage(), false, false)); // TODO: передача изображения
        chatMembers.add(new ChatMemberDTO(user.getName(), user.getId(), ImageUtils.convertBitmapToPrimitiveBytes(user.getMainImage()), false, false));

        java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ChatInfoDTO groupChatInfo = new ChatInfoDTO(user.getId(), timestamp , chatMembers, false);

        return new ChatDTO(null, null, null, null, null, groupChatInfo, like.getLikerId());
    }

    private void startChatActivity(int chatId, LikeDTO like) {
        ChatDTO chat = new ChatDTO(chatId, like.getUserName(), null, null, like.getImage(), null, like.getLikerId());
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        ChatDTO.selectedChat = chat;
        startActivity(intent);
    }

    private void deleteLike(View view, LikeDTO likeDTO) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DISLIKE";
        likesRepository.deleteLike(likeDTO, result -> {
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(activityView.getContext(), "Успешно!", Toast.LENGTH_LONG).show();
                    Log.i(logTag, "Удалено " + result.data + " лайков");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }
}

