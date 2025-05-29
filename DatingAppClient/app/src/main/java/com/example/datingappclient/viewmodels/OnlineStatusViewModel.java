package com.example.datingappclient.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class OnlineStatusViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<Integer, Boolean>> onlineStatusMap = new MutableLiveData<>(new HashMap<>());

    public OnlineStatusViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Map<Integer, Boolean>> getOnlineStatuses() {
        return onlineStatusMap;
    }

    public void setOnlineStatus(int userId, boolean online) {
        Map<Integer, Boolean> map = new HashMap<>(onlineStatusMap.getValue());
        map.put(userId, online);
        onlineStatusMap.setValue(map);
    }

    public Boolean isUserOnline(int userId) {
        Map<Integer, Boolean> map = onlineStatusMap.getValue();
        return map != null && map.getOrDefault(userId, false);
    }

}
