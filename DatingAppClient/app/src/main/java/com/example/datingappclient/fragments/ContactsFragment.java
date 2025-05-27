package com.example.datingappclient.fragments;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;

public class ContactsFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android objects === */
    private View activityView;
    private RecyclerView recyclerView;

    /* === Other === */
    private int userId;

    private ChatMembersAdapter membersAdapter;

    private ContactsFragment() {}

    public static ContactsFragment newInstance(int userId) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.userId = userId;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = activityView.findViewById(R.id.usersRecyclerView);

        setupRepository();
        setupToolbar();
        setupRecyclerView();
        setupFilter();

        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();
        ChatMembersViewModel contacts = app.getContactsViewModel();
        membersAdapter = new ChatMembersAdapter(requireContext(), userId, 0);
        membersAdapter.setEditMembers(false);

        if (contacts != null) {
            membersAdapter.setFullList(contacts.getChatMembers().getValue());
            recyclerView.setAdapter(membersAdapter);

            contacts.getChatMembers().observe(getViewLifecycleOwner(), membersAdapter::submitList);
        }

        return activityView;
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        // Кнопка возврата
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void setupFilter() {
        EditText editText = activityView.findViewById(R.id.searchEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                membersAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
