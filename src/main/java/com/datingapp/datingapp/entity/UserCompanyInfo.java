package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_company_info")
public class UserCompanyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_user_company_info")
    @JsonProperty("pk_user_company_info")
    private int pkUserCompany;
    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pkUser;
    @Column(name = "dolzh")
    @JsonProperty("dolzh")
    private String dolzh;
    @Column(name = "company_name")
    @JsonProperty("company_name")
    private String companyName;
    @Column(name = "otdel")
    @JsonProperty("otdel")
    private String otdel;
    @Column(name = "office")
    @JsonProperty("office")
    private String office;

    public UserCompanyInfo(int pkUserCompany, int pkUser, String dolzh, String companyName
    , String otdel, String office) {
        this.pkUserCompany = pkUserCompany;
        this.pkUser = pkUser;
        this.dolzh = dolzh;
        this.companyName = companyName;
        this.otdel = otdel;
        this.office = office;
    }

    public UserCompanyInfo() {
    }

    @Override
    public String toString() {
        return "UserCompanyInfo{" +
                "pk_user_company=" + pkUserCompany +
                ", pk_user=" + pkUser +
                ", dolzh='" + dolzh + '\'' +
                ", company_name='" + companyName + '\'' +
                ", otdel='" + otdel + '\'' +
                ", office='" + office + '\'' +
                '}';
    }
}
