package com.example.datingappclient.fragments;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.datingappclient.R;
import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.GroupChatInfoDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.repository.GroupChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CreateGroupChatFragment extends Fragment {

    private View activityView;
    private List<ChatMemberDTO> chatMembers;
    private UserDTO user;
    private GroupChatsRepository groupChatsRepository;

    private CreateGroupChatFragment() {};

    public static CreateGroupChatFragment newInstance(UserDTO user, List<ChatMemberDTO> chatMembers) {
        CreateGroupChatFragment fragment = new CreateGroupChatFragment();
        fragment.chatMembers = chatMembers;
        fragment.user = user;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_create_groupchat, container, false);

        setupRepository();
        setupToolbar();
        animUnderlineEdit();
        setupCreateChatButton();

        return activityView;
    }

    private void setupRepository() {
        groupChatsRepository = new GroupChatsRepository(requireContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {

            createGroupChat(getGroupChatDTO());
            /*etParentFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateGroupChatFragment.newInstance(adapter.getSelectedMembers()))
                    .addToBackStack(null)
                    .commit();*/
        });
    }

    private GroupChatDTO getGroupChatDTO() {
        // get chat name
        EditText chatnameInput = activityView.findViewById(R.id.chatNameEditText);
        String chatname = chatnameInput.getText().toString();
        // get chat image
        // TODO:

        // add user to chat members
        byte[] userImage = ImageUtils.convertBitmapToPrimitiveBytes(user.getMainImage());
        chatMembers.add(new ChatMemberDTO(user.getName(), user.getId(), userImage));

        // create group chat
        GroupChatInfoDTO groupChatInfo = new GroupChatInfoDTO(user.getId(), null, chatMembers, true);
        return new GroupChatDTO(null, chatname, null, null, groupChatInfo);
    }

    private void createGroupChat(GroupChatDTO groupChat) {
        String logTag = Constants.GLOBAL_LOG_TAG + "CREATE GROUP CHAT";
        groupChatsRepository.createChat(groupChat, result -> {
            switch (result.status) {
                case SUCCESS:
                    groupChat.setGroupChatId(result.data);
                    goToChatActivity(groupChat);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void goToChatActivity(GroupChatDTO groupChat) {
        Context context = activityView.getContext();
        Intent intent = new Intent(context, ChatActivity.class)
                .putExtra("senderID", user.getId())
                .putExtra("receiverID", groupChat.getGroupChatId())
                .putExtra("username", groupChat.getName())
                .putExtra("image", groupChat.getImage());
        context.startActivity(intent);
        requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void animUnderlineEdit() {
        EditText editText = activityView.findViewById(R.id.chatNameEditText);
        View underline = activityView.findViewById(R.id.underlineView);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            int colorFrom = ((ColorDrawable) underline.getBackground()).getColor();
            int colorTo = hasFocus
                    ? ContextCompat.getColor(requireContext(), R.color.green) // TODO: подогнать под темыы
                    : ContextCompat.getColor(requireContext(), R.color.gray);

            // Анимация цвета
            ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnim.setDuration(200);
            colorAnim.addUpdateListener(anim -> underline.setBackgroundColor((int) anim.getAnimatedValue()));

            // Анимация смещения по Y (вверх при фокусе, вниз при потере)
            float fromY = underline.getTranslationY();
            float toY = hasFocus ? -10f : 0f; // поднимаем на 6dp (примерно)

            ObjectAnimator moveAnim = ObjectAnimator.ofFloat(underline, "translationY", fromY, toY);
            moveAnim.setDuration(200);

            // Одновременный запуск
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(colorAnim, moveAnim);
            animatorSet.start();
        });
    }
}
