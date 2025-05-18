package com.example.datingappclient.fragments;

import static android.view.View.GONE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;

import java.util.List;

public class ChatEditFragment extends Fragment {

    /* === Repository === */

    /* === Android Objects === */
    private View activityView;

    /* === Other === */
    private Integer userId;
    private ChatDTO chat;

    /* === === */
    private ChatMembersViewModel viewModel;
    private ChatMembersAdapter adapter;

    private ChatEditFragment() {}

    public static ChatEditFragment newInstance (Integer user, ChatDTO chat) {
        ChatEditFragment chatEditFragment = new ChatEditFragment();
        chatEditFragment.userId = user;
        chatEditFragment.chat = chat;
        return chatEditFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_chat_properties, container, false);

        setupToolbar();
        setChatInfo();
        setChatMembers();
        setAddMembersButton();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatMembersViewModel();
            }
        }).get(ChatMembersViewModel.class);

        adapter = new ChatMembersAdapter();
        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        recyclerView.setAdapter(adapter);

        viewModel.getChatMembers().observe(getViewLifecycleOwner(), adapter::submitList);

        List<ChatMemberDTO> members = chat.getChatInfo().getMembers();
        viewModel.setChatMembers(members);

        return activityView;
    }

    private void setAddMembersButton() {
        View addMembers = activityView.findViewById(R.id.add_members);
        addMembers.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, EditChatMembersFragment.newInstance(userId, chat.getChatInfo().getMembers()))
                    .addToBackStack(null)
                    .commit();
        });

        if (!chat.getChatInfo().getIsGroup()) addMembers.setVisibility(GONE);
    }

    private void setChatMembers() {
        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setClickable(false);
    }

    private void setChatInfo() {
        ImageView imageView = activityView.findViewById(R.id.avatarImageView);
        TextView chatname = activityView.findViewById(R.id.chatnameTextView);

        if (chat.getImage() != null) {
            Bitmap image = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            imageView.setImageBitmap(image);
            imageView.setPadding(0,0,0, 0);
        }

        chatname.setText(chat.getName());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

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
