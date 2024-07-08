package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.model.User;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static User user;
    private ImageView profileImage;
    private int currentImageIndex = 0;
    private boolean isLogin ;

    public UserFragment(User user) {
        this.user = user;
        this.isLogin = false;
    }

    public UserFragment(User user, boolean isLogin) {
        this.user = user;
        this.isLogin = isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        MaterialButton button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(this);

        profileImage = view.findViewById(R.id.profileImage);
        setupImageTouchListener();

        if (isLogin)
            getUserinfo(view, user.getId());
        else {
            setUserinfo(view);
            profileImage.setImageBitmap(user.getMainImage());
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new UsereditFragment(user)).commit();
    }

    private void getUserinfo(View view, int userID) {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getUser(userID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject userinfo = response.body();
                String name = userinfo.get("name").toString().replace("\"", "");
                String description = userinfo.get("description").toString().replace("\"", "");
                String age = userinfo.get("age").toString();

                user = new User(userID, name, description, age);
                setUserinfo(view);

                serverAPI.getUserImages(userID).enqueue(new Callback<List<Object[]>>() {
                    @Override
                    public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                        Log.d("GETIMAGE", "Res: " + response.body());

                        user.setListImages(ImageUtils.objectListToUserImageList(response.body()));
                        setUserinfo(view);
                        if (!user.getListImages().isEmpty()) {
                            profileImage.setImageBitmap(user.getListImages().get(0).getImage());
                        }
                        // Чтобы больше не запрашивать данные
                        isLogin = false;
                    }

                    @Override
                    public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                        Log.d("BAD GETIMAGE", throwable.toString());
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUserinfo(View view) {
        TextView descLabel = view.findViewById(R.id.description_label), nameLabel = view.findViewById(R.id.username_label), ageLabel = view.findViewById(R.id.age_label);
        nameLabel.setText(user.getUsername() + ",");
        descLabel.setText(user.getDesc());
        ageLabel.setText(user.getAge());
    }
    //пальцы не совать
    private void setupImageTouchListener() {
        profileImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    int width = v.getWidth();
                    if (x < width / 2) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                return true;
            }
        });
    }

    private void onSwipeRight() {
        Log.d("UserFragment", "Clicked on the left half of the image");
        if (currentImageIndex > 0) {
            currentImageIndex--;
            updateProfileImage();
        }
    }

    private void onSwipeLeft() {
        Log.d("UserFragment", "Clicked on the right half of the image");
        if (currentImageIndex < user.getListImages().size() - 1) {
            currentImageIndex++;
            updateProfileImage();
        }
    }

    private void updateProfileImage() {
        Bitmap image = user.getListImages().get(currentImageIndex).getImage();
        profileImage.setImageBitmap(image);
    }
}
