package com.example.datingappclient.utils;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.chip.Chip;

public final class BubbleUtils {
    private BubbleUtils() {}

    @NonNull
    public static Chip getBubble(View view, String interest, ContextThemeWrapper wrapper) {
        Chip chip = new Chip(wrapper, null, com.google.android.material.R.attr.chipStyle);
        chip.setText(interest);

        // Поведение
        chip.setOnClickListener(v -> {
            Toast.makeText(view.getContext(), "Открыть: " + interest, Toast.LENGTH_SHORT).show();
        });

        // Layout
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 4, 0, 4);
        lp.setMinHeight(0);
        chip.setLayoutParams(lp);
        return chip;
    }

    @NonNull
    public static FlexboxLayout getBubblesFlexbox(View view, int labelId) {
        FlexboxLayout flexbox = new FlexboxLayout(view.getContext());
        flexbox.setId(View.generateViewId());

        flexbox.setFlexWrap(FlexWrap.WRAP);
        flexbox.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        flexbox.setClipChildren(false);
        flexbox.setClipToPadding(false);

        ConstraintLayout.LayoutParams flexParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        flexParams.topToBottom = labelId;
        flexParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        flexParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        flexParams.topMargin = 4;
        flexbox.setLayoutParams(flexParams);

        return flexbox;
    }
}
