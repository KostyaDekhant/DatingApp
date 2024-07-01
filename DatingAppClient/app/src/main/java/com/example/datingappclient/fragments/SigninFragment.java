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

public class SigninFragment extends Fragment {
    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View activityView = inflater.inflate(R.layout.fragment_signin, container, false);

        MaterialButton loginButton = activityView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
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
                serverAPI.login(jsonObject)
                        .enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                int returnCode = response.body();
                                if (returnCode > 0)
                                    ((AuthActivity) getActivity()).startMainActivity(returnCode);
                                else if (returnCode == -1 || returnCode == -2) {
                                    Snackbar.make(view, "Ошибка входа, неверный логин или пароль!", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable throwable) {
                                Toast.makeText(activityView.getContext(), "ERROR LOGIN", Toast.LENGTH_LONG).show();
                                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR LOGIN", throwable);
                            }
                        });
            }
        });

        return activityView;
    }
}