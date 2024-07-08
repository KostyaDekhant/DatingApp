package com.example.datingappclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.fragments.ChatListFragment;
import com.example.datingappclient.fragments.LikeFragment;
import com.example.datingappclient.fragments.SearchFragment;
import com.example.datingappclient.fragments.UserFragment;
import com.example.datingappclient.model.User;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int userID;
    private User user;

    RetrofitService retrofitService;
    ServerAPI serverAPI;
    BottomNavigationView bottomNavigationView;

    UserFragment userFragment;

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

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Get pk_user from auth activity
        Bundle arguments = getIntent().getExtras();
        userID = Objects.requireNonNull(arguments).getInt("pk_user");
        user = new User(userID);
        if (Objects.equals(arguments.getString("action"), "showchats")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatListFragment(userID)).commit();
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
        else {
            userFragment = new UserFragment(user, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).commit();
        }

        retrofitService = new RetrofitService();
        serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.user) {
            selectedFragment = userFragment;
        } else if (itemId == R.id.like) {
            selectedFragment = new LikeFragment(userID);
        } else if (itemId == R.id.search) {
            selectedFragment = SearchFragment.newInstance(userID); // Передаем userID в SearchFragment
        } else if (itemId == R.id.chat) {
            selectedFragment = new ChatListFragment(userID);
        } else if (itemId == R.id.slidemenu) {
            // Обработка элемента slidemenu
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };
}