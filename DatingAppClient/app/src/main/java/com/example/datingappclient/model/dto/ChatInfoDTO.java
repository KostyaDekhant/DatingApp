package com.example.datingappclient.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatInfoDTO implements Parcelable {

    @SerializedName("created_by")
    private Integer createdBy;

    @SerializedName("created_at")
    private Timestamp createdAt;

    @SerializedName("members")
    private List<ChatMemberDTO> members;

    @SerializedName("isGroup")
    private Boolean isGroup;

    protected ChatInfoDTO(Parcel in) {
        if (in.readByte() == 0) {
            createdBy = null;
        } else {
            createdBy = in.readInt();
        }

        long time = in.readLong();
        createdAt = time == -1 ? null : new Timestamp(time);

        members = new ArrayList<>();
        in.readList(members, ChatMemberDTO.class.getClassLoader());

        byte groupByte = in.readByte();
        isGroup = groupByte == 0 ? null : groupByte == 1;
    }

    public ChatMemberDTO findMemberById(int userId) {
        if (members == null) return null;
        for (ChatMemberDTO member : members) {
            if (member.getUserId() != null && member.getUserId() == userId) {
                return member;
            }
        }
        return null; // если не найден
    }

    public static final Creator<ChatInfoDTO> CREATOR = new Creator<>() {
        @Override
        public ChatInfoDTO createFromParcel(Parcel in) {
            return new ChatInfoDTO(in);
        }

        @Override
        public ChatInfoDTO[] newArray(int size) {
            return new ChatInfoDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (createdBy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(createdBy);
        }

        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);

        dest.writeList(members);

        if (isGroup == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) (isGroup ? 1 : 2));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}