package com.example.datingappclient.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.model.dto.ChatMemberDTO;

import java.util.ArrayList;
import java.util.List;

public class ChatMembersViewModel extends ViewModel {
    private final MutableLiveData<List<ChatMemberDTO>> members = new MutableLiveData<>();

    public ChatMembersViewModel()  {}

    public LiveData<List<ChatMemberDTO>> getChatMembers() {return  members;}

    public void setChatMembers(List<ChatMemberDTO> chatMembers) {
        members.setValue(chatMembers);
    }

    public void addChatMembers(List<ChatMemberDTO> chatMembers) {
        List<ChatMemberDTO> newList = new ArrayList<>(chatMembers);
        newList.addAll(members.getValue());
        members.setValue(newList);
    }
}
