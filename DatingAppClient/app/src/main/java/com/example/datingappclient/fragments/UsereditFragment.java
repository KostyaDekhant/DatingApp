package com.example.datingappclient.fragments;

import android.app.Activity;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Toast;

import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.model.Picture;
import com.example.datingappclient.model.User;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.example.datingappclient.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

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
        MaterialButton button = activityView.findViewById(R.id.save_button);

        setInputText(activityView);
        setImages(activityView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                TextInputEditText inputName = activityView.findViewById(R.id.login_inputEdit);
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
                        if (response.body()) {
                            Toast.makeText(activityView.getContext(), "SUCCESS SAVE", Toast.LENGTH_LONG).show();
                            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "SUCCESS SAVE");
                        } else {
                            Toast.makeText(activityView.getContext(), "BAD ID. ERROR", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable throwable) {
                        Toast.makeText(activityView.getContext(), "ERROR SAVE", Toast.LENGTH_LONG).show();
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SAVE", throwable);
                    }
                });

                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(new User(user.getId()))).commit();
            }
        });

        return activityView;
    }

    private void setInputText(View view) {
        TextInputEditText name_input = view.findViewById(R.id.login_inputEdit);
        TextInputEditText desc_input = view.findViewById(R.id.description_inputEdit);
        TextInputEditText age_input = view.findViewById(R.id.age_inputEdit);

        name_input.setText(user.getUsername());
        desc_input.setText(user.getDesc());
        age_input.setText(user.getAge());
    }

    private void setImages(View view) {
        gridLayout = view.findViewById(R.id.images_grid);
        // Получение LayoutInflater из контекста
        inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        gridLayout.removeAllViews();
        // Динамическое добавление элементов
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

        // Заполнение пустот
        while (gridLayout.getChildCount() < 6) {
            Space space = new Space(gridLayout.getContext());
            space.setLayoutParams(setLayoutParams(gridLayout.getChildCount()));
            // Добавление Space в GridLayout
            gridLayout.addView(space);
        }

    }

    private GridLayout.LayoutParams setLayoutParams(int elCount) {
        int dpWidth = 90;
        int dpHeight = 140;

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
        return params;
    }

    private View createCardImage(LayoutInflater inflater, int cardImageNum) {
        // Создание View из XML-файла макета
        View cardImage = inflater.inflate(R.layout.user_photo_item, gridLayout, false);
        cardImage.setId(View.generateViewId());

        ImageView imageView = cardImage.findViewById(R.id.userImage);
        imageView.setId(View.generateViewId());
        imageView.setImageBitmap(user.getListImages().get(cardImageNum).getImage());

        MaterialButton deleteButton = cardImage.findViewById(R.id.deleteImage_button);
        deleteButton.setId(View.generateViewId());

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

                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {

                    }
                });
            }
        });

        return cardImage;
    }

    private void sendImageOnServer(byte[] image, int imageNum) {
        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        JsonObject jsonObject = new JsonObject();

        Byte[] test = ImageUtils.converPrimitiveByteToByte(image);
        //image = ImageUtils.compressImage(image);
        //String stringImage = Arrays.toString(image);
        jsonObject.addProperty("image", Arrays.toString(test));
        jsonObject.addProperty("user_id", user.getId());
        jsonObject.addProperty("image_id", imageNum);

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

    // Обработка выбранного изображения
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                byte[] byteImage = uriToByteArray(getContext(), selectedImage);
                Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(byteImage);
                gridLayout.removeView(cardAddImage);
                int imageNum = user.getListImages().size() + 1;
                user.addUserImage(new UserImage(imageNum, 0, bitmapImage));

                View cardImage = createCardImage(inflater, imageNum - 1);
                cardImage.setLayoutParams(setLayoutParams(imageNum - 1));
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

    private byte[] uriToByteArray(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}