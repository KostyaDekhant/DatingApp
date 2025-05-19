package com.example.datingappclient.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.activity.MainActivity;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.List;
import java.util.Objects;

public class ChatPropertiesFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;

    /* === Other === */
    private Integer userId;
    private ChatDTO chat;

    /* === === */
    private ChatMembersViewModel viewModel;
    private ChatMembersAdapter adapter;

    private ChatPropertiesFragment() {}

    public static ChatPropertiesFragment newInstance (Integer user, ChatDTO chat) {
        ChatPropertiesFragment chatPropertiesFragment = new ChatPropertiesFragment();
        chatPropertiesFragment.userId = user;
        chatPropertiesFragment.chat = chat;
        return chatPropertiesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_chat_properties, container, false);

        setupRepository();
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
        if (chat.getChatInfo().getIsGroup()) {
            for (ChatMemberDTO member : members) {
                if (Objects.equals(chat.getChatInfo().getCreatedBy(), member.getId())) {
                        member.setChatOwner(true);
                        break;
                }
            }
        }
        viewModel.setChatMembers(members);

        return activityView;
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void setAddMembersButton() {
        View addMembers = activityView.findViewById(R.id.add_members);
        addMembers.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, EditChatMembersFragment.newInstance(userId, chat.getId(), chat.getChatInfo().getMembers()))
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

        // установить иконку троеточия
        Drawable overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_vert);
        if (overflowIcon != null) toolbar.setOverflowIcon(overflowIcon);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());

        // Новый способ добавления меню
        activity.addMenuProvider(new MenuProvider() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.group_toolbar_menu, menu);
                if (menu instanceof MenuBuilder) {
                    ((MenuBuilder) menu).setOptionalIconsVisible(true);

                    MenuItem menuItem = menu.findItem(R.id.action_delete_chat);
                    boolean userIsCreator = Objects.equals(userId, chat.getChatInfo().getCreatedBy());
                    menuItem.setVisible(userIsCreator);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_edit) {
                    // TODO: handle edit action
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, EditChatFragment.newInstance(userId))
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_delete_chat) {
                    showDeleteGroupDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void showDeleteGroupDialog() {
        Bitmap bitmap = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage())); // твой Bitmap
        Drawable drawable = null;
        if (bitmap != null)
            drawable = new BitmapDrawable(getResources(), bitmap);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setIcon(drawable)
                .setTitle(chat.getName())
                .setMessage("Вы уверенны, что хотите удалить чат для всех участников?")
                .setNegativeButton("Отмена", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("Удалить чат", ((dialogInterface, i) -> deleteChat()))
                .show();

        // Кастомизация цвета
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = requireContext().getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(colorPrimary);
    }

    private void deleteChat() {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE CHAT";
        chatsRepository.deleteChat(chat.getId(), userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Success");
                    Toast.makeText(requireContext(), "Чат успешно удален!", Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("chatIdToRemove", chat.getId());
                    requireActivity().setResult(RESULT_OK, resultIntent);
                    requireActivity().finish();
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

}
