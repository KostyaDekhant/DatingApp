package com.example.datingappclient;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.fragments.ChatsContainer;
import com.example.datingappclient.fragments.LikeFragment;
import com.example.datingappclient.fragments.SearchFragment;
import com.example.datingappclient.fragments.UserFragment;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private int userID;
    RetrofitService retrofitService;
    ServerAPI serverAPI;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get pk_user from auth activity
        Bundle arguments = getIntent().getExtras();
        userID = arguments.getInt("pk_user");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(userID)).commit();

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        retrofitService = new RetrofitService();
        serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.user) {
            selectedFragment = new UserFragment(userID);
        }
        else if (itemId == R.id.like) {
            selectedFragment = new LikeFragment();
        }
        else if (itemId == R.id.search) {
            selectedFragment = new SearchFragment();
        }
        else if (itemId == R.id.chat) {
            selectedFragment = new ChatsContainer();
        }
        else if (itemId == R.id.slidemenu) {

        }
        // It will help to replace the
        // one fragment to other.
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };
}