package com.example.datingappclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.datingappclient.fragments.SigninFragment;
import com.example.datingappclient.fragments.SignupFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SigninFragment()).commit();

        MaterialButton signinButton = findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SigninFragment()).commit();
            }
        });

        MaterialButton signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SignupFragment()).commit();
            }
        });
    }

    public void startMainActivity(int id) {
        startActivity(new Intent(AuthActivity.this, MainActivity.class).putExtra("pk_user", id));
        finish();
    }
}
