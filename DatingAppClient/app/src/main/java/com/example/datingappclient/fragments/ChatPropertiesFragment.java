package com.example.datingappclient.fragments;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;

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

    private ChatsViewModel chatsViewModel;

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

        chatsViewModel = ((DatingAppApplication)requireActivity().getApplication()).getChatsViewModel();

        setupRepository();
        setupToolbar();
        setChatName();
        setChatImage();
        setChatMembers();
        setAddMembersButton();

        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();
        viewModel = app.getChatMembersViewModel();

        adapter = new ChatMembersAdapter(requireContext(), userId,  chat.getId());
        adapter.setUserIsOwner(userId.equals(chat.getChatInfo().getCreatedBy()));

        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        recyclerView.setAdapter(adapter);

        viewModel.getChatMembers().observe(getViewLifecycleOwner(), list -> adapter.submitList(list));

        subscribeToUpdateChat();

        setChatOwner();

        return activityView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatsViewModel.unsubscribeUpdateChat(chat.getId());
    }

    private void subscribeToUpdateChat() {
        chatsViewModel.subscribeToUpdateChat(chat.getId(), result -> {
            Log.d(Constants.GLOBAL_LOG_TAG + "UPDATE CHAT", "ChatPropertiesFragment");
            AtomicReference<Disposable> disposableRef = new AtomicReference<>();
            chatsRepository.fetchChat(chat.getId(), userId, chatResult -> {
                if (chatResult.status != Result.Status.SUCCESS || chatResult.data == null) return;

                chat.setName(chatResult.data.getName());
                setChatName();

                chatsRepository.fetchChatAvatar(userId, chat.getId(), avatarResult -> {
                    if (result.status == Result.Status.SUCCESS && avatarResult.data != null) {
                        chat.setImage(avatarResult.data.getImage());
                        setChatImage();
                    }
                    // Отписываемся от обновления, если нужно
                    Disposable d = disposableRef.get();
                    if (d != null && !d.isDisposed()) {
                        d.dispose();
                    }
                });
            });
        });
    }

    private void setChatOwner() {
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
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void setAddMembersButton() {
        View addMembers = activityView.findViewById(R.id.add_members);
        addMembers.setOnClickListener(view -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EditChatMembersFragment.newInstance(userId, chat.getId(), chat.getChatInfo().getMembers()))
                .addToBackStack(null)
                .commit());

        boolean userIsOwner = userId.equals(chat.getChatInfo().getCreatedBy());
        if (!chat.getChatInfo().getIsGroup() || !userIsOwner) addMembers.setVisibility(GONE);
    }

    private void setChatMembers() {
        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setChatImage() {
        ImageView imageView = activityView.findViewById(R.id.avatarImageView);

        if (chat.getImage() != null) {
            Bitmap image = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            imageView.setImageBitmap(image);
            imageView.setPadding(0,0,0, 0);
        }
    }

    private void setChatName() {
        TextView chatname = activityView.findViewById(R.id.chatnameTextView);
        chatname.setText(chat.getName());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);

        // установить иконку троеточия
        Drawable overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_vert);
        if (overflowIcon != null) toolbar.setOverflowIcon(overflowIcon);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
//        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());

        //setup menu
        activity.addMenuProvider(new MenuProvider() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.group_toolbar_menu, menu);
                if (menu instanceof MenuBuilder) {
                    ((MenuBuilder) menu).setOptionalIconsVisible(true);

                    MenuItem menuItem = menu.findItem(R.id.action_delete_chat),
                            menuItemEdit = menu.findItem(R.id.action_edit_chat),
                            menuItemLeaveChat = menu.findItem(R.id.action_exit_chat);

                    boolean userIsCreator = Objects.equals(userId, chat.getChatInfo().getCreatedBy());
                    menuItem.setVisible(userIsCreator);
                    menuItemEdit.setVisible(userIsCreator);
                    menuItemLeaveChat.setVisible(!userIsCreator);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_edit_chat) {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, EditChatFragment.newInstance(userId, chat))
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_delete_chat) {
                    showDeleteGroupDialog();
                    return true;
                }
                else if (menuItem.getItemId() == R.id.action_exit_chat) {
                    showLeaveChatDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void showLeaveChatDialog() {
        Bitmap bitmap = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage())); // твой Bitmap
        Drawable drawable = null;
        if (bitmap != null)
            drawable = new BitmapDrawable(getResources(), bitmap);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setIcon(drawable)
                .setTitle(chat.getName())
                .setMessage("Вы уверенны, что хотите покинуть чат?")
                .setNegativeButton("Отмена", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("Покинуть чат", ((dialogInterface, i) -> leaveChat()))
                .show();

        // Кастомизация цвета
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = requireContext().getTheme();
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(colorPrimary);
    }

    private void leaveChat() {
        String logTag = Constants.GLOBAL_LOG_TAG + "LEAVE CHAT";
        requireActivity().finish();
        chatsViewModel.clearUpdateSubscribes();
        chatsRepository.removeMemberFromChat(userId,  chat.getId(),chat.getChatInfo().getCreatedBy(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Пользователь " + userId + " покинул чат " + chat.getId());
                    Toast.makeText(requireContext(), "Чат успешно покинут!", Toast.LENGTH_LONG).show();
                    chatsViewModel.deleteChat(chat.getId());
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка выхода из чата!", Toast.LENGTH_LONG).show();
                    Log.e(logTag, result.error);
                    break;
            }
        });
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
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(colorPrimary);
    }

    private void deleteChat() {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE CHAT";
        chatsViewModel.deleteChatFromServer(chat.getId(), userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Success");
                    Toast.makeText(requireContext(), "Чат успешно удален!", Toast.LENGTH_LONG).show();
                    requireActivity().finish();
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

}
