package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.CompanyInfoDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.recyclerViews.UserImageAdapter;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {


    /* === Repository === */
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    /* === DTO Models === */
    private UserDTO user;

    /* === Android Objects === */
    private ViewPager2 profileImage;
    private View activityView;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;

    private ProfileFragment() {}

    public static ProfileFragment getInstance(UserDTO user) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = user;
        return fragment;
    }

    public static ProfileFragment getInstance(int userId) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = new UserDTO(userId);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();

        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move);

        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_profile, container, false);

        setupRepository();

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                ViewPager2 viewPager = activityView.findViewById(R.id.profile_image);
                View currentView =  adapter.getCurrentImageViewFromPager(viewPager);

                if (currentView != null) {
                    sharedElements.put("profile_photo", currentView);
                }
            }
        });

        frameLayout = activityView.findViewById(R.id.mainContent);
        progressBar = activityView.findViewById(R.id.progressBar);

        frameLayout.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        profileImage = activityView.findViewById(R.id.profile_image);

        setupToolbar();
        setupScrollView();

        getUserInfo(user.getId());
        getUserImages(user.getId());
        getUserCompanyInfo(user.getId());
        getUserBubbles();


        return activityView;
    }

    private void setupScrollView() {
        NestedScrollView bottomSheet = activityView.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<NestedScrollView> behavior = BottomSheetBehavior.from(bottomSheet);

        // Высота "свернутого" состояния в dp → px
        int peekHeightInDp = 175;
        float density = requireContext().getResources().getDisplayMetrics().density;
        int peekHeightPx = (int) (peekHeightInDp * density);

        // Опции:
        behavior.setPeekHeight(peekHeightPx); // Высота в свернутом виде
        behavior.setHideable(false); // Нельзя полностью спрятать

        TextView description = activityView.findViewById(R.id.description_label);
        View gradient = activityView.findViewById(R.id.gradientOverlay);


        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    Log.d("PROFILE", "Раскрыт" +
                            ((TextView) activityView.findViewById(R.id.role_value)).getText());
                    // раскрываем описание
                    description.setMaxLines(Integer.MAX_VALUE);
                    description.setEllipsize(null);

                    // Отобразить все интересы
                    if (interests != null) {
                        renderAllBubbles(interests);
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Log.d("PROFILE", "Свернут");
                    description.setMaxLines(2);
                    description.setEllipsize(TextUtils.TruncateAt.END);

                    // Отобразить все интересы
                    if (interests != null) {
                        renderLimitedBubbles(interests);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = activityView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void setupRepository() {
        userRepository = new UserRepository(requireContext());
        imageRepository = new ImageRepository(requireContext());
    }

    private void getUserInfo(int userID){
        String logTag = Constants.GLOBAL_LOG_TAG + "PROFILE INFO";
        userRepository.fetchUserInfo(userID, result -> {
            switch (result.status) {
                case SUCCESS:
                    user.copyFrom(result.data);
                    Log.i(logTag, user.toString());
                    setUserinfo();

                    frameLayout.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        }) ;
    }

    private void getUserCompanyInfo(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "PROFILE COMPANY INFO";
        userRepository.fetchUserCompanyInfo(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    user.setCompanyInfo(result.data);
                    setCompanyInfoUI();
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

    private void setCompanyInfoUI() {
        CompanyInfoDTO company = user.getCompanyInfo();
        if (company == null) return;

        ((TextView) activityView.findViewById(R.id.role_value)).setText("Должность: " + company.getRole());
        ((TextView) activityView.findViewById(R.id.company_value)).setText("Компания: " + company.getCompanyName());
        ((TextView) activityView.findViewById(R.id.department_value)).setText("Отдел: " + company.getDepartment());
        ((TextView) activityView.findViewById(R.id.office_value)).setText("Офис:" + company.getOffice());
    }

    private void getUserImages(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "PROFILE IMAGES";
        // Загружаем изображения
        imageRepository.fetchUserImages(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    // сетап изображений
                    user.setListImages(ImageUtils.objectListToUserImageList(result.data));
                    Log.i(logTag, "Для пользователя " + userId + " - Count images: " + user.getListImagesSize());

                    // если изображений нет, то выводим дефолтное (возвращается с сервера)
                    // TODO: изображение по умолчанию можно хранить на клиенте, чтобы не гонять туда-сюда
                    if (!user.getImages().isEmpty()) {
                        setProfileImage();
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

    private List<UserInterestDTO> interests;
    private void getUserBubbles() {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER BUBBLES";

        BubblesRepository repository = new BubblesRepository(requireContext());
        repository.fetchUserInterests(user.getId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    this.interests = result.data;
                    renderLimitedBubbles(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Нет интересов у пользователя");
                    break;
            }
        });
    }

    private void renderLimitedBubbles(List<UserInterestDTO> interests) {
        FlexboxLayout bubbleContainer = activityView.findViewById(R.id.interests_container); // FlexboxLayout или LinearLayout
        bubbleContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        int maxVisible = 4; // можно 3, в зависимости от дизайна
        int count = 0;

        for (UserInterestDTO interest : interests) {
            if (count >= maxVisible) break;

            View chip = inflater.inflate(R.layout.item_profile_interest_bubble, bubbleContainer, false);
            ((TextView) chip.findViewById(R.id.text_interest)).setText(interest.getName());
            bubbleContainer.addView(chip);

            count++;
        }

        int hiddenCount = interests.size() - maxVisible;
        if (hiddenCount > 0) {
            View chip = inflater.inflate(R.layout.item_profile_interest_bubble, bubbleContainer, false);
            ((TextView) chip.findViewById(R.id.text_interest)).setText("+" + hiddenCount);
            bubbleContainer.addView(chip);
        }
    }

    private void renderAllBubbles(List<UserInterestDTO> interests) {
        FlexboxLayout bubbleContainer = activityView.findViewById(R.id.interests_container);
        bubbleContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (UserInterestDTO interest : interests) {
            View chip = inflater.inflate(R.layout.item_profile_interest_bubble, bubbleContainer, false);
            ((TextView) chip.findViewById(R.id.text_interest)).setText(interest.getName());
            bubbleContainer.addView(chip);
        }
    }

    private void renderAllBubblesGrouped(List<UserInterestDTO> interests) {
        /*FlexboxLayout bubbleContainer = activityView.findViewById(R.id.interests_container);
        bubbleContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        Map<String, List<UserInterestDTO>> grouped = new LinkedHashMap<>();
        for (UserInterestDTO interest : interests) {
            String category = interest.getCategory(); // если null, то можно default
            if (!grouped.containsKey(category)) grouped.put(category, new ArrayList<>());
            grouped.get(category).add(interest);
        }

        for (Map.Entry<String, List<UserInterestDTO>> entry : grouped.entrySet()) {
            // Заголовок категории
            TextView header = new TextView(requireContext());
            header.setText(entry.getKey());
            header.setTextSize(14f);
            header.setTextColor(getResources().getColor(R.color.white)); // или attr
            header.setTypeface(null, Typeface.BOLD);

            bubbleContainer.addView(header);

            // FlexboxLayout для категории
            FlexboxLayout inner = new FlexboxLayout(requireContext());
            inner.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            inner.setFlexWrap(FlexWrap.WRAP);
            inner.setJustifyContent(JustifyContent.FLEX_START);

            for (UserInterestDTO interest : entry.getValue()) {
                View chip = inflater.inflate(R.layout.item_profile_interest_bubble, inner, false);
                ((TextView) chip.findViewById(R.id.text_interest)).setText(interest.getName());
                inner.addView(chip);
            }

            bubbleContainer.addView(inner);
        }*/
    }

    @SuppressLint("SetTextI18n")
    private void setUserinfo() {
        TextView descLabel = activityView.findViewById(R.id.description_label), nameLabel = activityView.findViewById(R.id.username_label), ageLabel = activityView.findViewById(R.id.age_label);
        nameLabel.setText(user.getName() + ",");
        descLabel.setText(user.getDescription());
        ageLabel.setText("" + user.getAge());
    }

    private UserImageAdapter adapter;
    private void setProfileImage() {
        adapter = new UserImageAdapter(user.getImages());
        profileImage.setAdapter(adapter);

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


        profileImage.post(() -> {
            ImageView current = adapter.getCurrentImageViewFromPager(profileImage);
            if (current != null) {
                startPostponedEnterTransition();
            }
        });

    }
}
