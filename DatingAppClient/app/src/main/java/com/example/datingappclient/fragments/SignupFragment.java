package com.example.datingappclient.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.datingappclient.AuthActivity;
import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View activityView = inflater.inflate(R.layout.fragment_signup, container, false);

        MaterialButton signupButton = activityView.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                TextInputEditText inputLogin = activityView.findViewById(R.id.login_inputEdit);
                TextInputEditText inputPass = activityView.findViewById(R.id.pass_inputEdit);

                String login = inputLogin.getText().toString();
                String pass = inputPass.getText().toString();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("login", login);
                jsonObject.addProperty("password", pass);

                serverAPI.signup(jsonObject).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int returnCode = response.body();
                        if (returnCode > 0) {
                            TextInputEditText inputName = activityView.findViewById(R.id.username_inputEdit);
                            TextInputEditText inputAge = activityView.findViewById(R.id.age_inputEdit);

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("pk_user", returnCode);
                            jsonObject.addProperty("name", inputName.getText().toString());
                            jsonObject.addProperty("age", inputAge.getText().toString());

                            serverAPI.updateUser(jsonObject).enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Toast.makeText(activityView.getContext(), "SUCCESS SAVE", Toast.LENGTH_LONG).show();
                                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "SUCCESS SAVE", response.body());
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable throwable) {
                                    Toast.makeText(activityView.getContext(), "ERROR SAVE", Toast.LENGTH_LONG).show();
                                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SAVE", throwable);
                                }
                            });
                            ((AuthActivity) getActivity()).startMainActivity(returnCode);
                        } else if (returnCode == -1) {
                            Snackbar.make(view, "Ошибка регистрации, такой пользователь уже существует!", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Toast.makeText(activityView.getContext(), "ERROR SIGNUP", Toast.LENGTH_LONG).show();
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SIGNUP", throwable);
                    }
                });
            }
        });

        return activityView;
    }
}