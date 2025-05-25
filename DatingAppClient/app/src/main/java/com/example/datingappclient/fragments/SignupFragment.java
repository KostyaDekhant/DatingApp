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

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.AuthDTO;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.repository.UserRepository;
import com.example.datingappclient.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import com.example.datingappclient.activity.AuthActivity;
import com.example.datingappclient.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SignupFragment extends Fragment {

    /* === Repository === */
    private UserRepository userRepository;

    /* === Android Objects === */
    private View activityView;
    private TextInputEditText inputBirthday;
    private TextInputEditText inputLogin;
    private TextInputEditText inputPass;
    private TextInputEditText inputName;

    /* === Other === */
    private String login, pass, name, birthday;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityView = inflater.inflate(R.layout.fragment_signup, container, false);

        setupRepository();
        setupInputEditView();
        setupReturnButton();
        setupSignupButton();
        setupDateTimePicker();

        return activityView;
    }

    private void setupRepository() {
        userRepository = new UserRepository(requireContext());
    }

    private void setupInputEditView() {
        inputBirthday = activityView.findViewById(R.id.age_inputEdit);
        inputLogin = activityView.findViewById(R.id.login_inputEdit);
        inputPass = activityView.findViewById(R.id.pass_inputEdit);
        inputName = activityView.findViewById(R.id.username_inputEdit);
    }

    private void setupDateTimePicker() {
        // Установка DatePickerDialog для поля возраста
        inputBirthday.setInputType(InputType.TYPE_NULL); // Отключение ручного ввода
        inputBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            String currentText = inputBirthday.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Если в поле уже есть дата — пробуем её распарсить
            if (!currentText.isEmpty()) {
                try {
                    calendar.setTime(sdf.parse(currentText));
                } catch (Exception e) {
                    e.printStackTrace(); // оставляем текущую дату
                }
            }


            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
                // Преобразуем дату в формат yyyy-MM-dd
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year1, month1, dayOfMonth);
                String formattedDate = sdf.format(selectedDate.getTime());
                inputBirthday.setText(formattedDate);
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
        signupButton.setOnClickListener(view -> {

            // inputAge уже инициализирована ранее
            login = inputLogin.getText().toString();
            pass = inputPass.getText().toString();
            name = inputName.getText().toString();
            birthday = inputBirthday.getText().toString();

            // !!! Проверка на пустые поля
            if (login.isEmpty() || pass.isEmpty() || name.isEmpty() || birthday.isEmpty()) {
                Snackbar.make(view, "Все поля должны быть заполнены", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Хэширование пароля перед отправкой на сервер
            // pass = PasswordUtils.hashPassword(pass); // разкомментировать для хэширования

            signupUser(new AuthDTO(login, pass));
        });
    }

    private void signupUser(AuthDTO authData){
        String logTag = Constants.GLOBAL_LOG_TAG + "SIGNUP. Create user";
        userRepository.signup(authData, result -> {
            int userId = result.data.getUserId();
            switch (result.status) {
                case SUCCESS:
                    if (userId > 0) {
                        updateUser(result.data);
                    } else if (userId == -1) {
                        String errorMessage = "Ошибка регистрации, такой пользователь уже существует!";
                        Snackbar.make(activityView, errorMessage, Snackbar.LENGTH_LONG).show();
                        Log.d(logTag, errorMessage);
                    }
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
            }
        });
    }

    private void updateUser(AuthResponse authResponse) {
        UserDTO userDTO = new UserDTO(authResponse.getUserId(), name, DateUtils.stringToLocalDate(birthday));
        String logTag = Constants.GLOBAL_LOG_TAG + "SIGNUP. Update user";
        Log.d(logTag, userDTO.toString());

        userRepository.updateUser(userDTO, result -> {
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(activityView.getContext(), "Успешная регистрация!", Toast.LENGTH_LONG).show();
                    Log.d(logTag, "Success");
                    ((AuthActivity) getActivity()).startMainActivity(authResponse);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
            }
        });
    }

}
