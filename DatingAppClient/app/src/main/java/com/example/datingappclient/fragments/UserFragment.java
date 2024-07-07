package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements View.OnClickListener {

    private User user;

    public UserFragment(int userID) {
        user = new User(userID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        MaterialButton button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(this);

        getUserinfo(view, user.getId());

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

                serverAPI.getUserImages(userID).enqueue(new Callback<List<Object[]>>() {
                    @Override
                    public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                        ImageView profileImage = view.findViewById(R.id.profileImage);
                        Log.d("GETIMAGE", "Res: " + response.body());

                        List<Object[]> temp = response.body();
                        String imageStr = temp.get(0)[2].toString();

                        byte[] array = Base64.getDecoder().decode(imageStr);
                        Bitmap image = ImageUtils.convertPrimitiveByteToBitmap(array);

                        profileImage.setImageBitmap(image);
                    }

                    @Override
                    public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                        Log.d("BAD GETIMAGE", throwable.toString());
                    }
                });
                user = new User(userID, name, description, age, new ArrayList<UserImage>() );
                setUserinfo(view);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUserinfo(View view) {
        TextView descLabel = view.findViewById(R.id.description_lable), nameLabel = view.findViewById(R.id.name_label), ageLabel = view.findViewById(R.id.age_label);
        nameLabel.setText(user.getUsername() + ",");
        descLabel.setText(user.getDesc());
        ageLabel.setText(user.getAge());
    }

    ;
}
