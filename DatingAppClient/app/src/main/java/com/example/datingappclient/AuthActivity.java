package com.example.datingappclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            // пользователь уже вошёл — открыть основной экран
            startMainActivity(userId);
        } else {
            // пользователь не вошёл — показать экран входа
            getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SigninFragment()).commit();
        }
    }

    public void startMainActivity(int id) {
        startActivity(new Intent(AuthActivity.this, MainActivity.class).putExtra("pk_user", id));
        finish();
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, fragment).commit();
    }
}
