package com.example.datingappclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.fragments.ChatListFragment;
import com.example.datingappclient.fragments.LikeFragment;
import com.example.datingappclient.fragments.SearchFragment;
import com.example.datingappclient.fragments.UserFragment;
import com.example.datingappclient.model.UserDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int userId;
    private UserDTO user;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;

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

        drawerLayout = findViewById(R.id.drawer_layout);

        setupBottomnavMenu();
        setupSlidebarMenu();

        // Get pk_user from auth activity
        Bundle arguments = getIntent().getExtras();
        userId = Objects.requireNonNull(arguments).getInt("pk_user");

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", userId) // или .putString("token", ...)
                .apply();

        // создания инстанса пользователя
        if (user == null) user = new UserDTO(userId);
        else user.setId(userId);

        // переход в чаты или на страницу пользователя
        if (Objects.equals(arguments.getString("action"), "showchats")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatListFragment(userId)).commit();
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, UserFragment.getInstance(user, true)).commit();
        }
    }

    private void setupBottomnavMenu() {
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.user) {
                selectedFragment = UserFragment.getInstance();
            } else if (itemId == R.id.like) {
                selectedFragment = new LikeFragment(userId);
            } else if (itemId == R.id.search) {
                selectedFragment = SearchFragment.newInstance(userId); // Передаем userID в SearchFragment
            } else if (itemId == R.id.chat) {
                selectedFragment = new ChatListFragment(userId);
            } else if (itemId == R.id.slidemenu) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }

    private void setupSlidebarMenu() {
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.groupchat_create) {
                // открыть профиль
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply(); // или .remove("user_id")

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // очистить стек
        startActivity(intent);
    }
}