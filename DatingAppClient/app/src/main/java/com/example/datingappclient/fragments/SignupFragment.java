package com.example.datingappclient.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.text.InputType;
import android.widget.DatePicker;

import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }
    private final UserRepository userRepository = new UserRepository();
    private View activityView;
    private TextInputEditText inputAge;
    private TextInputEditText inputLogin;
    private TextInputEditText inputPass;
    private TextInputEditText inputName;
    private String login, pass, name, birthday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityView = inflater.inflate(R.layout.fragment_signup, container, false);

        setupInputEditView();
        setupReturnButton();
        setupSignupButton();
        setupDateTimePicker();

        return activityView;
    }

    private void setupInputEditView() {
        inputAge = activityView.findViewById(R.id.age_inputEdit);
        inputLogin = activityView.findViewById(R.id.login_inputEdit);
        inputPass = activityView.findViewById(R.id.pass_inputEdit);
        inputName = activityView.findViewById(R.id.username_inputEdit);
    }
    private void setupDateTimePicker() {
        // Установка DatePickerDialog для поля возраста
        inputAge.setInputType(InputType.TYPE_NULL); // Отключение ручного ввода
        inputAge.setOnClickListener(v -> {
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
        });
    }
    private void setupReturnButton() {
        MaterialButton returnButton = activityView.findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> ((AuthActivity) getActivity()).openFragment(new SigninFragment()));
    }
    private void setupSignupButton() {
        MaterialButton signupButton = activityView.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // inputAge уже инициализирована ранее
                login = inputLogin.getText().toString();
                pass = inputPass.getText().toString();
                name = inputName.getText().toString();
                birthday = inputAge.getText().toString();

                // !!! Проверка на пустые поля
                if (login.isEmpty() || pass.isEmpty() || name.isEmpty() || birthday.isEmpty()) {
                    Snackbar.make(view, "Все поля должны быть заполнены", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Хэширование пароля перед отправкой на сервер
                // pass = PasswordUtils.hashPassword(pass); // разкомментировать для хэширования

                RetrofitService retrofitService = new RetrofitService();
                ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

                JsonObject signupJsonObject = new JsonObject();
                signupJsonObject.addProperty("login", login);
                signupJsonObject.addProperty("password", pass);

                signupUser(signupJsonObject);
            }
        });
    }
    private void signupUser(JsonObject signupJsonObject){
        String logTag = "SIGNUP. Create user";
        userRepository.signup(signupJsonObject, new UserRepository.SignupCallback() {
            @Override
            public void onSuccess(int userId) {
                if (userId > 0) {
                    updateUser(userId);
                } else if (userId == -1) {
                    String errorMessage = "Ошибка регистрации, такой пользователь уже существует!";
                    Snackbar.make(activityView, errorMessage, Snackbar.LENGTH_LONG).show();
                    Log.d(logTag, errorMessage);
                }
            }
            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });
    }
    private void updateUser(int userId) {
        UserDTO userDTO = new UserDTO(userId, name, DateUtils.stringToLocalDate(birthday));
        String logTag = "SIGNUP. Update user";
        Log.d(logTag, userDTO.toString());
        userRepository.updateUser(userDTO, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(activityView.getContext(), "Успешная регистрация!", Toast.LENGTH_LONG).show();
                Log.d(logTag, "Success");
                ((AuthActivity) getActivity()).startMainActivity(userId);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(logTag, errorMessage);
            }
        });

    }

}
