package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditChatFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;
    private ChatsViewModel chatsViewModel;

    /* === Android Objects === */
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView avatarImageView;
    private EditText chatNameInput;
    private View activityView;
    private ProgressBar progressBar;
    private View content;

    /* === Other === */
    private ChatDTO chat;
    private Integer userId;
    private Bitmap selectedAvatarBitmap;
    private boolean avatarIsChanged;

    private EditChatFragment() {};

    public static EditChatFragment newInstance(Integer userId, ChatDTO chat) {
        EditChatFragment fragment = new EditChatFragment();
        fragment.userId = userId;
        fragment.chat = chat;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_create_groupchat, container, false);

        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();
        chatsViewModel = app.getChatsViewModel();
        chatsRepository = new ChatsRepository(requireContext());


        avatarImageView = activityView.findViewById(R.id.avatarImageView);
        chatNameInput = activityView.findViewById(R.id.chatNameEditText);

        progressBar = activityView.findViewById(R.id.progressBar);
        content = activityView.findViewById(R.id.mainContent);

        setupToolbar();
        setChatInfo();
        animUnderlineEdit();
        setupCreateChatButton();
        setupImagePicker();
        setupAddImageButton();

        return activityView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatsViewModel.unsubscribeUpdateChat(chat.getId());
    }

    private void setChatInfo() {
        chatNameInput.setText(chat.getName());
        Bitmap chatImage = ImageUtils.convertPrimitiveByteToBitmap(chat.getImage());
        if (chatImage != null) {
            avatarImageView.setImageBitmap(ImageUtils.getCroppedBitmap(chatImage));
            avatarImageView.setPadding(0,0,0,0);
        }
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
                            avatarIsChanged = true;
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
        avatarImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        activity.setSupportActionBar(toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        activity.addMenuProvider(new MenuProvider() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.edit_chat_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.save_button) {

                    chatNameInput.clearFocus();

                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }

                    content.setVisibility(GONE);
                    progressBar.setVisibility(VISIBLE);

                    if (chatNameInput.getText().toString().trim().isEmpty()) {
                        content.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Имя чата не может быть пустым!", Toast.LENGTH_LONG).show();
                    } else {
                        saveEditedChat();
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void saveEditedChat() {
        chat.setName(chatNameInput.getText().toString());
        if (avatarIsChanged && selectedAvatarBitmap != null) chat.setImage(ImageUtils.convertBitmapToPrimitiveBytes(selectedAvatarBitmap));

        byte[] chatImage = avatarIsChanged ? chat.getImage() : null;
        ChatPayloadInfo payloadInfo = new ChatPayloadInfo(chat.getId(), userId, chat.getName(), chatImage);

        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        chatsViewModel.subscribeToUpdateChat(chat.getId(), result1 -> {
            Toast.makeText(requireContext(), "Чат успешно обновлен!", Toast.LENGTH_LONG).show();
            chatsRepository.fetchChat(chat.getId(), userId, result -> {
                requireActivity().finish();

                Disposable d = disposableRef.get();
                if (d != null && !d.isDisposed()) {
                    d.dispose();
                }
            });
        });

        Single.fromCallable(() -> {
                String logTag = Constants.GLOBAL_LOG_TAG + "CHAT UPDATE";
                chatsRepository.updateChat(payloadInfo, result -> {
                    switch (result.status) {
                        case SUCCESS:
                            Log.i(logTag, "Чат успешно обновлен! " + payloadInfo.getChatId());
                            break;
                        case ERROR:
                            Log.e(logTag, result.error);
                            chatsViewModel.unsubscribeUpdateChat(payloadInfo.getChatId());
                            content.setVisibility(VISIBLE);
                            progressBar.setVisibility(GONE);
                            Toast.makeText(requireContext(), "Ошибка изменения чата!", Toast.LENGTH_LONG).show();
                            break;
                    }
                });
                return true;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe();
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setVisibility(GONE);
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
