package com.example.datingappclient.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.model.dto.OnlineStatusDTO;
import com.example.datingappclient.websocket.controllers.OnlineStatusController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class OnlineStatusViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<Integer, OnlineStatusDTO>> onlineStatusMap = new MutableLiveData<>(new HashMap<>());
    private final OnlineStatusController onlineStatusController = new OnlineStatusController();

    public OnlineStatusViewModel(@NonNull Application application) {
        super(application);
        subscribeToUpdateOnlineStatus();
    }

    public LiveData<Map<Integer, OnlineStatusDTO>> getOnlineStatuses() {
        return onlineStatusMap;
    }

    public void setOnlineStatus(OnlineStatusDTO status) {
        Map<Integer, OnlineStatusDTO> map = new HashMap<>(onlineStatusMap.getValue());
        map.put(status.getUserId(), status);
        onlineStatusMap.setValue(map);
    }

    public void setOnlineStatus(int userId, boolean isOnline, Timestamp timestamp) {
        Map<Integer, OnlineStatusDTO> map = new HashMap<>(onlineStatusMap.getValue());
        OnlineStatusDTO status = new OnlineStatusDTO();
        status.setUserId(userId);
        status.setOnline(isOnline);
        status.setTimestamp(timestamp);
        map.put(userId, status);
        onlineStatusMap.setValue(map);
    }

    public Boolean isUserOnline(int userId) {
        Map<Integer, OnlineStatusDTO> map = onlineStatusMap.getValue();
        return map != null && map.containsKey(userId) && map.get(userId).isOnline();
    }

    public Timestamp getLastOnline(int userId) {
        Map<Integer, OnlineStatusDTO> map = onlineStatusMap.getValue();
        if (map != null && map.containsKey(userId)) {
            return map.get(userId).getTimestamp();
        }
        return null;
    }

    public void subscribeToUpdateOnlineStatus() {
        onlineStatusController.subscribeToUpdateOnlineStatus(result -> {
            switch (result.status) {
                case SUCCESS:
                    setOnlineStatus(result.data);
                    break;
                case ERROR:
                    // TODO: логирование или уведомление
                    break;
            }
        });
    }
}
