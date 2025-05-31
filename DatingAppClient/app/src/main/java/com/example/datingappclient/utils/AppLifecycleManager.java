package com.example.datingappclient.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import lombok.Setter;

public class AppLifecycleManager implements Application.ActivityLifecycleCallbacks {

    private int started = 0;
    @Setter
    private OnAppStatusChangeListener listener;
    private boolean isInForeground = false;

    public interface OnAppStatusChangeListener {
        void onAppForegrounded();
        void onAppBackgrounded();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        started++;
        if (!isInForeground && started > 0) {
            isInForeground = true;
            if (listener != null) listener.onAppForegrounded();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        started--;
        if (isInForeground && started == 0) {
            isInForeground = false;
            if (listener != null) listener.onAppBackgrounded();
        }
    }

    // Остальное не требуется:
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
