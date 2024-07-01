package com.example.datingappclient.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.User;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsereditFragment extends Fragment{

    private User user;
    public UsereditFragment(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_useredit, container, false);
        MaterialButton button = activityView.findViewById(R.id.save_button);

        setInputText(activityView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                TextInputEditText inputName = activityView.findViewById(R.id.username_inputEdit);
                TextInputEditText inputDesc = activityView.findViewById(R.id.description_inputEdit);

                String username = inputName.getText().toString();
                String desc = inputDesc.getText().toString();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("pk_user", user.getId());
                jsonObject.addProperty("name", username);
                jsonObject.addProperty("description", desc);

                serverAPI.updateUser(jsonObject).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                Toast.makeText(activityView.getContext(),"SUCCESS SAVE", Toast.LENGTH_LONG).show();
                                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "SUCCESS SAVE", response.body());

                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable throwable) {
                                Toast.makeText(activityView.getContext(),"ERROR SAVE", Toast.LENGTH_LONG).show();
                                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SAVE", throwable);

                            }
                        });
            }
        });
        return activityView;
    }

    private void setInputText(View view) {
        TextInputEditText name_input = view.findViewById(R.id.username_inputEdit);
        TextInputEditText desc_input = view.findViewById(R.id.description_inputEdit);

        name_input.setText(user.getUsername());
        desc_input.setText(user.getDesc());
    }
}