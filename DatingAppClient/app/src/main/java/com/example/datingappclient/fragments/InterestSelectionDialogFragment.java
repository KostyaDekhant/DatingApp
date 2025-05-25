package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.wrapper.Result;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

public class InterestSelectionDialogFragment extends DialogFragment {

    private BubblesRepository bubblesRepository;

    private List<CategoryDTO> categories;
    private final Map<CategoryDTO, List<InterestDTO>> categoryInterestMap = new LinkedHashMap<>();
    private final Set<InterestDTO> selectedInterests = new HashSet<>();
    private final Map<CategoryDTO, LinearLayout> interestLayouts = new LinkedHashMap<>();

    @Setter
    private InterestSelectionCallback callback;

    private View dialogView;
    private LinearLayout container;

    public interface InterestSelectionCallback {
        void onSelectionConfirmed(Set<InterestDTO> selected);
    }

    public InterestSelectionDialogFragment(Set<InterestDTO> preselected) {
        selectedInterests.addAll(preselected);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bubblesRepository = new BubblesRepository(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_interest_selection, null);
        container = dialogView.findViewById(R.id.category_container);

        setupSearchEditText();

        builder.setView(dialogView)
                .setTitle("Выбор интересов")
                .setPositiveButton("Выбрать", (dialog, id) -> {
                    if (callback != null) callback.onSelectionConfirmed(selectedInterests);
                })
                .setNegativeButton("Отмена", (dialog, id) -> dismiss());

        renderCategoryBlocks();

        return builder.create();
    }

    private void setupSearchEditText() {
        // Поиск
        EditText searchInput = dialogView.findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterInterests(s.toString().toLowerCase());
            }
        });
    }

    private void renderCategoryBlocks() {
        container.removeAllViews();
        interestLayouts.clear();

        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();

        for (Map.Entry<CategoryDTO, List<InterestDTO>> entry : app.getCachedInterestsByCategory().entrySet()) {
            CategoryDTO category = entry.getKey();
            List<InterestDTO> interests = entry.getValue();

            // Заголовок
            LinearLayout headerLayout = new LinearLayout(requireContext());
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setPadding(0, 16, 0, 8);
            headerLayout.setGravity(Gravity.CENTER_VERTICAL);

            TextView title = new TextView(requireContext());
            title.setText(category.getName());
            title.setTextSize(18);
            title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            ImageView arrow = new ImageView(requireContext());
            arrow.setImageResource(R.drawable.ic_arrow_drop_down);
            arrow.setRotation(0);

            headerLayout.addView(title);
            headerLayout.addView(arrow);

            // Layout для чекбоксов
            LinearLayout interestLayout = new LinearLayout(requireContext());
            interestLayout.setOrientation(LinearLayout.VERTICAL);
            interestLayout.setVisibility(View.GONE);

            interestLayouts.put(category, interestLayout);

            for (InterestDTO interest : interests) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(interest.getName());
                checkBox.setChecked(selectedInterests.contains(interest));

                checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                    if (isChecked) selectedInterests.add(interest);
                    else selectedInterests.remove(interest);
                });

                checkBox.setTag(R.id.TAG_INTEREST_OBJECT, interest);
                interestLayout.addView(checkBox);
            }

            // Обработка сворачивания
            headerLayout.setOnClickListener(v -> {
                boolean expanded = interestLayout.getVisibility() == View.VISIBLE;
                interestLayout.setVisibility(expanded ? View.GONE : View.VISIBLE);
                arrow.animate().rotation(expanded ? 0 : 180).setDuration(200).start();
            });

            container.addView(headerLayout);
            container.addView(interestLayout);
        }
    }

    private void filterInterests(String query) {
        for (Map.Entry<CategoryDTO, LinearLayout> entry : interestLayouts.entrySet()) {
            LinearLayout layout = entry.getValue();
            int visibleCount = 0;

            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox cb = (CheckBox) child;
                    InterestDTO dto = (InterestDTO) cb.getTag(R.id.TAG_INTEREST_OBJECT);
                    boolean matches = dto.getName().toLowerCase().contains(query);
                    cb.setVisibility(matches ? View.VISIBLE : View.GONE);
                    if (matches) visibleCount++;
                }
            }

            // если ни один интерес не виден — скрываем категорию
            layout.setVisibility(visibleCount > 0 ? View.VISIBLE : View.GONE);
        }
    }
}

