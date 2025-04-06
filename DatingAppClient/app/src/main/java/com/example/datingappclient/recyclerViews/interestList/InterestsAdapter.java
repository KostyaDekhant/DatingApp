package com.example.datingappclient.recyclerViews.interestList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestHolder> {
    private final List<String> interests;
    private final OnInterestClickListener listener;

    private final int VIEW_TYPE_USEREDIT = 1;
    private final int VIEW_TYPE_PROFILE = 2;

    public interface OnInterestClickListener {
        void onInterestClick(String interest);
    }

    public InterestsAdapter(List<String> interests, OnInterestClickListener listener) {
        this.interests = interests;
        this.listener = listener;
    }

    @Override
    public InterestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        if (viewType == VIEW_TYPE_USEREDIT) layout = R.layout.item_useredit_interest_bubble;
        else layout = R.layout.item_profile_interest_bubble;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new InterestHolder(view);
    }

    @Override
    public void onBindViewHolder(InterestHolder holder, int position) {
        String interest = interests.get(position);
        holder.bind(interest, listener);
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }
}