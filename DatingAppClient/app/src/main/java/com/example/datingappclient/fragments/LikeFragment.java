package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import com.example.datingappclient.ChatActivity;
import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.LikeDTO;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.LikesRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.button.MaterialButton;

import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeFragment extends Fragment {

    /* === Repositories === */
    private final LikesRepository likesRepository;
    private final ChatsRepository chatsRepository;

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
        likesRepository = new LikesRepository();
        chatsRepository = new ChatsRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_like, container, false);
        this.inflater = inflater;
        Resources resources = getContext().getResources();
        metrics = resources.getDisplayMetrics();

        gridLayout = activityView.findViewById(R.id.likes_grid);

        getUserLikes(userId);

        return activityView;
    }

    private void getUserLikes(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET LIKES";
        likesRepository.fetchUserLikes(userId, new LikesRepository.LikesCallback() {
            @Override
            public void onSuccess(List<LikeDTO> likes) {
                Log.d(logTag, "Получены лайки: " + likes.size());

                likesArray = likes;
                TextView noLikesView = activityView.findViewById(R.id.noLikes_label);

                if (likesArray.isEmpty()) {
                    noLikesView.setVisibility(View.VISIBLE);
                } else {
                    noLikesView.setVisibility(View.GONE);
                    renderLikes();
                }
            }

            @Override
            public void onEmpty(String message) {
                Log.d(logTag, "Для userId = " + userId + " - " + message);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
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
            int likerID = like.getLikerId();
            String username = like.getName();
            String birthday = DateUtils.localDateToString(like.getBirthday());

            View cardLike = inflater.inflate(R.layout.user_like_item, gridLayout, false);
            RoundedImageView roundedImageView = cardLike.findViewById(R.id.userImage);
            MaterialButton likeButton = cardLike.findViewById(R.id.like_button);
            MaterialButton dislikeButton = cardLike.findViewById(R.id.dislike_button);
            TextView usernameLabel = cardLike.findViewById(R.id.username_label);
            TextView ageLabel = cardLike.findViewById(R.id.age_label);

            cardLike.setId(View.generateViewId());
            roundedImageView.setId(View.generateViewId());
            likeButton.setId(View.generateViewId());
            dislikeButton.setId(View.generateViewId());
            usernameLabel.setId(View.generateViewId());
            ageLabel.setId(View.generateViewId());

            ConstraintLayout constraintLayout = (ConstraintLayout) cardLike;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            if (like.getImage() != null) {
                Bitmap image = ImageUtils.convertPrimitiveByteToBitmap(like.getImage());
                roundedImageView.setImageBitmap(image);
            }

            usernameLabel.setText(username + ",");
            ageLabel.setText("" + DateUtils.dateToAge(birthday));

            int pxEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
            int pxBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
            int pxBottomImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics);
            constraintSet.connect(likeButton.getId(), ConstraintSet.END, roundedImageView.getId(), ConstraintSet.END, pxEnd);
            constraintSet.connect(likeButton.getId(), ConstraintSet.BOTTOM, roundedImageView.getId(), ConstraintSet.BOTTOM, pxBottom);
            constraintSet.connect(dislikeButton.getId(), ConstraintSet.START, roundedImageView.getId(), ConstraintSet.START, pxEnd);
            constraintSet.connect(dislikeButton.getId(), ConstraintSet.BOTTOM, roundedImageView.getId(), ConstraintSet.BOTTOM, pxBottom);
            constraintSet.connect(roundedImageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, pxBottomImage);
            constraintSet.connect(usernameLabel.getId(), ConstraintSet.TOP, roundedImageView.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(usernameLabel.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, pxBottomImage);
            constraintSet.connect(ageLabel.getId(), ConstraintSet.TOP, roundedImageView.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(ageLabel.getId(), ConstraintSet.START, usernameLabel.getId(), ConstraintSet.END, pxBottom);
            constraintSet.applyTo(constraintLayout);

            likeButton.setTag(R.id.TAG_LIKER_ID, likerID);
            likeButton.setTag(R.id.TAG_CARDLIKE_VIEW, cardLike);
            likeButton.setTag(R.id.TAG_CARDLIKE_ID, objNum);

            dislikeButton.setTag(R.id.TAG_LIKER_ID, likerID);
            dislikeButton.setTag(R.id.TAG_CARDLIKE_VIEW, cardLike);
            dislikeButton.setTag(R.id.TAG_CARDLIKE_ID, objNum);

            likeButton.setOnClickListener(setupLikeButton());
            dislikeButton.setOnClickListener(setupDislikeButton());

            return cardLike;
        } catch (Exception exception) {
            Log.d(Constants.GLOBAL_LOG_TAG + "ERROR GET OBJECT LIKE", exception.getMessage());
            return null;
        }
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
            createChat(view, userId, likerId);
        };
    }

    private View.OnClickListener setupDislikeButton() {
        return view -> {
            int likerID = ((int) view.getTag(R.id.TAG_LIKER_ID));
            deleteLike(view, new LikeDTO(likerID, userId));
        };
    }

    private void createChat(View view, int userId, int likerId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "LIKE (CREATE CHAT)";
        chatsRepository.createChat(userId, likerId, new ChatsRepository.CreateChatCallback() {
            @Override
            public void onSuccess(Integer chatId) {
                Log.d(logTag, "Чат успешно создан: " + chatId);

               /* Context context = view.getContext();

                Intent intent = new Intent(context, ChatActivity.class)
                        .putExtra("senderID", userId)
                        .putExtra("receiverID", likerId)
                        .putExtra("username", username)
                        .putExtra("image", imageBytes);

                context.startActivity(intent);*/


                deleteLike(view, new LikeDTO(likerId, userId));
            }

            @Override
            public void onExists(String message) {
                Toast.makeText(activityView.getContext(), message, Toast.LENGTH_LONG).show();
                Log.i(logTag, message);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        }) ;
    }

    private void deleteLike(View view, LikeDTO likeDTO) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DISLIKE";
        likesRepository.deleteLike(likeDTO, new LikesRepository.DislikeCallback() {
            @Override
            public void onSuccess(Integer deleteLikesCount) {
                gridLayout.removeView((View) view.getTag(R.id.TAG_CARDLIKE_VIEW));
                likesArray.remove((int)view.getTag(R.id.TAG_CARDLIKE_ID));
                renderLikes();
                Toast.makeText(activityView.getContext(), "Успешно!", Toast.LENGTH_LONG).show();

                Log.i(logTag, "Удалено " + deleteLikesCount + " лайков");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }
}

