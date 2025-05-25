package com.example.datingappclient.recyclerViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.fragments.FormsFragment;
import com.example.datingappclient.model.ProfileCardData;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ProfileCardAdapter extends RecyclerView.Adapter<ProfileCardAdapter.ViewHolder> {

    @Getter
    private final List<ProfileCardData> profiles;
    private final BubblesRepository bubblesRepository;
    private final ImageRepository imageRepository;

    public ProfileCardAdapter(List<ProfileCardData> profiles, Context context) {
        this.profiles = profiles;
        setHasStableIds(true);
        bubblesRepository = new BubblesRepository(context);
        imageRepository = new ImageRepository(context);
    }

    @Override
    public long getItemId(int position) {
        return profiles.get(position).getUserId(); // уникальный ID
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileCardData cardData = profiles.get(position);
        getUserInterests(cardData.getUserId(), holder::updateInterestsOnly);
        getUserImages(cardData.getUserId(), holder::updateImagesOnly);
        holder.bind(profiles.get(position));
        holder.setProfileImage(new UserImageAdapter(profiles.get(position).getImages()));
    }


    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void setProfiles(List<ProfileCardData> newProfiles) {
        // Сначала считаем разницу
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ProfileDiffCallback(this.profiles, newProfiles)
        );

        // Потом применяем разницу к адаптеру
        diffResult.dispatchUpdatesTo(this);

        // И только после этого обновляем список
        this.profiles.clear();
        this.profiles.addAll(newProfiles);
    }

    public void updateImages(int userId, List<UserImage> images) {
        for (int i = 0; i < profiles.size(); i++) {
            ProfileCardData profile = profiles.get(i);
            if (profile.getUserId() == userId) {
                profile.setImages(images);

                int index = findIndexByUserId(userId);
                if (index != -1) notifyItemChanged(index, "images_only");
                break;
            }
        }
    }

    private int findIndexByUserId(int userId) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getUserId() == userId) return i;
        }
        return -1;
    }

    public void updateInterests(Integer userId, List<UserInterestDTO> interests) {
        for (int i = 0; i < profiles.size(); i++) {
            ProfileCardData profile = profiles.get(i);
            if (profile.getUserId() == userId) {
                profile.setInterests(interests);

                int index = findIndexByUserId(userId);
                if (index != -1) notifyItemChanged(index, "interests_only");

                break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 imageView;
        TextView nameAgeLabel, descLabel;

        int userId;

        int currentImageIndex = 0;
        List<UserImage> images;
        List<UserInterestDTO> interests;

        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;

            imageView = itemView.findViewById(R.id.profile_image);
            imageView.setUserInputEnabled(false);

            nameAgeLabel = itemView.findViewById(R.id.userNameAge_label);
            descLabel = itemView.findViewById(R.id.description_label);
        }

        void bind(ProfileCardData data) {
            this.images = data.getImages();
            this.currentImageIndex = 0;
            nameAgeLabel.setText(data.getName() + ", " + data.getAge());
            descLabel.setText(data.getDescription());
            userId = data.getUserId();

            // Обрабатываем переключение фоток
            setupImageViewTouchListener();

            if (images != null && !images.isEmpty()) {
                imageView.setCurrentItem(0);
            }
        }

        private void setProfileImage(UserImageAdapter userImageAdapter) {
            ViewPager2 viewPager = itemView.findViewById(R.id.profile_image);
            viewPager.setAdapter(userImageAdapter);
            TabLayout tabLayout = itemView.findViewById(R.id.image_indicator);
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                // ничего не делаем — просто точки
            }).attach();

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                layoutParams.setMargins(8, 0, 8, 0);
                tab.requestLayout();
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        void setupImageViewTouchListener() {
            imageView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                    float x = event.getX();
                    float width = v.getWidth();
                    if (x < width / 2) {
                        // назад
                        int prev = imageView.getCurrentItem() - 1;
                        if (prev >= 0) imageView.setCurrentItem(prev, true);
                    } else {
                        // вперёд
                        int next = imageView.getCurrentItem() + 1;
                        if (next < imageView.getAdapter().getItemCount()) {
                            imageView.setCurrentItem(next, true);
                        }
                        // раскоментировать для цикличного переключения фоток
                            /*else {
                                imageView.setCurrentItem(0, true);
                            }*/
                    }
                }
                return true;
            });
        }

        public void updateImagesOnly(List<UserImage> images) {
            this.images = images;
            setProfileImage(new UserImageAdapter(images));
        }

        public void updateInterestsOnly(List<UserInterestDTO> interests) {
            this.interests = interests;
            renderLimitedBubbles(interests);
        }

        private void renderLimitedBubbles(List<UserInterestDTO> interests) {
            FlexboxLayout bubbleContainer = view.findViewById(R.id.interests_container); // FlexboxLayout или LinearLayout
            bubbleContainer.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(view.getContext());

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
    }

    interface BubblesCallback  {
        void onLoaded(List<UserInterestDTO> interests);
    }

    public void getUserInterests(Integer userId, BubblesCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET USER INTERESTS";
        bubblesRepository.fetchUserInterests(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag,  "Получены интересы пользователя " + userId + ": " + result.data.size());
                    callback.onLoaded(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    callback.onLoaded(new ArrayList<>());
                    break;
                case EMPTY:
                    Log.i(logTag, "Интересы пользователя " + userId + " не найдены!");
                    callback.onLoaded(new ArrayList<>());
                    break;
            }
        });
    }

    // Колбэк ответа получения изображений
    interface ImageCallback {
        void onLoaded(List<UserImage> images);
    }
    // Метод загрузка изображений для юзера из анкеты
    private void getUserImages(int userId, ImageCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "USER IMAGES (FORMS)";
        imageRepository.fetchUserImages(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "For userId = " + userId + " - Count images: " + result.data.size());
                    callback.onLoaded(ImageUtils.objectListToUserImageList(result.data));
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

    static class ProfileDiffCallback extends DiffUtil.Callback {
        private final List<ProfileCardData> oldList;
        private final List<ProfileCardData> newList;

        public ProfileDiffCallback(List<ProfileCardData> oldList, List<ProfileCardData> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Предположим, что userId уникален
            return oldList.get(oldItemPosition).getUserId() ==
                    newList.get(newItemPosition).getUserId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // Проверяем, изменились ли данные полностью
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}