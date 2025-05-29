package com.example.datingappclient.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;

public class UpdateEventsController {

    private final StompClient client;

    public UpdateEventsController() {
        client = new StompClientService().getClient();
    }

    // =============== UPDATE CHAT
    @SuppressLint("CheckResult")
    public Disposable subscribeToChatUpdatedEvents(int chatId, ResultCallback<Boolean> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT UPDATED";
        return client.topic("/topic/group_chats/" + chatId + "/updated")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        Log.i(logTag, "Чат обновлён: " + chatId);
                        callback.onResult(Result.success(true));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при обновлении чата", e);
                        callback.onResult(Result.error("Ошибка при обновлении чата " + chatId + " " + e));
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на обновления чатов", throwable));
    }
}
