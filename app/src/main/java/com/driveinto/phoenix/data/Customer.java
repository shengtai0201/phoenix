package com.driveinto.phoenix.data;

import java.util.Date;

// 客戶資料
public class Customer {
//    // 編號
//    private String serialNumber;
    // 老師
    private String mentor;

    // 姓名
    private String name;
    // 公司電話
    private String officePhone;
    // 家裡電話
    private String homePhone;
    // 行動電話
    private String mobilePhone;
    // 地址
    private String address;
    // E-mail
    private String eMail;
    // 生日
    private Date birthday;
    // 身高
    private Integer height;
    // 體重
    private Integer weight;
    // 職業
    private String career;

    public Customer() {
        // Needed for Firebase
    }
}