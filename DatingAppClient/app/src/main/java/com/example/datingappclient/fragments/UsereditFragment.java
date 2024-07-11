package com.example.datingappclient.fragments;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Space;

import com.example.datingappclient.R;
import com.example.datingappclient.model.Picture;
import com.example.datingappclient.model.User;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsereditFragment extends Fragment {

    private User user;

    View cardAddImage;
    GridLayout gridLayout;
    LayoutInflater inflater;

    public UsereditFragment(User user) {
        this.user = user;
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_useredit, container, false);
        MaterialButton acceptEdit = activityView.findViewById(R.id.save_button);

        setInputText(activityView);
        setBirthdayPicker(activityView);
        setImages(activityView);

        acceptEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                TextInputEditText inputName = activityView.findViewById(R.id.username_inputEdit);
                TextInputEditText inputDesc = activityView.findViewById(R.id.description_inputEdit);
                TextInputEditText inputAge = activityView.findViewById(R.id.age_inputEdit);

                String username = inputName.getText().toString();
                String desc = inputDesc.getText().toString();
                String age = inputAge.getText().toString();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("pk_user", user.getId());
                jsonObject.addProperty("name", username);
                jsonObject.addProperty("description", desc);
                jsonObject.addProperty("age", age);

                serverAPI.updateUser(jsonObject).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.body() != null && response.body()) {
                            Log.d("SAVE USERINFO ERROR", "");
                        } else {
                            Log.d("SAVE USERINFO ERROR", "BAD ID");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable throwable) {
                        Log.d("SAVE USERINFO ERROR", throwable.getMessage());
                    }
                });

                user.setUsername(username);
                user.setDesc(desc);
                user.setBirthday(age);

                UserFragment userFragment = UserFragment.getInstance(user, false);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).commit();
            }
        });

        return activityView;
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

        name_input.setText(user.getUsername());
        desc_input.setText(user.getDesc());
        age_input.setText(user.getBirthday().replace("\"", ""));
    }

    // Иницииализация элементов управления для редактирование изобращение(add/remove)
    private void setImages(View view) {
        gridLayout = view.findViewById(R.id.images_grid);
        // Получение LayoutInflater из контекста
        inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        gridLayout.removeAllViews();

        // Динамическое добавление карточек с изображенем пользователя
        for (int i = 0; i < user.getListImages().size(); i++) {
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
                    Log.d("BUTTON ADD PRESSED", "" + view.getId());

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

        imageView.setImageBitmap(user.getListImages().get(cardImageNum).getImage());

        // Установка ограничений внутри cardImage
        ConstraintLayout constraintLayout = (ConstraintLayout) cardImage;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(deleteButton.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, 10);
        constraintSet.connect(deleteButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP, 10);
        constraintSet.applyTo(constraintLayout);

        deleteButton.setTag(R.id.TAG_IMAGE_NUMBER, user.getListImages().get(cardImageNum).getImageNum());
        deleteButton.setTag(R.id.TAG_CARDIMAGE_ID, cardImage);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BUTTON DELETE PRESSED", view.getTag(R.id.TAG_IMAGE_NUMBER).toString());

                user.removeImage(((int) view.getTag(R.id.TAG_IMAGE_NUMBER)));
                setImages(getView());

                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);
                serverAPI.deleteImage(user.getUserImageID((int) view.getTag(R.id.TAG_IMAGE_NUMBER))).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.body() != null && response.body().intValue() == 1)
                            Log.d("DELETE IMAGE", "Success delete image from profile");
                        else
                            Log.d("ERROR DELETE IMAGE", "");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Log.d("ERROR DELETE IMAGE", throwable.getMessage());
                    }
                });
            }
        });

        return cardImage;
    }

    // Отправка полученного изображения на сервер
    private void sendImageOnServer(byte[] image, int imageNum) {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.uploadImage(new Picture(imageNum, image, user.getId())).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() > 0) {
                    user.setUserImageID(response.body(), imageNum);
                    Log.d("SUCCESS SEND IMAGE", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.d("ERROR SEND IMAGE", throwable.getMessage());
            }
        });
    }

    // Обработка выбранного пользователем изображения
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                byte[] byteImage = ImageUtils.uriToByteArray(getContext(), selectedImage);
                Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(byteImage);

                int imageNum = user.getListImages().size() + 1;
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

                sendImageOnServer(byteImage, imageNum);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}