package com.example.datingappclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse implements Parcelable {
    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("userId")
    private int userId;

    public boolean isExists() {
        return userId != -1 && token != null;
    }

    protected AuthResponse(Parcel in) {
        userId = in.readInt();
        token = in.readString();
    }

    public static final Creator<AuthResponse> CREATOR = new Creator<>() {
        @Override
        public AuthResponse createFromParcel(Parcel in) {
            return new AuthResponse(in);
        }

        @Override
        public AuthResponse[] newArray(int size) {
            return new AuthResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeString(token);
    }
}
