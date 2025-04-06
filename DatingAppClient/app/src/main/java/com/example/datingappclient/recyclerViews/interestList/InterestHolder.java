package com.example.datingappclient.recyclerViews.interestList;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

public class InterestHolder extends RecyclerView.ViewHolder {
    TextView textInterest;

    InterestHolder(View itemView) {
        super(itemView);
        textInterest = itemView.findViewById(R.id.text_interest);
    }

    void bind(final String interest, final InterestsAdapter.OnInterestClickListener listener) {
        textInterest.setText(interest);
        itemView.setOnClickListener(v -> listener.onInterestClick(interest));
    }
}