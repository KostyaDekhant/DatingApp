package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;

public class CreateGroupChatFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView avatarImageView;
    private View activityView;
    private EditText chatnameInput;
    private ProgressBar progressBar;
    private View content;

    /* === Other === */
    private List<ChatMemberDTO> chatMembers;
    private UserDTO user;
    private Bitmap selectedAvatarBitmap;

    private ChatsViewModel chatsViewModel;

    private final Disposable disposableCreate = null;


    private CreateGroupChatFragment() {}

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

        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();
        chatsViewModel = app.getChatsViewModel();

        chatnameInput = activityView.findViewById(R.id.chatNameEditText);
        progressBar = activityView.findViewById(R.id.progressBar);
        content = activityView.findViewById(R.id.mainContent);

        setupRepository();
        setupToolbar();
        animUnderlineEdit();
        setupCreateChatButton();
        setupImagePicker();
        setupAddImageButton();

        return activityView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposableCreate != null) disposableCreate.dispose();
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
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {
            if (chatnameInput.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Имя чата не задано!", Toast.LENGTH_LONG).show();
                return;
            }

            fabCreateChat.setVisibility(GONE);
            chatnameInput.clearFocus();

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }

            content.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);

            createGroupChat(getChatDTO());
        });
    }

    private ChatDTO getChatDTO() {
        // get chat name
        String chatname = chatnameInput.getText().toString();
        // get chat image
        //byte[] chatImage = ImageUtils.convertBitmapToPrimitiveBytes(selectedAvatarBitmap);

        // add user to chat members
        chatMembers.add(new ChatMemberDTO(user.getName(), user.getId(), null, false, true));

        // create group chat
        ChatInfoDTO groupChatInfo = new ChatInfoDTO(user.getId(), null, chatMembers, true);
        return new ChatDTO(null, chatname, null,null, null, groupChatInfo, null);
    }

    private void createGroupChat(ChatDTO chat) {
        // Подписка на создание чата
        /*AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        disposableCreate = chatsViewModel.subscribeToCreateChat(user.getId(), result1 -> {
            Toast.makeText(requireContext(), "Чат успешно создан!", Toast.LENGTH_LONG).show();

            chatsRepository.fetchChatInfo(result1.data.getId(), result -> {
                result1.data.setChatInfo(result.data);
                chatsViewModel.updateOrAddChat(result1.data);
                goToChatActivity(result1.data);
            });

            Disposable d = disposableRef.get();
            if (d != null && !d.isDisposed()) {
                d.dispose();
            }
        }); // сразу отписываемся*/

        // Создание чата
        String logTag = Constants.GLOBAL_LOG_TAG + "CREATE CHAT";
        chatsRepository.createChat(chat, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Чат успешно создан. chatId=" + result.data);
                    Toast.makeText(requireContext(), "Чат создан!", Toast.LENGTH_LONG).show();
                    // set id
                    chat.setId(result.data);
                    // update chat image
                    updateChatImage(chat.getId());
                    byte[] chatImage = ImageUtils.convertBitmapToPrimitiveBytes(selectedAvatarBitmap);
                    chat.setImage(chatImage);
                    // go to chat
                    goToChatActivity(chat);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    Toast.makeText(requireContext(), "Ошибка при создании чата!", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void updateChatImage(int chatId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE CHAT IMAGE";
        byte[] chatImage = ImageUtils.convertBitmapToPrimitiveBytes(selectedAvatarBitmap);
        ChatPayloadInfo updateChatImage = new ChatPayloadInfo(chatId, user.getId(), null, chatImage);
        chatsRepository.updateChat(updateChatImage, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag,"Изображение чата успешно обновлено после создания!" + chatId);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    Toast.makeText(requireContext(), "Не удалось установить изображение чата!", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    public final ActivityResultLauncher<Intent> chatLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    int removedChatId = data.getIntExtra("chatIdToRemove", -1);
                    if (removedChatId != -1) {
                        /*ChatsViewModel chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);
                        chatsViewModel.deleteChat(removedChatId);*/
                    }
                }
            });

    private void goToChatActivity(ChatDTO chat) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        ChatDTO.selectedChat = chat;

        chatLauncher.launch(intent);

        requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ChatListFragment.newInstance(user))
                .commit();
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
