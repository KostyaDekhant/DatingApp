package com.example.datingappclient.recyclerViews;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.model.ProfileCardData;
import com.example.datingappclient.model.UserImage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ProfileCardAdapter extends RecyclerView.Adapter<ProfileCardAdapter.ViewHolder> {

    private final List<ProfileCardData> profiles;

    public ProfileCardAdapter(List<ProfileCardData> profiles) {
        this.profiles = profiles;
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
        holder.bind(profiles.get(position));
        UserImageAdapter imageAdapter = new UserImageAdapter(profiles.get(position).getImages());
        holder.setProfileImage(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 imageView;
        TextView nameAgeLabel, descLabel;

        int currentImageIndex = 0;
        List<UserImage> images;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
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

            // Обрабатываем переключение фоток
            setupImageViewTouchListener();

            if (!images.isEmpty()) {
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
    }
}