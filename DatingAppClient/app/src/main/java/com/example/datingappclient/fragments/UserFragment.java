package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.recyclerViews.UserImageAdapter;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.ImageUtils;

import java.util.List;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static UserFragment instance;

    /* === Repository === */
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    /* === DTO Models === */
    private static UserDTO user;

    /* === Android Objects === */
    //private ImageView profileImage;
    private ViewPager2 profileImage;

    /* === Other === */
    private int currentImageIndex = 0;
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ImageButton button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(this);

        profileImage = view.findViewById(R.id.profile_image);
        setupImageTouchListener();

        // если только после авторизации, то запрашиваем инфу о пользователе
        if (isLogin) {
            getUserInfo(view, user.getId());
            getUserImages(user.getId());
        }
        // Нужно для того, что бы при переходе с другой вкладки проставлялась инфа и изображении
        else {
            setUserinfo(view);
            profileImage.setAdapter(new UserImageAdapter(user.getImages()));
        }

        return view;
    }

    private void getUserInfo(View view, int userID){
        String logTag = Constants.GLOBAL_LOG_TAG + "USER_INFO";
        userRepository.fetchUserInfo(userID, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(UserDTO fetchedUser) {
                // Сохраняем юзера в поле фрагмента
                user = fetchedUser;
                Log.i(logTag, user.toString());
                // Выводим инфу о пользователе в поля
                setUserinfo(view);
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
                    profileImage.setAdapter(new UserImageAdapter(user.getImages()));
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
    private void setUserinfo(View view) {
        TextView descLabel = view.findViewById(R.id.description_label), nameLabel = view.findViewById(R.id.username_label), ageLabel = view.findViewById(R.id.age_label);
        nameLabel.setText(user.getName() + ",");
        descLabel.setText(user.getDescription());
        ageLabel.setText("" + user.getAge());
    }

    //пальцы не совать
    // TODO: переделать систему свайпов (если вообще нужна)
    private void setupImageTouchListener() {
        profileImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    int width = v.getWidth();
                    if (x < width / 2) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                return true;
            }
        });
    }

    private void onSwipeRight() {
        Log.d("UserFragment", "Clicked on the left half of the image");
        if (currentImageIndex > 0) {
            currentImageIndex--;
            updateProfileImage();
        }
    }

    private void onSwipeLeft() {
        Log.d("UserFragment", "Clicked on the right half of the image");
        if (currentImageIndex < user.getImages().size() - 1) {
            currentImageIndex++;
            updateProfileImage();
        }
    }

    private void updateProfileImage() {
        Bitmap image = user.getImages().get(currentImageIndex).getImage();
        //profileImage.setImageBitmap(image);
    }
}
