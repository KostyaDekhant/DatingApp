package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserCompanyInfoDto {
    @JsonProperty("dolzh")
    private String dolzh;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("otdel")
    private String otdel;
    @JsonProperty("office")
    private String office;

    public UserCompanyInfoDto(UserCompanyInfo userCompanyInfo) {
        dolzh = userCompanyInfo.getDolzh();
        companyName = userCompanyInfo.getCompanyName();
        otdel = userCompanyInfo.getOtdel();
        office = userCompanyInfo.getOffice();
    }
    public UserCompanyInfoDto() {

    }
}
