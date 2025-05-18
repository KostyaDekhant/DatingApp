package com.example.datingappclient.fragments;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;

import java.util.List;

public class EditChatMembersFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;

    /* === Other === */
    private ChatMembersViewModel viewModel;
    private ChatMembersAdapter adapter;

    private Integer userId;
    private List<ChatMemberDTO> chatMembers;

    private EditChatMembersFragment() {};

    public static EditChatMembersFragment newInstance(Integer userId, List<ChatMemberDTO> chatMembers) {
        EditChatMembersFragment fragment = new EditChatMembersFragment();
        fragment.userId = userId;
        fragment.chatMembers = chatMembers;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_add_members_to_new_chat, container, false);
        setupRepository();
        setupToolbar();
        setupMembersRecyclerView();
        animUnderlineEdit();
        setupFilter();

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

        adapter = new ChatMembersAdapter();
        RecyclerView recyclerView = activityView.findViewById(R.id.usersRecyclerView);
        recyclerView.setAdapter(adapter);

        viewModel.getChatMembers().observe(getViewLifecycleOwner(), adapter::submitList);

        for (ChatMemberDTO member : chatMembers) member.setAlreadyInChat(true);
        viewModel.setChatMembers(chatMembers);

        adapter.setFullList(chatMembers);
        getChatMembers();
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void getChatMembers() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHAT MEMBERS";
        chatsRepository.fetchChatMembers(userId, 0, result -> {
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
        toolbar.setNavigationOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
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
