package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EditChatMembersFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private View content;
    private ProgressBar progressBar ;
    private EditText memberName;

    /* === Other === */
    private ChatMembersViewModel viewModel;
    private ChatMembersAdapter adapter;

    private Integer userId, chatId;
    private List<ChatMemberDTO> chatMembers;

    private EditChatMembersFragment() {}

    public static EditChatMembersFragment newInstance(Integer userId,Integer chatId, List<ChatMemberDTO> chatMembers) {
        EditChatMembersFragment fragment = new EditChatMembersFragment();
        fragment.userId = userId;
        fragment.chatId = chatId;
        fragment.chatMembers = chatMembers;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_add_members_to_new_chat, container, false);

        memberName = activityView.findViewById(R.id.searchEditText);
        content = activityView.findViewById(R.id.mainContent);
        progressBar = activityView.findViewById(R.id.progressBar);

        setupRepository();
        setupToolbar();
        setupMembersRecyclerView();
        animUnderlineEdit();
        setupFilter();
        setupAddMembersButton();

        return activityView;
    }

    private void setupFilter() {
        EditText editText = activityView.findViewById(R.id.searchEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatMembersViewModel();
            }
        }).get(ChatMembersViewModel.class);

        adapter = new ChatMembersAdapter(requireContext(), userId, chatId);
        adapter.setEditMembers(true);

        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        recyclerView.setAdapter(adapter);

        viewModel.getChatMembers().observe(getViewLifecycleOwner(), adapter::submitList);

        for (ChatMemberDTO member : chatMembers) member.setAlreadyInChat(true);
        viewModel.setChatMembers(chatMembers);

        adapter.setFullList(chatMembers);
        getChatMembers();
    }

    private void setupAddMembersButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.fabNext);
        fabCreateChat.setOnClickListener((view -> {
            // Были ли выбраны пользователи
            if (adapter.getSelectedUserIds().isEmpty()) {
                Toast.makeText(requireContext(), "Пользователи не выбраны!", Toast.LENGTH_LONG).show();
                return;
            }

            // Скрыть клавиатуру
            memberName.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }

            content.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);

            Single.fromCallable(() -> {
                        addMembers();
                        return true;
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

        }));
    }

    private void addMembers() {
        String logTag = Constants.GLOBAL_LOG_TAG + "ADD MEMBERS TO CHAT";
        chatsRepository.addMembersToChat(chatId, adapter.getSelectedUserIds(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "В чат " + chatId + "добавлены пользователи " + adapter.getSelectedUserIds());
                    requireActivity().finish();
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка добавления пользователей!", Toast.LENGTH_LONG).show();
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void getChatMembers() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHAT MEMBERS";
        chatsRepository.fetchPossibleChatMembers(userId, chatId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено " + result.data.size() + " участников чата!");
                    viewModel.addChatMembers(result.data);
                    adapter.setFullList(viewModel.getChatMembers().getValue());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.d(logTag, "Учатники чата не найдены!");
                    break;
            }
        });
    }

    private void setupMembersRecyclerView() {
        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void animUnderlineEdit() {
        EditText editText = activityView.findViewById(R.id.searchEditText);
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
