package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.datingappclient.MainActivity;
import com.example.datingappclient.R;
import com.google.android.material.button.MaterialButton;

public class UserFragment extends Fragment implements View.OnClickListener {

    public UserFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        MaterialButton button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(this) ;
        return view;
    }

    @Override
    public void onClick(View view) {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new UsereditFragment()).commit();
    }
}
