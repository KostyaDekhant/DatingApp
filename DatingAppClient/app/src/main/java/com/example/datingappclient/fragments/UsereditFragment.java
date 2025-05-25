package com.example.datingappclient.fragments;

import static com.example.datingappclient.utils.BubbleUtils.getBubble;
import static com.example.datingappclient.utils.BubbleUtils.getBubblesFlexbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.CompanyInfoDTO;
import com.example.datingappclient.model.dto.PictureDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.model.UsereditForm;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.Setter;

public class UsereditFragment extends Fragment {

    /* === CONSTANTS === */
    private static final int PICK_IMAGE_REQUEST = 1;

    /* === Repository === */
    private BubblesRepository bubblesRepository;
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    /* === DTO Models === */
    @Setter
    private UserDTO user;
    private List<CategoryDTO> userCategories;
    private final Map<CategoryDTO, List<UserInterestDTO>> categoryInterestMap = new LinkedHashMap<>();

    /* === Other === */
    private int categoriesLoaded = 0;

    /* === Android Objects ===*/
    private TextInputEditText inputName;
    private TextInputEditText inputDesc;
    private TextInputEditText inputAge;
    private TextInputEditText inputHeight;
    private TextInputEditText inputOffice;
    private TextInputEditText inputDepartment;
    private TextInputEditText inputRole;
    private TextInputEditText inputCompanyName;

    private View cardAddImage;
    private GridLayout gridLayout;
    private LayoutInflater inflater;
    private View activityView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;


    /* === Methods === */
    public UsereditFragment() {}

    public static UsereditFragment newInstance(UserDTO user) {
        UsereditFragment usereditFragment = new UsereditFragment();
        usereditFragment.setUser(user);
        return usereditFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_useredit, container, false);

        setupRepository();

        inputName = activityView.findViewById(R.id.username_inputEdit);
        inputDesc = activityView.findViewById(R.id.description_inputEdit);
        inputAge = activityView.findViewById(R.id.age_inputEdit);
        inputHeight = activityView.findViewById(R.id.height_inputEdit);

        setBirthdayPicker();
        setupImagePicker();
        renderUserInfo();
        renderUserCompanyInfo();
        renderUserImages();

        setupReturnButton();
        setupSaveButton();
        setupAddInterestButton();

        getCategories();

