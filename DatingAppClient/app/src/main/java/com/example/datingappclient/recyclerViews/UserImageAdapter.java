package com.example.datingappclient.recyclerViews;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingappclient.R;
import com.example.datingappclient.model.UserImage;

import java.util.List;

public class UserImageAdapter extends RecyclerView.Adapter<UserImageAdapter.ViewHolder> {
    private final List<UserImage> images;

    public UserImageAdapter(List<UserImage> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap image = images.get(position).getImage();
        holder.imageView.setImageBitmap(image);
        holder.imageView.setTransitionName("profile_photo");
    }

    @Override
    public int getItemCount() {
        if (images == null) return 0;
        return images.size();
    }

    public ImageView getCurrentImageViewFromPager(ViewPager2 viewPager) {
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        int currentPosition = viewPager.getCurrentItem();

        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(currentPosition);
        if (holder instanceof UserImageAdapter.ViewHolder) {
            return ((UserImageAdapter.ViewHolder) holder).imageView;
        }
        return null;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}