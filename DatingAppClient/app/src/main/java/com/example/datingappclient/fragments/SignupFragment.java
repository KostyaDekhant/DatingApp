package com.example.datingappclient.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.text.InputType;
import android.widget.DatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;
import com.example.datingappclient.AuthActivity;
import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View activityView = inflater.inflate(R.layout.fragment_signup, container, false);

        TextInputEditText inputAge = activityView.findViewById(R.id.age_inputEdit);  // Объявляем и инициализируем переменную здесь

        MaterialButton signupButton = activityView.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText inputLogin = activityView.findViewById(R.id.login_inputEdit);
                TextInputEditText inputPass = activityView.findViewById(R.id.pass_inputEdit);
                TextInputEditText inputName = activityView.findViewById(R.id.username_inputEdit);

                // inputAge уже инициализирована ранее

                String login = inputLogin.getText().toString();
                String pass = inputPass.getText().toString();
                String name = inputName.getText().toString();
                String age = inputAge.getText().toString();

                // Проверка на пустые поля
                if (login.isEmpty() || pass.isEmpty() || name.isEmpty() || age.isEmpty()) {
                    Snackbar.make(view, "Все поля должны быть заполнены", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "Login: " + login);
                Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "Password: " + pass);
                Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "Name: " + name);
                Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "Age: " + age);

                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                JsonObject signupJsonObject = new JsonObject();
                signupJsonObject.addProperty("login", login);
                signupJsonObject.addProperty("password", pass);

                serverAPI.signup(signupJsonObject).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        int returnCode = response.body();
                        if (returnCode > 0) {
                            JsonObject updateUserJsonObject = new JsonObject();
                            updateUserJsonObject.addProperty("pk_user", returnCode);
                            updateUserJsonObject.addProperty("name", name);
                            updateUserJsonObject.addProperty("age", age);

                            serverAPI.updateUser(updateUserJsonObject).enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Toast.makeText(activityView.getContext(), "SUCCESS SAVE", Toast.LENGTH_LONG).show();
                                    Logger.getLogger(MainActivity.class.getName()).log(Level.INFO, "SUCCESS SAVE", response.body());
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable throwable) {
                                    Toast.makeText(activityView.getContext(), "ERROR SAVE", Toast.LENGTH_LONG).show();
                                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SAVE", throwable);
                                }
                            });
                            ((AuthActivity) getActivity()).startMainActivity(returnCode);
                        } else if (returnCode == -1) {
                            Snackbar.make(view, "Ошибка регистрации, такой пользователь уже существует!", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Toast.makeText(activityView.getContext(), "ERROR SIGNUP", Toast.LENGTH_LONG).show();
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SIGNUP", throwable);
                    }
                });
            }
        });

        // Установка DatePickerDialog для поля возраста
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

        return activityView;
    }
}
