package com.example.datingappclient.model;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class UsereditForm {
    public String username;
    public String description;
    public String birthday;
    public String height;
    public String companyName;
    public String office;
    public String department;
    public String role;
    // и т.д.

    public static UsereditForm fromInputs(
            EditText usernameInput,
            EditText descInput,
            EditText birthdayInput,
            EditText heightInput,
            EditText companyNameInput,
            EditText officeInput,
            EditText departmentInput,
            EditText roleInput
    ) {
        UsereditForm form = new UsereditForm();
        form.username = usernameInput.getText().toString().trim();
        form.description = descInput.getText().toString().trim();
        form.birthday = birthdayInput.getText().toString().trim();
        form.height = heightInput.getText().toString().trim();
        form.companyName = companyNameInput.getText().toString().trim();
        form.office = officeInput.getText().toString().trim();
        form.department = departmentInput.getText().toString().trim();
        form.role = roleInput.getText().toString().trim();
        return form;
    }

    public boolean isValid(View view) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(birthday)) {
            Snackbar.make(view, "Имя и дата рождения обязательны", Snackbar.LENGTH_LONG).show();
            return false;
        }
        // можно добавить и другие проверки, например по формату
        return true;
    }
}
