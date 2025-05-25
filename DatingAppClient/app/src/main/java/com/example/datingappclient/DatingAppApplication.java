package com.example.datingappclient;

import android.app.Application;

import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatingAppApplication extends Application {
    private ChatsViewModel chatsViewModel;
    private ChatMembersViewModel chatMembersViewModel;

    // Кеш интересов и категорий
    private List<CategoryDTO> cachedCategories;
    private Map<CategoryDTO, List<InterestDTO>> cachedInterestsByCategory = new HashMap<>();
}
