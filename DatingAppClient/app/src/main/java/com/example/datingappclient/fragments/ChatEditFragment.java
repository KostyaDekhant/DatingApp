package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;

public class ChatEditFragment extends Fragment {

    /* === Repository === */

    /* === Android Objects === */
    private View activityView;

    /* === Other === */

    private ChatEditFragment() {}

    public static ChatEditFragment newInstance () {
        return new ChatEditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_add_members_to_chat, container, false);

        setupToolbar();

        return activityView;
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });

        // Новый способ добавления меню
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.group_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_edit) {
                    // TODO: handle edit action
                    return true;
                } else if (menuItem.getItemId() == R.id.action_more) {
                    // TODO: handle more action
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }
}