        return activityView;
    }

    private void setupAddInterestButton() {
        MaterialButton addButton = activityView.findViewById(R.id.add_interests_button);

        addButton.setOnClickListener(v -> {
            InterestSelectionDialogFragment dialog = new InterestSelectionDialogFragment(categoryInterestMap);
            dialog.setCallback(selected -> {
                // обработка добавленных интересов
                for (UserInterestDTO interest : selected) {
                    // можно сохранить на сервер или в локальную переменную
                    // и затем обновить UI
                }
                Toast.makeText(requireContext(), "Выбрано: " + selected.size(), Toast.LENGTH_SHORT).show();
            });
            dialog.show(getParentFragmentManager(), "InterestSelectionDialog");
        });
    }

    private void setupImagePicker() {
        String logTag = Constants.GLOBAL_LOG_TAG + "SETUP IMAGE AFTER GET";
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            byte[] byteImage = ImageUtils.uriToByteArray(requireContext(), selectedImage);
                            Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(byteImage);

                            int imageNum = user.getImages().size() + 1;

                            uploadImage(byteImage, imageNum);
                            user.addUserImage(new UserImage(imageNum, 0, bitmapImage));

                            // Добавляем карточку изображения
                            addCardImage(imageNum);
                            Log.i(logTag, "Succeess");
                        } catch (IOException e) {
                            Log.e(logTag, "Ошибка обработки изображения", e);
                            Toast.makeText(requireContext(), "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void addCardImage(int imageNum) {
        View cardImage = createCardImage(inflater, imageNum - 1);
        cardImage.setLayoutParams(setLayoutParams(imageNum - 1));

        gridLayout.removeView(cardAddImage);
        gridLayout.addView(cardImage);

        if (imageNum < 6) {
            cardAddImage.setLayoutParams(setLayoutParams(imageNum));
            gridLayout.addView(cardAddImage);
        }
    }

    private void setupRepository() {
        bubblesRepository = new BubblesRepository(requireContext());
        userRepository = new UserRepository(requireContext());
        imageRepository = new ImageRepository(requireContext());
    }

    private void getCategories() {
        String logTag = Constants.GLOBAL_LOG_TAG + "CATEGORIES";
        bubblesRepository.fetchCategories(result -> {
            switch (result.status) {
                case SUCCESS:
                    userCategories = result.data;
                    Log.i(logTag, userCategories.toString());

                    // Запрашиваем бабблы ПОСЛЕ категорий
                    getInterests();
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
            }
        });
    }

    private void getInterests() {
        categoryInterestMap.clear();
        categoriesLoaded = 0;

        String logTag = Constants.GLOBAL_LOG_TAG + "INTEREST";
        for (CategoryDTO category : userCategories) {
            bubblesRepository.fetchUserInterestsByCategory(user.getId(), category.getId(), result -> {
                switch (result.status) {
                    case SUCCESS:
                        categoryInterestMap.put(category, result.data);
                        categoriesLoaded++;
                        Log.i(logTag, category.getName() + ": " + result.data);

                        // После получения категорий и бабблов - отрисовка
                        if (categoriesLoaded == userCategories.size()) {
                            renderBubbles();
                        }
                        break;
                    case ERROR:
                        Log.e(logTag, result.error);
                        categoriesLoaded++;

                        if (categoriesLoaded == userCategories.size()) {
                            renderBubbles();
                        }
                        break;
                }
            });
        }
    }

    private void renderBubbles() {
        LinearLayout container = activityView.findViewById(R.id.categories_container);
        container.removeAllViews();

        for (Map.Entry<CategoryDTO, List<UserInterestDTO>> entry : categoryInterestMap.entrySet()) {
            // Получаем категорию и бабблы для нее
            CategoryDTO category = entry.getKey();
            List<UserInterestDTO> interests = entry.getValue();

            if (interests == null || interests.isEmpty()) continue;

            // Создаем ConstraintLayout
            ConstraintLayout categoryLayout = new ConstraintLayout(activityView.getContext());
            categoryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            categoryLayout.setId(View.generateViewId());

            // === TextView с названием категории ===
            TextView label = getCategoryLabel(category.getName());
            categoryLayout.addView(label);

            // === FlexboxLayout под интересы ===
            FlexboxLayout flexbox = getBubblesFlexbox(activityView, label.getId());
            categoryLayout.addView(flexbox);

            // === Добавление Bubble'ов в Flexbox ===
            ContextThemeWrapper wrapper = new ContextThemeWrapper(activityView.getContext(), R.style.ThemeOverlay_ChipStyleEdit);
            for (UserInterestDTO interest : interests) {
                Chip chip = getBubble(activityView, interest.getName(), wrapper);
                flexbox.addView(chip);
            }

            // Добавляем готовый блок в контейнер
            container.addView(categoryLayout);
        }
    }

    @NonNull
    private TextView getCategoryLabel(String categoryName) {
        TextView label = new TextView(activityView.getContext());
        label.setText(categoryName.toUpperCase());
        label.setId(View.generateViewId());
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        ConstraintLayout.LayoutParams labelParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        labelParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        label.setLayoutParams(labelParams);

        return label;
    }

    private void setupSaveButton() {
        MaterialButton acceptEdit = activityView.findViewById(R.id.save_button);
        acceptEdit.setOnClickListener(view -> {
            UsereditForm form = UsereditForm.fromInputs(inputName, inputDesc, inputAge, inputHeight, inputCompanyName, inputOffice, inputDepartment, inputRole);

            if (!form.isValid(view)) return;

            updateUserFromForm(form);
            updateUser();
            updateUserCompanyInfo();

            UserFragment userFragment = UserFragment.getInstance(user, false);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).commit();
        });
    }

    private void setupReturnButton() {
        MaterialButton returnButton = activityView.findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> {
            UserFragment userFragment = UserFragment.getInstance(user, false);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).commit();
        });
    }

    private void updateUserFromForm(UsereditForm form) {
        user.setName(form.username);
        user.setDescription(form.description);
        user.setBirthday(DateUtils.stringToLocalDate(form.birthday));
        user.setHeight(Integer.parseInt(form.height));
        CompanyInfoDTO companyInfo = new CompanyInfoDTO(form.role, form.companyName, form.department, form.office);
        user.setCompanyInfo(companyInfo);
    }

    private void updateUser() {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE USER";
        Log.d (logTag, user.toString());
        userRepository.updateUser(user, result -> {
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(activityView.getContext(), "Успешная обновлено!", Toast.LENGTH_LONG).show();
                    Log.d(logTag, "Success update");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
            }
        });
    }

    private void updateUserCompanyInfo() {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE USER COMPANY INFO";
        userRepository.updateUserCompanyInfo(user.getId(), user.getCompanyInfo(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Success update");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    // Установка DatePickerDialog для поля возраста
    private void setBirthdayPicker() {
        EditText inputAge = activityView.findViewById(R.id.age_inputEdit);
        inputAge.setInputType(InputType.TYPE_NULL); // Отключение ручного ввода
        inputAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Преобразуем дату в формат yyyy-MM-dd
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = sdf.format(selectedDate.getTime());
                        inputAge.setText(formattedDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void renderUserInfo() {
        TextInputEditText nameInput = activityView.findViewById(R.id.username_inputEdit);
        TextInputEditText descInput = activityView.findViewById(R.id.description_inputEdit);
        TextInputEditText ageInput = activityView.findViewById(R.id.age_inputEdit);
        TextInputEditText heightInput = activityView.findViewById(R.id.height_inputEdit);

        nameInput.setText(user.getName());
        descInput.setText(user.getDescription());
        String birthday = DateUtils.localDateToString(user.getBirthday());
        ageInput.setText(birthday);
        heightInput.setText(String.valueOf(user.getHeight()));
    }

    private void renderUserCompanyInfo() {
        inputCompanyName = activityView.findViewById(R.id.company_inputEdit);
        inputOffice = activityView.findViewById(R.id.office_inputEdit);
        inputDepartment = activityView.findViewById(R.id.departament_inputEdit);
        inputRole = activityView.findViewById(R.id.role_inputEdit);

        CompanyInfoDTO companyInfo = user.getCompanyInfo();
        inputCompanyName.setText(companyInfo.getCompanyName());
        inputOffice.setText(companyInfo.getOffice());
        inputDepartment.setText(companyInfo.getDepartment());
        inputRole.setText(companyInfo.getRole());
    }

    /* === IMAGES === */

    // Иницииализация элементов управления для редактирование изобращение(add/remove)
    private void renderUserImages() {
        gridLayout = activityView.findViewById(R.id.images_grid);
        // Получение LayoutInflater из контекста
        inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gridLayout.removeAllViews();

        // Динамическое добавление карточек с изображенем пользователя
        for (int i = 0; i < user.getImages().size(); i++) {
            View cardImage = createCardImage(inflater, i);
            cardImage.setLayoutParams(setLayoutParams(i));
            gridLayout.addView(cardImage);
        }

        // Создание карточки добавления фото
        if (gridLayout.getChildCount() < 6) {
            cardAddImage = inflater.inflate(R.layout.add_photo_item, gridLayout, false);

            setupAddImageButton(cardAddImage);

            cardAddImage.setLayoutParams(setLayoutParams(gridLayout.getChildCount()));
            gridLayout.addView(cardAddImage);
        }

        // Заполнение пустот для выравнивания
        while (gridLayout.getChildCount() < 6) {
            Space space = new Space(gridLayout.getContext());
            space.setLayoutParams(setLayoutParams(gridLayout.getChildCount()));
            gridLayout.addView(space);
        }

    }

    private void setupAddImageButton(View cardAddImage) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET IMAGE FROM DEVICE";
        MaterialButton addButton = cardAddImage.findViewById(R.id.addImage_button);
        addButton.setId(View.generateViewId());
        addButton.setOnClickListener(view -> {
            Log.i(logTag, String.valueOf(view.getId()));
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    // Установка параметров Layout'а для элемента с номером elCount
    private GridLayout.LayoutParams setLayoutParams(int elCount) {
        int dpWidth = 80;
        int dpHeight = 120;

        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        int pxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, metrics);
        int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, metrics);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = pxWidth;
        params.height = pxHeight;
        int rowNum = 0;
        int columnNum = elCount;
        if (elCount > 2) {
            rowNum = 1;
            columnNum = elCount - 3;
        }
        params.rowSpec = GridLayout.spec(rowNum, 1, 1f);
        params.columnSpec = GridLayout.spec(columnNum, 1, 1f);
        params.setGravity(Gravity.CENTER);
        return params;
    }

    // Создает View с карточкой-изображением пользователя
    private View createCardImage(LayoutInflater inflater, int cardImageNum) {
        // Создание View из XML-файла макета
        View cardImage = inflater.inflate(R.layout.user_photo_item, gridLayout, false);

        ImageView imageView = cardImage.findViewById(R.id.userImage);
        MaterialButton deleteButton = cardImage.findViewById(R.id.deleteImage_button);

        cardImage.setId(View.generateViewId());
        imageView.setId(View.generateViewId());
        deleteButton.setId(View.generateViewId());

        imageView.setImageBitmap(user.getImages().get(cardImageNum).getImage());

        // Установка ограничений внутри cardImage
        ConstraintLayout constraintLayout = (ConstraintLayout) cardImage;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(deleteButton.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, 10);
        constraintSet.connect(deleteButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP, 10);
        constraintSet.applyTo(constraintLayout);

        deleteButton.setTag(R.id.TAG_IMAGE_NUMBER, user.getImages().get(cardImageNum).getImageNum());
        deleteButton.setTag(R.id.TAG_CARDIMAGE_ID, cardImage);

        deleteButton.setOnClickListener(setupDeleteButton());

        return cardImage;
    }

    private View.OnClickListener setupDeleteButton() {
        return view -> {
            Log.i("DELETE IMAGE", view.getTag(R.id.TAG_IMAGE_NUMBER).toString());

            int imageNum = (int) view.getTag(R.id.TAG_IMAGE_NUMBER);
            int imageId = user.getUserImageID(imageNum);

            deleteImage(imageId);

            user.removeImage(imageNum);
            renderUserImages();
        };
    }

    private void deleteImage(int imageId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE USER IMAGE";
        imageRepository.deleteImage(imageId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(activityView.getContext(), "Изображение удалено!", Toast.LENGTH_LONG).show();
                    Log.i(logTag, "Изображение успешно удалено!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void uploadImage(byte[] image, int imageNum) {
        String logTag = Constants.GLOBAL_LOG_TAG + "SEND IMAGE";
        imageRepository.uploadImage(new PictureDTO(imageNum, image, user.getId()), result -> {
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(activityView.getContext(), "Изображение сохранено!", Toast.LENGTH_LONG).show();
                    Log.i(logTag, "Изображение успешно сохранено!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

}