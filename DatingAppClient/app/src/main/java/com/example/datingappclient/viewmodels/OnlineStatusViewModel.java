package com.example.datingappclient.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.model.dto.OnlineStatusDTO;
import com.example.datingappclient.websocket.controllers.OnlineStatusController;

import java.util.HashMap;
import java.util.Map;

public class OnlineStatusViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<Integer, Boolean>> onlineStatusMap = new MutableLiveData<>(new HashMap<>());
    private final OnlineStatusController onlineStatusController = new OnlineStatusController();

    public OnlineStatusViewModel(@NonNull Application application) {
        super(application);
        subscribeToUpdateOnlineStatus();
    }

    public LiveData<Map<Integer, Boolean>> getOnlineStatuses() {
        return onlineStatusMap;
    }

    public void setOnlineStatus(int userId, boolean online) {
        Map<Integer, Boolean> map = new HashMap<>(onlineStatusMap.getValue());
        map.put(userId, online);
        onlineStatusMap.setValue(map);
    }

    public void setOnlineStatus(OnlineStatusDTO onlineStatus) {
        Map<Integer, Boolean> map = new HashMap<>(onlineStatusMap.getValue());
        map.put(onlineStatus.getUserId(), onlineStatus.isOnline());
        onlineStatusMap.setValue(map);
    }

    public Boolean isUserOnline(int userId) {
        Map<Integer, Boolean> map = onlineStatusMap.getValue();
        return map != null && map.getOrDefault(userId, false);
    }

    public void subscribeToUpdateOnlineStatus() {
        onlineStatusController.subscribeToUpdateOnlineStatus(result -> {
            switch (result.status) {
                case SUCCESS:
                    setOnlineStatus(result.data);
                    break;
                case ERROR:
                    //
                    break;
            }
        });
    }

}
