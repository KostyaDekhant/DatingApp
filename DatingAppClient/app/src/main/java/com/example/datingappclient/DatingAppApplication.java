package com.example.datingappclient;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.utils.AppLifecycleManager;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.OnlineStatusViewModel;
import com.example.datingappclient.websocket.StompClientService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatingAppApplication extends Application {
    private ChatsViewModel chatsViewModel;
    private ChatMembersViewModel chatMembersViewModel;
    private ChatMembersViewModel contactsViewModel;
    private OnlineStatusViewModel onlineStatusViewModel;

    // Кеш интересов и категорий
    private List<CategoryDTO> cachedCategories;
    private Map<CategoryDTO, List<InterestDTO>> cachedInterestsByCategory = new HashMap<>();

    private final AppLifecycleManager lifecycleManager = new AppLifecycleManager();

    @Getter
    private static TokenManager tokenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        tokenManager = new TokenManager(this);
        instance = this;
        activityCallback();
    }

    @Getter
    private static DatingAppApplication instance;

    public void disconnect() {
        StompClientService.getInstance().disconnect();
    }

    public void connect() {
        StompClientService.getInstance().connect();
    }

    private int started = 0;
    private int stopped = 0;

    private void activityCallback() {

        String logTag = Constants.GLOBAL_LOG_TAG + "APP_STATE";
        registerActivityLifecycleCallbacks(lifecycleManager);

        lifecycleManager.setListener(new AppLifecycleManager.OnAppStatusChangeListener() {
            @Override
            public void onAppForegrounded() {
                Log.d(logTag, "Приложение на переднем плане");
                if (chatsViewModel != null)
                    connect();
            }

            @Override
            public void onAppBackgrounded() {
                Log.d(logTag, "Приложение свернуто");
                disconnect();
            }
        });
    }
}
