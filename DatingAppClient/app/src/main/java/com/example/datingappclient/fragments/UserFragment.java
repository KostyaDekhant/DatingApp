package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.activity.MainActivity;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.recyclerViews.UserImageAdapter;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static UserFragment instance;

    /* === Repository === */
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    /* === DTO Models === */
    private static UserDTO user;

    /* === Android Objects === */
    private ViewPager2 profileImage;
    private View activityView;
    private NavigationView navView;

    /* === Other === */
    private static boolean isLogin;

    /* === Methods === */
    @Override
    public void onClick(View view) {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, UsereditFragment.newInstance(user)).commit();
    }

    private UserFragment(UserDTO user, boolean isLogin) {
        UserFragment.user = user;
        UserFragment.isLogin = isLogin;
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

        navView = ((MainActivity) requireActivity()).findViewById(R.id.nav_view);

        setupRepository();

        profileImage = activityView.findViewById(R.id.profile_image);

        // если только после авторизации, то запрашиваем инфу о пользователе
        if (isLogin) {
            getUserInfo(user.getId());
            getUserImages(user.getId());
            getUserCompanyInfo(user.getId());
            isLogin = false;
        }
        // Нужно для того, что бы при переходе с другой вкладки проставлялась инфа и изображении
        else {
            setUserinfo();
            setProfileImage();
        }

        return activityView;
    }

    private void setupRepository() {
        userRepository = new UserRepository(requireContext());
        imageRepository = new ImageRepository(requireContext());
    }

    private void getUserInfo(int userID){
        String logTag = Constants.GLOBAL_LOG_TAG + "USER INFO";
        userRepository.fetchUserInfo(userID, result -> {
            switch (result.status) {
                case SUCCESS:
                    user.copyFrom(result.data);
                    Log.i(logTag, user.toString());
                    setUserinfo();

                    TextView headerName = navView.findViewById(R.id.nav_header_name);
                    headerName.setText(user.getName());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        }) ;
    }

    private void getUserCompanyInfo(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER COMPANY INFO";
        userRepository.fetchUserCompanyInfo(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    user.setCompanyInfo(result.data);
                    Log.i(logTag, result.data.toString());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Информация о компании не найдена для юзера id=" + userId);
                    break;
            }
        });
    }

    private void getUserImages(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER IMAGES";
        // Загружаем изображения
        imageRepository.fetchUserImages(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    // сетап изображений
                    user.setListImages(ImageUtils.objectListToUserImageList(result.data));
                    Log.i(logTag, "For userId = " + userId + " - Count images: " + user.getListImagesSize());

                    // если изображений нет, то выводим дефолтное (возвращается с сервера)
                    // TODO: изображение по умолчанию можно хранить на клиенте, чтобы не гонять туда-сюда
                    if (!user.getImages().isEmpty()) {
                        setProfileImage();

                        ImageView headerUserImage = navView.findViewById(R.id.nav_header_avatar);
                        headerUserImage.setImageBitmap(ImageUtils.getCroppedBitmap(user.getMainImage()));
                    }
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Для пользователя " + userId + " не найдено изображений!");
                    break;
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
