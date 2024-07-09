package com.example.datingappclient.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeFragment extends Fragment {

    private int userID;

    GridLayout gridLayout;
    LayoutInflater inflater;
    DisplayMetrics metrics;

    public LikeFragment(int userID) {
        this.userID = userID;
    }

    public LikeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_like, container, false);
        this.inflater = inflater;
        Resources resources = getContext().getResources();
        metrics = resources.getDisplayMetrics();

        gridLayout = activityView.findViewById(R.id.likes_grid);

        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getLikes(userID).enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.body() != null) {
                    Log.d("GET LIKES", response.body().toString());
                    int objNum = 0;
                    for (Object[] it : response.body()) {
                        View cardLike = createLikeCard(it);
                        if (cardLike != null) {
                            cardLike.setLayoutParams(setLayoutParams(objNum++));
                            gridLayout.addView(cardLike);
                        }
                    }
                }
                else {
                    Log.d("GET LIKES. BODY NULL", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                Log.d("ERROR GET LIKES", throwable.getMessage());
            }
        });

        return activityView;
    }

    private View createLikeCard(Object[] likeObj) {

        try {
            int likerID = ((Double) likeObj[0]).intValue();
            String username = likeObj[2].toString();
            String imageStr = likeObj[3].toString();
            String birthday = likeObj[4].toString();

        byte[] array = Base64.getDecoder().decode(imageStr);
        Bitmap image = ImageUtils.convertPrimitiveByteToBitmap(array);

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

        roundedImageView.setImageBitmap(image);
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
        constraintSet.applyTo(constraintLayout);;

        likeButton.setTag(R.id.TAG_CARDLIKE_VIEW, cardLike);
        likeButton.setTag(R.id.TAG_LIKER_ID, likerID);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridLayout.removeView((View) view.getTag(R.id.TAG_CARDLIKE_VIEW));

                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                int likerID = ((int) view.getTag(R.id.TAG_LIKER_ID));
                serverAPI.createChat(userID, likerID).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int returnCode = response.body().intValue();
                        if (returnCode == -1)
                            Log.d("CREATE CHAT", "Chat is already exists");
                        else
                            Log.d("CREATE CHAT", "Chat ID: " + returnCode);

                        serverAPI.deleteLike(likerID, userID).enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                int returnCode = response.body().intValue();
                                if (returnCode == 0) Log.d("DISLIKE", "No rows to delete");
                                if (returnCode > 0) Log.d("DISLIKE", "Delete " + returnCode + " rows");
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable throwable) {
                                Log.d("ERROR DISLIKE", throwable.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Log.d("CREATE CHAT ERROR", throwable.getMessage());
                    }
                });
            }
        });

        dislikeButton.setTag(R.id.TAG_LIKER_ID, likerID);
        dislikeButton.setTag(R.id.TAG_CARDLIKE_VIEW, cardLike);
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gridLayout.removeView((View) view.getTag(R.id.TAG_CARDLIKE_VIEW));

                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                int likerID = ((int) view.getTag(R.id.TAG_LIKER_ID));
                serverAPI.deleteLike(likerID, userID).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int returnCode = response.body().intValue();
                        if (returnCode == 0) Log.d("DISLIKE", "No rows to delete");
                        if (returnCode > 0) Log.d("DISLIKE", "Delete " + returnCode + " rows");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Log.d("ERROR DISLIKE", throwable.getMessage());
                    }
                });
            }
        });

        return cardLike;
        } catch (Exception exception) {
            Log.d("ERROR GET OBJECT LIKE", exception.getMessage());
            return null;
        }
    }

    private GridLayout.LayoutParams setLayoutParams(int elCount) {
        int dpWidth = 130;
        int dpHeight = 246;

        int pxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, metrics);
        int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, metrics);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        int rowNum = elCount / 2;
        int columnNum;
        if (elCount % 2 == 0 ) {
            columnNum = 0;
            params.setGravity(Gravity.START);
        }
        else {
            columnNum = 1;
            params.setGravity(Gravity.END);
        }

        params.rowSpec = GridLayout.spec(rowNum, 1, 1f);
        params.columnSpec = GridLayout.spec(columnNum, 1, 1f);
        params.setGravity(Gravity.CENTER);
        return params;
    }
}

