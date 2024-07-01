package com.example.datingappclient;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingappclient.fragments.LoginFragment;
import com.example.datingappclient.fragments.SignupFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new LoginFragment()).commit();

        MaterialButtonToggleGroup materialButtonToggleGroup = findViewById(R.id.auth_buttons);

        MaterialButton loginButton = findViewById(R.id.signin_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new LoginFragment()).commit();
            }
        });

        MaterialButton signButton = findViewById(R.id.signup_button);
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SignupFragment()).commit();
            }
        });
    }
}
