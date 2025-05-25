package com.example.datingappclient.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.UserInterestDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

public class InterestSelectionDialogFragment extends DialogFragment {

    private final Map<CategoryDTO, List<UserInterestDTO>> categoryInterestMap;
    private final Set<UserInterestDTO> selectedInterests = new HashSet<>();
    @Setter
    private InterestSelectionCallback callback;

    public interface InterestSelectionCallback {
        void onSelectionConfirmed(Set<UserInterestDTO> selected);
    }

    public InterestSelectionDialogFragment(Map<CategoryDTO, List<UserInterestDTO>> interestMap) {
        this.categoryInterestMap = interestMap;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_interest_selection, null);
        LinearLayout container = dialogView.findViewById(R.id.category_container);

        for (Map.Entry<CategoryDTO, List<UserInterestDTO>> entry : categoryInterestMap.entrySet()) {
            CategoryDTO category = entry.getKey();
            List<UserInterestDTO> interests = entry.getValue();

            TextView header = new TextView(requireContext());
            header.setText(category.getName());
            header.setTextSize(18);
            header.setPadding(0, 16, 0, 8);
            container.addView(header);

            LinearLayout interestLayout = new LinearLayout(requireContext());
            interestLayout.setOrientation(LinearLayout.VERTICAL);
            interestLayout.setVisibility(View.VISIBLE);

            for (UserInterestDTO interest : interests) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(interest.getName());
                checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                    if (isChecked) selectedInterests.add(interest);
                    else selectedInterests.remove(interest);
                });
                interestLayout.addView(checkBox);
            }

            container.addView(interestLayout);
        }

        builder.setView(dialogView)
                .setTitle("Выбор интересов")
                .setPositiveButton("Выбрать", (dialog, id) -> {
                    if (callback != null) callback.onSelectionConfirmed(selectedInterests);
                })
                .setNegativeButton("Отмена", (dialog, id) -> dismiss());

        return builder.create();
    }
}

