package com.example.datingappclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.fragments.ChatListFragment;
import com.example.datingappclient.fragments.ContactsFragment;
import com.example.datingappclient.fragments.GroupChatMembersFragment;
import com.example.datingappclient.fragments.LikeFragment;
import com.example.datingappclient.fragments.FormsFragment;
import com.example.datingappclient.fragments.UserFragment;
import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.AuthAPI;
import com.example.datingappclient.retrofit.repository.AuthRepository;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.OnlineStatusViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    /* === Repositories === */
    private BubblesRepository bubblesRepository;
    private ChatsRepository chatsRepository;
    private UserRepository userRepository;

    /* === Android Object === */
    private DrawerLayout drawerLayout;

    /* === Other === */
    private int userId;
    private UserDTO user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        receiveLogoutSignal();
        setupRepository();
        // get all interests
        fetchCategories();

        TokenManager tokenManager = new TokenManager(this);
        //Log.d("TOKEN", tokenManager.getAccessToken());
        userId = tokenManager.getUserId();

        // Получить контакты
        getUserContacts();

        getOnlineUsers();

        // создания инстанса пользователя
        if (user == null) user = new UserDTO(userId);
        else user.setId(userId);

        drawerLayout = findViewById(R.id.drawer_layout);

        setupBottomnavMenu();
        setupSlideMenu();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, UserFragment.getInstance(user, true)).commit();
    }

    private void getOnlineUsers() {
        String logTag = Constants.GLOBAL_LOG_TAG + "ONLINE USERS";
        if (DatingAppApplication.getInstance().getOnlineStatusViewModel() == null) {
            DatingAppApplication.getInstance().setOnlineStatusViewModel(new ViewModelProvider(this).get(OnlineStatusViewModel.class));
        }
        OnlineStatusViewModel onlineStatusViewModel = DatingAppApplication.getInstance().getOnlineStatusViewModel();
        userRepository.fetchOnlineUsers(result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, result.data.size() + " пользователей в сети!");
                    for (Integer userId : result.data){
                        onlineStatusViewModel.setOnlineStatus(userId, true);
                    }
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Нет пользователей в сети!");
                    break;
            }
        });
    }

    private void setupRepository() {
        Context context = getApplicationContext();
        bubblesRepository = new BubblesRepository(context);
        chatsRepository = new ChatsRepository(context);
        userRepository = new UserRepository(context);
    }

    private void receiveLogoutSignal() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent(MainActivity.this, AuthActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }, new IntentFilter("com.example.datingappclient.LOGOUT"));
    }

    private void getUserContacts() {
        DatingAppApplication app = (DatingAppApplication) getApplication();

        if (app.getContactsViewModel() == null) {
            ChatMembersViewModel contacts = new ViewModelProvider(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new ChatMembersViewModel();
                }
            }).get(ChatMembersViewModel.class);

            app.setContactsViewModel(contacts);
            contacts.setChatMembers(new ArrayList<>());

            fetchContacts(contacts);
        }
    }

    private void fetchContacts(ChatMembersViewModel contacts) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET USER CONTACTS";
        chatsRepository.fetchPossibleChatMembers(userId, 0, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено " + result.data.size() + " контактов юзера " + userId);
                    contacts.setChatMembers(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Контакты юзера " + userId + " не найдены");
                    break;
            }
        });
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
                selectedFragment = FormsFragment.newInstance(userId);
            } else if (itemId == R.id.chat) {
                selectedFragment = ChatListFragment.newInstance(user);
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
        View headerView = navView.getHeaderView(0);

        TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
        ImageView avatarImageView = headerView.findViewById(R.id.nav_header_avatar);

        nameTextView.setText(user.getName());
        avatarImageView.setImageBitmap(ImageUtils.getCroppedBitmap(user.getMainImage()));


        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.groupchat_create) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(user))
                        .addToBackStack(null)
                        .commit();
            }
            else if (id == R.id.nav_contacts) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactsFragment.newInstance(userId))
                        .addToBackStack(null)
                        .commit();
            }
            else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void logout() {
        TokenManager tokenManager = new TokenManager(getApplicationContext());

        String logTag = Constants.GLOBAL_LOG_TAG + "LOGOUT";

        AuthRepository authRepository = new AuthRepository(RetrofitClient.getAuthOnlyClient(this).create(AuthAPI.class));
        authRepository.logout(tokenManager.getUserId(), result -> {
            if (result.status == Result.Status.SUCCESS) {
                Log.i(logTag, "Success");
                tokenManager.clearTokens();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // очистить стек
                startActivity(intent);
            }
            else {
                Log.e(logTag, result.error);
                Toast.makeText(this, "Ошибка выхода!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchCategories() {
        boolean categoriesIsLoaded = false;

        DatingAppApplication app = (DatingAppApplication) getApplication();
        if (app.getCachedCategories() != null && !app.getCachedCategories().isEmpty()) {
            categoriesIsLoaded = true;
        }

        if (!categoriesIsLoaded ||
                app.getCachedInterestsByCategory() == null || app.getCachedInterestsByCategory().isEmpty())
            fetchAndCacheAllBubbles();
    }

    private void fetchAndCacheAllBubbles() {
        String logTag = Constants.GLOBAL_LOG_TAG + "CATEGORIES";
        bubblesRepository.fetchCategories(result -> {
            switch (result.status) {
                case SUCCESS:
                    DatingAppApplication app = (DatingAppApplication) getApplication();
                    app.setCachedCategories(result.data);
                    Log.i(logTag, "Получено " + result.data.size() + " категорий");
                    // Запрашиваем бабблы ПОСЛЕ категорий
                    fetchInterests();
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
            }
        });
    }

    private void fetchInterests() {
        DatingAppApplication app = (DatingAppApplication) getApplication();
        Map<CategoryDTO, List<InterestDTO>> categoryInterestMap = new HashMap<>();
        AtomicInteger categoriesLoaded = new AtomicInteger();

        for (CategoryDTO category : app.getCachedCategories()) {
            bubblesRepository.fetchInterestsByCategory(category.getId(), result -> {
                if (result.status == Result.Status.SUCCESS) {
                    categoryInterestMap.put(category, result.data);
                } else {
                    Log.e(Constants.GLOBAL_LOG_TAG + "INTEREST", result.error);
                    categoryInterestMap.put(category, List.of());
                }

                if (categoriesLoaded.incrementAndGet() == app.getCachedCategories().size()) {
                    app.setCachedInterestsByCategory(categoryInterestMap);
                }
            });
        }
    }
}