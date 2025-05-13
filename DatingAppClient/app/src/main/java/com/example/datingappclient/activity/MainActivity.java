package com.example.datingappclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.fragments.ChatListFragment;
import com.example.datingappclient.fragments.GroupChatMembersFragment;
import com.example.datingappclient.fragments.LikeFragment;
import com.example.datingappclient.fragments.SearchFragment;
import com.example.datingappclient.fragments.UserFragment;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.UserDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private int userId;
    private UserDTO user;

    private DrawerLayout drawerLayout;
    private TextView navHeaderNameLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init view
        super.onCreate(savedInstanceState);
        //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AuthResponse authResponse = getIntent().getParcelableExtra("authResponse");
        userId = authResponse.getUserId();

        // создания инстанса пользователя
        if (user == null) user = new UserDTO(userId);
        else user.setId(userId);

        drawerLayout = findViewById(R.id.drawer_layout);

        setupBottomnavMenu();
        setupSlideMenu();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, UserFragment.getInstance(user, true)).commit();
    }

    private void setupBottomnavMenu() {
        /* === Android Objects === */
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.user) {
                selectedFragment = UserFragment.getInstance();
            } else if (itemId == R.id.like) {
                selectedFragment = LikeFragment.newInstance(user);
            } else if (itemId == R.id.search) {
                selectedFragment = SearchFragment.newInstance(userId);
            } else if (itemId == R.id.chat) {
                selectedFragment = ChatListFragment.newInstance(userId);
            } else if (itemId == R.id.slidemenu) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }

    private void setupSlideMenu() {
        NavigationView navView = findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.groupchat_create) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(user.getId()))
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // очистить стек
        startActivity(intent);
    }
}