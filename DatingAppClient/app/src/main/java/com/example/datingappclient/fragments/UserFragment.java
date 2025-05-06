package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.recyclerViews.UserImageAdapter;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static UserFragment instance;

    /* === Repository === */
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    /* === DTO Models === */
    private static UserDTO user;

    /* === Android Objects === */
    private ViewPager2 profileImage;
    private View activityView;

    /* === Other === */
    private static boolean isLogin;

    /* === Methods === */
    @Override
    public void onClick(View view) {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new UsereditFragment(user)).commit();
    }

    private UserFragment(UserDTO user, boolean isLogin) {
        this.user = user;
        this.isLogin = isLogin;

        userRepository = new UserRepository();
        imageRepository = new ImageRepository();
    }

    public static UserFragment getInstance(UserDTO user, boolean isLogin) {
        if (instance == null) {
            instance = new UserFragment(user, isLogin);
        }
        else {
            UserFragment.user = user;
            UserFragment.isLogin = isLogin;
        }
        return instance;
    }

    public static UserFragment getInstance() {
        UserFragment.isLogin = false;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_user, container, false);
        ImageButton button = activityView.findViewById(R.id.edit_button);
        button.setOnClickListener(this);

        profileImage = activityView.findViewById(R.id.profile_image);

        // если только после авторизации, то запрашиваем инфу о пользователе
        if (isLogin) {
            getUserInfo(user.getId());
            getUserImages(user.getId());
        }
        // Нужно для того, что бы при переходе с другой вкладки проставлялась инфа и изображении
        else {
            setUserinfo();
            setProfileImage();
        }

        return activityView;
    }

    private void getUserInfo(int userID){
        String logTag = Constants.GLOBAL_LOG_TAG + "USER_INFO";
        userRepository.fetchUserInfo(userID, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(UserDTO fetchedUser) {
                // Сохраняем юзера в поле фрагмента
                user = fetchedUser;
                Log.i(logTag, user.toString());
                // Выводим инфу о пользователе в поля
                setUserinfo();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    private void getUserImages(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER IMAGES";
        // Загружаем изображения
        imageRepository.fetchUserImages(userId, new ImageRepository.ImagesCallback() {
            @Override
            public void onSuccess(List<Object[]> images) {
                // сетап изображений
                user.setListImages(ImageUtils.objectListToUserImageList(images));
                Log.i(logTag, "For userId = " + userId + " - Count images: " + user.getListImagesSize());

                // если изображений нет, то выводим дефолтное (возвращается с сервера)
                // TODO: изображение по умолчанию можно хранить на клиенте, чтобы не гонять туда-сюда
                if (!user.getImages().isEmpty()) {
                    setProfileImage();
                }
                isLogin = false;
            }

            @Override
            public void onEmpty(String message) {
                Log.i(logTag, message + " userId :" + userId);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    private void getUserBubbles(int userId) {

    }

    @SuppressLint("SetTextI18n")
    private void setUserinfo() {
        TextView descLabel = activityView.findViewById(R.id.description_label), nameLabel = activityView.findViewById(R.id.username_label), ageLabel = activityView.findViewById(R.id.age_label);
        nameLabel.setText(user.getName() + ",");
        descLabel.setText(user.getDescription());
        ageLabel.setText("" + user.getAge());
    }

    private void setProfileImage() {
        profileImage.setAdapter(new UserImageAdapter(user.getImages()));
        TabLayout tabLayout = activityView.findViewById(R.id.image_indicator);
        new TabLayoutMediator(tabLayout, profileImage, (tab, position) -> {
            // ничего не делаем — просто точки
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            layoutParams.setMargins(8, 0, 8, 0);
            tab.requestLayout();
        }
    }
}
