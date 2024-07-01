package com.example.datingappclient.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.model.User;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsereditFragment extends Fragment {

    private User user;

    public UsereditFragment(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_useredit, container, false);
        MaterialButton button = activityView.findViewById(R.id.save_button);

        setInputText(activityView);

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
                        }
                        else {
                            Toast.makeText(activityView.getContext(), "BAD ID. ERROR", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable throwable) {
                        Toast.makeText(activityView.getContext(), "ERROR SAVE", Toast.LENGTH_LONG).show();
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "ERROR SAVE", throwable);
                    }
                });

                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(user.getId())).commit();
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
}