package com.example.datingappclient.fragments;

import static android.view.View.GONE;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.datingappclient.R;
import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.List;

public class EditChatFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView avatarImageView;
    private View activityView;

    /* === Other === */
    private List<ChatMemberDTO> chatMembers;
    private Integer userId;
    private Bitmap selectedAvatarBitmap;

    private EditChatFragment() {};

    public static EditChatFragment newInstance(Integer userId) {
        EditChatFragment fragment = new EditChatFragment();
        fragment.userId = userId;
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
        setupImagePicker();
        setupAddImageButton();

        return activityView;
    }

    private void setupImagePicker() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET IMAGE FOR CHAT";
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                            selectedAvatarBitmap = BitmapFactory.decodeStream(inputStream);
                            avatarImageView.setImageBitmap(ImageUtils.getCroppedBitmap(selectedAvatarBitmap));
                            avatarImageView.setPadding(0,0,0,0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(logTag, e.getMessage());
                            Toast.makeText(requireContext(), "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    private void setupAddImageButton() {
        avatarImageView = activityView.findViewById(R.id.avatarImageView);
        avatarImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setVisibility(GONE);
    }

    private ChatDTO getChatDTO() {
        // get chat name
        EditText chatnameInput = activityView.findViewById(R.id.chatNameEditText);
        String chatname = chatnameInput.getText().toString();
        // get chat image
        byte[] chatImage = ImageUtils.convertBitmapToPrimitiveBytes(selectedAvatarBitmap);

        // add user to chat members
        chatMembers.add(new ChatMemberDTO(null, userId, null, false, true));

        // create group chat
        ChatInfoDTO groupChatInfo = new ChatInfoDTO(userId, null, chatMembers, true);
        return new ChatDTO(null, chatname, null, chatImage, groupChatInfo);
    }

    private void createGroupChat(ChatDTO groupChat) {
        String logTag = Constants.GLOBAL_LOG_TAG + "CREATE GROUP CHAT";
        /*chatsRepository.createChat(groupChat, result -> {
            switch (result.status) {
                case SUCCESS:
                    groupChat.setId(result.data);
                    Toast.makeText(requireContext(),"Susscess", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });*/
    }

    private void animUnderlineEdit() {
        EditText editText = activityView.findViewById(R.id.chatNameEditText);
        View underline = activityView.findViewById(R.id.underlineView);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            int colorFrom = ((ColorDrawable) underline.getBackground()).getColor();
            int colorTo = hasFocus
                    ? ContextCompat.getColor(requireContext(), R.color.green) // TODO: подогнать под темыы
                    : ContextCompat.getColor(requireContext(), R.color.darkgray_transparent);

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
