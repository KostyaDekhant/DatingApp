package com.example.datingappclient.fragments;

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
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class LikeFragment extends Fragment {

    /* === Repositories === */
    private LikesRepository likesRepository;
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private GridLayout gridLayout;
    private LayoutInflater inflater;
    private DisplayMetrics metrics;
    private View activityView;

    /* === Other === */
    @Setter
    private UserDTO user;
    private List<LikeDTO> likesArray;

    /* === Methods === */
    private LikeFragment() {

    }

    public static LikeFragment newInstance(UserDTO user) {
        LikeFragment likeFragment = new LikeFragment();
        likeFragment.user = user;
        return likeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_like, container, false);
        this.inflater = inflater;

        setupRepository();

        metrics = getContext().getResources().getDisplayMetrics();

        gridLayout = activityView.findViewById(R.id.likes_grid);

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

                    TextView noLikesView = activityView.findViewById(R.id.noLikes_label);

                    if (likesArray.isEmpty()) {
                        noLikesView.setVisibility(View.VISIBLE);
                    } else {
                        noLikesView.setVisibility(View.GONE);
                        renderLikes();
                    }
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Для user.getId() = " + user.getId() + " нет лайков!");
                    break;
            }
        });
    }

    private void renderLikes() {
        gridLayout.removeAllViews();
        int objNum = 0;
        for (LikeDTO it : likesArray) {
            View cardLike = createLikeCard(it, objNum);
            if (cardLike != null) {
                cardLike.setLayoutParams(setLayoutParams(objNum++));
                gridLayout.addView(cardLike);
            }
        }
    }

    private View createLikeCard(LikeDTO like, int objNum) {
        try {
            View card = inflater.inflate(R.layout.user_like_item, gridLayout, false);
            bindCardData(card, like);
            applyCardConstraints(card);
            applyTagsAndListeners(card, like, objNum);
            return card;
        } catch (Exception e) {
            Log.e(Constants.GLOBAL_LOG_TAG + "ERROR GET OBJECT LIKE", e.getMessage());
            return null;
        }
    }

    private void bindCardData(View card, LikeDTO like) {
        RoundedImageView imageView = card.findViewById(R.id.userImage);
        TextView usernameLabel = card.findViewById(R.id.username_label);
        TextView ageLabel = card.findViewById(R.id.age_label);

        if (like.getImage() != null) {
            Bitmap image = ImageUtils.convertPrimitiveByteToBitmap(like.getImage());
            imageView.setImageBitmap(image);
        }

        String username = like.getUserName();
        int age = DateUtils.dateToAge(like.getBirthday());

        usernameLabel.setText(username + ",");
        ageLabel.setText(String.valueOf(age));
    }

    private void applyCardConstraints(View card) {
        ConstraintLayout layout = (ConstraintLayout) card;
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        RoundedImageView image = layout.findViewById(R.id.userImage);
        MaterialButton likeBtn = layout.findViewById(R.id.like_button);
        MaterialButton dislikeBtn = layout.findViewById(R.id.dislike_button);
        TextView usernameLabel = layout.findViewById(R.id.username_label);
        TextView ageLabel = layout.findViewById(R.id.age_label);

        int pxEnd = dpToPx(15);
        int pxBottom = dpToPx(5);
        int pxBottomImage = dpToPx(20);

        set.connect(likeBtn.getId(), ConstraintSet.END, image.getId(), ConstraintSet.END, pxEnd);
        set.connect(likeBtn.getId(), ConstraintSet.BOTTOM, image.getId(), ConstraintSet.BOTTOM, pxBottom);
        set.connect(dislikeBtn.getId(), ConstraintSet.START, image.getId(), ConstraintSet.START, pxEnd);
        set.connect(dislikeBtn.getId(), ConstraintSet.BOTTOM, image.getId(), ConstraintSet.BOTTOM, pxBottom);
        set.connect(image.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, pxBottomImage);
        set.connect(usernameLabel.getId(), ConstraintSet.TOP, image.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(usernameLabel.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, pxBottomImage);
        set.connect(ageLabel.getId(), ConstraintSet.TOP, image.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(ageLabel.getId(), ConstraintSet.START, usernameLabel.getId(), ConstraintSet.END, pxBottom);

        set.applyTo(layout);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void applyTagsAndListeners(View card, LikeDTO like, int objNum) {
        MaterialButton likeBtn = card.findViewById(R.id.like_button);
        MaterialButton dislikeBtn = card.findViewById(R.id.dislike_button);

        likeBtn.setTag(R.id.TAG_LIKER_ID, like);
        likeBtn.setTag(R.id.TAG_CARDLIKE_VIEW, card);
        likeBtn.setTag(R.id.TAG_CARDLIKE_ID, objNum);

        dislikeBtn.setTag(R.id.TAG_LIKER_ID, like);
        dislikeBtn.setTag(R.id.TAG_CARDLIKE_VIEW, card);
        dislikeBtn.setTag(R.id.TAG_CARDLIKE_ID, objNum);

        likeBtn.setOnClickListener(setupLikeButton());
        dislikeBtn.setOnClickListener(setupDislikeButton());
    }

    private GridLayout.LayoutParams setLayoutParams(int elCount) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        int rowNum = elCount / 2;
        int columnNum;
        if (elCount % 2 == 0) {
            columnNum = 0;
            params.setGravity(Gravity.START);
        } else {
            columnNum = 1;
            params.setGravity(Gravity.END);
        }

        params.rowSpec = GridLayout.spec(rowNum, 1, 1f);
        params.columnSpec = GridLayout.spec(columnNum, 1, 1f);
        params.setGravity(Gravity.CENTER);
        return params;
    }

    private View.OnClickListener setupLikeButton() {
        return view -> {
            LikeDTO like = (LikeDTO) view.getTag(R.id.TAG_LIKER_ID);

            createGroupChat(view, like);
        };
    }

    private View.OnClickListener setupDislikeButton() {
        return view -> {
            int likerID = ((LikeDTO) view.getTag(R.id.TAG_LIKER_ID)).getLikerId();
            deleteLike(view, new LikeDTO(likerID, user.getId()));
        };
    }

    private void createGroupChat(View view, LikeDTO like) {
        String logTag = Constants.GLOBAL_LOG_TAG + "LIKE (CREATE CHAT)";

        ChatDTO groupChat = getGroupChatFromLike(like);

        chatsRepository.createChat(groupChat, result -> {
            switch (result.status) {
                case SUCCESS:
                    int chatId = result.data;
                    Log.i(logTag, "Чат успешно создан: " + chatId);
                    startChatActivity(chatId, like);
                    deleteLike(view, new LikeDTO(like.getLikerId(), user.getId()));
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EXISTS:
                    String message = "Чат уже существует!";
                    Toast.makeText(activityView.getContext(), message, Toast.LENGTH_LONG).show();
                    Log.i(logTag, message);
                    deleteLike(view, new LikeDTO(like.getLikerId(), user.getId()));
                    break;
            }
        });
    }

    private ChatDTO getGroupChatFromLike(LikeDTO like) {
        List<ChatMemberDTO> chatMembers = new ArrayList<>();
        chatMembers.add(new ChatMemberDTO(like.getUserName(), like.getLikerId(), like.getImage(), false)); // TODO: передача изображения
        chatMembers.add(new ChatMemberDTO(user.getName(), user.getId(), ImageUtils.convertBitmapToPrimitiveBytes(user.getMainImage()), false));

        java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ChatInfoDTO groupChatInfo = new ChatInfoDTO(user.getId(), timestamp , chatMembers, false);

        return new ChatDTO(null, null, null, null, groupChatInfo);
    }

    private void startChatActivity(int chatId, LikeDTO like) {
        ChatDTO chat = new ChatDTO(chatId, like.getUserName(), null, like.getImage(), null);
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
                    gridLayout.removeView((View) view.getTag(R.id.TAG_CARDLIKE_VIEW));
                    likesArray.remove((int)view.getTag(R.id.TAG_CARDLIKE_ID));
                    renderLikes();
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

