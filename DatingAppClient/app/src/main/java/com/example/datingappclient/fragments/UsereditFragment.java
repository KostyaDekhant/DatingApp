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
import com.example.datingappclient.model.CategoryDTO;
import com.example.datingappclient.model.PictureDTO;
import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.model.UserInterestDTO;
import com.example.datingappclient.retrofit.repository.BubblesRepository;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UsereditFragment extends Fragment {

    /* === CONSTANTS === */
    private static final int PICK_IMAGE_REQUEST = 1;

    /* === Repository === */
    private final BubblesRepository bubblesRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    /* === DTO Models === */
    private final UserDTO user;
    private List<CategoryDTO> userCategories;
    private final Map<CategoryDTO, List<UserInterestDTO>> categoryInterestMap = new LinkedHashMap<>();

    /* === Other === */
    private int categoriesLoaded = 0;

    /* === Android Objects ===*/
    private TextInputEditText inputName;
    private TextInputEditText inputDesc;
    private TextInputEditText inputAge;
    private View cardAddImage;
    private GridLayout gridLayout;
    private LayoutInflater inflater;
    private View activityView;


    /* === Methods === */
    public UsereditFragment(UserDTO user) {
        this.user = user;

        bubblesRepository = new BubblesRepository();
        userRepository = new UserRepository();
        imageRepository = new ImageRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_useredit, container, false);

        inputName = activityView.findViewById(R.id.username_inputEdit);
        inputDesc = activityView.findViewById(R.id.description_inputEdit);
        inputAge = activityView.findViewById(R.id.age_inputEdit);

        setInputText(activityView);
        setBirthdayPicker(activityView);
        setImages(activityView);
        setupReturnButton();
        setupSaveButton();

        getCategories();

        return activityView;
    }

    private void getCategories() {
        String logTag = Constants.GLOBAL_LOG_TAG + "CATEGORIES";
        bubblesRepository.fetchCategories(new BubblesRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<CategoryDTO> categories) {
                userCategories = categories;
                Log.i(logTag, categories.toString());

                // Запрашиваем бабблы ПОСЛЕ категорий
                getInterests();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    private void getInterests() {

        categoryInterestMap.clear();
        categoriesLoaded = 0;

        String logTag = Constants.GLOBAL_LOG_TAG + "INTEREST";
        for (CategoryDTO category : userCategories) {
            bubblesRepository.fetchUserInterestsByCategory(user.getId(), category.getId(),new BubblesRepository.UserInterestCallback() {
                @Override
                public void onSuccess(List<UserInterestDTO> interests) {
                    categoryInterestMap.put(category, interests);
                    categoriesLoaded++;
                    Log.i(logTag, category.getName() + ": " + interests);

                    // После получения категорий и бабблов - отрисовка
                    if (categoriesLoaded == userCategories.size()) {
                        renderBubbles();
                    }

                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(logTag, errorMessage);
                    categoriesLoaded++;

                    if (categoriesLoaded == userCategories.size()) {
                        renderBubbles();
                    }
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

            String username = inputName.getText().toString();
            String description = inputDesc.getText().toString();
            String birthday = inputAge.getText().toString();

            // !!! Проверка на пустые поля
            if (username.isEmpty() || birthday.isEmpty()) {
                Snackbar.make(view, "Все поля должны быть заполнены", Snackbar.LENGTH_LONG).show();
                return;
            }

            user.setName(username);
            user.setDescription(description);
            user.setBirthday(DateUtils.stringToLocalDate(birthday));

            updateUser();

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

    private void updateUser() {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE USER";
        Log.d (logTag, user.toString());
        userRepository.updateUser(user, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(activityView.getContext(), "Успешная обновлено!", Toast.LENGTH_LONG).show();
                Log.d(logTag, "Success");
            }
            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    // Установка DatePickerDialog для поля возраста
    private void setBirthdayPicker(View view) {
        EditText inputAge = view.findViewById(R.id.age_inputEdit);
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

    private void setInputText(View view) {
        TextInputEditText name_input = view.findViewById(R.id.username_inputEdit);
        TextInputEditText desc_input = view.findViewById(R.id.description_inputEdit);
        TextInputEditText age_input = view.findViewById(R.id.age_inputEdit);

        name_input.setText(user.getName());
        desc_input.setText(user.getDescription());
        String birthday = DateUtils.localDateToString(user.getBirthday());
        age_input.setText(birthday);
    }

    // Иницииализация элементов управления для редактирование изобращение(add/remove)
    private void setImages(View view) {
        gridLayout = view.findViewById(R.id.images_grid);
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

            MaterialButton addButton = cardAddImage.findViewById(R.id.addImage_button);
            addButton.setId(View.generateViewId());
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("ADD IMAGE", "" + view.getId());

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            });

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
            setImages(getView());
        };
    }

    private void deleteImage(int imageId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE USER IMAGE";
        imageRepository.deleteImage(imageId, new ImageRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(activityView.getContext(), "Изображение удалено!", Toast.LENGTH_LONG).show();
                Log.d(logTag, "Success");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    private void sendImageOnServer(byte[] image, int imageNum) {
        String logTag = Constants.GLOBAL_LOG_TAG + "SEND IMAGE";
        imageRepository.uploadImage(new PictureDTO(imageNum, image, user.getId()), new ImageRepository.UploadCallback() {
            @Override
            public void onSuccess(int imageId) {
                Toast.makeText(activityView.getContext(), "Изображение сохранено!", Toast.LENGTH_LONG).show();
                Log.d(logTag, "Success");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }

    // Обработка выбранного пользователем изображения
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Log.d ("GET USER IMAGE", data.toString());
            Uri selectedImage = data.getData();
            try {
                byte[] byteImage = ImageUtils.uriToByteArray(getContext(), selectedImage);
                Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(byteImage);

                int imageNum = user.getImages().size() + 1;

                sendImageOnServer(byteImage, imageNum);

                user.addUserImage(new UserImage(imageNum, 0, bitmapImage));

                // Создаем карточку
                View cardImage = createCardImage(inflater, imageNum - 1);
                cardImage.setLayoutParams(setLayoutParams(imageNum - 1));

                gridLayout.removeView(cardAddImage);
                gridLayout.addView(cardImage);

                if (imageNum != 6) {
                    cardAddImage.setLayoutParams(setLayoutParams(imageNum));
                    gridLayout.addView(cardAddImage);
                }
            } catch (IOException e) {
                Log.d("GET USER IMAGE ERROR", e.toString());
                throw new RuntimeException(e);
            }
        }
    }

}