package com.example.datingappclient.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoDTO {
    @SerializedName("dolzh")
    private String role;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("otdel")
    private String department;
    @SerializedName("office")
    private String office;

    @Override
    public String toString() {
        return "CompanyInfoDTO{" +
                "role='" + role + '\'' +
                ", companyName='" + companyName + '\'' +
                ", department='" + department + '\'' +
                ", office='" + office + '\'' +
                '}';
    }
}
