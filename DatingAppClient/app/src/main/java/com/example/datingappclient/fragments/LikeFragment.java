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
import com.example.datingappclient.model.ChatDTO;
import com.example.datingappclient.model.LikeDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.LikesRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

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
    private final int userId;
    private List<LikeDTO> likesArray;

    /* === Methods === */
    public LikeFragment(int userID) {
        this.userId = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_like, container, false);
        this.inflater = inflater;

        setupRepository();

        metrics = getContext().getResources().getDisplayMetrics();

        gridLayout = activityView.findViewById(R.id.likes_grid);

        getUserLikes(userId);

        return activityView;
    }

    private void setupRepository() {
        likesRepository = new LikesRepository(requireContext());
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void getUserLikes(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET LIKES";
        likesRepository.fetchUserLikes(userId, result -> {
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
                    Log.d(logTag, "Для userId = " + userId + " нет лайков!");
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

        String username = like.getName();
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
        int likerID = like.getLikerId();

        MaterialButton likeBtn = card.findViewById(R.id.like_button);
        MaterialButton dislikeBtn = card.findViewById(R.id.dislike_button);

        likeBtn.setTag(R.id.TAG_LIKER_ID, likerID);
        likeBtn.setTag(R.id.TAG_CARDLIKE_VIEW, card);
        likeBtn.setTag(R.id.TAG_CARDLIKE_ID, objNum);

        dislikeBtn.setTag(R.id.TAG_LIKER_ID, likerID);
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
            int likerId = ((int) view.getTag(R.id.TAG_LIKER_ID));
            createChat(view, likerId);
        };
    }

    private View.OnClickListener setupDislikeButton() {
        return view -> {
            int likerID = ((int) view.getTag(R.id.TAG_LIKER_ID));
            deleteLike(view, new LikeDTO(likerID, userId));
        };
    }

    interface ChatCallback {
        void onGetChat(ChatDTO chat);
    }

    private void getChat(int chatId, ChatCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHAT AFTER LIKE";
        chatsRepository.fetchUserChat(userId, chatId, result -> {
            switch (result.status) {
                case SUCCESS:
                    callback.onGetChat(result.data);
                    Log.i(logTag, "Success get chat");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Чат не найден");
                    break;
            }
        });
    }

    private void createChat(View view, int likerId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "LIKE (CREATE CHAT)";
        chatsRepository.createChat(userId, likerId, result -> {
            switch (result.status) {
                case SUCCESS:
                    int chatId = result.data;
                    Log.i(logTag, "Чат успешно создан: " + chatId);

                    getChat(chatId, (chat) -> {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, ChatActivity.class)
                                .putExtra("senderID", userId)
                                .putExtra("receiverID", chat.getChatId())
                                .putExtra("username", chat.getPartnerName())
                                .putExtra("image", chat.getAvatar());
                        context.startActivity(intent);
                    });

                    deleteLike(view, new LikeDTO(likerId, userId));
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EXISTS:
                    String message = "Чат уже существует!";
                    Toast.makeText(activityView.getContext(), message, Toast.LENGTH_LONG).show();
                    Log.i(logTag, message);
                    break;
            }
        });
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

