package com.example.datingappclient.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datingappclient.AuthActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

public class SigninFragment extends Fragment {

    public SigninFragment() {
        // Required empty public constructor
    }

    private final UserRepository userRepository = new UserRepository();;
    private TextInputEditText inputLogin;
    private TextInputEditText inputPass;
    private View activityView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityView = inflater.inflate(R.layout.fragment_signin, container, false);

        inputLogin = activityView.findViewById(R.id.login_inputEdit);
        inputPass = activityView.findViewById(R.id.pass_inputEdit);

        setupSignupButton();
        setupLoginButton();

        return activityView;
    }

    private void loginUser(JsonObject userLoginInfoJson) {
        String logTag = Constants.GLOBAL_LOG_TAG + "SIGNIN";
        userRepository.login(userLoginInfoJson, result -> {
            int userId = result.data;
            switch (result.status) {
                case SUCCESS:
                    if (userId > 0) {
                        Log.i(logTag, "User successfully sign in with id: " + userId);
                        ((AuthActivity) getActivity()).startMainActivity(userId);
                    }
                    else {
                        Log.i(logTag, "Wrong login or password: " + userId);
                        Snackbar.make(activityView, "Ошибка входа, неверный логин или пароль!", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }
    private void setupLoginButton() {
        MaterialButton loginButton = activityView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            String login = inputLogin.getText().toString();
            String pass = inputPass.getText().toString();

            // Хэширование пароля перед отправкой на сервер
            // pass = PasswordUtils.hashPassword(pass); // Расскоментировать для хэширования

            JsonObject userLoginInfoJson = new JsonObject();
            userLoginInfoJson.addProperty("login", login);
            userLoginInfoJson.addProperty("password", pass);

            loginUser(userLoginInfoJson);
        });
    }
    private void setupSignupButton() {
        TextView regButton = activityView.findViewById(R.id.reg_button);
        regButton.setOnClickListener(view -> ((AuthActivity)getActivity()).openFragment(new SignupFragment()));
    }
}
